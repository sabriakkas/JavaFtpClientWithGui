
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author akkas
 */
public class ActiveMode extends Thread {

    private Screen screen;
    private ServerSocket myServer;
    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;

    public ActiveMode(ServerSocket server) throws IOException {
        this.screen = new Screen();
        this.myServer = server;      
    }

    @Override
    public void run() {
        try {
            screen.setTitle("Active Mode On " + myServer.getLocalPort());
            screen.appendText("Ready To Read\n");
            socket = myServer.accept();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            String whatServerSays = "";
            while (true) {
                whatServerSays = in.readLine();
                if (whatServerSays == null) {
                    screen.appendText("Finished \n");
                    socket.close();
                    break;
                } else {
                    screen.appendText(whatServerSays + "\n");
                }
            }
        } catch (Exception e) {
            screen.appendText("Connection Error... \n");
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(PassiveMode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
