FROM jenkinsci/jenkins:latest

# machine conf
USER root

RUN curl -sL https://deb.nodesource.com/setup_6.x | bash -
RUN apt-get update
RUN apt-get install -y ca-certificates
RUN apt-get install -y  bash git curl python
RUN apt-get install -y nodejs build-essential
RUN apt-get install -y dbus dbus-x11
RUN apt-get upgrade -y
RUN apt-get clean
RUN apt-get autoremove -y
RUN npm install -g coffee-script grunt jshint coffeelint mocha
RUN npm cache clean
RUN mkdir -p /var/run/dbus
RUN chmod -R 777 /var/run/dbus
RUN chown -R jenkins: /var/run/dbus
VOLUME /var/run/dbus

USER jenkins
ENV JENKINS_OPTS --httpPort=9090
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
EXPOSE 9090
RUN npm set prefix ~/.npm

#jenkins conf
COPY config/*.groovy /usr/share/jenkins/ref/init.groovy.d/
COPY config/plugins.txt /usr/share/jenkins/plugins.txt
ADD templates /var/tmp/templates

RUN /usr/local/bin/plugins.sh /usr/share/jenkins/plugins.txt
