package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
		
		// экземпляр нашего сервера
    private Server server;
		// исходящее сообщение
    private PrintWriter outMessage;
		// входящее собщение
    private Scanner inMessage;

    public int playerNumber;

    private static final String HOST = "localhost";
    private static final int PORT = 3443;
		// клиентский сокет
    private Socket clientSocket = null;
		// количество клиента в чате, статичное поле
    private static int clients_count = 0;

    public String clientName;

		// конструктор, который принимает клиентский сокет и сервер
    public ClientHandler(Socket socket, Server server) {
        try {
            playerNumber = clients_count;
            clients_count++;
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
		// Переопределяем метод run(), который вызывается когда
		// мы вызываем new Thread(client).start();
    @Override
    public void run() {
        try {
            while (true) {
								// сервер отправляет сообщение
                server.sendMessageToAllClients("Новый игрок вошёл в игру!");
                server.sendMessageToAllClients("Игроков: " + clients_count);

                if (clients_count == 2)
                    server.sendGameInfo();


                break;
            }

            while (true) {
                // Если от клиента пришло сообщение
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();

                    if (clientMessage.startsWith("turn")) {
                        String[] msgParts  = clientMessage.split(" ");
                        int row = Integer.parseInt(msgParts[1]);
                        int column = Integer.parseInt(msgParts[2]);

                        server.makeTurn(this, row, column);
                    }

										// если клиент отправляет данное сообщение, то цикл прерывается и 
										// клиент выходит из чата
                    if (clientMessage.equalsIgnoreCase("##session##end##")) {
                        break;
                    }
										// выводим в консоль сообщение (для теста)
                    System.out.println(clientMessage);
										// отправляем данное сообщение всем клиентам
                    server.sendMessageToAllClients(clientMessage);
                }
								// останавливаем выполнение потока на 100 мс
                Thread.sleep(100);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        finally {
            this.close();
        }
    }
		// отправляем сообщение
    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
		// клиент выходит из чата
    public void close() {
				// удаляем клиента из списка
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
    }
}
