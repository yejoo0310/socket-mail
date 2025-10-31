package socketmail;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Main {
    static SmtpTransport transport = new TcpSmtpTransport();
    static SmtpParser parser = new DefaultSmtpParser();

    public static void main(String[] args) {
        String hostname = ConfigManager.getProperty("smtp.host");
        String smtpPort = ConfigManager.getProperty("smtp.port");
        String sender = ConfigManager.getProperty("smtp.sender");
        String password = ConfigManager.getProperty("smtp.password");
        String receiver = ConfigManager.getProperty("smtp.receiver");
        String content = "I like hamburger and pizza.\n" +
                "Please give me.";

        int port;
        try {
            port = Integer.parseInt(smtpPort);
        } catch (NumberFormatException e) {
            System.err.println("Invalid smtp port number: " + smtpPort);
            port = 587;
        }

        try{
            transport.connect(hostname, port);
            checkResponse(parser.read(transport), 220, "Connection Failed");

            // EHLO: multi-line
            transport.writeLine("EHLO " + hostname);
            checkResponse(parser.read(transport), 250, "EHLO failed");

            transport.writeLine("STARTTLS");
            checkResponse(parser.read(transport), 220, "STARTTLS failed");
            transport.startTls(hostname, port);
            System.out.println("TLS/SSL layer established");

            // EHLO: multi-line
            transport.writeLine("EHLO " + hostname);
            checkResponse(parser.read(transport), 250, "EHLO failed");

            transport.writeLine("AUTH LOGIN");
            checkResponse(parser.read(transport), 334, "AUTH LOGIN failed");

            String encodedEmail = Base64.getEncoder().encodeToString(sender.getBytes(StandardCharsets.US_ASCII));
            transport.writeLine(encodedEmail);
            checkResponse(parser.read(transport), 334, "Username failed");

            String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.US_ASCII));
            transport.writeLine(encodedPassword);
            checkResponse(parser.read(transport), 235, "Password failed");

            System.out.println("Authentication successful.");

            transport.writeLine("MAIL FROM: <" + sender + ">");
            checkResponse(parser.read(transport), 250, "MAIL FROM failed");

            transport.writeLine("RCPT TO: <" + receiver + ">");
            checkResponse(parser.read(transport), 250, "RCPT failed");

            transport.writeLine("DATA");
            checkResponse(parser.read(transport), 354, "DATA command failed");

            transport.writeLine("To: <" + receiver + ">");
            transport.writeLine("From: <" + sender + ">");
            transport.writeLine("Subject: SMTP project");
            transport.writeLine("");

           for (String ln : content.split("\r?\n")) {
                transport.writeLine(ln);
           }
           transport.writeLine(".");

           checkResponse(parser.read(transport), 250, "DATA delivery failed");

           System.out.println("Email successfully sent!");

            transport.writeLine("QUIT");
            checkResponse(parser.read(transport), 221, "QUIT failed");
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + hostname);
        } catch (IOException e) {
            System.err.println("I/O exception: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unknown exception: " + e.getMessage());
        } finally {
            try {
                transport.close();
                System.out.println("Connection closed.");
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    private static void checkResponse(SmtpResponse response, int expectedCode, String errorMessage) throws Exception {
        if (response == null || response.code() != expectedCode) {
            if (response == null || response.lines().isEmpty()) {
                throw new Exception(errorMessage + "-" + "null");
            } else {
                throw new Exception(errorMessage + "-" + response.lines().get(0));
            }
        }
    }
}