import java.io.*;
import java.net.*;
/**
 * classe che si occupa di inviare i log al server, implementata come singleton
 */
public class LogXML {
    private final String serverAddr;
    private final int serverPort;
    
    private static LogXML singleton;
    
    private LogXML(String serverAddr, int serverPort){
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
    }
    
    public static LogXML getInstance(String serverAddr, int serverPort){
        if(singleton == null)
            singleton = new LogXML(serverAddr, serverPort);
        return singleton;
    }
    
    public void send( EventoXML evento ){
        try(Socket sock = new Socket(serverAddr, serverPort);
                DataOutputStream dos = 
                        new DataOutputStream(sock.getOutputStream());
                ){
            dos.writeUTF(evento.toString());
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
    }
}
