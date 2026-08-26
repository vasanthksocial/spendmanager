package com.dept.spendmgmt.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Used by the original submitter (JE) to correct and resend an invoice that was RETURNED.
 * amount and invoiceFileUrl are optional - only include them if the correction changes that field.
 */
public record InvoiceResubmitRequest(
    @NotNull Long submittedByUserId,
    @DecimalMin(value = "0.01") BigDecimal amount,
    String invoiceFileUrl,
    String remarks
) {}
