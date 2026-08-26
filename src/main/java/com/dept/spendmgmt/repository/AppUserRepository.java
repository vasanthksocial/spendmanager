package com.dept.spendmgmt.repository;

import com.dept.spendmgmt.model.AppUser;
import com.dept.spendmgmt.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    List<AppUser> findByRoleAndDivisionOfficeId(UserRole role, Long divisionOfficeId);
    List<AppUser> findByRole(UserRole role);
}
