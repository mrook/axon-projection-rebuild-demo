Rebuilding projections with Axon Framework
==========================================

This is a simple project to demonstrate how to rebuild projections (read models) in Axon Framework (3.x).

This uses the Tracking Event Processor component. For more information see the following web pages:

- https://www.michielrook.nl/2017/09/using-tracking-processors-replay-events-axon-framework-3/
- https://docs.axonframework.org/part3/event-processing.html#event-processors

# Prerequisities

- Docker
- Docker Compose
- JDK 8

# To run

Execute the following commands:

```
$ docker-compose up -d
$ gradle bRu
```

Then, in a separate terminal, run:

```
$ curl http://localhost:8080/hello?name=Foo
$ curl http://localhost:8080/rebuild-status
```
