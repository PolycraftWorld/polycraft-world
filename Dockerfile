FROM openjdk:8u212-jre-alpine

LABEL maintainer "stephengs"

RUN dos2unix /start* && chmod +x /start*