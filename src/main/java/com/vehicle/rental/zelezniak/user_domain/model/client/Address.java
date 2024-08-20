package com.vehicle.rental.zelezniak.user_domain.model.client;

import com.vehicle.rental.zelezniak.common_value_objects.address.City;
import com.vehicle.rental.zelezniak.common_value_objects.address.Country;
import com.vehicle.rental.zelezniak.common_value_objects.address.Street;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "addresses")
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @Embedded
    @AttributeOverride(
            name = "streetName",
            column = @Column(name = "street"))
    private Street street;

    @Column(name = "house_number")
    private String houseNumber;

    @Column(name = "flat_number")
    private String flatNumber;

    @Embedded
    @AttributeOverride(
            name = "cityName",
            column = @Column(name = "city"))
    private City city;

    @Column(name = "postal_code")
    private String postalCode;

    @Embedded
    @AttributeOverride(
            name = "countryName",
            column = @Column(name = "country"))
    private Country country;
}
