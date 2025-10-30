package rw.velocity.api;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Custom BodyPublisher for multi-part since there is no native support for this.
 * <p>
 * Reference: <a href="https://stackoverflow.com/questions/56481475/how-to-define-multiple-parameters-for-a-post-request-using-java-11-http-client">...</a>
 * Answer by: <a href="https://stackoverflow.com/users/3523579/mikhail-kholodkov">...</a>
 * <p>
 * Added InputStream handling to original logic.
 */
class MultiPartPublisher {

    public static HttpRequest.BodyPublisher ofMimeMultipartData(
            Map<Object, Object> data,
            @Nullable ArrayList<byte[]> multipartData
    ) throws IOException {

        // Result request body
        List<byte[]> formDataBytes = new ArrayList<>();

        // Param separator
        byte[] separator = toBytes("--" + Constants.MULTIPART_BOUNDARY + "\r\nContent-Disposition: form-data; name=");


        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            // Opening boundary
            formDataBytes.add(separator);

            var key = entry.getKey();
            var value = entry.getValue();

            if (value instanceof Path) {
                var path = (Path) value;
                String mimeType = Files.probeContentType(path);
                String s =
                        "\"" + key + "\"; " + "filename=\"" + path.getFileName() + "\"\r\n" +
                                "Content-Type: " + mimeType + "\r\n\r\n";

                formDataBytes.add(toBytes(s));
                formDataBytes.add(Files.readAllBytes(path));
                formDataBytes.add(toBytes("\r\n"));
            } else if (value instanceof InputStream) {
                var stream = (InputStream) value;
                String s =
                        "\"" + key + "\"; filename=\"stream" + "\"\r\n" +
                                "Content-Type: " + "*/*" + "\r\n\r\n";

                formDataBytes.add(toBytes(s));
                formDataBytes.add(stream.readAllBytes());
                formDataBytes.add(toBytes("\r\n"));
            } else {
                String s = "\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n";
                formDataBytes.add(toBytes(s));
            }
        }

        // Closing boundary
        formDataBytes.add(toBytes("--" + Constants.MULTIPART_BOUNDARY + "--"));

        if (multipartData != null) {
            multipartData.addAll(formDataBytes);
        }

        // Serializing as byte array
        return HttpRequest.BodyPublishers.ofByteArrays(formDataBytes);
    }

    private static byte[] toBytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }
}
