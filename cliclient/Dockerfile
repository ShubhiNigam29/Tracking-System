FROM alpine
LABEL maintainer Shubhi Nigam <shubhi.nigam@stonybrook.edu>

# Install Python dependencies
RUN apk add --update python python-dev py-pip build-base

WORKDIR /app
ADD . /app

ENTRYPOINT ["python", "cliclient.py"]