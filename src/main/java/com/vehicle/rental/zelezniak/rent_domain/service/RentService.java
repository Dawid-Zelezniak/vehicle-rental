package com.vehicle.rental.zelezniak.rent_domain.service;

import com.vehicle.rental.zelezniak.rent_domain.model.Rent;
import com.vehicle.rental.zelezniak.rent_domain.repository.RentRepository;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.vehicle.rental.zelezniak.util.validation.InputValidator.*;

@Service
@RequiredArgsConstructor
public class RentService {

    private final RentRepository rentRepository;
    private final InputValidator inputValidator;

    @Transactional(readOnly = true)
    public Page<Rent> findAll(Pageable pageable) {
        return rentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Rent findById(Long id) {
        validateId(id);
        return findRent(id);
    }

    @Transactional
    public void add(Rent rent) {
        validateRent(rent);
        handleAddRent(rent);
    }

    @Transactional(readOnly = true)
    public Page<Rent> findAllByClientId(Long id, Pageable pageable) {
        validateClientId(id);
        return rentRepository.findAllByClientId(id, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> findVehiclesByRentId(Long id, Pageable pageable) {
        validateId(id);
        return rentRepository.findVehiclesByRentId(id, pageable);
    }

    private Rent findRent(Long id) {
        return rentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Rent with id: " + id + " does not exists."));
    }

    private void handleAddRent(Rent rent) {
        rentRepository.save(rent);
    }

    private void validateId(Long id) {
        inputValidator.throwExceptionIfObjectIsNull(id, RENT_ID_NOT_NULL);
    }

    private void validateRent(Rent rent) {
        inputValidator.throwExceptionIfObjectIsNull(rent, RENT_NOT_NULL);
    }

    private void validateClientId(Long clientId) {
        inputValidator.throwExceptionIfObjectIsNull(clientId, CLIENT_ID_NOT_NULL);
    }
}
