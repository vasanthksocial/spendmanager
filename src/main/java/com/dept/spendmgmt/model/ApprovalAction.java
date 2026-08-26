package com.dept.spendmgmt.model;

public enum ApprovalAction {
    APPROVE,
    REJECT,
    MODIFY,
    RETURN,   // approver sends the invoice back to the submitter for correction (not terminal)
    RESUBMIT  // submitter corrects and resubmits after a RETURN
}
