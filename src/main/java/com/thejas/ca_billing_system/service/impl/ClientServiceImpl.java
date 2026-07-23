package com.thejas.ca_billing_system.service.impl;

import com.thejas.ca_billing_system.dto.ClientRequest;
import com.thejas.ca_billing_system.dto.ClientResponse;
import com.thejas.ca_billing_system.entity.Client;
import com.thejas.ca_billing_system.exception.DuplicateResourceException;
import com.thejas.ca_billing_system.exception.ResourceNotFoundException;
import com.thejas.ca_billing_system.repository.ClientRepository;
import com.thejas.ca_billing_system.service.ClientService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientResponse addClient(ClientRequest request) {

        // Check duplicate mobile number
        if (clientRepository.findByMobileNumber(request.getMobileNumber()).isPresent()) {
            throw new DuplicateResourceException("Mobile number already exists.");
        }

        // Check duplicate GSTIN
        if (request.getGstin() != null && !request.getGstin().isBlank()) {
            if (clientRepository.findByGstin(request.getGstin()).isPresent()) {
                throw new DuplicateResourceException("GSTIN already exists.");
            }
        }

        // Convert DTO to Entity
        Client client = Client.builder()
                .clientName(request.getClientName())
                .mobileNumber(request.getMobileNumber())
                .gstin(request.getGstin())
                .address(request.getAddress())
                .notes(request.getNotes())
                .build();

        // Save in database
        Client savedClient = clientRepository.save(client);

        // Convert Entity to Response DTO
        return mapToResponse(savedClient);
    }

    @Override
    public List<ClientResponse> getAllClients() {

        List<Client> clients = clientRepository.findAll();

        return clients.stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public ClientResponse getClientById(Long clientId) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Client not found with ID: " + clientId));

        return mapToResponse(client);
    }

    @Override
    public ClientResponse updateClient(Long clientId, ClientRequest request) {

        // Find client by ID
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Client not found with ID: " + clientId));

        // Check duplicate mobile number
        Optional<Client> existingMobileClient =
                clientRepository.findByMobileNumber(request.getMobileNumber());

        if (existingMobileClient.isPresent() &&
                !existingMobileClient.get().getClientId().equals(clientId)) {

            throw new DuplicateResourceException("Mobile number already exists.");
        }

        // Check duplicate GSTIN (if GSTIN is provided)
        if (request.getGstin() != null && !request.getGstin().isBlank()) {

            Optional<Client> existingGstinClient =
                    clientRepository.findByGstin(request.getGstin());

            if (existingGstinClient.isPresent() &&
                    !existingGstinClient.get().getClientId().equals(clientId)) {

                throw new DuplicateResourceException("GSTIN already exists.");
            }
        }

        // Update fields
        client.setClientName(request.getClientName());
        client.setMobileNumber(request.getMobileNumber());
        client.setGstin(request.getGstin());
        client.setAddress(request.getAddress());
        client.setNotes(request.getNotes());

        // Save updated client
        Client updatedClient = clientRepository.save(client);

        // Return response
        return mapToResponse(updatedClient);
    }

    @Override
    public void deleteClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Client not found with ID: " + clientId));

        clientRepository.delete(client);

    }
    private ClientResponse mapToResponse(Client client) {

        return ClientResponse.builder()
                .clientId(client.getClientId())
                .clientName(client.getClientName())
                .mobileNumber(client.getMobileNumber())
                .gstin(client.getGstin())
                .address(client.getAddress())
                .notes(client.getNotes())
                .build();
    }
}