package com.dept.spendmgmt.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_no", nullable = false, length = 50)
    private String invoiceNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "division_office_id", nullable = false)
    private DivisionOffice divisionOffice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submitted_by", nullable = false)
    private AppUser submittedBy;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    /** Original amount claimed by the vendor/JE at submission time - never changes after submission. */
    @Column(name = "claimed_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal claimedAmount;

    /** Running amount as adjusted by approvers along the chain - this is what actually gets paid. */
    @Column(name = "current_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal currentAmount;

    @Column(name = "invoice_file_url", nullable = false)
    private String invoiceFileUrl;

    @Type(JsonType.class)
    @Column(name = "checklist_json", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> checklist = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InvoiceStatus status = InvoiceStatus.SUBMITTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_stage", nullable = false, length = 30)
    private UserRole currentStage = UserRole.JUNIOR_ENGINEER;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public WorkOrder getWorkOrder() { return workOrder; }
    public void setWorkOrder(WorkOrder workOrder) { this.workOrder = workOrder; }
    public DivisionOffice getDivisionOffice() { return divisionOffice; }
    public void setDivisionOffice(DivisionOffice divisionOffice) { this.divisionOffice = divisionOffice; }
    public AppUser getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(AppUser submittedBy) { this.submittedBy = submittedBy; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    public BigDecimal getClaimedAmount() { return claimedAmount; }
    public void setClaimedAmount(BigDecimal claimedAmount) { this.claimedAmount = claimedAmount; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
    public String getInvoiceFileUrl() { return invoiceFileUrl; }
    public void setInvoiceFileUrl(String invoiceFileUrl) { this.invoiceFileUrl = invoiceFileUrl; }
    public Map<String, Object> getChecklist() { return checklist; }
    public void setChecklist(Map<String, Object> checklist) { this.checklist = checklist; }
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public UserRole getCurrentStage() { return currentStage; }
    public void setCurrentStage(UserRole currentStage) { this.currentStage = currentStage; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
