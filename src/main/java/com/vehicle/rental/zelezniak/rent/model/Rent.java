package com.vehicle.rental.zelezniak.rent.model;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "rents")
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @Valid
    private RentInformation rentInformation;

    @Embedded
    @Valid
    @AttributeOverride(
            name = "value",
            column = @Column(name = "total_cost"))
    private Money totalCost;

    @Embedded
    @Valid
    @AttributeOverride(
            name = "value",
            column = @Column(name = "deposit_amount"))
    private Money depositAmount;

    @Enumerated(EnumType.STRING)
    private RentStatus rentStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Valid
    private User user;

    @ManyToMany
    @JoinTable(
            name = "rented_vehicles",
            joinColumns = @JoinColumn(name = "rent_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id"))
    @Valid
    private Set<Vehicle> vehicles;

    public enum RentStatus {
        ACTIVE,
        CANCELLED,
        COMPLETED,
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Rent rent = (Rent) object;
        return Objects.equals(id, rent.id)
                && Objects.equals(rentInformation, rent.rentInformation)
                && Objects.equals(totalCost, rent.totalCost)
                && rentStatus == rent.rentStatus
                && Objects.equals(user, rent.user);
    }

    public int hashCode() {
        return Objects.hash(
                id, rentInformation,
                totalCost, rentStatus,
                user);
    }

    public String toString() {
        return "Rent{" +
                "user=" + user +
                ", id=" + id +
                ", totalCost=" + totalCost +
                ", rentInformation=" + rentInformation +
                ", rentStatus=" + rentStatus +
                '}';
    }
}
