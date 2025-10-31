package socketmail;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Main {
    static SmtpTransport transport = new TcpSmtpTransport();

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
            String line = transport.readLine();
            checkResponse(line, "220", "Connection Failed");

            // EHLO: multi-line
            transport.writeLine("EHLO " + hostname);
            line = transport.readLine();
            checkResponse(line, "250", "EHLO failed");
            while (line.length() >= 4 && line.charAt(3) == '-') {
                line = transport.readLine();
                checkResponse(line, "250", "EHLO failed");
            }

            transport.writeLine("STARTTLS");
            line = transport.readLine();
            checkResponse(line, "220", "STARTTLS failed");
            transport.startTls(hostname, port);
            System.out.println("TLS/SSL layer established");

            // EHLO: multi-line
            transport.writeLine("EHLO " + hostname);
            line = transport.readLine();
            checkResponse(line, "250", "second EHLO failed");
            while (line.length() >= 4 && line.charAt(3) == '-') {
                line = transport.readLine();
                checkResponse(line, "250", "EHLO failed");
            }

            transport.writeLine("AUTH LOGIN");
            line = transport.readLine();
            checkResponse(line, "334", "AUTH LOGIN failed");

            String encodedEmail = Base64.getEncoder().encodeToString(sender.getBytes(StandardCharsets.US_ASCII));
            transport.writeLine(encodedEmail);
            line = transport.readLine();
            checkResponse(line, "334", "Username failed");

            String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.US_ASCII));
            transport.writeLine(encodedPassword);
            line = transport.readLine();
            checkResponse(line, "235", "Password failed");

            System.out.println("Authentication successful.");

            transport.writeLine("MAIL FROM: <" + sender + ">");
            line = transport.readLine();
            checkResponse(line, "250", "MAIL FROM failed");

            transport.writeLine("RCPT TO: <" + receiver + ">");
            line = transport.readLine();
            checkResponse(line, "250", "RCPT TO failed");

            transport.writeLine("DATA");
            line = transport.readLine();
            checkResponse(line, "354", "DATA command failed");

            transport.writeLine("To: <" + receiver + ">");
            transport.writeLine("From: <" + sender + ">");
            transport.writeLine("Subject: SMTP project");
            transport.writeLine("");

           for (String ln : content.split("\r?\n")) {
                transport.writeLine(ln); // (dot-stuffing은 다음 단계에서 Composer로)
           }
           transport.writeLine(".");

           line = transport.readLine();
           checkResponse(line, "250", "DATA delivery failed");
           while (line.length() >= 4 && line.charAt(3) == '-') {
               line = transport.readLine();
               checkResponse(line, "250", "DATA delivery failed");
           }

           System.out.println("Email successfully sent!");

            transport.writeLine("QUIT");
            line = transport.readLine();
            checkResponse(line, "221", "QUIT failed");
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

    private static void checkResponse(String response, String expectedCode, String errorMessage) throws Exception {
        if (response == null || !response.startsWith(expectedCode)) {
            throw new Exception(errorMessage + " - Response: " + response);
        }
    }
}