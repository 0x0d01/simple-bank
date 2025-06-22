package com.d01.simplebank.service;

import com.d01.simplebank.dto.CustomerResponse;
import com.d01.simplebank.entity.Customer;
import com.d01.simplebank.exception.CustomerAlreadyExistsException;
import com.d01.simplebank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Create a new customer
     */
    @Transactional
    public CustomerResponse createCustomer(String cid, String nameTh, String nameEn, String pin, String userId) {
        // Validate CID format (13 digits)
        if (cid == null || !cid.matches("\\d{13}")) {
            throw new IllegalArgumentException("CID must be exactly 13 numeric digits");
        }
        
        // FIXME: need to validate cid checksum - implement Thai National Citizen ID checksum verification
        // The current validation only checks for 13 digits, but should also verify the checksum digit
        
        // Validate PIN format (6 digits)
        if (pin == null || !pin.matches("\\d{6}")) {
            throw new IllegalArgumentException("PIN must be exactly 6 numeric digits");
        }
        
        // Validate userId is not null
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        // Check if customer already exists
        if (customerRepository.existsByCid(cid)) {
            throw new CustomerAlreadyExistsException("Customer with CID " + cid + " already exists");
        }
        
        // Check if user already has a customer
        if (customerRepository.existsByUserId(userId)) {
            throw new CustomerAlreadyExistsException("User with ID " + userId + " already has a customer");
        }
        
        // Encrypt PIN
        String encryptedPin = passwordEncoder.encode(pin);
        
        // Create new customer
        Customer customer = new Customer(cid, nameTh, nameEn, encryptedPin, userId);
        
        try {
            Customer savedCustomer = customerRepository.save(customer);
            return new CustomerResponse(savedCustomer);
        } catch (DataIntegrityViolationException e) {
            // Handle concurrent registration attempts
            String errorMessage = e.getMessage();
            
            // Check for MySQL duplicate entry errors
            if (errorMessage.contains("Duplicate entry")) {
                throw new CustomerAlreadyExistsException("Customer with CID " + cid + " already exists", e);
            }
            throw e; // Re-throw if it's a different constraint violation
        }
    }
    
    /**
     * Get all customers
     */
    public List<CustomerResponse> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(CustomerResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get customer by ID
     */
    public CustomerResponse getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        return new CustomerResponse(customer);
    }
    
    /**
     * Get customer by CID
     */
    public CustomerResponse getCustomerByCid(String cid) {
        Customer customer = customerRepository.findByCid(cid)
                .orElseThrow(() -> new RuntimeException("Customer not found with CID: " + cid));
        
        return new CustomerResponse(customer);
    }
    
    /**
     * Verify PIN for a customer
     */
    public boolean verifyPin(String cid, String pin) {
        Customer customer = customerRepository.findByCid(cid)
                .orElseThrow(() -> new RuntimeException("Customer not found with CID: " + cid));
        
        return passwordEncoder.matches(pin, customer.getPin());
    }
} 