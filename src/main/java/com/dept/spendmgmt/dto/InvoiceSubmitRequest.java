package com.dept.spendmgmt.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record InvoiceSubmitRequest(
    @NotBlank String invoiceNo,
    @NotNull Long workOrderId,
    @NotNull Long submittedByUserId,
    @NotNull LocalDate invoiceDate,
    @NotNull @DecimalMin(value = "0.01") BigDecimal claimedAmount,
    @NotBlank String invoiceFileUrl,
    Map<String, Object> checklist,
    String category // optional - defaults to "GENERAL" if not provided
) {}
