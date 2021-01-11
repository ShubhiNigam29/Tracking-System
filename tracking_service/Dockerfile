FROM ubuntu:14.04

RUN apt update

# Installation of add-apt-repository and sbt
RUN apt install -y apt-transport-https default-jdk

# Installation of add-apt-repository
RUN apt install -y software-properties-common python-software-properties

# Install ZeroMQ
RUN add-apt-repository ppa:chris-lea/zeromq
RUN apt install -y libzmq-dev

# Install sbt
RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
RUN apt update
RUN apt install -y sbt=0.13.8

# Install Cassandra
RUN apt-get install -y python-pip python-dev build-essential    
RUN pip install --upgrade pip
RUN pip install cassandra_driver==3.7.1 --ignore-installed six

WORKDIR /app
ADD . /app

RUN chmod +x /app/runcassandra.sh

CMD ["sbt", "run"]