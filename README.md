# Vehicle Sharing Application

_Aplikacja w trakcie budowy_

Aplikacja umożliwia użytkownikom wyszukiwanie, rezerwowanie i zarządzanie wynajmem pojazdów. 

## Aktualnie działające funkcje
- Rejestracja i logowanie użytkownika z wykorzystaniem JWT.
- Wyszukiwanie pojazdów dostępnych w określonym przedziale czasowym.
- Operacje CRUD oraz walidacja danych dla pojazdów, rezerwacji, wynajmów i użytkowników.
- Dynamiczne wyszukiwanie wszystkich dostępnych w wypożyczalni pojazdów według różnych kryteriów jednocześnie, takich jak marka, model czy rok produkcji.
- Możliwość dynamicznego wyszukiwania pojazdów tak jak powyżej dla pojazdów dostępnch w danym przedziale czasu
- Obliczenie kosztu rezerwacji na podstawie czasu trwania wypożyczenia i pojazdów , które użytkownik chce wypożyczyć.
- Role użytkowników: Ograniczony dostęp do części aplikacji w zależności od uprawnień.
- Panel użytkownika: Możliwość podglądu informacji o dokonanych rezerwacjach.
- Profil administratora: Automatycznie tworzony podczas uruchamiania aplikacji, z dostępem do kluczowych funkcji systemu.
- Testy integracyjne serwisów, kontrolerów oraz innych klas w celu sprawdzenia poprawności działania aplikacji, a także zachowania spójności przechowywanych danych 

  ## Następne aktualizacje obejmują poniższe funkcjonalności:
- Użytkownik dokonuje zapłaty, a jego rezerwacja zmienia status na ACTIVE.
- W momencie rozpoczęcia wynajmu, rezerwacja użytkownika przekształca się w wypożyczenie.
- Na 24 godziny przed wypożyczeniem, klient otrzymuje powiadomienie (e-mail oraz SMS) o zbliżającym się wypożyczeniu.
- Klient może zrezygnować z wypożyczenia, a jego opłata zostanie zwrócona na konto.

# Technologie:
- Java 17
- Spring Boot 3.30
- Junit 5
- MySQL

#Vehicle Rental Application
_Application in development_

The application is a system that enables users to search, book, and manage vehicle rentals.

#Currently Implemented Features:
- User registration and login using JWT.
- Search for vehicles available within a specified time frame.
- CRUD operations and data validation for vehicles, reservations, rentals, and users.
- Dynamic search of all vehicles available in the rental service using multiple criteria simultaneously, such as brand, model, or year of production.
- Time-specific vehicle search: Search for vehicles available in a given time frame using the same dynamic criteria as above.
- Reservation cost calculation based on rental duration and selected vehicles.
- User roles: Restricted access to parts of the application depending on user permissions.
- User dashboard: Allows viewing information about made reservations.
- Administrator profile: Automatically created during application startup with access to key system functions.
- Integration tests: Tests for services, controllers, and other classes to verify the application's functionality and ensure data consistency.

 #Upcoming Updates:
- Payment processing: Users make payments, and their reservations change to the ACTIVE status.
- Real-time rentals: Reservations automatically convert to rentals at the start time.
- Notifications: Clients receive reminders (email and SMS) 24 hours before their rental starts.
- Cancellations and refunds: Clients can cancel their reservations and receive a refund.

#Technologies:
- Java 17
- Spring Boot 3.30
- JUnit 5
- MySQL
