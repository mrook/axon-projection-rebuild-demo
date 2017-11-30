Rebuilding projections with Axon Framework
==========================================

This is a simple app to demonstrate how to rebuild projections (read models) in Axon Framework (3.x).

The app has a very simple domain: Persons can be registered (through a controller), and there is a single projection which can be rebuilt. The status / progress of rebuilds can be tracked.

Rebuilding is done using the Tracking Event Processor component. For more information see the following web pages:

- https://www.michielrook.nl/2017/09/using-tracking-processors-replay-events-axon-framework-3/
- https://docs.axonframework.org/part3/event-processing.html#event-processors

*This is a proof of concept and probably needs significant cleanup. It could also stop working with future Axon updates.*

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

You should see something like:

`{"org.demo.projections.PersonProjections/1":{"ready":true,"progress":100,"timeBehindInMillis":0}}`
