package com.dept.spendmgmt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ApprovalMatrixCreateRequest(
    @NotBlank String category,
    @NotNull BigDecimal minAmount,
    BigDecimal maxAmount, // null = no upper bound
    @NotEmpty List<String> approvalChain // e.g. ["JUNIOR_ENGINEER","ASSISTANT_ENGINEER","EXECUTIVE_ENGINEER"]
) {}
