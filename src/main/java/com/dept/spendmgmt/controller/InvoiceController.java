package com.dept.spendmgmt.controller;

import com.dept.spendmgmt.dto.InvoiceApprovalActionRequest;
import com.dept.spendmgmt.dto.InvoiceResubmitRequest;
import com.dept.spendmgmt.dto.InvoiceSubmitRequest;
import com.dept.spendmgmt.model.Invoice;
import com.dept.spendmgmt.model.InvoiceApproval;
import com.dept.spendmgmt.model.UserRole;
import com.dept.spendmgmt.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<Invoice> submit(@Valid @RequestBody InvoiceSubmitRequest req) {
        return ResponseEntity.ok(invoiceService.submit(req));
    }

    @PostMapping("/{id}/actions")
    public ResponseEntity<Invoice> act(@PathVariable Long id, @Valid @RequestBody InvoiceApprovalActionRequest req) {
        return ResponseEntity.ok(invoiceService.act(id, req));
    }

    @PostMapping("/{id}/resubmit")
    public ResponseEntity<Invoice> resubmit(@PathVariable Long id, @Valid @RequestBody InvoiceResubmitRequest req) {
        return ResponseEntity.ok(invoiceService.resubmit(id, req));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<InvoiceApproval>> history(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.history(id));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Invoice>> pending(@RequestParam Long divisionOfficeId, @RequestParam UserRole role) {
        return ResponseEntity.ok(invoiceService.pendingFor(divisionOfficeId, role));
    }
}
