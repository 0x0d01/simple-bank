package com.d01.simplebank.controller;

import com.d01.simplebank.dto.CustomerResponse;
import com.d01.simplebank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable String id) {
        CustomerResponse customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
    
    /**
     * Get customer by CID
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<CustomerResponse> getCustomerByCid(@PathVariable String cid) {
        CustomerResponse customer = customerService.getCustomerByCid(cid);
        return ResponseEntity.ok(customer);
    }
    
    /**
     * Verify PIN for a customer
     */
    @PostMapping("/{cid}/verify-pin")
    public ResponseEntity<Boolean> verifyPin(@PathVariable String cid, @RequestBody String pin) {
        boolean isValid = customerService.verifyPin(cid, pin);
        return ResponseEntity.ok(isValid);
    }
} 