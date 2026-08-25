package com.dept.spendmgmt.repository;

import com.dept.spendmgmt.model.Invoice;
import com.dept.spendmgmt.model.InvoiceStatus;
import com.dept.spendmgmt.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByDivisionOfficeIdAndCurrentStageAndStatus(Long divisionOfficeId, UserRole stage, InvoiceStatus status);
    List<Invoice> findByDivisionOfficeIdAndStatus(Long divisionOfficeId, InvoiceStatus status);
}
