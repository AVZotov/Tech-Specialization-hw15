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
            broadcastMessage("Server: " + name + " has connected");
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
                broadcastMessage(incomingMessage);
            } catch (IOException e) {
                closeConnection();
                break;
            }
        }
    }

    private void broadcastMessage(String messageToSend) {
        for (ClientHandler client : clients){
            try {
                if (!client.name.equals(this.name)) {
                    client.bufferedWriter.write(messageToSend);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            } catch (IOException e){
                closeConnection();
            }
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
