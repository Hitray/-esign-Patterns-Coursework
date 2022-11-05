package ru.netology.javacore;

import com.google.gson.*;
import java.io.*;
import java.net.*;

public class TodoServer {
    private int port;
    private Todos todos;

    public TodoServer(int port, Todos todos) {
        this.port = port;
        this.todos = todos;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Starting server at " + port + "...");
            while (true) {
                try (Socket clientSocket = serverSocket.accept(); // ждем подключения
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
                    System.out.printf("New connection accepted. Port: %d%n", clientSocket.getPort());
                    while (true) {
                        String json = in.readLine();
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        JsonClient jsonClient = gson.fromJson(json, JsonClient.class);
                        if (jsonClient.type.equals("ADD")) {
                            todos.addTask(jsonClient.task);
                            out.println(todos.getAllTasks());
                            break;
                        }
                        if (jsonClient.type.equals("REMOVE")) {
                            todos.removeTask(jsonClient.task);
                            out.println(todos.getAllTasks());
                            break;
                        }
                    }
                }
            }
        } catch (Exception err) {
            System.out.println(err);
        } finally {
            System.out.println("Server closed!");
        }
    }
}
