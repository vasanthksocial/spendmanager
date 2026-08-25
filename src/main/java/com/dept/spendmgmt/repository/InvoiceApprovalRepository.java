package com.dept.spendmgmt.repository;

import com.dept.spendmgmt.model.InvoiceApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceApprovalRepository extends JpaRepository<InvoiceApproval, Long> {
    List<InvoiceApproval> findByInvoiceIdOrderByActedAtAsc(Long invoiceId);
}
