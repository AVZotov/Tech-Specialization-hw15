package ru.gb;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private final String name;

    public Client(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeConnection();
        }
    }

    public void sendMessage(){
        try {
            bufferedWriter.write(this.name);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            while (socket.isConnected()){
                Scanner scanner = new Scanner(System.in);
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(name + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (IOException e){
            closeConnection();
        }
    }

    public void messageListener(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromServer;

                while (socket.isConnected()){
                    try {
                        messageFromServer = bufferedReader.readLine();
                        System.out.println(messageFromServer);
                    } catch (IOException e){
                        closeConnection();
                    }
                }
            }
        }).start();
    }

    private void closeConnection() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
