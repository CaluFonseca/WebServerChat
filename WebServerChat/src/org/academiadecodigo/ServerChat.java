package org.academiadecodigo;

import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerChat {

    private ServerSocket serverSocket;
    private List<ClientHandler> clients;

    public void start(int port) {
        try {
            //Starting the server socket
            serverSocket = new ServerSocket(port);
            System.out.println("Server online in port: " + port);

            clients = new ArrayList<>();

            //Waiting for clients to connect
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(clientSocket.getInetAddress().getHostName()
                + "...is online");

                //Creating a thread to deal with the clients connections

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Message error: " + e);
        } finally {
            try {
                if(serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Message error: " + e);
            }
        }
    }

    private class ClientHandler implements Runnable {

        private Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {

                // Starting reader and writer
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);

                String clientMessage;

                while ((clientMessage = reader.readLine()) != null) {

                    //Sending messages received to the connected clients

                    System.out.println(clientSocket.getInetAddress().getHostAddress()
                            + "say..." + clientMessage);
                    broadCastMessage(clientMessage, this);
                }
            } catch (IOException e) {
                System.out.println("Message error: " + e);
            } finally {
                try {
                    if(clientSocket != null) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    System.out.println("Message error:" + e);
                }
            }
        }

        public void sendMessage(String message) {
            writer.println(message);
        }
    }

    public void broadCastMessage(String message, ClientHandler sender) {
        for(ClientHandler clientHandler : clients) {
            if(clientHandler != sender) {
                clientHandler.sendMessage(message);
            }
        }
    }

    public static void main(String[] args) {
        ServerChat serverChat = new ServerChat();
        serverChat.start(8080);
    }
}
