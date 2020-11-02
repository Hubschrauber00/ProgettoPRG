import com.thoughtworks.xstream.*;
import java.io.*;
import java.nio.file.*;
import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;
import org.w3c.dom.*;
import org.xml.sax.*;
/***
 * classe che effettua il parsing del file xml di configurazione
 */
public class ParametriConfigurazioneXML {

    public final int windowSizeX;
    public final int windowSizeY;
    public final String font;
    public final String background;
    public final int defaultDatePeriod;
    public final String logServerAddress;
    public final int logServerPort;
    public final String dbmsAddress;
    public final int dbmsPort;
    public final String dbmsPassword;
    public final String dbmsUsername;
    public final String dbmsName;

    //istanza singleton
    private static ParametriConfigurazioneXML singleton;

    //costruttori, in realtà non vengono usati
    private ParametriConfigurazioneXML() {
        this(0, 0, "", "", 0, "", 0, "", 0, "", "", "");
    }
    
    private ParametriConfigurazioneXML(
            int wX, int wY, String f, String bg, int dd, String logAdd,
            int logPort, String dbmsAdd, int dbmsP,
            String dbmsPwd, String dbmsUser, String name) {
        windowSizeX = wX;
        windowSizeY = wY;
        font = f;
        background = bg;
        defaultDatePeriod = dd;
        logServerAddress = logAdd;
        logServerPort = logPort;
        dbmsAddress = dbmsAdd;
        dbmsPort = dbmsP;
        dbmsPassword = dbmsPwd;
        dbmsUsername = dbmsUser;
        dbmsName = name;
    }

    //valida il file xml secondo l'xml schema
    private static boolean valida() {
        try {
            DocumentBuilder db
                    = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            SchemaFactory sf
                    = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Document d = db.parse(
                    new File(System.getProperty("user.dir")
                            + "./myFiles/config.xml")
            );
            Schema s = sf.newSchema(
                    new StreamSource(
                            new File(
                                    System.getProperty("user.dir")
                                    + "./myFiles/configSchema.xsd")
                    )
            );
            s.newValidator().validate(new DOMSource(d));

        } catch (Exception ex) {
            if (ex instanceof SAXException) {
                System.err.println("config.xml non valido:" + ex.getMessage());
                return false;
            }
            System.err.println(ex.getMessage());
        }
        return true;
    }

    //restituisce un riferimento all'istanza singleton, se non è presente la crea
    public static ParametriConfigurazioneXML getInstance() {
        if (singleton == null) {
            try {
                singleton = parseConfigFile();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
        return singleton;
    }

    //valida il file, se è corretto ne effettua il parsing 
    private static ParametriConfigurazioneXML parseConfigFile()
            throws IOException {
        if (valida()) {
            XStream xs = new XStream();
            String x = new String(
                    Files.readAllBytes(Paths.get(System.getProperty("user.dir")
                                    + "./myFiles/config.xml"))
            );
            return (ParametriConfigurazioneXML) xs.fromXML(x);
        }
        return null;
    }
}
