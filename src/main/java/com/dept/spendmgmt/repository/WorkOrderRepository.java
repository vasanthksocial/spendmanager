package com.dept.spendmgmt.repository;

import com.dept.spendmgmt.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
}
