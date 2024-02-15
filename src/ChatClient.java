import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient extends JFrame implements ActionListener {
    private JTextField hostInput;
    private static JTextArea display;
    private JButton sendButton;
    private JButton exitButton;
    private JPanel buttonPanel;
    private static Socket socket = null;
    private final int PORT = 1234;
    private ReceiveWorker receiveWorker;

    public ChatClient() {
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
        setTitle("Client");

        receiveWorker = new ReceiveWorker();
        receiveWorker.execute();
    }

    public static void main(String[] args) throws IOException {
        String serverIP = "localhost";
        int PORT = 1234;
        try {
            socket = new Socket(serverIP, PORT);
        } catch (IOException e) {
            System.out.println("Connection to " + serverIP + " failed: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            ChatClient frame = new ChatClient();
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
                    display.append(InetAddress.getLocalHost().getHostName() + ": " + sendMessage + "\n");
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
    }
}
