
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class PassiveMode extends Thread {

    private Screen screen;
    private String host;
    private int port;
    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;

    public PassiveMode(String host, int port) throws IOException {
        this.screen = new Screen();
        this.host = host;
        this.port = port;
        this.socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try { 
            screen.setTitle("Passive Mode For "+host+" on "+port);
            String whatServerSays = "";
            screen.appendText("Ready To Read\n");
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
            screen.appendText("Connection Closed By Server... \n");
            e.printStackTrace();
        }finally{
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(PassiveMode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
