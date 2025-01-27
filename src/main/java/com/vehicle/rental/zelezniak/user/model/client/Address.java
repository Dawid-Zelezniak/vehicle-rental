package com.vehicle.rental.zelezniak.user.model.client;

import com.vehicle.rental.zelezniak.common_value_objects.location.City;
import com.vehicle.rental.zelezniak.common_value_objects.location.Country;
import com.vehicle.rental.zelezniak.common_value_objects.location.Street;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Builder
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Address {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @Embedded
    @Valid
    @AttributeOverride(
            name = "countryName",
            column = @Column(name = "country"))
    private Country country;

    @Embedded
    @Valid
    @AttributeOverride(
            name = "cityName",
            column = @Column(name = "city"))
    private City city;

    @Embedded
    @Valid
    @AttributeOverride(
            name = "streetName",
            column = @Column(name = "street"))
    private Street street;

    @NotBlank(message = "House number cannot be blank.")
    @Column(name = "house_number")
    private String houseNumber;

    @NotBlank(message = "Flat number cannot be blank.")
    @Column(name = "flat_number")
    private String flatNumber;

    @NotBlank(message = "Postal code cannot be blank.")
    @Column(name = "postal_code")
    private String postalCode;
}
