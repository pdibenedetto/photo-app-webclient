FROM openjdk:11-jdk
RUN addgroup --system pdibenedetto && adduser --system pdibenedetto --ingroup pdibenedetto
USER pdibenedetto:pdibenedetto
# Commenting out for performance increase
#ARG JAR_FILE=target/*.jar
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
#COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-cp","app:app/lib/*","com.appsdeveloperblog.ws.clients.photoappwebclient.PhotoAppWebClientApplication"]
