package com.dept.spendmgmt.controller;

import com.dept.spendmgmt.dto.ApprovalMatrixCreateRequest;
import com.dept.spendmgmt.model.ApprovalMatrix;
import com.dept.spendmgmt.service.ApprovalMatrixService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval-matrix")
public class ApprovalMatrixController {

    private final ApprovalMatrixService service;

    public ApprovalMatrixController(ApprovalMatrixService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApprovalMatrix> create(@Valid @RequestBody ApprovalMatrixCreateRequest req) {
        ApprovalMatrix rule = new ApprovalMatrix();
        rule.setCategory(req.category());
        rule.setMinAmount(req.minAmount());
        rule.setMaxAmount(req.maxAmount());
        rule.setApprovalChain(req.approvalChain());
        return ResponseEntity.ok(service.create(rule));
    }

    @GetMapping
    public ResponseEntity<List<ApprovalMatrix>> list() {
        return ResponseEntity.ok(service.listAll());
    }
}
