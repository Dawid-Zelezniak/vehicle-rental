# Vehicle Sharing Application

_Aplikacja w trakcie budowy._

Aplikacja jest systemem, który umożliwia użytkownikom wyszukiwanie, rezerwowanie i zarządzanie wynajmem pojazdów. 

## Kluczowe funkcje (wersja końcowa):
- Użytkownik wybiera przedział czasowy, w którym chce dokonać rezerwacji.
- Zwracane są pojazdy dostępne w danym terminie.
- Klient może wypożyczyć więcej niż jeden pojazd na raz.
- Przed opłaceniem rezerwacji obliczany jest jej koszt na podstawie czasu trwania wypożyczenia i pojazdów , które użytkownik chce wypożyczyć.
- Użytkownik dokonuje zapłaty, a jego rezerwacja zmienia status na ACTIVE.
- Na 24 godziny przed wypożyczeniem, klient otrzymuje powiadomienie (e-mail oraz SMS) o zbliżającym się wypożyczeniu.
- Klient może zrezygnować z wypożyczenia, a jego opłata zostanie zwrócona na konto.
- W momencie rozpoczęcia wynajmu, rezerwacja użytkownika przekształca się w wypożyczenie.

## Aktualnie działające funkcje
-Rejestracja i logowanie użytkownika z wykorzystaniem JWT.
-Wyszukiwanie pojazdów dostępnych w określonym przedziale czasowym.
-Operacje CRUD oraz walidacja danych dla pojazdów, rezerwacji, wynajmów i użytkowników.
-Dynamiczne wyszukiwanie pojazdów według różnych kryteriów, takich jak marka, model czy rok produkcji.
-Role użytkowników:
Ograniczony dostęp do części aplikacji w zależności od uprawnień.
-Panel użytkownika:
Możliwość podglądu informacji o dokonanych rezerwacjach.
-Profil administratora:
Automatycznie tworzony podczas uruchamiania aplikacji, z dostępem do kluczowych funkcji systemu.
-Testy integracyjne sprawdzające poprawność działania logiki aplikacji.

# Technologie:
- Java 17
- Spring Boot 3.30
- Junit 5
- MySQL

