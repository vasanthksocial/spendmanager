package com.dept.spendmgmt.dto;

import com.dept.spendmgmt.model.ApprovalAction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * amount is required only when action == MODIFY (the approver's adjusted claim amount).
 * For APPROVE / REJECT it is ignored and current_amount stays unchanged.
 */
public record InvoiceApprovalActionRequest(
    @NotNull Long actedByUserId,
    @NotNull ApprovalAction action,
    @DecimalMin(value = "0.01") BigDecimal amount,
    String remarks
) {}
