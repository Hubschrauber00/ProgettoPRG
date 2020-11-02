import com.thoughtworks.xstream.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.xml.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;
import org.xml.sax.*;
/***
 * Classe che contiene i campi da inviare al server di log 
 */
public class EventoXML implements Serializable {

    public final String nome;
    public final String timestamp;
    public final String etichetta;

    public EventoXML(String etichetta) {
        this.nome = "GymLogger";
        this.timestamp = new Date().toString();
        this.etichetta = etichetta;
    }

    @Override

    /***
     *serializza la classe come un stringa in formato xml 
     */
    public String toString() {
        XStream xs = new XStream();
        return xs.toXML(this);
    }
    /***
     * 
     * @param xml documento xml da validare
     * @param schemaPath path dello schema xsd
     * @return 
     */
    public static boolean validate(String xml, Path schemaPath) {
        try {
            SchemaFactory sf
                    = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            //legge il file configSchema.xsd e lo interpreta come un xmlSchema
            Schema s = sf.newSchema(new StreamSource(new File(schemaPath.toString())));
            //interpreto la stringa ricevuta come testo xml
            //e ne effettuo il parsing in base allo schema
            s.newValidator().validate(new StreamSource(new StringReader(xml)));

        } catch (SAXException ex) {
            System.err.println("EventoXML non valido:" + ex.getMessage());
            return false;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return true;
    }
}
