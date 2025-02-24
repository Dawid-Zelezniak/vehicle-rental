package com.vehicle.rental.zelezniak.user.repository;

import com.vehicle.rental.zelezniak.user.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByCredentialsEmail(String email);

    Optional<User> findByCredentialsEmail(String email);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE reservations r SET r.user_id=NULL WHERE r.user_id =:id")
    void removeUserFromReservations(Long id);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE rents r SET r.user_id=NULL WHERE r.user_id =:id")
    void removeUserFromRents(Long id);
}
