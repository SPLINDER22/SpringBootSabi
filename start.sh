#!/bin/bash
cd sabi
java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/sabi-0.0.1-SNAPSHOT.jar

