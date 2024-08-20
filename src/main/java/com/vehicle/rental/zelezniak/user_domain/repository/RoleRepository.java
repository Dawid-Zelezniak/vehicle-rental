package com.vehicle.rental.zelezniak.user_domain.repository;

import com.vehicle.rental.zelezniak.user_domain.model.client.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Integer> {

    Role findByRoleName(String user);
}
