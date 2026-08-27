package com.dept.spendmgmt.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record WorkOrderCreateRequest(
    @NotBlank String workOrderNo,
    @NotNull Long divisionOfficeId,
    @NotBlank String vendorName,
    @NotBlank String schemeType, // CENTRAL_SPONSORED, STATE_SPONSORED, OTHER
    @NotNull @DecimalMin(value = "0.01") BigDecimal contractValue,
    @NotNull @DecimalMin(value = "0.00") BigDecimal budgetAllocated,
    @NotNull Long createdByUserId // must be HEAD_OFFICE
) {}