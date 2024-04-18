package ru.gb;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("FOR PRIVATE MESSAGES PLEASE USE FOLLOWING FORMAT: \"To:receiverName message\"");
        System.out.println("Enter you name: ");
        String name = scanner.nextLine();

        try (Socket socket = new Socket("localhost", 5002)){
            Client client = new Client(socket, name);
            client.messageListener();
            client.sendMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}