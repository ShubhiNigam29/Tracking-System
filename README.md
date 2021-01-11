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

