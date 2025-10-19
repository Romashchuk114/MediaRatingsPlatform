# Media Ratings Platform (MRP) - Protokoll

**GitHub:** https://github.com/Romashchuk114/MediaRatingsPlatform  

## 1. Projektübersicht

REST-API Backend für Media Ratings Platform mit User-Management und CRUD-Operationen für Movies/Series/Games.

**Tech Stack:**
- Java 24
- HTTP Server: `com.sun.net.httpserver.HttpServer`
- JSON: Jackson
- Persistence: In-Memory

---

## 2. Architektur

### 4-Layer Architecture

- **Presentation** - HTTP-Server, Controller, DTOs
- **Service** - Business-Logik
- **Business** - Domain-Modelle (User, MediaEntry, Rating)
- **Data** - Repositories (In-Memory)

### Design Patterns

**Repository Pattern**
```java
public class UserRepository {
    public Optional<User> findById(UUID id) { ... }
    public User save(User user) { ... }
}
```

**Dependency Injection**
```java
public class MediaService {
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;
    
    public MediaService(MediaRepository mediaRepository, UserRepository userRepository) {
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
    }
}
```

**DTO Pattern**
```java
public class MediaEntryDTO {
    public MediaEntryDTO(MediaEntry mediaEntry) {
        this.id = mediaEntry.getId();
        this.title = mediaEntry.getTitle();
    }
}
```

---

## 3. Implementierte Features

### User Management
- **POST /api/users/register** - Registrierung mit Username-Eindeutigkeit
- **POST /api/users/login** - Login mit Token-Generierung (`username-mrpToken`)

### Media CRUD
- **POST /api/media** - Create (Auth required, Creator wird gespeichert)
- **GET /api/media** - Read all (Public)
- **GET /api/media/{id}** - Read single (Public)
- **PUT /api/media/{id}** - Update (Auth required, nur Creator)
- **DELETE /api/media/{id}** - Delete (Auth required, nur Creator)

### Token Authentication
```java
Authorization: Bearer username-mrpToken
```
- Token wird bei Login generiert
- Bei POST/PUT/DELETE validiert
- Owner-Check für Modifikationen


## 4. Lessons Learned

**Was gut war:**
- Layered Architecture - Klare Trennung
- Repository Pattern - Einfache Datenmigration
- DTO Pattern - Saubere API

**Was verbesserbar ist:**
- Custom Exceptions (für finale Abgabe)
- JWT statt simple Tokens
- Repository-Interfaces


## 5. Nächste Schritte (Finale Abgabe)

- [ ] PostgreSQL Integration
- [ ] Custom Exception
- [ ] Rating System (1-5 Stars + Comments)
- [ ] Favorites Management
- [ ] Search & Filter
- [ ] Recommendation System
- [ ] Leaderboard
- [ ] 20+ meaningful Unit Tests
- [ ] Docker Setup
