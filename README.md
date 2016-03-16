Google App Engine Java location-based online query

## UC Santa Barbara Winter 2016
## CS263 Runtime Systems Final Project

Requires [Apache Maven](http://maven.apache.org) 3.3 or greater, and JDK 7+ in order to run.

To build, run 

    mvn package

Building will run the tests, but to explicitly run tests you can use the test target

    mvn test

To start the app, use the [App Engine Maven Plugin](http://code.google.com/p/appengine-maven-plugin/) that is already included in this demo.  Just run the command.

    mvn appengine:devserver

To see all the available goals for the App Engine plugin, run

    mvn help:describe -Dplugin=appengine
    
When browse login.jsp, you can choose a username and share your location on sharelocation.jsp. After that, you can either post a query, check your own queries or answer queries. A quick demo can be found at [Project Presentation](https://youtu.be/ehP7qoC6DJI).
