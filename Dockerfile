FROM gradle:7.6.1-jdk17-alpine AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
ENTRYPOINT ["gradle", "bootRun"]