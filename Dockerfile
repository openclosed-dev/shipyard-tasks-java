FROM eclipse-temurin:21.0.4_7-jre-noble

RUN mkdir /agents
ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.6.1/applicationinsights-agent-3.6.1.jar /agent/applicationinsights-agent.jar

RUN mkdir /opt/app
COPY build/libs/*.jar /opt/app

ENTRYPOINT ["java", "-jar", "/opt/app/shipyard-tasks.jar"]
