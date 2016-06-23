FROM jenkinsci/jenkins:latest

USER root

RUN apt-get update
RUN apt-get install -y ca-certificates
RUN apt-get install -y  bash git curl python
RUN curl -sL https://deb.nodesource.com/setup_6.x | bash -
RUN apt-get install -y nodejs build-essential
RUN apt-get install -y dbus dbus-x11
RUN apt-get upgrade
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
EXPOSE 9090
RUN npm set prefix ~/.npm
