package com.dept.spendmgmt.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FundDemandRaiseRequest(
    @NotNull Long divisionOfficeId,
    @NotNull Long raisedByUserId,
    @NotEmpty List<Long> invoiceIds
) {}
