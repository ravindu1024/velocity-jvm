package rw.velocity.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class RequestBuilderTests {

    private static class Data {
        String data;
        int id;
    }

    @Test
    void getMethod() throws IOException, InterruptedException {
        var testVelocity = new TestableVelocity(new VelocityImpl.BuilderImpl(), 200, "response");

        var response = testVelocity
                .get("http://localhost")
                .contentType("application/json")
                .header("header1", "val1")
                .header("header2", "val2")
                .request();

        Assertions.assertEquals(response.statusCode, 200);
        Assertions.assertEquals(response.body, "response");
        Assertions.assertEquals(testVelocity.request().method(), "GET");
        Assertions.assertEquals(testVelocity.request().headers().firstValue("header1").get(), "val1");
        Assertions.assertEquals(testVelocity.request().headers().firstValue("Content-Type").get(), "application/json");
    }

    @Test
    void getWithGsonNoDecodeFactory() throws IOException, InterruptedException, HttpException {
        var testVelocity = new TestableVelocity(new VelocityImpl.BuilderImpl(), 200, "{\"data\":\"abc\", \"id\":10}");

        try {
            var data = testVelocity
                    .get("http://localhost")
                    .request(Data.class);
            Assertions.assertTrue(false);
        } catch (Throwable th) {
            Assertions.assertTrue(th instanceof IllegalStateException);
        }

    }

    @Test
    void getWithGson() throws IOException, InterruptedException, HttpException {
        var builder = new VelocityImpl.BuilderImpl();
        builder.decodeFactory(new GsonDecoderFactory());

        var testVelocity = new TestableVelocity(builder, 200, "{\"data\":\"abc\", \"id\":10}");

        try {
            var data = testVelocity
                    .get("http://localhost")
                    .request(Data.class);
            Assertions.assertEquals(data.id, 10);
            Assertions.assertEquals(data.data, "abc");
        } catch (Throwable th) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    void getArrayWithGson() throws IOException, InterruptedException, HttpException {
        var builder = new VelocityImpl.BuilderImpl();
        builder.decodeFactory(new GsonDecoderFactory());

        var testVelocity = new TestableVelocity(builder, 200, "[{\"data\":\"abc\", \"id\":10}, {\"data\":\"xyz\", \"id\":12}]");

        try {
            var data = testVelocity
                    .get("http://localhost")
                    .request(Data[].class);
            Assertions.assertEquals(data[0].id, 10);
            Assertions.assertEquals(data[0].data, "abc");
            Assertions.assertEquals(data[1].id, 12);
            Assertions.assertEquals(data[1].data, "xyz");
        } catch (Throwable th) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    void getWithBasicAuth() throws IOException, InterruptedException {
        var builder = new VelocityImpl.BuilderImpl();
        var testVelocity = new TestableVelocity(builder, 200, "");

        var data = testVelocity
                .get("http://localhost")
                .authentication().basic("username", "password")
                .request();

        Assertions.assertEquals(testVelocity.request().headers().firstValue("Authorization").get(), "Basic dXNlcm5hbWU6cGFzc3dvcmQ=");
    }

    @Test
    void getWithBearerAuth() throws IOException, InterruptedException {
        var builder = new VelocityImpl.BuilderImpl();
        var testVelocity = new TestableVelocity(builder, 200, "");

        var data = testVelocity
                .get("http://localhost")
                .authentication().bearer("bearer_token")
                .request();

        Assertions.assertEquals(testVelocity.request().headers().firstValue("Authorization").get(), "Bearer bearer_token");
    }

    @Test
    void multiPartFormData() throws IOException, InterruptedException {

        var formData = new HashMap<Object, Object>();
        formData.put("param1", "value1");
        formData.put("param2", "value2");
        var bytes = new ArrayList<byte[]>();
        MultiPartPublisher.ofMimeMultipartData(formData, bytes);

        var strings = bytes.stream().map(String::new).collect(Collectors.toList());

        if(strings.isEmpty()){
            Assertions.assertTrue(false);
        }else{
            var strBuilder = new StringBuilder();
            strings.forEach(s -> strBuilder.append(s));
            Assertions.assertEquals(strBuilder.toString(), "--X-VELOCITY_JVM-BOUNDARY\r\n" +
                    "Content-Disposition: form-data; name=\"param1\"\r\n" +
                    "\r\n" +
                    "value1\r\n" +
                    "--X-VELOCITY_JVM-BOUNDARY\r\n" +
                    "Content-Disposition: form-data; name=\"param2\"\r\n" +
                    "\r\n" +
                    "value2\r\n" +
                    "--X-VELOCITY_JVM-BOUNDARY--");
        }
    }

}
