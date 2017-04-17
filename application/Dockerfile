FROM java:8-jre
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>

ADD ./build/libs/application.jar /app/
CMD ["java", "-Xmx128m", "-jar", "/app/application.jar"]

EXPOSE 7000