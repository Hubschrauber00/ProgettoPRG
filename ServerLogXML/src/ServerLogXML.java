import com.thoughtworks.xstream.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class ServerLogXML {

    public static int serverPort;

    public static void main(String[] args) {
        if (args.length > 0) {
            serverPort = Integer.parseInt(args[0]);
        } else {
            serverPort = 8086;
        }
        try (ServerSocket serv = new ServerSocket(serverPort);) {
            while (true) {
                try (Socket sock = serv.accept();
                        DataInputStream dis = new DataInputStream(sock.getInputStream());) {
                    String xml = dis.readUTF();
                    Path schemaPath = Paths.get(
                            System.getProperty("user.dir") 
                                    + "/myFiles/eventSchema.xsd");
                    if (EventoXML.validate(xml, schemaPath)) {
                        EventoXML evento = (EventoXML) (new XStream()).fromXML(xml);
                        StringBuilder sb = new StringBuilder();
                        sb.append(evento.nome).append(" ");
                        sb.append(sock.getRemoteSocketAddress()).append(" ");
                        sb.append(evento.timestamp).append(" ");
                        sb.append(evento.etichetta).append('\n');
                        Path path = Paths.get(
                                System.getProperty("user.dir") 
                                        + "/myFiles/log.txt");
                        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
                            Files.createFile(path);
                        }
                        System.out.println(sb.toString());
                        Files.write(path, sb.toString().getBytes(),
                                StandardOpenOption.APPEND);
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
