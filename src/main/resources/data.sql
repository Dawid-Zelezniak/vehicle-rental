-- Add roles
insert into roles (role_id, role_name) values
(1, 'USER'),
(2, 'ADMIN');

-- Add addresses
insert into addresses (address_id, city, country, flat_number, house_number, postal_code, street) values
(2, 'Warsaw', 'Poland', '150', '5', '00-001', 'teststreet'),
(3, 'Warsaw', 'Poland', '150', '5', '00-001', 'teststreet'),
(4, 'Lublin', 'Poland', '10', '18', '21-090', 'teststreet');

-- Add clients
-- encrypted password is "somepass"
insert into clients (client_id, first_name, last_name, email, password, address_id, phone_number, created_at) values
(2, 'UserTwo', 'Two', 'usertwo@gmail.com', '$2a$10$53viTAvUEN.0LdWJ9Hwbq.uyqFWiyhSVkMa//Blhi9Zk12SqePz5a', 2, '+48 111222333', '2024-01-02 13:00:00'),
(3, 'UserThree', 'Three', 'userthree@gmail.com', '$2a$10$53viTAvUEN.0LdWJ9Hwbq.uyqFWiyhSVkMa//Blhi9Zk12SqePz5a', 3, '+48 222333444', '2024-01-03 14:00:00'),
(4, 'UserFour', 'Four', 'userfour@gmail.com', '$2a$10$53viTAvUEN.0LdWJ9Hwbq.uyqFWiyhSVkMa//Blhi9Zk12SqePz5a', 4, '+48 333444555', '2024-01-04 15:00:00');


-- Add roles for clients
insert into clients_roles (client_id, role_id) values
(2, 1),
(3, 1),
(4, 1);

-- Add vehicles
insert into vehicles (id, price_per_day, deposit, seats_number, status, brand, description, cylinders, displacement, engine_type, fuel_type, horsepower, gear_type, model, production_year, registration_number) values
(1, 50.00, 1000.00, 5, 'AVAILABLE', 'Seat', 'Seat Leon car', 4, 1900, '1.9TDI AVG', 'DIESEL', 110, 'MANUAL', 'Leon 1M', 2001, 'ABC55555'),
(2, 100.00, 1500.00, 2, 'AVAILABLE', 'Yamaha', 'Legendary Yamaha 125', 1, 125, 'Minarelli-Yamaha 5D1E', 'GASOLINE', 15, 'MANUAL', 'YZF-R125', 2015, 'ABC66666'),
(3, 70.00, 4000.00, 5, 'AVAILABLE', 'Toyota', 'Reliable Toyota Corolla', 4, 1800, '1.8L I4', 'GASOLINE', 132, 'AUTOMATIC', 'Corolla', 2018, 'TOY77777'),
(4, 80.00, 3500.00, 5, 'AVAILABLE', 'Honda', 'Efficient Honda Civic', 4, 2000, '2.0L I4', 'GASOLINE', 158, 'MANUAL', 'Civic', 2020, 'HON88888'),
(5, 90.00, 2500.00, 2, 'AVAILABLE', 'Suzuki', 'Fast Suzuki GSX-R750', 4, 750, '750cc 4-stroke', 'GASOLINE', 148, 'MANUAL', 'GSX-R750', 2020, 'SUZ99999');

-- Add cars
insert into cars (body_type, doors_number, drive_type, id) values
('HATCHBACK', 5, 'FRONT_WHEEL_DRIVE', 1),
('SEDAN', 4, 'FRONT_WHEEL_DRIVE', 3),
('COUPE', 2, 'FRONT_WHEEL_DRIVE', 4);

-- Add motorcycles
insert into motorcycles (id, motorcycle_type) values
(2, 'SPORT'),
(5, 'SPORT');

-- Add reservations
insert into reservations (id, total_cost, deposit_amount, rental_start, rental_end, pick_up_city, pick_up_street, pick_up_additional_info, drop_off_city, drop_off_street, drop_off_additional_info, reservation_status, client_id) values
(1, 1250.00, 1000.00, '2024-07-25 10:00:00', '2024-07-29 10:00:00', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'ACTIVE', 2),
(2, 1200.00, 1000.00, '2024-07-07 10:00:00', '2024-07-10 10:00:00', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'COMPLETED', 2),
(3, 7183.00, 5500.00, '2024-07-11 10:00:00', '2024-07-21 10:00:00', 'Warsaw', 'Marszałkowska', 'In front of the Palace of Culture and Science', 'Warsaw', 'Marszałkowska', 'In front of the Palace of Culture and Science', 'COMPLETED', 3),
(4, 6850.00, 6000.00, '2024-07-18 10:00:00', '2024-07-22 10:00:00', 'Warsaw', 'Marszałkowska', 'Near the Central Station', 'Warsaw', 'Marszałkowska', 'Near the Central Station', 'COMPLETED', 4),
(5, 3184.00, 2500.00, '2024-08-05 10:00:00', '2024-08-12 10:00:00', 'Warsaw', 'Marszałkowska', 'Near the Central Station', 'Warsaw', 'Marszałkowska', 'Near the Central Station', 'ACTIVE', 4),
(6, 2770.00, 2500.00, '2025-02-05 10:00:00', '2025-02-07 10:00:00', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'NEW', 4);

-- Add reserved vehicles
insert into reserved_vehicles (reservation_id, vehicle_id) values
(1, 1),
(2, 1),
(3, 2),
(3, 3),
(4, 4),
(4, 5),
(5, 5),
(6, 5);

-- Add rents
insert into rents (id, total_cost, deposit_amount, rental_start, rental_end, pick_up_city, pick_up_street, pick_up_additional_info, drop_off_city, drop_off_street, drop_off_additional_info, rent_status, client_id) values
(1, 1200.00, 1000.00, '2024-07-07 10:00:00', '2024-07-10 10:00:00', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'Lublin', 'Turystyczna', 'Next to the Leclerc mall', 'COMPLETED', 2),
(2, 7183.00, 5500.00, '2024-07-11 10:00:00', '2024-07-21 10:00:00', 'Warsaw', 'Marszałkowska', 'In front of the Palace of Culture and Science', 'Warsaw', 'Marszałkowska', 'In front of the Palace of Culture and Science', 'ACTIVE', 3),
(3, 6850.00, 6000.00, '2024-07-18 10:00:00', '2024-07-22 10:00:00', 'Warsaw', 'Marszałkowska', 'Near the Central Station', 'Warsaw', 'Marszałkowska', 'Near the Central Station', 'ACTIVE', 4);

-- Add rented vehicles
insert into rented_vehicles (rent_id, vehicle_id) values
(1, 1),
(2, 2),
(2, 3),
(3, 4),
(3, 5);
