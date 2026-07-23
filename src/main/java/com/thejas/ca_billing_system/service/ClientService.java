package com.thejas.ca_billing_system.service;

import com.thejas.ca_billing_system.dto.ClientRequest;
import com.thejas.ca_billing_system.dto.ClientResponse;

import java.util.List;

public interface ClientService {

    ClientResponse addClient(ClientRequest request);

    List<ClientResponse> getAllClients();

    ClientResponse getClientById(Long clientId);

    ClientResponse updateClient(Long clientId, ClientRequest request);

    void deleteClient(Long clientId);
}