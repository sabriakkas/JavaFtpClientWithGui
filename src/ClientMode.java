
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class ClientMode extends Thread {

    private Screen screen;
    private String host;
    private int port;
    private String userName;
    private String password;
    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;

    public ClientMode(String host, int port, String userName, String pass) throws IOException {
        this.screen = new Screen();
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = pass;
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            screen.setTitle("Working with " + host + " on port " + port);
            screen.addListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (screen.getInput().equals("port")) {
                            ServerSocket c = new ServerSocket(0); // 0 gives any available port for us
                            ActiveMode active = new ActiveMode(c);
                            active.start();
                            screen.appendText("Listening Port On My Computer Is " + c.getLocalPort() + "\n");
                            out.writeBytes("port "+getPortCommandString(c)+"\n");
                        } else {
                            screen.appendText("Me->" + screen.getInput() + "\n");
                            out.writeBytes(screen.getInput() + "\n");
                        }
                        screen.getInput_field().setText("");

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            screen.appendText(in.readLine() + "\n");
            out.writeBytes("user " + userName + "\n");
            out.flush();
            in.readLine();
            out.writeBytes("pass " + password + "\n");
            out.flush();
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

                    String code = getFtpCode(whatServerSays);
                    if (code.equals("227")) {
                        //it means time to start passive mode
                        PassiveMode passive = new PassiveMode(host, getConnectionPort(whatServerSays));
                        passive.start();

                    } else if (code.equals("221")) {
                        //it means quitted.
                        socket.close();
                        screen.dispose();
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            screen.appendText("Connection Closed By Server... \n");
            ex.printStackTrace();
        }finally{
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientMode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int getConnectionPort(String connectionMessageServerGives) {
        String x = connectionMessageServerGives.substring(connectionMessageServerGives.indexOf("("), connectionMessageServerGives.indexOf(")"));
        //get substring between ( and )
        String[] arr = x.split(","); //split it from ,
        return Integer.parseInt(arr[4]) * 256 + Integer.parseInt(arr[5]); //4 and 5 for port 
    }

    public String getFtpCode(String serverMessage) {
        //returns first 3 char as string
        return serverMessage.substring(0, Math.min(serverMessage.length(), 3));
    }

    public String getPortCommandString(ServerSocket openedServer) {
        String result = "";
        String myAdressToSend = socket.getLocalAddress().toString();
        myAdressToSend = myAdressToSend.substring(1);
        String arr[] = myAdressToSend.split("\\.");
        for (int i = 0; i < arr.length; i++) {
            result = result + arr[i] + ",";
        }
        return result + openedServer.getLocalPort() / 256 + "," + openedServer.getLocalPort() % 256;
    }
}

