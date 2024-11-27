package com.vehicle.rental.zelezniak.rent.service;

import com.vehicle.rental.zelezniak.rent.model.Rent;
import com.vehicle.rental.zelezniak.rent.repository.RentRepository;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.*;

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
        validateNotNull(id, RENT_ID_NOT_NULL);
        return findRent(id);
    }

    @Transactional
    public void add(Rent rent) {
        validateNotNull(rent, RENT_NOT_NULL);
        handleAddRent(rent);
    }

    @Transactional(readOnly = true)
    public Page<Rent> findAllByClientId(Long id, Pageable pageable) {
        validateNotNull(id, CLIENT_ID_NOT_NULL);
        return rentRepository.findAllByClientId(id, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> findVehiclesByRentId(Long id, Pageable pageable) {
        validateNotNull(id, RENT_ID_NOT_NULL);
        return rentRepository.findVehiclesByRentId(id, pageable);
    }

    private void validateNotNull(Object o, String message) {
        inputValidator.throwExceptionIfObjectIsNull(o, message);
    }

    private Rent findRent(Long id) {
        return rentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rent with id: " + id + " does not exists."));
    }

    private void handleAddRent(Rent rent) {
        rentRepository.save(rent);
    }
}
