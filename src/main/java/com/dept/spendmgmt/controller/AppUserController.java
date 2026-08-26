package com.dept.spendmgmt.controller;

import com.dept.spendmgmt.model.AppUser;
import com.dept.spendmgmt.model.UserRole;
import com.dept.spendmgmt.repository.AppUserRepository;
import com.dept.spendmgmt.repository.DivisionOfficeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read-only lookups to populate "act as" selectors on the demo screens, since there's no
 * auth yet. Once real login exists, this endpoint becomes unnecessary for that purpose.
 */
@RestController
@RequestMapping("/api/users")
public class AppUserController {

    private final AppUserRepository userRepository;
    private final DivisionOfficeRepository divisionOfficeRepository;

    public AppUserController(AppUserRepository userRepository, DivisionOfficeRepository divisionOfficeRepository) {
        this.userRepository = userRepository;
        this.divisionOfficeRepository = divisionOfficeRepository;
    }

    @GetMapping
    public ResponseEntity<List<AppUser>> list(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Long divisionOfficeId) {
        if (role != null && divisionOfficeId != null) {
            return ResponseEntity.ok(userRepository.findByRoleAndDivisionOfficeId(role, divisionOfficeId));
        }
        if (role != null) {
            return ResponseEntity.ok(userRepository.findByRole(role));
        }
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/division-offices")
    public ResponseEntity<?> divisionOffices() {
        return ResponseEntity.ok(divisionOfficeRepository.findAll());
    }
}
