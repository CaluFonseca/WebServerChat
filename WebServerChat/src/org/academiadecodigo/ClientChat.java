package org.academiadecodigo;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientChat {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public void start(String serverAddress, int serverPort) {

        try {
            // Starting the client socket
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected...");

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            Thread receiveThread = new Thread(new ReceiveMessageRunnable());
            receiveThread.start();

            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            String userInput;

            while ((userInput = userInputReader.readLine()) != null) {
                writer.println(userInput);
            }

        } catch (IOException e) {
            System.out.println("Message error: " + e);

        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("Message error: " + e);
            }
        }
    }

        private class ReceiveMessageRunnable implements Runnable {

            @Override
            public void run() {
                try {
                    String message;

                    while ((message = reader.readLine()) != null) {
                        System.out.println("From server..." + message);
                    }
                } catch (IOException e) {
                    System.out.println("Message error: " + e);

                }
            }
        }



    public static void main(String[] args) {

        ClientChat client = new ClientChat();
        client.start("localhost", 8080);
    }
}

