# server
This repository contains Java Springboot backend used to generate semantic annotations from JSON payloads uploaded from the [webapp application](https://github.com/proofd-it/webapp). The semantic annotations are saved on the blockchain network as transactions. 

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

# Competency questions evaluation

The code also contains a package com.proofd.evaluate. You can re-run the competency questions evaluation by running the main method in RunEval.java. The code will use files in the resource folder which contain local copies of the required ontologies, university catering delivery plan, and a sample execution trace for a non-compliant use case (i.e. this produces the richest provenance records) which was produced by the server app 

# Expected Result 

Q1 
---------------------------------------------------------------------------------------------------------------------
| plan                                       | const                                            | result            |
=====================================================================================================================
| ucp:UniversityCateringSandwichDeliveryPlan | ucp:TotalNumberOfAmbientTemperatureStorageStages | ep-plan:violated  |
| ucp:UniversityCateringSandwichDeliveryPlan | ucp:TotalTimeAllowedForColdStorage               | ep-plan:satisfied |
| ucp:UniversityCateringSandwichDeliveryPlan | ucp:TotalTimeAllowedForAmbientTemperatureStorage | ep-plan:satisfied |
---------------------------------------------------------------------------------------------------------------------

Q2 
------------------------------------
| deliveryAgent                    |
====================================
| example-trace:UniversityCatering |
------------------------------------

Q3 
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
| step                          | stepComment                                                                                                                                                             | const                        | act                                                |
===============================================================================================================================================================================================================================================================================================
| ucp:ColdStorage               | "Food is intended to be kept below the required HACP temperature threshold for cold storage"                                                                            | ucp:ColdStorageConstraint    | example-trace:5ea2c78c-2104-4330-b99a-c34a727dd062 |
| ucp:AmbientTemperatureStorage | "Step describing the part of a food delivery workflow where food is not stored in a refrigirator or is teh transport stage (i.e. the final leg of the delivery proces)" | ucp:AmbientStorageConstraint | example-trace:f7613365-fb9b-4609-b501-383365926b57 |
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Q4 
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
| step                          | const                        | act                                                | unit                                                                      | avg                                                         | numbReadings                                 |
==============================================================================================================================================================================================================================================================================================================
| ucp:ColdStorage               | ucp:ColdStorageConstraint    | example-trace:5ea2c78c-2104-4330-b99a-c34a727dd062 | <http://www.ontology-of-units-of-measure.org/resource/om-2/degreeCelsius> | "11.2"^^<http://www.w3.org/2001/XMLSchema#double>           | "11"^^<http://www.w3.org/2001/XMLSchema#int> |
| ucp:ColdStorage               | ucp:ColdStorageConstraint    | example-trace:c981be8e-900c-4ad8-b7b2-62f3e68b6b99 | <http://www.ontology-of-units-of-measure.org/resource/om-2/degreeCelsius> | "1.6875"^^<http://www.w3.org/2001/XMLSchema#double>         | "10"^^<http://www.w3.org/2001/XMLSchema#int> |
| ucp:AmbientTemperatureStorage | ucp:AmbientStorageConstraint | example-trace:f7613365-fb9b-4609-b501-383365926b57 | <http://www.ontology-of-units-of-measure.org/resource/om-2/degreeCelsius> | "27.79949951171"^^<http://www.w3.org/2001/XMLSchema#double> | "11"^^<http://www.w3.org/2001/XMLSchema#int> |
| ucp:AmbientTemperatureStorage | ucp:AmbientStorageConstraint | example-trace:a166cd58-9065-4573-b84c-3035d9ca19fc | <http://www.ontology-of-units-of-measure.org/resource/om-2/degreeCelsius> | "21.3"^^<http://www.w3.org/2001/XMLSchema#double>           | "11"^^<http://www.w3.org/2001/XMLSchema#int> |
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Q5
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
| step                          | const                        | act                                                | featureOfInterest                                  | var                      | observableProperty           |
====================================================================================================================================================================================================================================
| ucp:ColdStorage               | ucp:ColdStorageConstraint    | example-trace:5ea2c78c-2104-4330-b99a-c34a727dd062 | example-trace:670761ff-baf9-4ed5-b41d-c68e39e9a12a | ucp:ChilledItem          | example-trace:AirTemperature |
| ucp:ColdStorage               | ucp:ColdStorageConstraint    | example-trace:c981be8e-900c-4ad8-b7b2-62f3e68b6b99 | example-trace:e48f85f6-d021-43b0-9cfb-43d5a4eafeca | ucp:ChilledItem          | example-trace:AirTemperature |
| ucp:AmbientTemperatureStorage | ucp:AmbientStorageConstraint | example-trace:f7613365-fb9b-4609-b501-383365926b57 | example-trace:882490fa-1636-4fdf-b1c9-47b6de4c7190 | ucp:OutOfColdStorageItem | example-trace:AirTemperature |
| ucp:AmbientTemperatureStorage | ucp:AmbientStorageConstraint | example-trace:a166cd58-9065-4573-b84c-3035d9ca19fc | example-trace:0979cb7b-6386-4cb6-a171-3278c404ce80 | ucp:OutOfColdStorageItem | example-trace:AirTemperature |
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Q6
---------------------------------------------------
| step                          | nubOfExecutions |
===================================================
| ucp:ColdStorage               | 2               |
| ucp:AmbientTemperatureStorage | 2               |
| ucp:RecievedByCustomer        | 1               |
---------------------------------------------------

Q7
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
| step                          | const                        | act                                                | readingValue                                         |
============================================================================================================================================================================
| ucp:ColdStorage               | ucp:ColdStorageConstraint    | example-trace:5ea2c78c-2104-4330-b99a-c34a727dd062 | "13.0"^^<http://www.w3.org/2001/XMLSchema#double>    |
| ucp:ColdStorage               | ucp:ColdStorageConstraint    | example-trace:5ea2c78c-2104-4330-b99a-c34a727dd062 | "10.0"^^<http://www.w3.org/2001/XMLSchema#double>    |
| ucp:ColdStorage               | ucp:ColdStorageConstraint    | example-trace:5ea2c78c-2104-4330-b99a-c34a727dd062 | "12.0"^^<http://www.w3.org/2001/XMLSchema#double>    |
| ucp:ColdStorage               | ucp:ColdStorageConstraint    | example-trace:5ea2c78c-2104-4330-b99a-c34a727dd062 | "12.0"^^<http://www.w3.org/2001/XMLSchema#double>    |
| ucp:ColdStorage               | ucp:ColdStorageConstraint    | example-trace:5ea2c78c-2104-4330-b99a-c34a727dd062 | "12.0"^^<http://www.w3.org/2001/XMLSchema#double>    |
| ucp:AmbientTemperatureStorage | ucp:AmbientStorageConstraint | example-trace:f7613365-fb9b-4609-b501-383365926b57 | "28.25"^^<http://www.w3.org/2001/XMLSchema#double>   |
| ucp:AmbientTemperatureStorage | ucp:AmbientStorageConstraint | example-trace:f7613365-fb9b-4609-b501-383365926b57 | "26.75"^^<http://www.w3.org/2001/XMLSchema#double>   |
| ucp:AmbientTemperatureStorage | ucp:AmbientStorageConstraint | example-trace:f7613365-fb9b-4609-b501-383365926b57 | "26.5"^^<http://www.w3.org/2001/XMLSchema#double>    |
| ucp:AmbientTemperatureStorage | ucp:AmbientStorageConstraint | example-trace:f7613365-fb9b-4609-b501-383365926b57 | "27.25"^^<http://www.w3.org/2001/XMLSchema#double>   |
| ucp:AmbientTemperatureStorage | ucp:AmbientStorageConstraint | example-trace:f7613365-fb9b-4609-b501-383365926b57 | "26.8125"^^<http://www.w3.org/2001/XMLSchema#double> |
| ucp:AmbientTemperatureStorage | ucp:AmbientStorageConstraint | example-trace:f7613365-fb9b-4609-b501-383365926b57 | "27.875"^^<http://www.w3.org/2001/XMLSchema#double>  |
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Q8
----------------------------------------------------------------------------------------------------------------------------------------------------------------
| deliveryId                             | deliveryProcessStart                                     | timeDelivered                                            |
================================================================================================================================================================
| "96ca4ad5-3847-4ee0-a879-f4d187878b24" | "1581983457000"^^<http://www.w3.org/2001/XMLSchema#long> | "1582037656000"^^<http://www.w3.org/2001/XMLSchema#long> |
----------------------------------------------------------------------------------------------------------------------------------------------------------------

Q9
-------------------------------------------------------------------------------------------------------------------------------------
| var               | ent                                                | timeReceived                                             |
=====================================================================================================================================
| ucp:DeliveredItem | example-trace:302026ff-195a-4318-8827-9da60764d113 | "1582037656000"^^<http://www.w3.org/2001/XMLSchema#long> |
-------------------------------------------------------------------------------------------------------------------------------------

Q10
----------------------------------------------------------------------------------------
| var                | ent                                                | result     |
========================================================================================
| ucp:DeliveryResult | example-trace:39d5a3f2-3b2a-4557-bffe-5c730d4582a3 | "rejected" |
----------------------------------------------------------------------------------------

