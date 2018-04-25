## Package overview
This package contains functions to support gRPC protocol based communication. gRPC is a framework developed by Google
to support the RPC (Remote Procedure Call) protocol. The gRPC protocol is layered over HTTP/2. This protocol only supports client-initiated communication.

### Protocol buffers
This is a mechanism to serialize the structured data introduced by Google and used by the gRPC framework. Specify the
 structured data that needs to be serialized in the  `.proto` file. This package contains functions to automatically generate the `.proto` file based on the service definition of a gRPC service. A sample `.proto` file is shown below.
```
syntax = "proto3";
import "google/protobuf/wrappers.proto";
service ServerStreaming {
     rpc receiveMessage(google.protobuf.StringValue) returns (stream google.protobuf.StringValue);
}
```

gRPC allows client applications to directly call the server-side methods using the auto-generated client stubs. Protocol buffer compilers are used to generate the stubs for the specified language. In Ballerina, the client stubs are generated using the built-in proto compiler.

### Endpoints
Endpoints specify how the client and server communicate. This package supports service endpoints and client endpoints. The service endpoint specifies how the client communicates with the server. The client endpoint is automatically generated by passing the `.proto` file of a service to a proto compiler. 

The Ballerina tooling distribution provides a proto compiler to generate the Ballerina client endpoint. The client endpoint generated using the Ballerina proto compiler has two client endpoints. Use the endpoint to suit the use case. 

### RPC types
This package supports the following RPC client-to-service communication methods: unary, server streaming, client streaming, and bidirectional streaming. Listeners are used to support the asynchronous and streaming communication.
#### Unary 
The server sends a response to each client request. Unary supports blocking and non-blocking communication. 
#### Server streaming
The server sends multiple responses in an asynchronous manner for each client request.
#### Client streaming
The client sends multiple requests while the server sends a single response to these requests.
#### Bidirectional Streaming
The client and server send multiple requests and responses.

## Samples
### Unary communication
The sample given below contains a service that sends a response to each request.

```ballerina
// Server endpoint configuration.
endpoint grpc:Listener ep {
   host:"localhost",
   port:9090
};

// The gRPC service that binds to the server endpoint.
service SamplegRPCService bind ep {
   // A resource that accepts a string message.
   receiveMessage (endpoint caller, string name) {
       // Print the received message.
       io:println("Received message from : " + name);
       // Send the response to the client.
       error? err = caller->send("Hi " + name + "! Greetings from gRPC service!");

       // After sending the response, call ‘complete()’ to indicate that the request was completely sent.
       _ = caller->complete();
   }
}
```
A proto file is automatically generated after this service is run. It is generated in the same folder as the service.

The sample given below calls the above service in a blocking manner using an auto-generated Ballerina client stub.

```ballerina
// Use ‘BlockingClient’ to execute the call in the blocking mode.
endpoint SamplegRPCServiceBlockingClient SamplegRPCServiceBlockingEp {
   host:"localhost",
   port:9090
};

// Create gRPC headers.
grpc:Headers headers = new;
headers.setEntry("id", "newrequest1");

// Call the method in the service using a client stub.
var responseFromServer = SamplegRPCServiceBlockingEp->receiveMessage("Ballerina", headers = headers);
match responseFromServer {
   // If a response is received, print the payload.
   (string, grpc:Headers) payload => {
       string result;
       grpc:Headers resHeaders;
       (result, resHeaders) = payload;
       io:println("Response received : " + result);
   }
   // If an error is returned, print the error message.
   error err => {
       io:println("Error while connecting grpc end-point : " + err.message);
   }
}
```
### Server Streaming
The sample given below shows a server streaming service.

```ballerina
// Server endpoint configuration.
endpoint grpc:Listener ep {
   host:"localhost",
   port:9090
};

service ServerStreaming bind ep {
   // Set the Streaming Annotation to ‘true’. It specifies that the resource is capable of sending multiple responses per request.
   @grpc:ResourceConfig {streaming:true}
   receiveMessage(endpoint caller, string name) {
       string[] greets = ["Hi", "Welcome"];
       io:println("HelloWorld");
       // Send multiple responses to the client.
       foreach greet in greets {
           error? err = caller->send(greet + " " + name + "! Greetings from Ballerina service");
           // If an error is returned, print the error message. print response message otherwise.
           io:println(err.message but { () => "send reply: " + greet + " " + name });
       }
       // Once the messages are sent to the server, call ‘complete()’ to indicate that the request was completely sent.
       _ = caller->complete();
   }
}
```
The sample given below calls the above service using the auto-generated Ballerina client stub and listens to the multiple responses from the server.

```ballerina
// Keep track of the message that were completely received.
boolean isCompleted = false;
function main (string... args) {
   // Client endpoint configurations.
    endpoint ServerStreamingClient serverStreamingEp {
       host: "localhost",
       port: 9090
    };

    // Execute the service streaming call by registering a message listener.
    error? result = serverStreamingEp->receiveMessage("test", ServerStreamingMessageListener);
    match result {
         // If the service returns an error, print the error.
        error payloadError => {
            io:println("Error occured while sending event " + payloadError.message);
        }
        () => {
            io:println("Connected successfully to service");
        }
    }
   // Waits for the service to send the message.
   while (!isCompleted) {}
}

// Define a listener service to receive the messages from the server.
service<grpc:Service> ServerStreamingMessageListener {
   // This resource method is invoked when the service receives a message.
   onMessage (string message) {
       io:println("Response received from server: " + message);
   }
   
   // This resource method is invoked if an error is returned.
   onError (error err) {
       if (err != ()) {
           io:println("Error reported from server: " + err.message);
       }
   }

   // Invoke this resource when the server sends all the responses to the request.
   onComplete () {
       isCompleted = true;
       io:println("Server Complete Sending Responses.");
   }
}
```
### Bidirectional Streaming

The sample given below includes a service that handles bidirectional streaming.

```ballerina
// Set the ‘clientStreaming’ and ‘serverStreaming’ to true. It specifies that the service supports bidirectional streaming.
@grpc:ServiceConfig {
    name: "chat",
    clientStreaming: true,
    serverStreaming: true
}
service Chat bind ep {
    // This resource method is invoked when there is a connection request from a client.
   onOpen(endpoint caller) {
       
   }
    // This resource method is invoked when the client sends a request.
   onMessage(endpoint caller, string message) {
   
   }
    // This resource method is invoked when the client returns an error while sending requests.
   onError(endpoint caller, error err) {
   
   }
    // This resource method is invoked when the client has finished sending requests.
   onComplete(endpoint caller) {
   
   }
}
```

The sample given below calls the above service.

```ballerina
endpoint grpc:Client ep;

// Call the relevant service.
var res = chatEp -> chat(ChatMessageListener);
match res {
   // If an error is returned while connecting to the service, print the error.
   error err => {
       io:print("error");
   }
   grpc:Client con => {
       ep = con;
   }
}

// Send multiple messages to the service.
error connErr = ep -> send(“Hello”);

// After sending the message, call ‘complete()’ to indicate that the request was completely sent. 
_ = ep -> complete();

```
