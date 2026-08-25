package com.dept.spendmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "work_order")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_order_no", nullable = false, unique = true, length = 50)
    private String workOrderNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "division_office_id", nullable = false)
    private DivisionOffice divisionOffice;

    @Column(name = "vendor_name", nullable = false, length = 200)
    private String vendorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "scheme_type", nullable = false, length = 30)
    private SchemeType schemeType;

    @Column(name = "contract_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal contractValue;

    @Column(name = "budget_allocated", nullable = false, precision = 18, scale = 2)
    private BigDecimal budgetAllocated = BigDecimal.ZERO;

    @Column(name = "claimed_to_date", nullable = false, precision = 18, scale = 2)
    private BigDecimal claimedToDate = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Transient
    public BigDecimal getBalanceAvailable() {
        return contractValue.subtract(claimedToDate);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getWorkOrderNo() { return workOrderNo; }
    public void setWorkOrderNo(String workOrderNo) { this.workOrderNo = workOrderNo; }
    public DivisionOffice getDivisionOffice() { return divisionOffice; }
    public void setDivisionOffice(DivisionOffice divisionOffice) { this.divisionOffice = divisionOffice; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public SchemeType getSchemeType() { return schemeType; }
    public void setSchemeType(SchemeType schemeType) { this.schemeType = schemeType; }
    public BigDecimal getContractValue() { return contractValue; }
    public void setContractValue(BigDecimal contractValue) { this.contractValue = contractValue; }
    public BigDecimal getBudgetAllocated() { return budgetAllocated; }
    public void setBudgetAllocated(BigDecimal budgetAllocated) { this.budgetAllocated = budgetAllocated; }
    public BigDecimal getClaimedToDate() { return claimedToDate; }
    public void setClaimedToDate(BigDecimal claimedToDate) { this.claimedToDate = claimedToDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
