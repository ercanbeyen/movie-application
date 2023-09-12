# Movie Application
---

## Spring Boot Application
---

### Summary
There are 3 entities and 1 document in this project

Entities:
- Movie
- Director
- Actor

 Documents:
 - Cinema

### Requirements
- Each movie must have title, language, genre, release year, rating.
- Each director and actor must have name, surname, nationality and birth year.
- Directors and actors may appear in multiple movies.
- Each cinema must have a name, country, city, address, contact number, hall amount and statuses (reservation, 3-D animation, parking place, air conditioning and cafe & food)

### Additionals
- If scheduling is enabled, then cron jobs work.
- Statistical values can be observed for each entity and document.

### Tech Stack
---
- Java 18
- Spring Boot
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
