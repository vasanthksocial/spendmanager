package com.dept.spendmgmt.dto;

import jakarta.validation.constraints.NotNull;

public record FundDemandDecisionRequest(
    @NotNull Long decidedByUserId,
    boolean authorize,
    String remarks
) {}
