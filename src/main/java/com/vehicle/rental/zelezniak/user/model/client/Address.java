package com.vehicle.rental.zelezniak.user.model.client;

import com.vehicle.rental.zelezniak.common_value_objects.location.City;
import com.vehicle.rental.zelezniak.common_value_objects.location.Country;
import com.vehicle.rental.zelezniak.common_value_objects.location.Street;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

@Entity
@Builder
@Table(name = "addresses")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @Embedded
    @Valid
    @AttributeOverride(
            name = "streetName",
            column = @Column(name = "street"))
    private Street street;

    @Column(name = "house_number")
    private String houseNumber;

    @Column(name = "flat_number")
    private String flatNumber;

    @Embedded
    @Valid
    @AttributeOverride(
            name = "cityName",
            column = @Column(name = "city"))
    private City city;

    @Column(name = "postal_code")
    private String postalCode;

    @Embedded
    @Valid
    @AttributeOverride(
            name = "countryName",
            column = @Column(name = "country"))
    private Country country;
}
