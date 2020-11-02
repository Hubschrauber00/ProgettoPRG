import java.io.*;
import java.util.*;
public class SessioneAllenamentoSerializable implements Serializable{
    private final int id;
    private final String username;
    private final String esercizio;
    private final int peso;
    private final int ripetizioni;
    private final int serie;
    private final long data;
    /*** 
     * classe di utilità che viene utilizzata dalla cache per serializzare
     * il record modificato ma non salvato
     * @param ses 
     */
    public SessioneAllenamentoSerializable(SessioneAllenamento ses){
        this.id = ses.getId();
        this.username= ses.getUsername();
        this.esercizio = ses.getEsercizio();
        this.peso = ses.getPeso();
        this.ripetizioni = ses.getRipetizioni();
        this.serie = ses.getSerie();
        this.data = ses.getData().getTime();
    }
    /***
     * 
     * @return istanza di SessioneAllenamento che contiene i dati che erano stati serializzati 
     */
    public SessioneAllenamento toBean(){
        return new SessioneAllenamento(
                this.id, 
                this.username, 
                this.esercizio, 
                this.peso, 
                this.ripetizioni, 
                this.serie,
                new Date(this.data)
        );
    }
}
