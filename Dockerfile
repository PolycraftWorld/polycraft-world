FROM openjdk:8u212-jre-alpine

LABEL maintainer "stephengs"

exec gradlew setupDecompWorkspace
exec gradlew build
exec gradlew runClient