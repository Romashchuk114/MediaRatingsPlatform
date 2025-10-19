# Media Ratings Platform (MRP) 

REST-API Backend für Media Ratings Platform mit User-Management und CRUD-Operationen für Movies/Series/Games.

**GitHub Repository:** https://github.com/Romashchuk114/MediaRatingsPlatform

## Über das Projekt

Die Media Ratings Platform ermöglicht es Usern, Medien-Inhalte (Filme, Serien, Games) zu verwalten. Die Zwischenabgabe umfasst User-Management, basic Media-CRUD und Token-basierte Authentifizierung.

## Architektur

**4-Layer Architecture:**
- **Presentation** - HTTP-Server, Controller, DTOs
- **Service** - Business-Logik
- **Business** - Domain-Modelle (User, MediaEntry, Rating)
- **Data** - Repositories (In-Memory)

## ✅ Implementierte Features (Zwischenabgabe)

### User Management
- ✅ User Registration mit Username-Eindeutigkeit
- ✅ User Login mit Token-Generierung
- ✅ Token-Format: `{username}-mrpToken`

### Media Management (Complete CRUD)
- ✅ **Create** - Erstelle Media-Einträge (Auth required)
- ✅ **Read** - Liste aller Media / Single Media (Public)
- ✅ **Update** - Bearbeite eigene Media (Auth + Ownership)
- ✅ **Delete** - Lösche eigene Media (Auth + Ownership)

### Authorization
- ✅ Token-basierte Authentifizierung
- ✅ Ownership-Logik (nur Creator kann editieren/löschen)
- ✅ Public Read-Access

## API Endpoints

### User
```http
POST /api/users/register
POST /api/users/login
```

### Media
```http
POST   /api/media           # Create (Auth required)
GET    /api/media           # Get all (Public)
GET    /api/media/{id}      # Get single (Public)
PUT    /api/media/{id}      # Update (Auth + Owner only)
DELETE /api/media/{id}      # Delete (Auth + Owner only)
```

