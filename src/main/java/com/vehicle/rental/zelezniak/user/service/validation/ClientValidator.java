package com.vehicle.rental.zelezniak.user.service.validation;

import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientValidator {

    private final ClientRepository userRepository;

    public void validateUserDoesNotExists(String email) {
        if (userByEmailExists(email)) {
            log.warn("Client with email: {} exist.", email);
            throw new IllegalArgumentException(createMessage(email));
        }
    }

    public void validateUserCanBeUpdated(String userFromDbEmail, Client newData) {
        String newEmail = newData.getEmail();
        if (emailsAreNotSame(userFromDbEmail, newEmail) && userByEmailExists(newEmail)) {
            log.warn("Client with email: {} can not change email to: {}", userFromDbEmail, newEmail);
            throw new IllegalArgumentException(createMessage(newEmail));
        }
        log.info("Client with email: {} can be updated with new email: {}", userFromDbEmail, newEmail);
    }

    private String createMessage(String email) {
        return "Client with email : " + email + " already exist";
    }

    private boolean emailsAreNotSame(String userFromDbEmail, String newEmail) {
        return !userFromDbEmail.equals(newEmail);
    }

    private boolean userByEmailExists(String newEmail) {
        return userRepository.existsByCredentialsEmail(newEmail);
    }
}
