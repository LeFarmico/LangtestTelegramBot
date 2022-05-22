FROM openjdk:11
COPY /build/libs/SpanishWordsTelegramBot-1.0-SNAPSHOT-all.jar /usr/src/myapp/
WORKDIR /usr/src/myapp
CMD ["java", "-jar", "SpanishWordsTelegramBot-1.0-SNAPSHOT-all.jar"]