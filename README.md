# POC Spring boot forum API

## Presentation
Hi there,

This project is designed to be a POC of all the features and concepts I want to test and implement in a Spring Boot context. I try to follow all the best practices in a Craftmanship way.
Stack used locally :
- Java 17
- Spring boot 3.2.0 (Embedded Tomcat)
- JPA
- PostgreSQL

Here is a non exhaustive and destructured list of some of these :
- Integration of Postgres fulltext search
- Redis : Redis Streams to manage tasks in an asynchronous way (Mail sending for example. First, I implemented Redis Pub/Sub feature but Redis Streams is more appropriated for this usage)
- Liquibase : Follow database changes in a proper way
- Spring security : JWT authentication for authentication with refresh token generation
- Spring security : Permission evaluators feature for authorization
- Image optimization : Implement image optimization to resize image width in order to displaying (Objective : If I need to display an image in a 150px box, I don't want to load an image with a width of 600px)
- Thymeleaf : Email templates

I use the Spring boot embedded Tomcat server so no need to configure a dedicated server.

WIP :
- React frontend in a new dedicated repository
- Write unit tests with jUnit and Mockito

## Utils

1) Clone the project

2) Create a new Podman pod dedicated to services :
```
podman pod create --name forum-services -p 5432:5432 -p 9080:8080 -p 9081:8081 -p 9025:8025 -p 6379:6379
```

3) Startup a new Postgres 16 instance :
```
podman run -d --pod forum-services --name postgres-16-db -v [local mounting path]:/var/lib/postgresql/data docker.io/postgres:16-alpine
```
4) (psql) Create a new database named javaforum, with a owner named javaforum as well

5) Startup a new Fake SMTP Server instance :
```
podman run -d --pod forum-services --name forum-fake-smtp-server docker.io/gessnerfl/fake-smtp-server
```

6) Startup a new Redis server instance :
```
podman run -d --pod forum-services --name forum-redis -v [local mounting path]:/usr/local/etc/redis docker.io/redis:7.2-alpine redis-server /usr/local/etc/redis/redis.conf
```

Create the application.properties file (src/main/resources/application.properties)
**Examples :**
```
#Global configuration
spring.application.name=forum-backend

#Tomcat configuration
server.port=9000

#Log level configuration
spring.jpa.show-sql=true
logging.level.root=ERROR
logging.level.com.fleo.javaforum=DEBUG
logging.level.org.springframework.security=ERROR
logging.level.org.springframework.boot.web.embedded.tomcat=INFO
logging.level.org.thymeleaf=ERROR
logging.level.jakarta.mail=ERROR
logging.level.org.io.lettuce=TRACE

#Datasource configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/javaforum
spring.datasource.username=javaforum
spring.datasource.password=javaforum

#Hibernate configuration
spring.jpa.hibernate.ddl-auto=none

#Devtool config
spring.devtools.restart.enabled=true

#Jwt config
application.security.jwt.secret-key=[Your secret]
application.security.jwt.expiration=900000
application.security.refresh-token.expiration=1296000000

#Mail server config
spring.mail.host=localhost
spring.mail.port=9025
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=true

#Redis config
spring.data.redis.host=localhost
spring.data.redis.port=6379
stream.key.email=email

#Liquibase config
spring.liquibase.change-log=classpath:/db/db.changelog-master.yaml

#Uploads
storage.location.root=${user.dir}/uploads
storage.location.avatars=${storage.location.root}/avatars
```

7) Load the maven dependencies
8) Populate the database using Liquibase maven plugin :
```
# Before executing the migration, it's good to only generate the SQL which will be executed first to control. The fill will be generated in target/liquibase by default
mvn liquibase:updateSQL 

# After controlling the file, you can execute the update command
mvn liquibase:update
```

9) Build and run the Spring Boot App