package socketmail.service.smtp;

public enum SmtpCommand {
    EHLO("EHLO"), STARTTLS("STARTTLS"), AUTH_LOGIN("AUTH LOGIN"), MAIL_FROM("MAIL FROM"), RCPT_TO(
            "RCPT TO"), DATA("DATA"), QUIT("QUIT");

    private final String command;

    SmtpCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return command;
    }
}
