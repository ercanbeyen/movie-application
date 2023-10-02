# Movie Application
---

## Spring Boot Application
---

### Summary
It is a movie based entartainment application which uses session based authentication.<br/>
There are 5 entities and 1 document in this project

Entities:
- Movie
- Director
- Actor
- Audience
- Role

 Documents:
 - Cinema

There are 2 roles among audiences
- Admin
- User

### Requirements
- Audiences must login to use the application.
- Each audience must have a unique username.
- Each movie must have title, language, genre, release year, rating.
- Each director and actor must have name, surname, nationality and birth year.
- Directors and actors may appear in multiple movies.
- Each cinema must have a name, country, city, address, contact number, hall amount and statuses (reservation, 3-D animation, parking place, air conditioning and cafe & food)

### Additionals
- If scheduling is enabled, then cron jobs work.
- Statistical values can be observed for each entity and document.

### Tech Stack
---
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- JUnit 5
- PostgreSQL
- Redis
- Elasticsearch

### Prerequisties
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
