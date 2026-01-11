# Media Ratings Platform - Protokoll

**GitHub:** https://github.com/Romashchuk114/MediaRatingsPlatform

---

## 1. Architektur

### Layered Architecture

```
presentation/  - Controller, DTOs, HTTP-Server
services/      - Business-Logik
business/      - Entities (User, MediaEntry, Rating)
data/          - Repositories (JDBC)
```

### Klassendiagramm

```
Controller --> Service --> Repository --> Database
     |            |             
     v            v             
   DTOs      Entities     
```

### Design-Entscheidungen

| Entscheidung | Begruendung |
|--------------|-------------|
| com.sun.net.httpserver | Erlaubte HTTP-Library |
| Jackson | JSON Serialisierung |
| UUID v7 | Time-ordered IDs |
| BCrypt | Password Hashing |
| PreparedStatements | SQL Injection Prevention |

---

## 2. Lessons Learned

**Was gut funktioniert hat:**
- Layered Architecture ermöglicht klare Trennung der Verantwortlichkeiten
- Repository-Interfaces erleichtern das Testen mit Mocks
- Constructor Injection macht Abhängigkeiten explizit

**Was ich anders machen würde:**
- Früher mit PostgreSQL starten statt In-Memory
- Besseres Error-Handling implementieren

---

## 3. Unit Tests (32 Tests)

| Test-Klasse | Tests | Getestete Logik |
|-------------|-------|-----------------|
| UserServiceTest | 8     | Registration, Login, Validierung |
| MediaServiceTest | 8     | CRUD, Ownership-Pruefung |
| RatingServiceTest | 9     | Rating-Erstellung, Likes, Average |
| LeaderboardServiceTest | 2     | Ranking nach Rating-Anzahl |
| RecommendationServiceTest | 5     | Genre- und Content-Empfehlungen |

**Warum diese Tests:**
- Core Business Logic (Service-Layer)
- Validierungsregeln
- Ownership-Logik
- Edge Cases

---

## 4. SOLID Principles

### Single Responsibility Principle (SRP)

Jede Klasse hat genau eine Verantwortung:

```java
// UserService - nur User-Logik
public class UserService {
    public User registerUser(String username, String password) { ... }
    public Optional<User> loginUser(String username, String password) { ... }
}

// RatingService - nur Rating-Logik
public class RatingService {
    public Rating createRating(UUID mediaId, UUID userId, int stars, String comment) { ... }
    public void likeRating(UUID ratingId, UUID userId) { ... }
}
```

### Dependency Inversion Principle (DIP)

High-level Module hängen von Abstraktionen ab, nicht von konkreten Implementierungen:

```java
// Interface (Abstraktion)
public interface UserRepository extends Repository<User> {
    Optional<User> findByUsername(String username);
}

// Service hängt von Interface ab
public class UserService {
    private final UserRepository userRepository;  // Interface, nicht JdbcUserRepository

    public UserService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
    }
}

// Konkrete Implementierung
public class JdbcUserRepository implements UserRepository { ... }
```

---

## 5. Probleme und Lösungen

| Problem | Loesung |
|---------|---------|
| JSON LocalDateTime | jackson-datatype-jsr310 Modul |
| Token Persistenz | Tokens in DB statt In-Memory |
| Average Score Update | Neuberechnung bei Rating-CRUD |

---

## 6. Zeitaufwand

| Aufgabe | Stunden |
|---------|---------|
| HTTP Server Setup | 7       |
| User Management | 5       |
| Media CRUD | 10      |
| PostgreSQL Integration | 5       |
| Rating System | 6       |
| Favorites | 2       |
| Search & Filter | 5       |
| Recommendations | 5       |
| Leaderboard | 3       |
| Unit Tests | 6       |
| Bugfixes & Refactoring | 6       |
| **Gesamt** | **60**  |

---