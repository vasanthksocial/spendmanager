package com.dept.spendmgmt.repository;

import com.dept.spendmgmt.model.ApprovalMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApprovalMatrixRepository extends JpaRepository<ApprovalMatrix, Long> {

    /**
     * Active rules for a category, ordered so the narrowest amount band comes first.
     * This makes the first match in the list the most specific rule, resolving any
     * ambiguity when amount ranges overlap (e.g. a general 0-unbounded rule and a
     * more specific 50000-100000 rule for the same category).
     */
    @Query("""
        SELECT a FROM ApprovalMatrix a
        WHERE a.category = :category AND a.active = true
        ORDER BY COALESCE(a.maxAmount, 999999999999) - a.minAmount ASC
        """)
    List<ApprovalMatrix> findByCategoryOrderedBySpecificity(@Param("category") String category);
}
