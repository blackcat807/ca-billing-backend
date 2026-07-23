package com.thejas.ca_billing_system.controller;

import com.thejas.ca_billing_system.dto.ClientRequest;
import com.thejas.ca_billing_system.dto.ClientResponse;
import com.thejas.ca_billing_system.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse addClient(@RequestBody ClientRequest request) {
        return clientService.addClient(request);
    }

    @GetMapping
    public List<ClientResponse> getAllClients() {
        return clientService.getAllClients();
    }
    @GetMapping("/{clientId}")
    public ClientResponse getClientById(@PathVariable Long clientId) {
        return clientService.getClientById(clientId);
    }
    @PutMapping("/{clientId}")
    public ClientResponse updateClient(@PathVariable Long clientId,
                                       @RequestBody ClientRequest request) {

        return clientService.updateClient(clientId, request);
    }
    @DeleteMapping("/{clientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long clientId) {
        clientService.deleteClient(clientId);
    }

}