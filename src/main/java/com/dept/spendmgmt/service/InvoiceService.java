package com.dept.spendmgmt.service;

import com.dept.spendmgmt.dto.InvoiceApprovalActionRequest;
import com.dept.spendmgmt.dto.InvoiceSubmitRequest;
import com.dept.spendmgmt.model.*;
import com.dept.spendmgmt.repository.AppUserRepository;
import com.dept.spendmgmt.repository.InvoiceApprovalRepository;
import com.dept.spendmgmt.repository.InvoiceRepository;
import com.dept.spendmgmt.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceApprovalRepository approvalRepository;
    private final WorkOrderRepository workOrderRepository;
    private final AppUserRepository userRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
                           InvoiceApprovalRepository approvalRepository,
                           WorkOrderRepository workOrderRepository,
                           AppUserRepository userRepository) {
        this.invoiceRepository = invoiceRepository;
        this.approvalRepository = approvalRepository;
        this.workOrderRepository = workOrderRepository;
        this.userRepository = userRepository;
    }

    /**
     * Branch/division submission. Validates the claim fits within the work order's remaining
     * contract balance (supports partial/progressive billing) and reserves that amount against
     * the work order immediately, so two invoices can't over-claim the same balance concurrently.
     */
    @Transactional
    public Invoice submit(InvoiceSubmitRequest req) {
        WorkOrder workOrder = workOrderRepository.findById(req.workOrderId())
            .orElseThrow(() -> new IllegalArgumentException("Work order not found: " + req.workOrderId()));

        if (!"ACTIVE".equals(workOrder.getStatus())) {
            throw new IllegalStateException("Work order " + workOrder.getWorkOrderNo() + " is not active");
        }

        AppUser submitter = userRepository.findById(req.submittedByUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.submittedByUserId()));

        if (submitter.getRole() != UserRole.JUNIOR_ENGINEER) {
            throw new IllegalStateException("Only a Junior Engineer can submit an invoice");
        }

        if (req.claimedAmount().compareTo(workOrder.getBalanceAvailable()) > 0) {
            throw new IllegalStateException(
                "Claimed amount " + req.claimedAmount() + " exceeds remaining work order balance "
                    + workOrder.getBalanceAvailable());
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(req.invoiceNo());
        invoice.setWorkOrder(workOrder);
        invoice.setDivisionOffice(workOrder.getDivisionOffice());
        invoice.setSubmittedBy(submitter);
        invoice.setInvoiceDate(req.invoiceDate());
        invoice.setClaimedAmount(req.claimedAmount());
        invoice.setCurrentAmount(req.claimedAmount());
        invoice.setInvoiceFileUrl(req.invoiceFileUrl());
        invoice.setChecklist(req.checklist() != null ? req.checklist() : new HashMap<>());
        invoice.setStatus(InvoiceStatus.SUBMITTED);
        invoice.setCurrentStage(UserRole.JUNIOR_ENGINEER);

        // Reserve the claim against the work order's remaining balance right away.
        workOrder.setClaimedToDate(workOrder.getClaimedToDate().add(req.claimedAmount()));
        workOrderRepository.save(workOrder);

        return invoiceRepository.save(invoice);
    }

    /**
     * Single entry point for JE / AE / EE action on an invoice: approve (advance to next stage,
     * optionally adjusting the amount in the same action), reject (terminal), or modify
     * (adjust the amount only, invoice stays at the same stage for the approver to act again).
     * Every call appends one InvoiceApproval row - nothing is ever overwritten.
     */
    @Transactional
    public Invoice act(Long invoiceId, InvoiceApprovalActionRequest req) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        AppUser actor = userRepository.findById(req.actedByUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.actedByUserId()));

        if (invoice.getCurrentStage() == null || invoice.getCurrentStage() != actor.getRole()) {
            throw new IllegalStateException(
                "Invoice is at stage " + invoice.getCurrentStage() + "; user role " + actor.getRole() + " cannot act on it");
        }
        if (actor.getDivisionOffice() == null || !actor.getDivisionOffice().getId().equals(invoice.getDivisionOffice().getId())) {
            throw new IllegalStateException("User does not belong to the division office that owns this invoice");
        }
        if (invoice.getStatus() != InvoiceStatus.SUBMITTED && invoice.getStatus() != InvoiceStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Invoice is in status " + invoice.getStatus() + " and cannot be acted on");
        }

        BigDecimal amountBefore = invoice.getCurrentAmount();
        BigDecimal amountAfter = amountBefore;

        switch (req.action()) {
            case MODIFY -> {
                if (req.amount() == null) {
                    throw new IllegalArgumentException("amount is required for a MODIFY action");
                }
                amountAfter = req.amount();
                invoice.setCurrentAmount(amountAfter);
                invoice.setStatus(InvoiceStatus.UNDER_REVIEW);
                // stage unchanged - same approver must still explicitly approve or reject
                adjustWorkOrderClaim(invoice.getWorkOrder(), amountBefore, amountAfter);
            }
            case APPROVE -> {
                if (req.amount() != null) {
                    amountAfter = req.amount();
                    invoice.setCurrentAmount(amountAfter);
                    adjustWorkOrderClaim(invoice.getWorkOrder(), amountBefore, amountAfter);
                }
                UserRole next = actor.getRole().next();
                if (next == null) {
                    invoice.setCurrentStage(UserRole.EXECUTIVE_ENGINEER); // stays, marked completed via status
                    invoice.setStatus(InvoiceStatus.APPROVED);
                } else {
                    invoice.setCurrentStage(next);
                    invoice.setStatus(InvoiceStatus.UNDER_REVIEW);
                }
            }
            case REJECT -> {
                invoice.setStatus(InvoiceStatus.REJECTED);
                // release the reserved balance back to the work order
                releaseWorkOrderClaim(invoice.getWorkOrder(), amountBefore);
            }
        }

        invoice.setUpdatedAt(Instant.now());
        invoiceRepository.save(invoice);

        InvoiceApproval log = new InvoiceApproval();
        log.setInvoice(invoice);
        log.setStage(actor.getRole());
        log.setActedBy(actor);
        log.setAction(req.action());
        log.setAmountBefore(amountBefore);
        log.setAmountAfter(amountAfter);
        log.setRemarks(req.remarks());
        approvalRepository.save(log);

        return invoice;
    }

    public List<InvoiceApproval> history(Long invoiceId) {
        return approvalRepository.findByInvoiceIdOrderByActedAtAsc(invoiceId);
    }

    public List<Invoice> pendingFor(Long divisionOfficeId, UserRole role) {
        return invoiceRepository.findByDivisionOfficeIdAndCurrentStageAndStatus(
            divisionOfficeId, role,
            role == UserRole.JUNIOR_ENGINEER ? InvoiceStatus.SUBMITTED : InvoiceStatus.UNDER_REVIEW);
    }

    private void adjustWorkOrderClaim(WorkOrder workOrder, BigDecimal oldAmount, BigDecimal newAmount) {
        BigDecimal delta = newAmount.subtract(oldAmount);
        BigDecimal updated = workOrder.getClaimedToDate().add(delta);
        if (updated.compareTo(workOrder.getContractValue()) > 0) {
            throw new IllegalStateException("Adjusted amount would exceed the work order's contract value");
        }
        workOrder.setClaimedToDate(updated);
        workOrderRepository.save(workOrder);
    }

    private void releaseWorkOrderClaim(WorkOrder workOrder, BigDecimal amount) {
        workOrder.setClaimedToDate(workOrder.getClaimedToDate().subtract(amount));
        workOrderRepository.save(workOrder);
    }
}
