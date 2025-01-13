# Vehicle Sharing Application

_Aplikacja w trakcie budowy._

Aplikacja jest systemem, który umożliwia użytkownikom wyszukiwanie, rezerwowanie i zarządzanie wynajmem pojazdów. 

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

