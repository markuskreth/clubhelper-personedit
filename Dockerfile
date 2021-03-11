FROM adoptopenjdk:11-jre-hotspot as builder
ARG JAR_FILE=target/clubhelper*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract
 
FROM adoptopenjdk:11-jre-hotspot
RUN localedef -i de_DE -f UTF-8 de_DE.UTF-8
RUN echo "LANG=\"de_DE.UTF-8\"" > /etc/locale.conf
RUN echo "Europe/Berlin" > /etc/timezone && dpkg-reconfigure -f noninteractive tzdata
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENV LANG de_DE.UTF-8
ENV LANGUAGE de_DE.UTF-8
ENV LC_ALL de_DE.UTF-8
ENV TZ Europe/Berlin
ENV JAVA_OPTS="-Duser.language=de -Duser.country=DE"
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
