package com.dept.spendmgmt.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * One row per approve/reject/modify action taken on an invoice, at any stage.
 * Never updated after insert - this is the audit trail for adjustments.
 */
@Entity
@Table(name = "invoice_approval")
public class InvoiceApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole stage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "acted_by", nullable = false)
    private AppUser actedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApprovalAction action;

    @Column(name = "amount_before", nullable = false, precision = 18, scale = 2)
    private BigDecimal amountBefore;

    @Column(name = "amount_after", nullable = false, precision = 18, scale = 2)
    private BigDecimal amountAfter;

    @Column(columnDefinition = "text")
    private String remarks;

    @Column(name = "acted_at", nullable = false, updatable = false)
    private Instant actedAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
    public UserRole getStage() { return stage; }
    public void setStage(UserRole stage) { this.stage = stage; }
    public AppUser getActedBy() { return actedBy; }
    public void setActedBy(AppUser actedBy) { this.actedBy = actedBy; }
    public ApprovalAction getAction() { return action; }
    public void setAction(ApprovalAction action) { this.action = action; }
    public BigDecimal getAmountBefore() { return amountBefore; }
    public void setAmountBefore(BigDecimal amountBefore) { this.amountBefore = amountBefore; }
    public BigDecimal getAmountAfter() { return amountAfter; }
    public void setAmountAfter(BigDecimal amountAfter) { this.amountAfter = amountAfter; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Instant getActedAt() { return actedAt; }
    public void setActedAt(Instant actedAt) { this.actedAt = actedAt; }
}
