package com.vehicle.rental.zelezniak.user_domain.model.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vehicle.rental.zelezniak.user_domain.model.client.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user_domain.model.client.user_value_objects.UserName;
import jakarta.persistence.*;
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
@Table(name = "clients")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Embedded
    private UserName name;

    @Embedded
    private UserCredentials credentials;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE, CascadeType.DETACH})
    @JoinTable(name = "clients_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
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

    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Client client = (Client) object;
        return Objects.equals(id, client.id)
                && Objects.equals(name, client.name)
                && Objects.equals(credentials, client.credentials)
                && Objects.equals(createdAt, client.createdAt)
                && Objects.equals(address, client.address);
    }

    public int hashCode() {
        return Objects.hash(id,
                name, credentials,
                createdAt, address);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name=" + name +
                ", credentials=" + credentials +
                ", createdAt=" + createdAt +
                ", address=" + address +
                ", roles=" + roles +
                '}';
    }
}
