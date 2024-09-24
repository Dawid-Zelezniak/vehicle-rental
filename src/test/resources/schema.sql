DROP TABLE IF EXISTS reserved_vehicles;
DROP TABLE IF EXISTS clients_roles;
DROP TABLE IF EXISTS motorcycles;
DROP TABLE IF EXISTS rented_vehicles;
DROP TABLE IF EXISTS rents;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS cars;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL
);

CREATE TABLE addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    city VARCHAR(255),
    country VARCHAR(255),
    flat_number VARCHAR(255),
    house_number VARCHAR(255),
    postal_code VARCHAR(255),
    street VARCHAR(255)
);

CREATE TABLE clients (
    client_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    phone_number VARCHAR(255),
    address_id INT,
    created_at TIMESTAMP,
    FOREIGN KEY (address_id) REFERENCES addresses(address_id)
);

CREATE TABLE vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    price_per_day DECIMAL(10,2) NOT NULL,
    deposit DECIMAL(10,2) NOT NULL,
    seats_number INT NOT NULL,
    status VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    cylinders INT,
    displacement INT,
    engine_type VARCHAR(255),
    fuel_type VARCHAR(255),
    horsepower INT,
    gear_type VARCHAR(255),
    model VARCHAR(255) NOT NULL,
    production_year INT NOT NULL,
    registration_number VARCHAR(255) NOT NULL
);

CREATE TABLE cars (
    id INT AUTO_INCREMENT PRIMARY KEY,
    body_type VARCHAR(255),
    doors_number INT,
    drive_type VARCHAR(255),
    FOREIGN KEY (id) REFERENCES vehicles(id)
);

CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    total_cost DECIMAL(10,2),
    deposit_amount DECIMAL(10,2),
    rental_start TIMESTAMP,
    rental_end TIMESTAMP,
    pick_up_city VARCHAR(255),
    pick_up_street VARCHAR(255),
    pick_up_additional_info VARCHAR(255),
    drop_off_city VARCHAR(255),
    drop_off_street VARCHAR(255),
    drop_off_additional_info VARCHAR(255),
    reservation_status VARCHAR(255),
    client_id INT,
    FOREIGN KEY (client_id) REFERENCES clients(client_id)
);

CREATE TABLE reserved_vehicles (
    reservation_id INT,
    vehicle_id INT,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    PRIMARY KEY (reservation_id, vehicle_id)
);

CREATE TABLE clients_roles (
    client_id INT,
    role_id INT,
    FOREIGN KEY (client_id) REFERENCES clients(client_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    PRIMARY KEY (client_id, role_id)
);

CREATE TABLE motorcycles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    motorcycle_type VARCHAR(255),
    FOREIGN KEY (id) REFERENCES vehicles(id)
);

CREATE TABLE rents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    total_cost DECIMAL(10,2),
    deposit_amount DECIMAL(10,2),
    rental_start TIMESTAMP,
    rental_end TIMESTAMP,
    pick_up_city VARCHAR(255),
    pick_up_street VARCHAR(255),
    pick_up_additional_info VARCHAR(255),
    drop_off_city VARCHAR(255),
    drop_off_street VARCHAR(255),
    drop_off_additional_info VARCHAR(255),
    rent_status VARCHAR(255),
    client_id INT,
    FOREIGN KEY (client_id) REFERENCES clients(client_id)
);

CREATE TABLE rented_vehicles (
    rent_id INT,
    vehicle_id INT,
    FOREIGN KEY (rent_id) REFERENCES rents(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    PRIMARY KEY (rent_id, vehicle_id)
);
