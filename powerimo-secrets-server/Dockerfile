FROM amazoncorretto:17
ARG APP_FILE=target/*.jar

ENV JAVA_TOOL_OPTS="-Xms128m -Xmx128m"
ENV SERVER_PORT=8080
ENV SPRING_DATASOURCE_URL="jdbc:postgresql://db:5432/postgres"
ENV SPRING_DATASOURCE_USERNAME="postgres"
ENV APP_BASE_URL="http://localhost:3000"

RUN mkdir /app

COPY ${APP_FILE} /app/powerimo-secrets-server.jar

EXPOSE 8080/tcp

WORKDIR "/app"

CMD ["java", "-jar", "powerimo-secrets-server.jar"]
