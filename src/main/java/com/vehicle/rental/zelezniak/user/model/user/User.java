package com.vehicle.rental.zelezniak.user.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.PhoneNumber;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserName;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Embedded
    @Valid
    private UserName name;

    @Embedded
    @Valid
    private UserCredentials credentials;

    @Embedded
    @AttributeOverride(name = "number", column = @Column(name = "phone_number"))
    @Valid
    private PhoneNumber phoneNumber;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    @Valid
    private Address address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Valid
    private Set<Role> roles = new HashSet<>();

    public String getEmail() {
        return credentials.getEmail();
    }

    public String getPassword() {
        return credentials.getPassword();
    }

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public String getUsername() {
        return credentials.getEmail();
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(id, user.id)
                && Objects.equals(name, user.name)
                && Objects.equals(credentials, user.credentials)
                && Objects.equals(phoneNumber, user.phoneNumber)
                && Objects.equals(createdAt, user.createdAt)
                && Objects.equals(address, user.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, credentials,
                phoneNumber, createdAt, address);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name=" + name +
                ", credentials=" + credentials +
                ", createdAt=" + createdAt +
                ", address=" + address +
                ", roles=" + roles +
                '}';
    }
}
