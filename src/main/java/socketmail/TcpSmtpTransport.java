package socketmail;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpSmtpTransport implements SmtpTransport {
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    @Override
    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        socket.setSoTimeout(5000);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
        System.out.println("Connected to " + host + ":" + port);
    }

    @Override
    public void startTls(String host, int port) throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = factory.createSocket(socket, host, port, true);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
    }

    @Override
    public void writeLine(String line) throws IOException {
        ensureConnected();
        writer.write(line);
        writer.write("\r\n");
        writer.flush();
    }
    @Override
    public String readLine() throws IOException {
        ensureConnected();
        return reader.readLine();
    }
    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
        if (reader != null) {
            reader.close();
        }
        if (socket != null) {
            socket.close();
        }
    }

    private void ensureConnected() {
        if (socket == null || socket.isClosed()) throw new IllegalStateException("Socket not connected");
    }

}
