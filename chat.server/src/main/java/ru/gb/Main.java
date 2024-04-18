package ru.gb;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5002)) {
            Server server = new Server(serverSocket);
            server.start();
            System.out.println("Server started...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}