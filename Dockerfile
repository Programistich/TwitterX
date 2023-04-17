FROM gradle:7.4.1-jdk11-alpine AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
ENTRYPOINT ["gradle", "bootRun"]