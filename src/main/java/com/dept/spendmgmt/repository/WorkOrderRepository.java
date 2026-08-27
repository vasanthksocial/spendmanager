package com.dept.spendmgmt.repository;

import com.dept.spendmgmt.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByDivisionOfficeId(Long divisionOfficeId);
}