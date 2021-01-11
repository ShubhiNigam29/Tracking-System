FROM ubuntu:18.04

# Cassandra
RUN apt-get install -y python-pip python-dev build-essential
RUN pip install --upgrade pip
RUN pip install cassandra-driver

WORKDIR /app
ADD . /app

RUN chmod +x /app/cassandra.sh

CMD ["sbt", "run"]