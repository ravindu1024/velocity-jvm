package rw.velocity.main;

import rw.velocity.api.Velocity;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {

        Velocity.enableLogging(true);
        Velocity.setGlobalTimeout(30);

        try {

            //GET
            var r = Velocity.get("https://jsonplaceholder.typicode.com/posts/1")
                    .request();

            System.out.println("GET: " + r.getBody());

            //POST
            r = Velocity.post("https://jsonplaceholder.typicode.com/posts")
                    .body("{\"body\":\"hello world\"}")
                    .request();

            System.out.println("POST: " + r.getBody());

            //PUT
            r = Velocity.put("https://jsonplaceholder.typicode.com/posts/1")
                    .body("{\"body\":\"hello world\"}")
                    .request();

            System.out.println("PUT: " + r.getBody());

            //DELETE
            r = Velocity.delete("https://jsonplaceholder.typicode.com/posts/1")
                    .request();

            System.out.println("DELETE: " + r.getBody());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
