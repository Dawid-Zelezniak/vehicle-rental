package com.vehicle.rental.zelezniak.vehicle.model.vehicles;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.VehicleInformation;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Car.class, name = "car"),
        @JsonSubTypes.Type(value = Motorcycle.class, name = "motorcycle")})
@Entity
@Table(name = "vehicles")
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
public abstract class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @Valid
    private VehicleInformation vehicleInformation;

    @NotNull(message = "Price per day can not be null.")
    @Valid
    @AttributeOverride(
            name = "value",
            column = @Column(name = "price_per_day"))
    private Money pricePerDay;

    @NotNull(message = "Deposit can not be null.")
    @Valid
    @AttributeOverride(
            name = "value",
            column = @Column(name = "deposit"))
    private Money deposit;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    protected Vehicle() {
    }

    public RegistrationNumber getRegistrationNumber() {
        return vehicleInformation.getRegistrationNumber();
    }

    public String getRegistrationValue() {
        return getRegistrationNumber().getRegistration();
    }

    public boolean canBeDeleted() {
        return VehicleStatus.UNAVAILABLE == this.status;
    }

    public String getBrand(){
        return vehicleInformation.getBrand();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Vehicle vehicle = (Vehicle) object;
        return Objects.equals(id, vehicle.id)
                && Objects.equals(vehicleInformation, vehicle.vehicleInformation)
                && Objects.equals(pricePerDay, vehicle.pricePerDay)
                && Objects.equals(deposit, vehicle.deposit)
                && status == vehicle.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id, vehicleInformation,
                pricePerDay, deposit,
                status);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", vehicleInformation=" + vehicleInformation +
                ", pricePerDay=" + pricePerDay +
                ", deposit=" + deposit +
                ", status=" + status +
                '}';
    }

    /**
     * A vehicle marked as AVAILABLE is operational and listed in the rental offer,
     * even if it is currently rented. This status indicates that the vehicle is
     * generally ready for use when not in active rental or reservation.
     * <p>
     * A vehicle marked as UNAVAILABLE is not operational or requires maintenance,
     * and thus is excluded from the rental offer until it is repaired and marked as AVAILABLE
     * Vehicle must be UNAVAILABLE before removal.
     */
    @RequiredArgsConstructor
    @Getter
    public enum VehicleStatus {
        AVAILABLE("available"),
        UNAVAILABLE("unavailable");

        private final String value;

        public static VehicleStatus getStatusFromString(String s){
            for (VehicleStatus status : VehicleStatus.values()) {
                if(status.value.equalsIgnoreCase(s)) return status;
            }
            throw new IllegalArgumentException("Unknown status value: " + s);
        }

    }
}
