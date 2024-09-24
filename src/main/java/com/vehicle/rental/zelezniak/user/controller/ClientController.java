package com.vehicle.rental.zelezniak.user.controller;

import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public Page<Client> findAll(Pageable pageable) {
        return clientService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Client findById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @PutMapping("/update/{id}")
    public Client update(@PathVariable Long id,  @RequestBody @Validated Client newData) {
       return clientService.update(id, newData);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        clientService.delete(id);
    }

    @GetMapping("/email/{email}")
    public Client findByEmail(@PathVariable String email){
        return clientService.findByEmail(email);
    }
}
