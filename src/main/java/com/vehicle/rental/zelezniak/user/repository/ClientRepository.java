package com.vehicle.rental.zelezniak.user.repository;

import com.vehicle.rental.zelezniak.user.model.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByCredentialsEmail(String email);

    Optional<Client> findByCredentialsEmail(String email);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE reservations r SET r.client_id=NULL WHERE r.client_id =:id")
    void removeClientFromReservations(Long id);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE rents r SET r.client_id=NULL WHERE r.client_id =:id")
    void removeClientFromRents(Long id);
}
