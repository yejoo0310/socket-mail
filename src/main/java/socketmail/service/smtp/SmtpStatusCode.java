package socketmail.service.smtp;

public enum SmtpStatusCode {
    SERVICE_READY(220), OK(250), AUTH_CONTINUE(334), AUTH_SUCCESS(235), START_MAIL_INPUT(354);

    private final int code;

    SmtpStatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
