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
 *
 * Reference: https://stackoverflow.com/questions/56481475/how-to-define-multiple-parameters-for-a-post-request-using-java-11-http-client
 * Answer by: https://stackoverflow.com/users/3523579/mikhail-kholodkov
 *
 * Added InputStream handling to original logic.
 */
public class MultiPartPublisher {

    static final String MULTIPART_BOUNDARY = "X-VELOCITY_JVM-BOUNDARY";

    public static HttpRequest.BodyPublisher ofMimeMultipartData(Map<Object, Object> data, @Nullable RequestBuilderImpl builder) throws IOException {
        // Result request body
        List<byte[]> byteArrays = new ArrayList<>();

        // Separator with boundary
        byte[] separator = ("--" + MULTIPART_BOUNDARY + "\r\nContent-Disposition: form-data; name=").getBytes(StandardCharsets.UTF_8);

        // Iterating over data parts
        for (Map.Entry<Object, Object> entry : data.entrySet()) {

            // Opening boundary
            byteArrays.add(separator);

            // If value is type of Path (file) append content type with file name and file binaries, otherwise simply append key=value
            if (entry.getValue() instanceof Path) {
                var path = (Path) entry.getValue();
                String mimeType = Files.probeContentType(path);
                byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName()
                        + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                byteArrays.add(Files.readAllBytes(path));
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            }else if (entry.getValue() instanceof InputStream){
                InputStream stream = (InputStream) entry.getValue();
                byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"stream"
                        + "\"\r\nContent-Type: " + "*/*" + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                byteArrays.add(stream.readAllBytes());
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            }
            else {
                byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n")
                        .getBytes(StandardCharsets.UTF_8));
            }
        }

        // Closing boundary
        byteArrays.add(("--" + MULTIPART_BOUNDARY + "--").getBytes(StandardCharsets.UTF_8));

        if(builder != null)
            builder.multiPartData = byteArrays;

        // Serializing as byte array
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }
}
