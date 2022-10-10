FROM azul/zulu-openjdk:8-latest as mainstage
USER root
RUN apt-get update; apt-get install -y curl
RUN echo | openssl s_client -servername egov.uscis.gov -connect egov.uscis.gov:443 |\
      sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > certificate.crt
RUN keytool -import -noprompt -trustcacerts -importcert -storepass changeit -alias egov.uscis.gov -file certificate.crt -keystore $JAVA_HOME/jre/lib/security/cacerts

WORKDIR /opt/docker
COPY target/scala-**/reply-sms-assembly-*-SNAPSHOT.jar /opt/docker/reply-sms-assembly.jar
ENTRYPOINT ["java", "-jar", "/opt/docker/reply-sms-assembly.jar"]