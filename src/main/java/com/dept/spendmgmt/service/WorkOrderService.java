package com.dept.spendmgmt.service;

import com.dept.spendmgmt.dto.WorkOrderCreateRequest;
import com.dept.spendmgmt.model.*;
import com.dept.spendmgmt.repository.AppUserRepository;
import com.dept.spendmgmt.repository.DivisionOfficeRepository;
import com.dept.spendmgmt.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final DivisionOfficeRepository divisionOfficeRepository;
    private final AppUserRepository userRepository;

    public WorkOrderService(WorkOrderRepository workOrderRepository,
                             DivisionOfficeRepository divisionOfficeRepository,
                             AppUserRepository userRepository) {
        this.workOrderRepository = workOrderRepository;
        this.divisionOfficeRepository = divisionOfficeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public WorkOrder create(WorkOrderCreateRequest req) {
        AppUser creator = userRepository.findById(req.createdByUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.createdByUserId()));

        if (creator.getRole() != UserRole.HEAD_OFFICE) {
            throw new IllegalStateException("Only a Head Office user can create a work order");
        }

        DivisionOffice divisionOffice = divisionOfficeRepository.findById(req.divisionOfficeId())
            .orElseThrow(() -> new IllegalArgumentException("Division office not found: " + req.divisionOfficeId()));

        SchemeType schemeType;
        try {
            schemeType = SchemeType.valueOf(req.schemeType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid schemeType '" + req.schemeType()
                + "'. Must be one of CENTRAL_SPONSORED, STATE_SPONSORED, OTHER");
        }

        if (req.budgetAllocated().compareTo(req.contractValue()) > 0) {
            throw new IllegalStateException("Budget allocated cannot exceed the contract value");
        }

        WorkOrder wo = new WorkOrder();
        wo.setWorkOrderNo(req.workOrderNo());
        wo.setDivisionOffice(divisionOffice);
        wo.setVendorName(req.vendorName());
        wo.setSchemeType(schemeType);
        wo.setContractValue(req.contractValue());
        wo.setBudgetAllocated(req.budgetAllocated());
        wo.setStatus("ACTIVE");

        return workOrderRepository.save(wo);
    }

    public List<WorkOrder> listByDivisionOffice(Long divisionOfficeId) {
        return workOrderRepository.findByDivisionOfficeId(divisionOfficeId);
    }

    public List<WorkOrder> listAll() {
        return workOrderRepository.findAll();
    }
}.