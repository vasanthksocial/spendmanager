package com.dept.spendmgmt.service;

import com.dept.spendmgmt.dto.FundDemandDecisionRequest;
import com.dept.spendmgmt.dto.FundDemandRaiseRequest;
import com.dept.spendmgmt.model.*;
import com.dept.spendmgmt.repository.AppUserRepository;
import com.dept.spendmgmt.repository.DivisionOfficeRepository;
import com.dept.spendmgmt.repository.FundDemandRepository;
import com.dept.spendmgmt.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class FundDemandService {

    private final FundDemandRepository fundDemandRepository;
    private final InvoiceRepository invoiceRepository;
    private final AppUserRepository userRepository;
    private final DivisionOfficeRepository divisionOfficeRepository;

    public FundDemandService(FundDemandRepository fundDemandRepository,
                              InvoiceRepository invoiceRepository,
                              AppUserRepository userRepository,
                              DivisionOfficeRepository divisionOfficeRepository) {
        this.fundDemandRepository = fundDemandRepository;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.divisionOfficeRepository = divisionOfficeRepository;
    }

    /** Division office bundles one or more fully-approved invoices into a single demand to Head Office. */
    @Transactional
    public FundDemand raise(FundDemandRaiseRequest req) {
        DivisionOffice divisionOffice = divisionOfficeRepository.findById(req.divisionOfficeId())
            .orElseThrow(() -> new IllegalArgumentException("Division office not found: " + req.divisionOfficeId()));

        AppUser raisedBy = userRepository.findById(req.raisedByUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.raisedByUserId()));

        List<Invoice> invoices = invoiceRepository.findAllById(req.invoiceIds());
        if (invoices.size() != req.invoiceIds().size()) {
            throw new IllegalArgumentException("One or more invoice IDs were not found");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Invoice inv : invoices) {
            if (inv.getStatus() != InvoiceStatus.APPROVED) {
                throw new IllegalStateException("Invoice " + inv.getInvoiceNo() + " is not fully approved (status: " + inv.getStatus() + ")");
            }
            if (!inv.getDivisionOffice().getId().equals(divisionOffice.getId())) {
                throw new IllegalStateException("Invoice " + inv.getInvoiceNo() + " does not belong to division office " + divisionOffice.getCode());
            }
            total = total.add(inv.getCurrentAmount());
            inv.setStatus(InvoiceStatus.FUND_DEMAND_RAISED);
        }
        invoiceRepository.saveAll(invoices);

        FundDemand demand = new FundDemand();
        demand.setDivisionOffice(divisionOffice);
        demand.setRaisedBy(raisedBy);
        demand.setTotalAmount(total);
        demand.setStatus(FundDemandStatus.RAISED);
        demand.setInvoices(invoices);

        return fundDemandRepository.save(demand);
    }

    /** Head Office authorizes or rejects a fund demand. Authorization is the trigger point for the (future) bank transfer. */
    @Transactional
    public FundDemand decide(Long fundDemandId, FundDemandDecisionRequest req) {
        FundDemand demand = fundDemandRepository.findById(fundDemandId)
            .orElseThrow(() -> new IllegalArgumentException("Fund demand not found: " + fundDemandId));

        AppUser decider = userRepository.findById(req.decidedByUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.decidedByUserId()));

        if (decider.getRole() != UserRole.HEAD_OFFICE) {
            throw new IllegalStateException("Only a Head Office user can authorize a fund demand");
        }
        if (demand.getStatus() != FundDemandStatus.RAISED) {
            throw new IllegalStateException("Fund demand is in status " + demand.getStatus() + " and cannot be decided again");
        }

        demand.setStatus(req.authorize() ? FundDemandStatus.AUTHORIZED : FundDemandStatus.REJECTED);
        demand.setAuthorizedBy(decider);
        demand.setAuthorizedAt(Instant.now());
        demand.setRemarks(req.remarks());

        InvoiceStatus newInvoiceStatus = req.authorize() ? InvoiceStatus.FUND_DEMAND_AUTHORIZED : InvoiceStatus.APPROVED;
        for (Invoice inv : demand.getInvoices()) {
            inv.setStatus(newInvoiceStatus);
        }
        invoiceRepository.saveAll(demand.getInvoices());

        return fundDemandRepository.save(demand);
    }
}
