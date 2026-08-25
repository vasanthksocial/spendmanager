package com.dept.spendmgmt.controller;

import com.dept.spendmgmt.dto.FundDemandDecisionRequest;
import com.dept.spendmgmt.dto.FundDemandRaiseRequest;
import com.dept.spendmgmt.model.FundDemand;
import com.dept.spendmgmt.service.FundDemandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fund-demands")
public class FundDemandController {

    private final FundDemandService fundDemandService;

    public FundDemandController(FundDemandService fundDemandService) {
        this.fundDemandService = fundDemandService;
    }

    @PostMapping
    public ResponseEntity<FundDemand> raise(@Valid @RequestBody FundDemandRaiseRequest req) {
        return ResponseEntity.ok(fundDemandService.raise(req));
    }

    @PostMapping("/{id}/decision")
    public ResponseEntity<FundDemand> decide(@PathVariable Long id, @Valid @RequestBody FundDemandDecisionRequest req) {
        return ResponseEntity.ok(fundDemandService.decide(id, req));
    }
}
