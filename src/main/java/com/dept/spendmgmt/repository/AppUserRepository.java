package com.dept.spendmgmt.repository;

import com.dept.spendmgmt.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}
