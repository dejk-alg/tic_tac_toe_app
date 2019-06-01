package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientWindow extends JFrame {
    // адрес сервера
    private static final String SERVER_HOST = "localhost";
    // порт
    private static final int SERVER_PORT = 3443;
    // клиентский сокет
    private Socket clientSocket;
    // входящее сообщение
    private Scanner inMessage;
    // исходящее сообщение
    private PrintWriter outMessage;
    // следующие поля отвечают за элементы формы

    private JSpinner jsRow;
    private JSpinner jsColumn;

    private JTextField jtfMessage;
    private JTextField jtfName;
    private JTextArea jtaTextAreaMessage;
    // имя клиента
    private String clientName = "";
    // получаем имя клиента
    public String getClientName() {
        return this.clientName;
    }

    // конструктор
    public ClientWindow() {
        try {
            // подключаемся к серверу
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Задаём настройки элементов на форме
        setBounds(600, 300, 600, 650);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jtaTextAreaMessage = new JTextArea(20, 50);
        jtaTextAreaMessage.setEditable(false);

        jtaTextAreaMessage.setFont(new Font("Serif", Font.ITALIC, 16));

        //jtaTextAreaMessage.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
        add(jsp, BorderLayout.CENTER);
        // label, который будет отражать количество клиентов в чате
        JLabel jlNumberOfClients = new JLabel("Количество игроков в игре: ");
        add(jlNumberOfClients, BorderLayout.NORTH);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);

        JPanel columnPanel = new JPanel(new BorderLayout());
        columnPanel.setPreferredSize(new Dimension(600, 120));
        bottomPanel.add(columnPanel, BorderLayout.NORTH);

        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setPreferredSize(new Dimension(600, 50));
        columnPanel.add(rowPanel, BorderLayout.NORTH);

        JButton jbSendMessage = new JButton("Отправить");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        jtfMessage = new JTextField("Введите ваше сообщение: ");
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);
        jtfName = new JTextField("Введите ваше имя: ");
        bottomPanel.add(jtfName, BorderLayout.WEST);

        SpinnerModel valueModelRow = new SpinnerNumberModel(1, //initial value
                1, //min
                3, //max
                1);                //step

        SpinnerModel valueModelColumn = new SpinnerNumberModel(1, //initial value
                1, //min
                3, //max
                1);


        jsRow = new JSpinner(valueModelRow);
        jsRow.setPreferredSize(new Dimension(200, 40));
        JLabel jLabelRow = new JLabel("Ряд");
        jLabelRow.setPreferredSize(new Dimension(200, 40));
        rowPanel.add(jLabelRow, BorderLayout.WEST);
        jLabelRow.setLabelFor(jsRow);
        rowPanel.add(jsRow, BorderLayout.CENTER);

        jsColumn = new JSpinner(valueModelColumn);
        jsColumn.setPreferredSize(new Dimension(200, 40));
        JLabel jColumnRow = new JLabel("Столбец");
        jColumnRow.setPreferredSize(new Dimension(200, 40));
        columnPanel.add(jColumnRow, BorderLayout.WEST);
        jColumnRow.setLabelFor(jsColumn);
        columnPanel.add(jsColumn, BorderLayout.CENTER);

        JButton makeTurn = new JButton("Сделать ход");
        columnPanel.add(makeTurn, BorderLayout.SOUTH);


        // обработчик события нажатия кнопки отправки сообщения
        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // если имя клиента, и сообщение непустые, то отправляем сообщение
                if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
                    clientName = jtfName.getText();
                    sendMsg();
                    // фокус на текстовое поле с сообщением
                    jtfMessage.grabFocus();
                }
            }
        });


        makeTurn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendTurnMsg();
            }
        });

        // при фокусе поле сообщения очищается
        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });
        // при фокусе поле имя очищается
        jtfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfName.setText("");
            }
        });
        // в отдельном потоке начинаем работу с сервером
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // бесконечный цикл
                    while (true) {
                        // если есть входящее сообщение
                        if (inMessage.hasNext()) {
                            // считываем его
                            String inMes = inMessage.nextLine();
                            String clientsInChat = "Игроков в игре = ";
                            if (inMes.indexOf(clientsInChat) == 0) {
                                jlNumberOfClients.setText(inMes);
                            } else {
                                // выводим сообщение
                                jtaTextAreaMessage.append(inMes);
                                // добавляем строку перехода
                                jtaTextAreaMessage.append("\n");
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();
        // добавляем обработчик события закрытия окна клиентского приложения
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    // здесь проверяем, что имя клиента непустое и не равно значению по умолчанию
                    if (!clientName.isEmpty() && clientName != "Введите ваше имя: ") {
                        outMessage.println(clientName + " вышел из чата!");
                    } else {
                        outMessage.println("Участник вышел из чата, так и не представившись!");
                    }
                    // отправляем служебное сообщение, которое является признаком того, что клиент вышел из чата
                    outMessage.println("##session##end##");
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                } catch (IOException exc) {

                }
            }
        });
        // отображаем форму
        setVisible(true);
    }

    private String constructTurnString(int row, int column) {
        return "turn " + row + " " + column;
    }

    public void sendTurnMsg() {
        try {
            jsRow.commitEdit();
        } catch ( java.text.ParseException exc ) { exc.printStackTrace(); }

        int row = (Integer)jsRow.getValue();

        try {
            jsColumn.commitEdit();
        } catch ( java.text.ParseException exc ) { exc.printStackTrace(); }

        int column = (Integer)jsColumn.getValue();

        String turnString = constructTurnString(row, column);

        outMessage.println(turnString);
        outMessage.flush();
    }

    // отправка сообщения
    public void sendMsg() {
        // формируем сообщение для отправки на сервер
        String messageStr = jtfName.getText() + ": " + jtfMessage.getText();
        // отправляем сообщение
        outMessage.println(messageStr);
        outMessage.flush();
        jtfMessage.setText("");
    }
}

