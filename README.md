# Media Ratings Platform (MRP) 

REST-API Backend fuer Media Ratings Platform mit User-Management, Ratings, Favorites und Recommendations fuer Movies/Series/Games.

**GitHub Repository:** https://github.com/Romashchuk114/MediaRatingsPlatform

## Setup

1. PostgreSQL starten: `docker-compose up -d`
2. Projekt bauen: `mvn clean install`
3. Server starten: `mvn exec:java`

Server läuft auf `http://localhost:8080`

## API Endpoints

### Auth
```
POST /api/users/register
POST /api/users/login
```

### User
```
GET /api/users/{userId}/profile
PUT /api/users/{userId}/profile
GET /api/users/{userId}/ratings
GET /api/users/{userId}/favorites
GET /api/users/{userId}/recommendations
```

### Media
```
POST   /api/media
GET    /api/media
GET    /api/media/{id}
PUT    /api/media/{id}
DELETE /api/media/{id}
POST   /api/media/{id}/rate
GET    /api/media/{id}/ratings
POST   /api/media/{id}/favorite
DELETE /api/media/{id}/favorite
```

### Ratings
```
PUT    /api/ratings/{id}
DELETE /api/ratings/{id}
POST   /api/ratings/{id}/like
POST   /api/ratings/{id}/confirm
```

### Leaderboard
```
GET /api/leaderboard
```

## API testen

Postman Collection: `FINAL MRP API Collection.json`