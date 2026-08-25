package com.dept.spendmgmt.repository;

import com.dept.spendmgmt.model.ApprovalMatrix;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalMatrixRepository extends JpaRepository<ApprovalMatrix, Long> {
    List<ApprovalMatrix> findByCategoryAndActiveTrue(String category);
}
