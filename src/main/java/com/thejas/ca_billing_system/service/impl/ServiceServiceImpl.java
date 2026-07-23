package com.thejas.ca_billing_system.service.impl;

import com.thejas.ca_billing_system.dto.ServiceRequest;
import com.thejas.ca_billing_system.dto.ServiceResponse;
import com.thejas.ca_billing_system.entity.Service;
import com.thejas.ca_billing_system.exception.ResourceAlreadyExistsException;
import com.thejas.ca_billing_system.exception.ResourceNotFoundException;
import com.thejas.ca_billing_system.repository.ServiceRepository;
import com.thejas.ca_billing_system.service.ServiceService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    @Override
    public ServiceResponse createService(ServiceRequest request) {

        String serviceName = request.getServiceName().trim();

        if (serviceRepository.existsByServiceNameIgnoreCase(serviceName)) {
            throw new ResourceAlreadyExistsException(
                    "Service already exists with name: " + serviceName);
        }

        Service service = Service.builder()
                .serviceName(serviceName)
                .active(request.getActive() != null ? request.getActive() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        return mapToResponse(serviceRepository.save(service));
    }

    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findByActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceResponse getServiceById(Long serviceId) {

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Service not found with id: " + serviceId));

        return mapToResponse(service);
    }

    @Override
    public ServiceResponse updateService(Long serviceId, ServiceRequest request) {

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Service not found with id: " + serviceId));

        String serviceName = request.getServiceName().trim();

        serviceRepository.findByServiceNameIgnoreCase(serviceName)
                .ifPresent(existing -> {
                    if (!existing.getServiceId().equals(serviceId)) {
                        throw new ResourceAlreadyExistsException(
                                "Service already exists with name: " + serviceName);
                    }
                });

        service.setServiceName(serviceName);
        service.setActive(request.getActive() != null ? request.getActive() : true);
        service.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        return mapToResponse(serviceRepository.save(service));
    }

    @Override
    public void deleteService(Long serviceId) {

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Service not found with id: " + serviceId));

        service.setActive(false);

        serviceRepository.save(service);
    }

    private ServiceResponse mapToResponse(Service service) {

        return ServiceResponse.builder()
                .serviceId(service.getServiceId())
                .serviceName(service.getServiceName())
                .active(service.getActive())
                .displayOrder(service.getDisplayOrder())
                .build();
    }
}