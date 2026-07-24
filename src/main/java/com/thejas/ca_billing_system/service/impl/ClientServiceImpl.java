package com.thejas.ca_billing_system.service.impl;

import com.thejas.ca_billing_system.dto.ClientRequest;
import com.thejas.ca_billing_system.dto.ClientResponse;
import com.thejas.ca_billing_system.entity.Client;
import com.thejas.ca_billing_system.entity.User;
import com.thejas.ca_billing_system.exception.DuplicateResourceException;
import com.thejas.ca_billing_system.exception.ResourceNotFoundException;
import com.thejas.ca_billing_system.repository.ClientRepository;
import com.thejas.ca_billing_system.security.SecurityHelper;
import com.thejas.ca_billing_system.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final SecurityHelper   securityHelper;

    @Override
    public ClientResponse addClient(ClientRequest request) {
        String username = securityHelper.currentUsername();
        User   user     = securityHelper.currentUser();

        if (clientRepository.findByMobileNumberAndUserUsername(
                request.getMobileNumber(), username).isPresent())
            throw new DuplicateResourceException("Mobile number already exists.");

        if (request.getGstin() != null && !request.getGstin().isBlank())
            if (clientRepository.findByGstinAndUserUsername(
                    request.getGstin(), username).isPresent())
                throw new DuplicateResourceException("GSTIN already exists.");

        Client client = Client.builder()
                .user(user)
                .clientName(request.getClientName())
                .mobileNumber(request.getMobileNumber())
                .gstin(request.getGstin())
                .address(request.getAddress())
                .notes(request.getNotes())
                .build();

        return mapToResponse(clientRepository.save(client));
    }

    @Override
    public List<ClientResponse> getAllClients() {
        return clientRepository.findByUserUsername(securityHelper.currentUsername())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ClientResponse getClientById(Long clientId) {
        return mapToResponse(findOwnedClient(clientId));
    }

    @Override
    public ClientResponse updateClient(Long clientId, ClientRequest request) {
        Client client   = findOwnedClient(clientId);
        String username = securityHelper.currentUsername();

        if (!client.getMobileNumber().equals(request.getMobileNumber()))
            if (clientRepository.findByMobileNumberAndUserUsername(
                    request.getMobileNumber(), username).isPresent())
                throw new DuplicateResourceException("Mobile number already exists.");

        if (request.getGstin() != null && !request.getGstin().isBlank()
                && !request.getGstin().equals(client.getGstin()))
            if (clientRepository.findByGstinAndUserUsername(
                    request.getGstin(), username).isPresent())
                throw new DuplicateResourceException("GSTIN already exists.");

        client.setClientName(request.getClientName());
        client.setMobileNumber(request.getMobileNumber());
        client.setGstin(request.getGstin());
        client.setAddress(request.getAddress());
        client.setNotes(request.getNotes());

        return mapToResponse(clientRepository.save(client));
    }

    @Override
    public void deleteClient(Long clientId) {
        clientRepository.delete(findOwnedClient(clientId));
    }

    private Client findOwnedClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found: " + clientId));
        if (!client.getUser().getUsername().equals(securityHelper.currentUsername()))
            throw new ResourceNotFoundException("Client not found: " + clientId);
        return client;
    }

    private ClientResponse mapToResponse(Client c) {
        ClientResponse r = new ClientResponse();
        r.setClientId(c.getClientId());
        r.setClientName(c.getClientName());
        r.setMobileNumber(c.getMobileNumber());
        r.setGstin(c.getGstin());
        r.setAddress(c.getAddress());
        r.setNotes(c.getNotes());
        return r;
    }
}
