import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatServer {
    static int PORT;
    public static void main(String[] args) {
        PORT = 12345;

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server Started. Waiting for clients...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: "+ clientSocket.getInetAddress());

            while(true) {
                Scanner otherInput = new Scanner(clientSocket.getInputStream());
                Scanner closeInput = new Scanner(System.in);
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

                String messageOut = closeInput.nextLine();
                output.println(clientSocket.getLocalAddress() + ": " + messageOut);

                String messageIn = otherInput.nextLine();
                System.out.println(messageIn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
