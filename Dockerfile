FROM java:8-jre
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>

ADD ./build/libs/akka-demo-1.0.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/akka-demo-1.0.0.jar"]

EXPOSE 7000