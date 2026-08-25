package com.dept.spendmgmt.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fund_demand")
public class FundDemand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "division_office_id", nullable = false)
    private DivisionOffice divisionOffice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "raised_by", nullable = false)
    private AppUser raisedBy;

    @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FundDemandStatus status = FundDemandStatus.RAISED;

    @Column(name = "raised_at", nullable = false, updatable = false)
    private Instant raisedAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorized_by")
    private AppUser authorizedBy;

    @Column(name = "authorized_at")
    private Instant authorizedAt;

    @Column(columnDefinition = "text")
    private String remarks;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fund_demand_invoice",
        joinColumns = @JoinColumn(name = "fund_demand_id"),
        inverseJoinColumns = @JoinColumn(name = "invoice_id")
    )
    private List<Invoice> invoices = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DivisionOffice getDivisionOffice() { return divisionOffice; }
    public void setDivisionOffice(DivisionOffice divisionOffice) { this.divisionOffice = divisionOffice; }
    public AppUser getRaisedBy() { return raisedBy; }
    public void setRaisedBy(AppUser raisedBy) { this.raisedBy = raisedBy; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public FundDemandStatus getStatus() { return status; }
    public void setStatus(FundDemandStatus status) { this.status = status; }
    public Instant getRaisedAt() { return raisedAt; }
    public void setRaisedAt(Instant raisedAt) { this.raisedAt = raisedAt; }
    public AppUser getAuthorizedBy() { return authorizedBy; }
    public void setAuthorizedBy(AppUser authorizedBy) { this.authorizedBy = authorizedBy; }
    public Instant getAuthorizedAt() { return authorizedAt; }
    public void setAuthorizedAt(Instant authorizedAt) { this.authorizedAt = authorizedAt; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public List<Invoice> getInvoices() { return invoices; }
    public void setInvoices(List<Invoice> invoices) { this.invoices = invoices; }
}
