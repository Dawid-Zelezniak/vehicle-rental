package com.vehicle.rental.zelezniak.user.service;

import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import com.vehicle.rental.zelezniak.user.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
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
        validateNotNull(id, InputValidator.CLIENT_ID_NOT_NULL);
        log.debug("Searching for client with id: {}", id);
        return findClient(id);
    }

    @Transactional
    public Client update(Long id, Client newData) {
        log.debug("Validating client data before update: {}", newData.getEmail());
        validateNotNull(id, InputValidator.CLIENT_ID_NOT_NULL);
        validateNotNull(newData, InputValidator.CLIENT_NOT_NULL);
        Client clientFromDb = findClient(id);
        return validateAndUpdateClient(clientFromDb, newData);
    }

    @Transactional
    public void delete(Long id) {
        validateNotNull(id, InputValidator.CLIENT_ID_NOT_NULL);
        Client clientToDelete = findClient(id);
        log.debug("Starting deletion process for client: {}", clientToDelete.getEmail());
        handleDeleteClient(clientToDelete);
    }

    @Transactional(readOnly = true)
    public Client findByEmail(String email) {
        validateNotNull(email, InputValidator.CLIENT_EMAIL_NOT_NULL);
        return clientRepository.findByCredentialsEmail(email)
                .orElseThrow(() -> {
                    log.error("Client with email: {} not found.", email);
                    return new NoSuchElementException("Client with email: " + email + " does not exist.");
                });
    }

    private void validateNotNull(Object o, String message) {
        inputValidator.throwExceptionIfObjectIsNull(o, message);
    }

    private Client findClient(Long id) {
        log.debug("Fetching client with id: {}", id);
        return clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Client with id: {} not found", id);
                    return new NoSuchElementException("Client with id: " + id + " does not exist.");
                });
    }

    private Client validateAndUpdateClient(Client clientFromDb, Client newData) {
        String clientEmail = clientFromDb.getEmail();
        clientValidator.validateUserCanBeUpdated(clientEmail, newData);
        log.info("Updating client: {}", clientEmail);
        updateClient(clientFromDb, newData);
        Client save = clientRepository.save(clientFromDb);
        log.info("Client: {} has been updated.New email: {}", clientEmail, newData.getEmail());
        return save;
    }

    private void updateClient(Client clientFromDb, Client newData) {
        clientFromDb.setName(newData.getName());
        clientFromDb.setCredentials(newData.getCredentials());
        clientFromDb.setAddress(newData.getAddress());
    }

    private void handleDeleteClient(Client clientToDelete) {
        String email = clientToDelete.getEmail();
        log.info("Deleting client: {}", email);
        removeRoles(clientToDelete);
        deleteClientFromAllTables(clientToDelete);
        log.info("Client: {} has been deleted", email);
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

