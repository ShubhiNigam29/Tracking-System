FROM alpine:3.6
LABEL maintainer Shubhi Nigam <shubhi.nigam@stonybrook.edu>

# Install Python dependencies
RUN apk add --update python python-dev py-pip build-base

# Install requirements
COPY requirements.txt requirements.txt
RUN pip install --upgrade pip
RUN pip install --requirement requirements.txt

WORKDIR /app
ADD . /app

ENTRYPOINT ["python", "cliclient.py"]