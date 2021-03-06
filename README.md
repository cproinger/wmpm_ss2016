# WMPM Project Voting


[![Build Status](https://travis-ci.org/cproinger/wmpm_ss2016.svg?branch=master)](https://travis-ci.org/cproinger/wmpm_ss2016)


# Running the tests

to run the junit tests run

## Unix

```
./gradlew test
```

## Windows

```
gradlew test
```

# Running the Application

## Unix

```
./gradlew bootRun
```

## Windows

```
gradlew bootRun
```

# Endpoints

	* Spring-WS Endpoint http://localhost:8080/ws
		* TIP: Use for example SOAP-UI to create a project from the WSDL-URL (http://localhost:8080/ws/votes.wsdl)

#Current status:
Set up project environment for all team members

All team members have specific tasks specified inside [Issues tab on github](https://github.com/cproinger/wmpm_ss2016/issues)

Running project with tests

#Time tracking
https://docs.google.com/spreadsheets/d/149ocru7V8RhDNBeTdGYIPDcPE2QEVjqHYENup_pYy1E/edit?usp=sharing
#Running mongodb on os X
1. [ Install brew](http://brew.sh/)
2. [Install mongodb](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-os-x/)
   ``brew install mongodb`
3. Run mongo
	``sudo mongod`` ; sudo mongo

//ToDo
Add BPMN processes with higlighted task parts(which parts of BPMN processes are covered with our tasks)

For Unit tests. If no mongodb server is running, it uses a fake one which does not 
suuport all the features of a real mongodb (which we are not using anyway). 

# Our Slack Team

- https://wmgroup4.slack.com/

## Using a different slack team

Run the application with the a different value for

```
slack.url=<your-webhook-url>
```

# Using a real database. 


## SQL Database
Run the application with

spring.datasource.url=jdbc:h2:./database

which will create a database in the working directory if none exists. 

## Change MongoDB URL

Out of the box, the application will try to connect to "mongodb://localhost/test" with default credentials. 

If you want to use a differnt database run the application with the system property

```
spring.data.mongodb.uri=<your-url>

```

for example, further details can be found at http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-nosql.html

```
spring.data.mongodb.uri=mongodb://user:secret@mongo1.example.com:12345,mongo2.example.com:23456/test
```