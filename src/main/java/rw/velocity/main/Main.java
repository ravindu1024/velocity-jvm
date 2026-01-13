package rw.velocity.main;

import rw.velocity.api.GsonDecoderFactory;
import rw.velocity.api.HttpException;
import rw.velocity.api.Velocity;
import rw.velocity.api.VelocityLogger;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class Main {
    public static class Post {
        public int userId;
        public String title;
        public String body;
    }

    public static void main(String[] args) {

        Velocity velocity = Velocity.newBuilder()
                .version(Velocity.HttpVersion.V2_PREFERRED)
                .logger(new VelocityLogger())
                .executor(Executors.newSingleThreadExecutor())
                .userAgentOverride("test/test")
                .decodeFactory(new GsonDecoderFactory())
                .build();

        var t = System.currentTimeMillis();

        try {

            //GET
            var t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    Post p = null;
                    try {
                        p = velocity
                                .get("https://jsonplaceholder.typicode.com/posts/1")
                                .request(Post.class);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (HttpException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("GET: " + p.title);
                }
            });
            t1.start();
            t1.join();


            //POST
            var r = velocity
                    .post("https://jsonplaceholder.typicode.com/posts")
                    .body("{\"body\":\"hello world\"}")
                    .request();

            System.out.println("POST: " + r.getBody());

            //PUT
            r = velocity
                    .put("https://jsonplaceholder.typicode.com/posts/1")
                    .body("{\"body\":\"hello world\"}")
                    .request();

            System.out.println("PUT: " + r.getBody());

            //DELETE
            r = velocity
                    .delete("https://jsonplaceholder.typicode.com/posts/1")
                    .request();

            System.out.println("DELETE: " + r.getBody());

            //PATCH
            r = velocity
                    .call("PATCH", "https://jsonplaceholder.typicode.com/posts/1")
                    .body("{\"body\":\"hello world\"}")
                    .request();

            System.out.println("PATCH: " + r.getBody());

            //HEAD
            r = velocity
                    .call("HEAD", "https://jsonplaceholder.typicode.com/posts/1")
                    .request();

            System.out.println("HEAD: " + r.getBody());

            //OPTIONS
            r = velocity
                    .call("OPTIONS", "https://jsonplaceholder.typicode.com/posts/1")
                    .request();

            System.out.println("OPTIONS: " + r.getBody());

        } catch (Throwable e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }

        System.out.println("total time: " + (System.currentTimeMillis() - t) + "ms");
    }
}
