import java.io.*;
import java.util.*;
import javafx.beans.property.*;
/***
 * classe bean che contiene tutti i dati del record 
 * e viene utilizzata per la comunicazione con il database
 * @author andre
 */
public class SessioneAllenamento implements Serializable{
    private SimpleIntegerProperty id;
    private SimpleStringProperty username;
    private SimpleStringProperty esercizio;
    private SimpleIntegerProperty peso;
    private SimpleIntegerProperty ripetizioni;
    private SimpleIntegerProperty serie;
    private ObjectProperty<Date> data;
    
    public SessioneAllenamento(){
        this(0,"","",0,0,0,new Date());
    }
    public SessioneAllenamento(
            int id, String user, String es, int peso, int rip, int serie, Date data){
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(user);
        this.esercizio = new SimpleStringProperty(es);
        this.peso = new SimpleIntegerProperty(peso);
        this.ripetizioni = new SimpleIntegerProperty(rip);
        this.serie = new SimpleIntegerProperty(serie);
        this.data = new SimpleObjectProperty<>(data);
    }
    public int getId(){
        return id.get();
    }
    public String getUsername(){
        return username.get();
    }
    public String getEsercizio(){
        return esercizio.get();
    }
    public int getPeso(){
        return peso.get();
    }
    public int getRipetizioni(){
        return ripetizioni.get();
    }
    public int getSerie(){
        return serie.get();
    }
    public Date getData(){
        return data.get();
    }
    
    public void setId(int id){
        this.id = new SimpleIntegerProperty(id);
    }
    public void setUsername(String user){
        this.username = new SimpleStringProperty(user);
    }
    public void setEsercizio(String es){
        this.esercizio = new SimpleStringProperty(es);
    }
    public void setPeso(int peso){
        this.peso = new SimpleIntegerProperty(peso);
    }
    public void setRipetizioni(int rip){
        this.ripetizioni = new SimpleIntegerProperty(rip);
    }
    public void setSerie(int serie){
        this.serie = new SimpleIntegerProperty(serie);
    }
    public void setData(Date data){
        this.data = new SimpleObjectProperty<>(data);
    }
    /***
     * 
     * @param ses istanza di sessione da confrontare
     * @return restituisce true se contengono gli stessi valori
     */
    public boolean equals(SessioneAllenamento ses){
        return this.id.get() == ses.id.get() &&
                this.username.get().equals(ses.username.get()) &&
                this.esercizio.get().equals(ses.esercizio.get()) &&
                this.peso.get()== ses.peso.get() &&
                this.ripetizioni.get() == ses.ripetizioni.get() &&
                this.serie.get() == ses.serie.get() &&
                this.data.get().getTime() == ses.data.get().getTime();
    }
}
