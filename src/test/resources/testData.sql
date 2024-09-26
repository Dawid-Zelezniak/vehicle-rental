
-- Add role
insert into roles (role_id, role_name) values
(1, 'USER'),
(2, 'ADMIN');

-- Add addresses
insert into addresses (address_id, city, country, flat_number, house_number, postal_code, street) values
(5, 'Warsaw', 'Poland', '150', '5', '00-001', 'teststreet'),
(6, 'Warsaw', 'Poland', '150', '5', '00-001', 'teststreet'),
(7, 'Lublin', 'Poland', '10', '18', '21-090', 'teststreet');

-- Add clients
insert into clients (client_id, first_name, last_name, email, password, address_id, phone_number) values
(5, 'UserFive', 'Five', 'userfive@gmail.com', 'somepass', 5, '+48111222333'),
(6, 'UserSix', 'Six', 'usersix@gmail.com', 'somepass', 6, '+48222333444'),
(7, 'UserSeven', 'Seven', 'userseven@gmail.com', 'somepass', 7, '+48333444555');

-- Add roles for clients
insert into clients_roles (client_id, role_id) values
(5, 1),
(6, 1),
(7, 1);

-- Add vehicles
insert into vehicles (id, price_per_day, deposit, seats_number, status, brand, description, cylinders, displacement, engine_type, fuel_type, horsepower, gear_type, model, production_year, registration_number) values
(5, 50.00, 1000.00, 5, 'AVAILABLE', 'Seat', 'Seat Leon car', 4, 1900, '1.9TDI AVG', 'DIESEL', 110, 'MANUAL', 'Leon 1M', 2001, 'ABC55555'),
(6, 100.00, 1500.00, 2, 'AVAILABLE', 'Yamaha', 'Legendary Yamaha 125', 1, 125, 'Minarelli-Yamaha 5D1E', 'GASOLINE', 15, 'MANUAL', 'YZF-R125', 2015, 'ABC66666'),
(7, 70.00, 4000.00, 5, 'AVAILABLE', 'Toyota', 'Reliable Toyota Corolla', 4, 1800, '1.8L I4', 'GASOLINE', 132, 'AUTOMATIC', 'Corolla', 2018, 'TOY77777'),
(8, 80.00, 3500.00, 5, 'AVAILABLE', 'Honda', 'Efficient Honda Civic', 4, 2000, '2.0L I4', 'GASOLINE', 158, 'MANUAL', 'Civic', 2020, 'HON88888'),
(9, 90.00, 2500.00, 2, 'AVAILABLE', 'Suzuki', 'Fast Suzuki GSX-R750', 4, 750, '750cc 4-stroke', 'GASOLINE', 148, 'MANUAL', 'GSX-R750', 2020, 'SUZ99999');

-- Add cars
insert into cars (body_type, doors_number, drive_type, id) values
('HATCHBACK', 5, 'FRONT_WHEEL_DRIVE', 5),
('SEDAN', 4, 'FRONT_WHEEL_DRIVE', 7),
('COUPE', 2, 'FRONT_WHEEL_DRIVE', 8);

-- Add motorcycles
insert into motorcycles (id, motorcycle_type) values
(6, 'SPORT'),
(9, 'SPORT');

-- Add reservations
insert into reservations (id, total_cost, deposit_amount, rental_start, rental_end, pick_up_city, pick_up_street, pick_up_additional_info, drop_off_city, drop_off_street, drop_off_additional_info, reservation_status, client_id) values
(4, 1250.00, 1000.00, '2024-07-25 10:00:00', '2024-07-29 10:00:00', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'ACTIVE', 5),
(5, 1200.00, 1000.00, '2024-07-07 10:00:00', '2024-07-10 10:00:00', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'COMPLETED', 5),
(6, 7183.00, 5500.00, '2024-07-11 10:00:00', '2024-07-21 10:00:00', 'Warsaw', 'Marszałkowska', 'In front of the Palace of Culture and Science', 'Warsaw', 'Marszałkowska', 'In front of the Palace of Culture and Science', 'COMPLETED', 6),
(7, 6850.00, 6000.00, '2024-07-18 10:00:00', '2024-07-22 10:00:00', 'Warsaw', 'Marszałkowska', 'Near the Central Station', 'Warsaw', 'Marszałkowska', 'Near the Central Station', 'COMPLETED', 7),
(8, 3184.00, 2500.00, '2024-08-05 10:00:00', '2024-08-12 10:00:00', 'Warsaw', 'Marszałkowska', 'Near the Central Station', 'Warsaw', 'Marszałkowska', 'Near the Central Station', 'ACTIVE', 7);

-- Add reserved vehicles
insert into reserved_vehicles (reservation_id, vehicle_id) values
(4, 5),
(5, 5),
(6, 6),
(6, 7),
(7, 8),
(7, 9),
(8, 9);

-- Add rents
insert into rents (id, total_cost, deposit_amount, rental_start, rental_end, pick_up_city, pick_up_street, pick_up_additional_info, drop_off_city, drop_off_street, drop_off_additional_info, rent_status, client_id) values
(5, 1200.00, 1000.00, '2024-07-07 10:00:00', '2024-07-10 10:00:00', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'COMPLETED', 5),
(6, 7183.00, 5500.00, '2024-07-11 10:00:00', '2024-07-21 10:00:00', 'Warsaw', 'Marszałkowska', 'In front of the Palace of Culture and Science', 'Warsaw', 'Marszałkowska', 'In front of the Palace of Culture and Science', 'ACTIVE', 6),
(7, 6850.00, 6000.00, '2024-07-18 10:00:00', '2024-07-22 10:00:00', 'Warsaw', 'Marszałkowska', 'Near the Central Station', 'Warsaw', 'Marszałkowska', 'Near the Central Station', 'ACTIVE', 7);

-- Add rented vehicles
insert into rented_vehicles (rent_id, vehicle_id) values
(5, 5),
(6, 6),
(6, 7),
(7, 8),
(7, 9);
