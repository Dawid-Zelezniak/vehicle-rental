package com.vehicle.rental.zelezniak.user_domain.service;

import com.vehicle.rental.zelezniak.user_domain.model.client.Client;
import com.vehicle.rental.zelezniak.user_domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientValidator {

    private final ClientRepository userRepository;

    public void ifUserExistsThrowException(String email) {
        if (userByEmailExists(email))
            throw new IllegalArgumentException(createMessage(email));
    }

    public void checkIfUserCanBeUpdated(String userFromDbEmail, Client newData) {
        String newEmail = newData.getEmail();
        if (emailsAreNotSame(userFromDbEmail, newEmail) && userByEmailExists(newEmail)) {
            throw new IllegalArgumentException(createMessage(newEmail));
        }
    }

    private String createMessage(String email) {
        return "User with email : " + email + " already exist";
    }

    private boolean emailsAreNotSame(String userFromDbEmail, String newEmail) {
        return !userFromDbEmail.equals(newEmail);
    }

    private boolean userByEmailExists(String newEmail) {
        return userRepository.existsByCredentialsEmail(newEmail);
    }
}
