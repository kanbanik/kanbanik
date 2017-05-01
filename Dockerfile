FROM jetty

RUN apt-get update && apt-get install -y wget unzip && rm -rf /var/lib/apt/lists/*

RUN wget https://sourceforge.net/projects/kanbanik/files/kanbanik-application-1.0.2-RC1.zip/download

RUN unzip download

RUN cp kanbanik-application/kanbanik.war /var/lib/jetty/webapps/ROOT.war

