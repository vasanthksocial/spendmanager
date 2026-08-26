package com.dept.spendmgmt.service;

import com.dept.spendmgmt.model.ApprovalMatrix;
import com.dept.spendmgmt.repository.ApprovalMatrixRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ApprovalMatrixService {

    private final ApprovalMatrixRepository repository;

    public ApprovalMatrixService(ApprovalMatrixRepository repository) {
        this.repository = repository;
    }

    /**
     * Resolves the approval chain for a category + amount at submission time.
     * Rules are checked narrowest-band-first, so if ranges overlap, the most
     * specific rule wins rather than whichever the database happened to return first.
     * The result is frozen onto the invoice - later matrix changes never affect
     * invoices already in flight.
     */
    public List<String> resolveChain(String category, BigDecimal amount) {
        List<ApprovalMatrix> rules = repository.findByCategoryOrderedBySpecificity(category);
        for (ApprovalMatrix rule : rules) {
            boolean aboveMin = amount.compareTo(rule.getMinAmount()) >= 0;
            boolean belowMax = rule.getMaxAmount() == null || amount.compareTo(rule.getMaxAmount()) <= 0;
            if (aboveMin && belowMax) {
                return rule.getApprovalChain();
            }
        }
        throw new IllegalStateException(
            "No approval matrix rule configured for category '" + category + "' at amount " + amount);
    }

    public ApprovalMatrix create(ApprovalMatrix rule) {
        return repository.save(rule);
    }

    public List<ApprovalMatrix> listAll() {
        return repository.findAll();
    }
}
