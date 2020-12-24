## Velocity JVM

This is an extremely lightweight thin wrapper around the new HttpRequest classes introduced in Java 11. 
<br>The library supports the main 4 Http verbs: GET, POST, PUT and DELETE. It also supports basic and bearer 
authentication.

###Installation
I haven't uploaded this to Maven yet. You can clone the project or grab the jar file from the releases page.

###Usage

####Basic usage

``` java
var r = Velocity.get("https://jsonplaceholder.typicode.com/posts/1")
                .queryParam("key", "value")
                .request();
```

or Post with mutipart

``` java
File file = new File("path");
var r = Velocity.get("https://jsonplaceholder.typicode.com/posts/1")
                .body(file)
                .request();
                
var r = Velocity.get("https://jsonplaceholder.typicode.com/posts/1")
                .body(Map<String, String>)
                .contentType(ContentType.MULTIPART_FORM)
                .request();                
```

####Authentication

``` java
var r = Velocity.get("https://jsonplaceholder.typicode.com/posts/1")
                .authentication().basic("username", "password")
                .queryParam("key", "value")
                .request();
```

Improvements and suggestions are most welcome as long as it keeps the lib as a light thin wrapper.
