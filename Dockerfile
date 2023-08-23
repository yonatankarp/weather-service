FROM --platform=linux/x86_64 eclipse-temurin:17-jre-alpine

ENV APP_BASE="/home" \
    APP_NAME="cat-fact-service" \
    OTEL_ATTRIBUTES="github.repository=https://github.com/yonatankarp/cat-fact-service" \
    SERVER_PORT="8080"

EXPOSE ${SERVER_PORT}

RUN apk update && apk upgrade && apk add curl openssl gcompat bash busybox-extras iputils

RUN mkdir -p ${APP_BASE}/${APP_NAME}

# Otel agent
COPY "/build/output/libs" "${APP_BASE}/${APP_NAME}"

COPY "/build/libs/${APP_NAME}*.jar" "${APP_BASE}/${APP_NAME}.jar"

CMD java $JAVA_OPTS \
    -Dotel.resource.attributes="${OTEL_ATTRIBUTES}" \
    -javaagent:${APP_BASE}/${APP_NAME}/honeycomb-opentelemetry-javaagent.jar \
    -jar ${APP_BASE}/${APP_NAME}.jar
