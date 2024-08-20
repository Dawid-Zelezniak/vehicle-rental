package com.vehicle.rental.zelezniak.user_domain.service;

import com.vehicle.rental.zelezniak.user_domain.model.client.Client;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import com.vehicle.rental.zelezniak.user_domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientValidator clientValidator;
    private final InputValidator inputValidator;

    @Transactional(readOnly = true)
    public Page<Client> findAll(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Client findById(Long id) {
        validateId(id);
        return findClient(id);
    }

    @Transactional
    public Client update(Long id, Client newData) {
        validateId(id);
        validateClient(newData);
        Client clientFromDb = findClient(id);
        return validateAndUpdateClient(clientFromDb, newData);
    }

    @Transactional
    public void delete(Long id) {
        validateId(id);
        Client clientToDelete = findClient(id);
        handleDeleteClient(clientToDelete);
    }

    @Transactional(readOnly = true)
    public Client findByEmail(String email) {
        validateEmail(email);
        return clientRepository.findByCredentialsEmail(email)
                .orElseThrow(() -> new NoSuchElementException(
                        "Client with email: " + email + " does not exists."));
    }

    private void validateId(Long id) {
        inputValidator.throwExceptionIfObjectIsNull(id, InputValidator.CLIENT_ID_NOT_NULL);
    }

    private void validateClient(Client client) {
        inputValidator.throwExceptionIfObjectIsNull(client, InputValidator.CLIENT_NOT_NULL);
    }

    private void validateEmail(String email) {
        inputValidator.throwExceptionIfObjectIsNull(email, InputValidator.CLIENT_EMAIL_NOT_NULL);
    }

    private Client findClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id: " + id + " does not exist."));
    }

    private Client validateAndUpdateClient(Client clientFromDb, Client newData) {
        String clientEmail = clientFromDb.getEmail();
        clientValidator.checkIfUserCanBeUpdated(clientEmail, newData);
        updateClient(clientFromDb, newData);
        return clientRepository.save(clientFromDb);
    }

    private void updateClient(Client clientFromDb, Client newData) {
        clientFromDb.setName(newData.getName());
        clientFromDb.setCredentials(newData.getCredentials());
        clientFromDb.setAddress(newData.getAddress());
    }

    private void handleDeleteClient(Client clientToDelete) {
        removeRoles(clientToDelete);
       deleteClientFromAllTables(clientToDelete);
    }

    private void deleteClientFromAllTables(Client c) {
        clientRepository.removeClientFromReservations(c.getId());
        clientRepository.removeClientFromRents(c.getId());
        clientRepository.delete(c);
    }

    private void removeRoles(Client userToDelete) {
        userToDelete.setRoles(null);
    }
}

