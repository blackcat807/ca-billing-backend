package com.thejas.ca_billing_system.service.impl;

import com.thejas.ca_billing_system.dto.ServiceRequest;
import com.thejas.ca_billing_system.dto.ServiceResponse;
import com.thejas.ca_billing_system.entity.Service;
import com.thejas.ca_billing_system.entity.User;
import com.thejas.ca_billing_system.exception.ResourceAlreadyExistsException;
import com.thejas.ca_billing_system.exception.ResourceNotFoundException;
import com.thejas.ca_billing_system.repository.ServiceRepository;
import com.thejas.ca_billing_system.security.SecurityHelper;
import com.thejas.ca_billing_system.service.ServiceService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final SecurityHelper    securityHelper;

    @Override
    public ServiceResponse createService(ServiceRequest request) {
        String username = securityHelper.currentUsername();
        User   user     = securityHelper.currentUser();
        String name     = request.getServiceName().trim();

        if (serviceRepository.existsByServiceNameIgnoreCaseAndUserUsername(name, username))
            throw new ResourceAlreadyExistsException("Service already exists: " + name);

        Service service = Service.builder()
                .user(user)
                .serviceName(name)
                .active(request.getActive() != null ? request.getActive() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        return mapToResponse(serviceRepository.save(service));
    }

    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceRepository
                .findByUserUsernameOrderByDisplayOrderAsc(securityHelper.currentUsername())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ServiceResponse getServiceById(Long id) {
        return mapToResponse(findOwnedService(id));
    }

    @Override
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        Service service = findOwnedService(id);
        service.setServiceName(request.getServiceName().trim());
        service.setActive(request.getActive() != null ? request.getActive() : service.getActive());
        service.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : service.getDisplayOrder());
        return mapToResponse(serviceRepository.save(service));
    }

    @Override
    public void deleteService(Long id) {
        serviceRepository.delete(findOwnedService(id));
        
    }

    private Service findOwnedService(Long id) {
        Service s = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + id));
        if (!s.getUser().getUsername().equals(securityHelper.currentUsername()))
            throw new ResourceNotFoundException("Service not found: " + id);
        return s;
    }

    private ServiceResponse mapToResponse(Service s) {
        ServiceResponse r = new ServiceResponse();
        r.setServiceId(s.getServiceId());
        r.setServiceName(s.getServiceName());
        r.setActive(s.getActive());
        r.setDisplayOrder(s.getDisplayOrder());
        return r;
    }
}
