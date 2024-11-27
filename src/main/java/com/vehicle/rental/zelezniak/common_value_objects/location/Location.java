package com.vehicle.rental.zelezniak.common_value_objects.location;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import lombok.*;

@Embeddable
@Builder(toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Location {

    @Embedded
    @AttributeOverride(
            name = "cityName",
            column = @Column(name = "city"))
    @Valid
    private final City city;

    @Embedded
    @AttributeOverride(
            name = "streetName",
            column = @Column(name = "street"))
    @Valid
    private final Street street;

    private final String additionalInformation;
}
