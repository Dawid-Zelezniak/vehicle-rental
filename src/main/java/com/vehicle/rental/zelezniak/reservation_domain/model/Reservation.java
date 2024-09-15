package com.vehicle.rental.zelezniak.reservation_domain.model;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.user_domain.model.client.Client;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "reservations")
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private RentInformation rentInformation;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "total_cost"))
    private Money totalCost;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "deposit_amount"))
    private Money depositAmount;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @ManyToOne(cascade = {
            CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.REFRESH})
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToMany(cascade = {
            CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "reserved_vehicles",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id"))
    private Set<Vehicle> vehicles;

    public enum ReservationStatus {
        NEW,
        ACTIVE,
        CANCELLED,
        COMPLETED
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Reservation that = (Reservation) object;
        return Objects.equals(id, that.id)
                && Objects.equals(rentInformation, that.rentInformation)
                && Objects.equals(totalCost, that.totalCost)
                && reservationStatus == that.reservationStatus
                && Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                rentInformation, totalCost,
                reservationStatus, client);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", rentInformation=" + rentInformation +
                ", estimatedCost=" + totalCost +
                ", reservationStatus=" + reservationStatus +
                ", client=" + client +
                '}';
    }
}
