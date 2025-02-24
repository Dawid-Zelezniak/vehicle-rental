package com.vehicle.rental.zelezniak.user.service;

import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.model.user.dto.UserDto;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.service.validation.UserValidator;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import com.vehicle.rental.zelezniak.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final InputValidator inputValidator;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserDto> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserMapper::toDto);
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        validateNotNull(id, USER_ID_NOT_NULL);
        User user = findUser(id);
        return UserMapper.toDto(user);
    }

    // let the user search for himself by id
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        validateNotNull(id, USER_ID_NOT_NULL);
        return findUser(id);
    }

    @Transactional
    public User update(Long id, User newData) {
        log.debug("Validating user data before update: {}", newData.getEmail());
        validateNotNull(id, USER_ID_NOT_NULL);
        validateNotNull(newData, USER_NOT_NULL);
        User userFromDb = findUser(id);
        return validateAndUpdateUser(userFromDb, newData);
    }

    @Transactional
    public void delete(Long id) {
        validateNotNull(id, USER_ID_NOT_NULL);
        User userToDelete = findUser(id);
        log.debug("Starting deletion process for user: {}", userToDelete.getEmail());
        handleDeleteUser(userToDelete);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        validateNotNull(email, USER_EMAIL_NOT_NULL);
        return userRepository.findByCredentialsEmail(email)
                .orElseThrow(() -> {
                    log.error("User with email: {} not found.", email);
                    return new NoSuchElementException("User with email: " + email + " does not exist.");
                });
    }

    private void validateNotNull(Object o, String message) {
        inputValidator.throwExceptionIfObjectIsNull(o, message);
    }

    private User findUser(Long id) {
        log.debug("Fetching user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with id: {} not found", id);
                    return new NoSuchElementException("User with id: " + id + " does not exist.");
                });
    }

    private User validateAndUpdateUser(User userFromDb, User newData) {
        String userEmail = userFromDb.getEmail();
        userValidator.validateUserCanBeUpdated(userEmail, newData);
        log.info("Updating user: {}", userEmail);
        updateUser(userFromDb, newData);
        User save = userRepository.save(userFromDb);
        log.info("User: {} has been updated.New email: {}", userEmail, newData.getEmail());
        return save;
    }

    private void updateUser(User userFromDb, User newData) {
        userFromDb.setName(newData.getName());
        String password = newData.getPassword();
        String email = newData.getEmail();
        userFromDb.setCredentials(new UserCredentials(email, passwordEncoder.encode(password)));
        userFromDb.setAddress(newData.getAddress());
    }

    private void handleDeleteUser(User userToDelete) {
        String email = userToDelete.getEmail();
        log.info("Deleting user: {}", email);
        userToDelete.setRoles(null);
        userRepository.save(userToDelete);
        deleteUserFromAllTables(userToDelete);
        log.info("User: {} has been deleted", email);
    }

    private void deleteUserFromAllTables(User user) {
        userRepository.removeUserFromReservations(user.getId());
        userRepository.removeUserFromRents(user.getId());
        userRepository.delete(user);
    }
}

