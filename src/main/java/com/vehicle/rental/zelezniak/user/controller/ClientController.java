package com.vehicle.rental.zelezniak.user.controller;

import com.vehicle.rental.zelezniak.security.validation.AccessValidator;
import com.vehicle.rental.zelezniak.security.validation.UserAccess;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.dto.ClientDto;
import com.vehicle.rental.zelezniak.user.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final AccessValidator validator;

    @GetMapping
    public Page<ClientDto> findAll(Pageable pageable) {
        return clientService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ClientDto findById(@PathVariable Long id,Principal principal) {
        validator.validateUserAccess(new UserAccess(principal,id,
                "You are not authorized to search for other users."));
        return clientService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Client update(@PathVariable Long id, @RequestBody @Valid Client newData, Principal principal) {
        validator.validateUserAccess(new UserAccess(principal,id,
                "You are not authorized to update another client data."));
        return clientService.update(id, newData);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        clientService.delete(id);
    }

    @GetMapping("/email/{email}")
    public Client findByEmail(@PathVariable String email) {
        return clientService.findByEmail(email);
    }
}
