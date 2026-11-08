package com.payflow.service;

import com.payflow.dto.request.ClientRequest;
import com.payflow.dto.response.ClientResponse;

import java.util.List;

public interface ClientService {
    ClientResponse createClient(ClientRequest request);
    List<ClientResponse> getAllClients();
    ClientResponse getClientById(Long id);
    ClientResponse updateClient(Long id, ClientRequest request);
    void deleteClient(Long id);
}
