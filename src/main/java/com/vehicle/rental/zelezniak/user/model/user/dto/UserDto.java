package com.vehicle.rental.zelezniak.user.model.user.dto;

import com.vehicle.rental.zelezniak.user.model.user.Address;
import com.vehicle.rental.zelezniak.user.model.user.Role;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.PhoneNumber;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserName;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UserDto {

    private Long id;

    private String email;

    private UserName name;

    @AttributeOverride(name = "number", column = @Column(name = "phone_number"))
    private PhoneNumber phoneNumber;

    private LocalDateTime createdAt;

    private Address address;

    private Set<Role> roles = new HashSet<>();
}
