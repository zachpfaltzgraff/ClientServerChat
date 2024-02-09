import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    static int PORT;
    static String IPADDRESS;
    public static void main(String[] args) {
        PORT = 12345;
        IPADDRESS = "localhost";
        try {
            Socket socket = new Socket(IPADDRESS, PORT);

            while(true) {
                Scanner otherInput = new Scanner(socket.getInputStream());
                Scanner closeInput = new Scanner(System.in);
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                String messageIn = otherInput.nextLine();
                System.out.println(socket.getInetAddress() + ": " + messageIn);

                String messageOut = closeInput.nextLine();
                System.out.println(socket.getLocalAddress() + ": " + messageOut);
                output.println(socket.getLocalAddress() + ": " + messageOut);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
