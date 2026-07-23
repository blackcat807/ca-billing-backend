package com.thejas.ca_billing_system.service;

import com.thejas.ca_billing_system.dto.ServiceRequest;
import com.thejas.ca_billing_system.dto.ServiceResponse;

import java.util.List;

public interface ServiceService {

    ServiceResponse createService(ServiceRequest request);

    List<ServiceResponse> getAllServices();

    ServiceResponse getServiceById(Long serviceId);

    ServiceResponse updateService(Long serviceId, ServiceRequest request);

    void deleteService(Long serviceId);
}