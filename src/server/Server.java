package server;

import game.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
		// порт, который будет прослушивать наш сервер
    static final int PORT = 3443;
		// список клиентов, которые будут подключаться к серверу
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    private int getClientsAmount() {
        return clients.size();
    }

    private Game game;

    public Server() {
				// сокет клиента, это некий поток, который будет подключаться к серверу
				// по адресу и порту
        game = new Game();

        Socket clientSocket = null;
				// серверный сокет
        ServerSocket serverSocket = null;
        try {
						// создаём серверный сокет на определенном порту
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен!");
						// запускаем бесконечный цикл
            while (true) {
								// таким образом ждём подключений от сервера
                clientSocket = serverSocket.accept();
								// создаём обработчик клиента, который подключился к серверу
								// this - это наш сервер
                if (getClientsAmount() < 2) {
                    ClientHandler client = new ClientHandler(clientSocket, this);
                    clients.add(client);
                    // каждое подключение клиента обрабатываем в новом потоке
                    new Thread(client).start();
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
								// закрываем подключение
                clientSocket.close();
                System.out.println("Сервер остановлен");
                serverSocket.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
		
		// отправляем сообщение всем клиентам
    public void sendMessageToAllClients(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }

    }

    public int getPlayer() {
        return game.getPlayer();
    }

    public void sendGameInfo() {
        String msg = game.outputField();

        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public void makeTurn(ClientHandler client, int row, int column) {
        int playerNumber = client.playerNumber;

        if (client.playerNumber != getPlayer()) {
            client.sendMsg("Ожидание хода противника");
            return;
        }
        boolean result = game.makeTurn(client.playerNumber, row-1, column-1);
        sendGameInfo();
        if (result) {
            sendMessageToAllClients("Player " + playerNumber + " won");
            game = new Game();
        }
    }

		// удаляем клиента из коллекции при выходе из чата
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

}
