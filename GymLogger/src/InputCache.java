import java.io.*;
import java.time.*;
/***
 * cache implementata come singleton, salva i valori temporanei 
 * in modo da renderli disponibili alla riaperturaa
 */
public class InputCache implements Serializable {

    private SessioneAllenamentoSerializable ses;
    private String es;
    private String us;
    private LocalDate from;
    private LocalDate to;
    private int index;

    private static InputCache singleton;

    private InputCache(SessioneAllenamento sessione,
            String es, String us, LocalDate from, LocalDate to, int row) {
         this.ses = null;
        if (sessione != null) {
            this.ses = new SessioneAllenamentoSerializable(sessione);
        }
        this.es = es;
        this.us = us;
        this.from = from;
        this.to = to;
        this.index = (row >= 0) ? row : 0;
    }
    private InputCache(){
        this(null,null,null,null,null,0);
    }
/***
 * 
 * @return restituisce l'istanza di cache, creata facendo il parsing del file binario 
 */
    public static InputCache getInstance() {
        if (singleton == null) {
            singleton = loadBinFile();
        }
        return singleton;
    }
    /***
     * crea una nuova istanza di cache a partire dai parametri
     * @param sessione record ancora da salvare
     * @param es contenuto filtro esercizio
     * @param us contenuto filtro utente
     * @param from contenuto filtro data da
     * @param to contenuto filtro data a
     * @param row riga su cui era presente focus
     * @return istanza di cache da salvare
     */
    public static InputCache getInstance(SessioneAllenamento sessione,
            String es, String us, LocalDate from, LocalDate to, int row){
        return new InputCache(sessione, es, us, from, to, row);
    }
    /***
     * salva il contenuto della cache su file binario
     * @param cache dati da salvare su file binario 
     */
    public static void salva(InputCache cache) {
        try (ObjectOutputStream oosf
                = new ObjectOutputStream(new FileOutputStream("./myFiles/cache.bin"))) {
            oosf.writeObject(cache);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
/***
 * restituisce un'istanza di cache ottenuta effettuando il parsing del file binario
 * @return  istanza di cache
 */
    private static InputCache loadBinFile() {
        InputCache cache = new InputCache();
        try (ObjectInputStream oisf
                = new ObjectInputStream(new FileInputStream("./myFiles/cache.bin"))) {
            cache = (InputCache) oisf.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
        return cache;
    }
    /***
     * 
     * @return sessione temporanea che era stata salvata nella cache 
     */
    public SessioneAllenamento getSessione() {
        SessioneAllenamento s = null;
        if (this.ses != null) {
            s = this.ses.toBean();
        }
        return s;
    }
/***
 * 
 * @return contenuto che era presente nel filtro esercizio alla chiusura 
 */
    public String getEsercizio() {
        return this.es;
    }
/***
 * 
 * @return contenuto che era presente nel filtro utente alla chiusura 
 */
    public String getUser() {
        return this.us;
    }
/***
 * 
 * @return contenuto che era presente nel filtro data da alla chiusura 
 */
    public LocalDate getDateFrom() {
        return this.from;
    }
/***
 * 
 * @return contenuto che era presente nel filtro data a alla chiusura 
 */
    public LocalDate getDateTo() {
        return this.to;
    }
/***
 * 
 * @return indice della riga selezionata alla chiusura 
 */
    public int getIndex() {
        return this.index;
    }
}
