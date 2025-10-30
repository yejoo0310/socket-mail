package socketmail;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;

public class Main {
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

        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;

        try{
            socket = new Socket(hostname, port);
            System.out.println("Connected to " + hostname + ":" + port);

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            writer = new PrintWriter(output, true);
            reader = new BufferedReader(new InputStreamReader(input));

            String line = readResponse(reader);
            checkResponse(line, "220", "Connection Failed");

            writer.println("EHLO " + hostname);
            line = readFullResponse(reader);
            checkResponse(line, "250", "EHLO failed");

            writer.println("STARTTLS");
            line = readResponse(reader);
            checkResponse(line, "220", "STARTTLS failed");

            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = factory.createSocket(socket, hostname, port, true);

            input = socket.getInputStream();
            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            reader = new BufferedReader(new InputStreamReader(input));

            System.out.println("TLS/SSL layer established");

            writer.println("EHLO " + hostname);
            line = readResponse(reader);
            checkResponse(line, "250", "second EHLO failed");

            try {
                while (reader.ready()) {
                    reader.readLine();
                }
            } catch (IOException ignored) {
            }

            writer.println("AUTH LOGIN");
            line = readResponse(reader);
            checkResponse(line, "334", "AUTH LOGIN failed");

            String encodedEmail = Base64.getEncoder().encodeToString(sender.getBytes());
            writer.println(encodedEmail);
            line = readResponse(reader);
            checkResponse(line, "334", "Username failed");

            String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
            writer.println(encodedPassword);
            line = readResponse(reader);
            checkResponse(line, "235", "Password failed");

            System.out.println("Authentication successful.");

            writer.println("MAIL FROM: <" + sender + ">");
            line = readResponse(reader);
            checkResponse(line, "250", "MAIL FROM failed");

            writer.println("RCPT TO: <" + receiver + ">");
            line = readResponse(reader);
            checkResponse(line, "250", "RCPT TO failed");

            writer.println("DATA");
            line = readResponse(reader);
            System.out.println(line);
            checkResponse(line, "354", "DATA command failed");

            writer.write("To: <" + receiver + ">\r\n");
            writer.write("From: <" + sender + ">\r\n");
            writer.write("Subject: SMTP project\r\n");
            writer.write("\r\n");
            writer.write(content + "\r\n");
            writer.write(".\r\n");
            writer.flush();

            line = readFullResponse(reader);
            checkResponse(line, "250", "DATA delivery failed");

            System.out.println("Email successfully sent!");

            writer.println("quit");

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + hostname);
        } catch (IOException e) {
            System.err.println("I/O exception: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unknown exception: " + e.getMessage());
        } finally {
            try {
                if (writer != null) writer.close();
                if (reader != null) reader.close();
                if (socket != null) socket.close();
                System.out.println("Connection closed.");
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    private static String readResponse(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line != null) {
            System.out.println("SERVER: " + line);
        }
        return line;
    }

    private static void checkResponse(String response, String expectedCode, String errorMessage) throws Exception {
        if (response == null || !response.startsWith(expectedCode)) {
            throw new Exception(errorMessage + " - Response: " + response);
        }
    }

    private static String readFullResponse(BufferedReader reader) throws IOException {
        StringBuilder fullResponse = new StringBuilder();
        String line;
        do {
            line = reader.readLine();
            if (line == null) {
                throw new IOException("Connection closed by server unexpectedly.");
            }
            System.out.println("SERVER: " + line);
            fullResponse.append(line).append("\n");
        } while (line.length() >= 4 && line.charAt(3) == '-');

        return fullResponse.substring(0, Math.min(3, fullResponse.length()));
    }
}