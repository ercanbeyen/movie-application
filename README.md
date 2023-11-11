# Movie Application
---

## Spring Boot Application
---

### Summary
It is a movie based entertainment application which uses session based authentication.<br/>
There are 5 entities and 1 document in this project

Entities:
- Movie
- Director
- Actor
- Audience
- Rating
- Role

Documents:
- Cinema

There are 2 roles among audiences
- Admin
- User

### Requirements
- Audiences must log in to use the application.
- Each audience must have an unique username.
- Each movie must have imdb id, title, language, genre, release year and rating.
- Each director and actor must have name, surname, nationality and birth year.
- Each role's name must be unique.
- Each cinema must have a name, country, city, address, contact number, hall amount and statuses (reservation, 3-D animation, parking place, air conditioning and cafe & food)
- Only admins may apply DML statement to movies, actors, directors, cinemas and roles.
- Only admins may update roles of users.

### Additionals
- Cron jobs work during runtime.
- Statistical values can be observed for each entity and document.
- Directors and actors may appear in multiple movies.
- Audiences may retrieve partial response from requests related with actors.
- Audiences may have multiple roles.
- Audiences may rate each movie between 1 and 5.
- Admins may apply DML statements to movies only in allowed time periods.

### Tech Stack
---
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Spring AOP
- JUnit 5
- PostgreSQL
- Redis
- Elasticsearch

### Prerequisites
---
- Maven

### Run & Build
---
Open one of the terminal in your project directory and run the below commands

```
$ mvn clean install
$ mvn spring-boot:run
```

### Api Documentation
---
You may use swagger-ui with the port of the application to access the project's api documentation.<br/>
`http://localhost:${PORT}/swagger-ui.html`
