# server
This repository contains Java Springboot backend used to process and pass data from the [webapp application](https://github.com/proofd-it/webapp)

## Deployment

Currently, the application is served using tomcat. WAR can be built by running `mvn package` at project's root. Then, copy / symlink the obtained .war package to your tomcat home folder. At the moment, it's symlinked, so should any changes happen, simple `mvn package` should be sufficient.

## Endpoints

Two enpoits are provided
- `/transaction`
- `/save`

## /transcation
POST endpoint expecting a JSON payload, which then is sent to blockchain instance, set as `const` to `localhost:3000/api/delivery`.
The JSON is prior enriched by semantic annotations.

## /save
For debugging purposes, simple POST endpoint that saves any payload to log folder. 

## ENV variables
You will need to provide `APP_MASTER_USERNAME` and `APP_MASTER_PASSWORD` to the server, which is used to access the blockchain endpoints. As this is currently run via tomcat, the variables are set in tomcat.
