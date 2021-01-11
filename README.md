# Objective

The objective is to build a real-time tracking system that should consist of three main parts:
- Tracking service
- Pub/sub system for inter-service communication
- CLI client for displaying results

# Tracking service

Service should expose a single endpoint that receives events. Event contains the following
parameters:
- Account id
- Data = random string of your choice
*API example: BASE_URL/<accountId>?data=”<data>”*
Service is connected to a persistent database that contains the information about active
accounts.
*Example, table with “accountId”. “accountName”, “isActive” fields. (generate & populate yourself)*

Service should accept events, validate accountId (via DB) and if account “isActive” propagate
event to the pub/sub system. The propagated event should contain:
- Account id
- Timestamp
- Data

## Requirements:

- High throughput system => should handle at least 500 req/s
- System should be scalable

# Pub/sub system

Second part of the architecture is a Publish-subscribe messaging system. The system accepts
events from the Tracker service, and propagates them to the subscribed services (over
network).

# CLI client

Fault tolerant CLI client, subscribed to the Pub/sub system and displaying messages as they
arrive to the Pub/Sub.

## Requirements:

- Supports filtering (on accountId), (optionally also aggregation)
- Fault tolerant:
- In case of network errors (e.g. pub/sub system unavailable / no connection) - the
client should not break
- Should recover seamlessly from errors (no restart needed when connection is
again available)
Notes
- Code has to be covered by tests.
- The choice of architecture, programming language, framework, communication protocols
and formats is up to you.
- The whole project should be easy to set up and run.
- Finally, the application should be documented, explaining the architectural, technological
and other decision you made
- Prefer technologies you are used to (if they are a good fit for the solution)

# Solution & Design

1. Colossus

```
Colossus is a low-level event-based framework. In a nutshell, it spins up multiple event loops, generally one per CPU core. TCP connections are bound to event loops and request handlers are attached to connections to transform incoming requests into responses. I/O operations (such as making requests to a remote cache or another service) can be done asynchronously and entirely within the event loop, eliminating the need for complex multi-threaded code and greatly reducing the overhead of asynchronous operations.
```

2. Actors

```
Actors are implemented by extending the *Actor* base trait and implementing the receive method. The receive method should define a series of case statements (which has the type PartialFunction[Any, Unit]) that defines which messages your Actor can handle, using standard Scala pattern matching, along with the implementation of how the messages should be processed.
```

3. Cassandra

```
The Apache Cassandra database is the right choice because it is persistent and we need scalability and high availability without compromising performance. Linear scalability and proven fault-tolerance on commodity hardware or cloud infrastructure make it the perfect platform for mission-critical data. Cassandra's support for replicating across multiple datacenters is best-in-class, providing lower latency for users.
```

4. Docker

```
Docker is an open platform for developing, shipping, and running applications. Docker enables us to separate applications from infrastructure so the software can be delivered quickly. Docker is a software platform for building applications based on containers — small and lightweight execution environments that make shared use of the operating system kernel but otherwise run in isolation from one another.
```

5. Docker compose

```
Compose is a tool for defining and running multi-container Docker applications. With Compose, we use a YAML file to configure application’s services. Then, with a single command, we create and start all the services from the configuration.
```

# Tracking Service

1. Port: 9000 is used by the service to listen to incoming requests <br />
2. Let request be - localhost:9000/1/"test string sample", where 1 represents accountId, and test string sample is our data string. <br />
Request validation: <br />
a) If account id doesn't exist, HTTP 400: Account Id does not exist! is returned. <br />
b) If result is retrieved, accountId is checked for activeness. If inactive, HTTP 400: Inactive account! is returned. <br />
c) If active account, HTTP 200: Request propagated to the subscribers! is returned, and message is pushed to the message queue. <br />

# Pub/Sub System

ZeroMQ (also known as ØMQ, 0MQ, or zmq) acts like a concurrency framework. It gives sockets that carry atomic messages across various transports like in-process, inter-process, TCP, and multicast. One can connect sockets N-to-N with patterns like fan-out, pub-sub, task distribution, and request-reply. It's fast enough to be the fabric for clustered products. Its asynchronous I/O model gives scalable multicore applications, built as asynchronous message-processing tasks.

# CLI Client

CLI client is a lightweight Python service that can filter messages based on Account id. Client is subscribed to specific ZeroMQ topic and displays data string sent by tracking service. Client also provides support for aggregation, default value is 500.

# How to use?

1. Clone Repo from github <br />
2. Run `docker-compose build` for building trackingservice and cliclient <br />
3. Run `docker-compose run tracking_service` to start tracking service <br />
4. Run `docker-compose run cli_client` to start cli client <br />

# Tests

Pytests for cliclient are available by running the below command: <br />
`docker-compose run test_client`

![alt text](https://github.com/ShubhiNigam29/Tracking-System/blob/main/Test.JPG)