package com.dept.spendmgmt.controller;

import com.dept.spendmgmt.dto.WorkOrderCreateRequest;
import com.dept.spendmgmt.model.WorkOrder;
import com.dept.spendmgmt.service.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @PostMapping
    public ResponseEntity<WorkOrder> create(@Valid @RequestBody WorkOrderCreateRequest req) {
        return ResponseEntity.ok(workOrderService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<WorkOrder>> list(@RequestParam(required = false) Long divisionOfficeId) {
        if (divisionOfficeId != null) {
            return ResponseEntity.ok(workOrderService.listByDivisionOffice(divisionOfficeId));
        }
        return ResponseEntity.ok(workOrderService.listAll());
    }
}