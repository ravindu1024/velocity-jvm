package rw.velocity.api;

public class HttpException extends Throwable{

    public final int status;
    public final Response response;


    public HttpException(int status, Response response) {
        super("Http exception: " + status);
        this.status = status;
        this.response = response;
    }

    public HttpException(int status, String message) {
        super(message);
        this.status = status;
        this.response = null;
    }
}
