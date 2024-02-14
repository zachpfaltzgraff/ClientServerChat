import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatServer extends JFrame implements ActionListener {
    private JTextField hostInput;
    private static JTextArea display;
    private JButton sendButton;
    private JButton exitButton;
    private JPanel buttonPanel;
    private static Socket socket = null;
    private ReceiveWorker receiveWorker;

    public ChatServer() {
        hostInput = new JTextField(20);
        add(hostInput, BorderLayout.NORTH);
        display = new JTextArea(10, 15);
        display.setWrapStyleWord(true);
        display.setLineWrap(true);
        add(new JScrollPane(display), BorderLayout.CENTER);
        buttonPanel = new JPanel();
        sendButton = new JButton("Send Message");
        sendButton.addActionListener(this);
        buttonPanel.add(sendButton);
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);
        setTitle("Server");

        receiveWorker = new ReceiveWorker();
        receiveWorker.execute(); // Start the SwingWorker to receive messages
    }

    public static void main(String[] args) throws IOException {
        int PORT = 1234;
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server Started. Waiting for clients...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());
            socket = clientSocket;
        } catch (Exception e) {
            System.out.println(e);
        }

        SwingUtilities.invokeLater(() -> {
            ChatServer frame = new ChatServer();
            frame.setSize(400, 300);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == exitButton) {
            System.exit(0);
        } else if (event.getSource() == sendButton) {
            String sendMessage = hostInput.getText();
            if (!sendMessage.isEmpty()) {
                try {
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                    output.println(sendMessage);
                    display.append(InetAddress.getLocalHost().getHostName() + ": " + sendMessage  + "\n");
                    hostInput.setText("");
                } catch (IOException e) {
                    display.append("Error sending message: " + e.getMessage() + "\n");
                }
            }
        }
    }

    private static class ReceiveWorker extends SwingWorker<Void, String> {
        @Override
        protected Void doInBackground() throws Exception {
            try (Scanner scanner = new Scanner(socket.getInputStream())) {
                while (scanner.hasNextLine()) {
                    String message = scanner.nextLine();
                    display.append(InetAddress.getLocalHost().getHostName() + ": " + message + "\n");
                }
            } catch (IOException e) {
                System.out.println("Error receiving message: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void process(java.util.List<String> chunks) {
            for (String message : chunks) {
                display.append(message + "\n");
            }
        }
    }
}
