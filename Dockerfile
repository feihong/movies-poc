FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/movies.jar /movies/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/movies/app.jar"]
