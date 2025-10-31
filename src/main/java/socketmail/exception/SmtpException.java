package socketmail.exception;

import socketmail.model.SmtpResponse;

import java.io.IOException;

public class SmtpException extends IOException {
    private final SmtpResponse response;

    public SmtpException(String message, SmtpResponse response) {
        super(message + ": " + response.toString());
        this.response = response;
    }

    public SmtpResponse getResponse() {
        return response;
    }
}
