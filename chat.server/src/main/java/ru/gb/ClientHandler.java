package ru.gb;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.name = bufferedReader.readLine();
            clients.add(this);
            broadcastMessage("Server: " + name + " has connected to chat");
        } catch (IOException e) {
            closeConnection();
        }
    }

    @Override
    public void run() {
        String incomingMessage;

        while (socket.isConnected()){
            try {
                incomingMessage = bufferedReader.readLine();
                messageHandler(incomingMessage);
            } catch (IOException e) {
                closeConnection();
                break;
            }
        }
    }

    private void broadcastMessage(String messageToSend) {
        for (ClientHandler client : clients){
            if (!client.name.equals(this.name)) messageSender(client, messageToSend);
        }
    }

    private void messageHandler(String incomingMessage){
        if (incomingMessage.substring(4).startsWith("To:")){
            String parsedMessage = incomingMessage.substring(7);
            int splitIndex = parsedMessage.indexOf(" ");
            String name = parsedMessage.substring(0, splitIndex);
            sendDirectMessage(name, incomingMessage);
        } else broadcastMessage(incomingMessage);
    }

    private void sendDirectMessage(String messageReceiverName, String message){
        for (ClientHandler client : clients){
            if (client.name.equals(messageReceiverName)){
                messageSender(client, message);
                return;
            }
        }

        messageSender(this, "Message receiver: " + messageReceiverName + " not found in chat!");
    }

    private void messageSender(ClientHandler clientHandler, String message){
        try {
            clientHandler.bufferedWriter.write(message);
            clientHandler.bufferedWriter.newLine();
            clientHandler.bufferedWriter.flush();
        } catch (IOException e){
            closeConnection();
        }
    }

    private void removeClient(){
        clients.remove(this);
        broadcastMessage("Server: " + name + " has left the chat");
    }

    private void closeConnection() {
        removeClient();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
