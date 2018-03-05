# AdditionAPI
Web API Demonstration for Deontics Ltd

## 1 About

AdditionAPI is a web based API built on the SpringBoot framework. It follows a typical Spring webserver architecture using controllers to handle remote interactions, services for internal business logic and model objects to represent the different data components that can be exchanged between the client and the server.

AdditionAPI performs caching of results, sessions and prior user actions so it cannot be considered a fully RESTfull API although it exhibits many of these traits.

It allows a user to initiate a session and send arrays of numbers to be added. The result of the sum of each array is returned to the user as a JSON object and cached along with the user’s session. If a user sends further arrays to be processed, the result of the previous operation is always added on to the new array before this one is evaluated.

The user can send individual arrays to be evaluated immediately or a batch of arrays to be processed by a background server task to be retrieved at a later time.

AdditionAPI has been developed using SpringBoot and JAVA 8. A POM file is supplied in the project root to help with setup.

## 2 Client server communication

### 2.1 Session initialisation
Before the client can send any data to be processed, the client must first send a GET request to the domain vertical http://localhost:8080/AdditionEngine. Following this request, the server will initiate a session and send the session packet to the client as a JSON response body payload.
```
[Client] ------<HTTP GET>------> [server]
[Client] <---<JSON SESSION>----- [server]
```
Server response JSON payload:
```
{
  "id": 1,
  "clientIP": "192.168.1.1",
  "status": "AVAILABLE"
}
```

Note the server indicates a "status" of "AVAILABLE", this is because a user cannot transmit more files on a session awaiting the result of a “batch process” until this has been finished and retrieved. More on this later.

At this point, two types of exchanges are possible, SINGLE and BATCH.

### 2.1 SINGLE Array transmission
In the case of a single array transmission, the server responds immediately with the result in the response payload.

The client transmits the request array in a JSON payload via an HTTP POST request to the same http://localhost:8080/AdditionEngine vertical.
```
[Client] ----<single array>----> [server]
[Client] <----<sum result>------ [server]
```

Client request JSON payload:
```
{
  "session": {
    "id": 19,
    "clientIP": "192.168.1.1",
    "status": "AVAILABLE"
  },
  "data": [
    {
      "@class": "com.deontics.AdditionAPI.models.ApiRequestArray",
      "sumList": [
        87.2,
        42.0,
        9.81
      ]
    }
  ],
  "transmissionType": "SINGLE"
}
```

Note the client has to specify a "transmissionType" of "SINGLE". This actually allows a client to be able to force a particularly large “single” payload to be traded as a “batch” in spite of the fact that only one array is transmitted.

Server response JSON payload:
```
{
    "session": {
        "id": 19,
        "clientIP": "0:0:0:0:0:0:0:1",
        "status": "AVAILABLE"
    },
    "data": [
        {
            "@class": "com.deontics.AdditionAPI.models.ApiResultContainer",
            "result": 139.01
        }
    ],
    "transmissionType": "SINGLE"
}
```

The server responds with a computation result, a confirmation that the transmission request was single and a confirmation of the session’s further availability for future requests.

### 2.2 BATCH transmission of multiple arrays
In the case of batch transmission of multiple arrays, the server responds only with an acknowledgment of receipt. The job is launched in the background and the client will have to return later in order to check if his or her session’s result available. 

The client transmits the request array in a JSON payload via an HTTP POST request to the same http://localhost:8080/AdditionEngine vertical.
```
[Client] ---<BATCH of array>---> [server]
[Client] <--<Acknowledgment>---- [server]
```
Client request JSON payload:
```
{
  "session": {
    "id": 19,
    "clientIP": "192.168.1.1",
    "status": "AVAILABLE"
  },
  "data": [
    {
      "@class": "com.deontics.AdditionAPI.models.ApiRequestArray",
      "sumList": [
        87.2,
        42.0,
        9.81
      ]
    },
    {
      "@class": "com.deontics.AdditionAPI.models.ApiRequestArray",
      "sumList": [
        87.2,
        42.0,
        9.81,
        1.618
      ]
    },
    {
      "@class": "com.deontics.AdditionAPI.models.ApiRequestArray",
      "sumList": [
        87.2,
        6.67,
        2.99
      ]
    }
  ],
  "transmissionType": "BATCH"
}
```

Note how the client must specify that the transmission is of type BATCH, otherwise, only the first array will be processed as a “SINGLE” and immediate computation.

Server response JSON payload:
```
{
    "session": {
        "id": 19,
        "clientIP": "0:0:0:0:0:0:0:1",
        "status": "BATCH_PROCESSING"
    },
    "data": [
        {
            "@class": "com.deontics.AdditionAPI.models.ApiMessageContainer",
            "message": "STATUS: Batch request achnowledged - Being processed"
        }
    ],
    "transmissionType": "BATCH"
}
```

Note how the session’s status has shifted to "BATCH_PROCESSING". In some cases if the batch job was short and the computation is completed before the server responds, the session status field may already display AWAITING_RETRIEVAL.

Regardless of the value of this field, the client will have to manually initiate the batch retrieval operation.
### 2.3 BATCH retrieval
As previously discussed, so long as a session has a status of "BATCH_PROCESSING" or "AWAITING_RETRIEVAL" no further data set transmission is possible for the client:
```
{
    "timestamp": "2018-03-05T06:07:50.238+0000",
    "status": 400,
    "error": "Bad Request",
    "message": "Session 19, 0:0:0:0:0:0:0:1 is currently BATCH PROCESSING an existing requests, no more requests can be accepted at this time",
    "path": "/AdditionEngine"
}
```
In order to unlock a session, the client must send an HTTP GET request to the http://localhost:8080/AdditionEngine/{#sessionId} vertical where {#sessionId} is the current session’s reference identifier. If the batch has ben processed and is " AWAITING_RETRIEVAL", server will return the data and unlock the session status back to "AVAILABLE".
```
[Client] ----<GET sessionId>---> [server]
[Client] <-<Data if Available>-- [server]
```
Server response JSON payload:
```
{
    "session": {
        "id": 19,
        "clientIP": "0:0:0:0:0:0:0:1",
        "status": "AVAILABLE"
    },
    "data": [
        {
            "@class": "com.deontics.AdditionAPI.models.ApiResultContainer",
            "result": 278.02
        },
        {
            "@class": "com.deontics.AdditionAPI.models.ApiResultContainer",
            "result": 418.647
        },
        {
            "@class": "com.deontics.AdditionAPI.models.ApiResultContainer",
            "result": 515.507
        }
    ],
    "transmissionType": "BATCH"
}
```

Otherwise the server will simply confirm that the session is locked in a "BATCH_PROCESSING" state.
```
{
    "session": {
        "id": 19,
        "clientIP": "0:0:0:0:0:0:0:1",
        "status": " BATCH_PROCESSING "
    },
    "data": [
        {
            "@class": "com.deontics.AdditionAPI.models.ApiMessageContainer",
            "message": 278.02
    ],
    "transmissionType": "BATCH"
}
```
### 2.4 Session deletion
Finally sessions can be terminated at any time by sending an HTTP DELETE request to the http://localhost:8080/AdditionEngine/{#sessionId} vertical.
```
Response
200
Content-Length: 0
Date: Mon, 05 Mar 2018 06:30:20 GMT
```
### 2.5 Session States Recap:
```
public enum SessionStatus {
        AVAILABLE,
        BATCH_PROCESSING,
        AWAITING_RETRIEVAL
    }
```
```
### 2.6 transmissionType Recap:
public enum TransmissionType {
        SINGLE,
        BATCH
    }
```
## 3 AdditionClient and Unit tests
A basic client to demo a typical client server interaction is available at the following GitHub repository:
https://github.com/chopo87/AdditionClient


Please note that due to time constraints, this client is rather under developed. For a more exhaustive demonstration of Client-Server interactions, please see the controller unit tests in the test/java/com.deontics.AdditionAPI.controllers package.

Alternatively a browser plugin such as FireFox’s RESTer extension can be used to further experiment with the API.

## 4 Exercise notes
Most of the code was written for Exercise 1. The code relevant to Exercise 2 can be found in the following two locations:

### 4.1 com.deontics.AdditionAPI.services package:
```
- AdditionService.batchCalculate
```
### 4.2 com.deontics.AdditionAPI.controllers package:
```
- AdditionService.postRequest
--> BLOCK: if (atc.getTransmissionType() == ApiTransferContainer.TransmissionType.BATCH) 

- AdditionService.getStatusRequest
```
### 4.3 Difference from Exercise specs:

1) I wasn’t sure if you still wanted me to cache prior server results as we discussed last week so I included that functionality even though it is not strictly specified.

2) Given that I used the spring boot framework in order to develop this project I did not use AdditionClient for unit testing AdditionAPI. The Spring Framework has a really rich set of mocking and unit testing tools for HTTP so I judged it was both easier, far more exhaustive in terms of testing and more maintainable to utilise these tools.

You will find all the unit tests under the regular /test/java path.

## 5 The End
Thank you so much for your time, I hope this project is to your liking
