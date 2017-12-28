FROM jetty

RUN echo "deb http://http.debian.net/debian jessie-backports main" >> /etc/apt/sources.list

RUN apt-get update && apt-get install -y -t jessie-backports openjdk-8-jdk && apt-get install -y wget unzip maven git && rm -rf /var/lib/apt/lists/*

RUN update-java-alternatives -s java-1.8.0-openjdk-amd64

RUN git clone https://github.com/kanbanik/kanbanik.git

RUN cd kanbanik && mvn clean install -DskipTests

RUN cp kanbanik/kanbanik-web/target/kanbanik.war /var/lib/jetty/webapps/ROOT.war
