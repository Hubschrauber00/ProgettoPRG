import java.sql.*;
import javafx.collections.*;
import javafx.scene.chart.*;

/***
 * 
 * gestisce le comunicazioni con il database
 */
public class GestoreDB {

    private static GestoreDB singleton;
    private final String address;
    private final int port;
    private final String username;
    private final String password;
    private final String databaseName;

    private Connection conn;

    
    private GestoreDB(ParametriConfigurazioneXML config) {
        this.address = config.dbmsAddress;
        this.port = config.dbmsPort;
        this.username = config.dbmsUsername;
        this.password = config.dbmsPassword;
        this.databaseName = config.dbmsName;
    }
    /***
     * la classe implementa il paradigma singleton, crea l'istanza solo se non è già presente
     * @return restituisce l'istanza singleton
     */
    public static GestoreDB getInstance() {
        if (singleton == null) {
            singleton = new GestoreDB(ParametriConfigurazioneXML.getInstance());
        }
        return singleton;
    }
/***
 * effettua una connessione al database
 */
    private void connect() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(
                        "jdbc:mysql://" + address + ":" + port + "/"
                        + databaseName, username, password);
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
                conn = null;
            }
        }
    }
/***
 * chiude la connessione col database
 */
    private void disconnect() {
        try {
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            conn = null;
        }
    }
/***
 * inserisce un nuovo record nella tabella allenamento
 * @param sessione record da inserire
 */
    public void inserisciSessione(SessioneAllenamento sessione) {
        connect();
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO allenamento(username,esercizio,peso,ripetizioni,serie,data)"
                + "values(?,?,?,?,?,?)");) {
            ps.setString(1, sessione.getUsername());
            ps.setString(2, sessione.getEsercizio());
            ps.setInt(3, sessione.getPeso());
            ps.setInt(4, sessione.getRipetizioni());
            ps.setInt(5, sessione.getSerie());
            ps.setDate(6, new java.sql.Date(sessione.getData().getTime()));
            ps.executeUpdate();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        disconnect();
    }
/***
 * modifica la sessione desiderata 
 * @param sessione record da modificare
 */
    public void modificaSessione(SessioneAllenamento sessione) {
        connect();
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE allenamento SET username = ?, esercizio = ?, peso = ?,"
                + "ripetizioni = ?, serie = ?, data = ? WHERE id = ?");) {
            ps.setString(1, sessione.getUsername());
            ps.setString(2, sessione.getEsercizio());
            ps.setInt(3, sessione.getPeso());
            ps.setInt(4, sessione.getRipetizioni());
            ps.setInt(5, sessione.getSerie());
            ps.setDate(6, new java.sql.Date(sessione.getData().getTime()));
            ps.setInt(7, sessione.getId());
            ps.executeUpdate();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        disconnect();
    }
/***
 * 
 * @param sessione record da eliminare 
 */
    public void eliminaSessione(SessioneAllenamento sessione) {
        connect();
        try (PreparedStatement ps
                = conn.prepareStatement("DELETE FROM allenamento WHERE id = ?")) {
            ps.setInt(1, sessione.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        disconnect();
    }
/***
 * 
 * @param us filtro utente
 * @param es filtro esercizio
 * @param from filtro data da
 * @param to filtro data a
 * @return restituisce la lista dei record filtrata in base ai parametri
 */
    public ObservableList<SessioneAllenamento> getSessioni(String us, String es,
            java.sql.Date from, java.sql.Date to) {
        connect();
        ObservableList<SessioneAllenamento> ol = FXCollections.observableArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT* FROM allenamento ");
        if (!isNullOrEmpty(us) || !isNullOrEmpty(es) || from != null || to != null) {
            sb.append("WHERE ");
            if (!isNullOrEmpty(us)) {
                sb.append("username = '").append(us).append("'");
                if (!isNullOrEmpty(es) || from != null || to != null) {
                    sb.append(" AND ");
                }
            }
            if (!isNullOrEmpty(es)) {
                sb.append("esercizio = '").append(es).append("'");
                if (from != null || to != null) {
                    sb.append(" AND ");
                }
            }
            if (from != null) {
                sb.append("data >= ? ");
                if (to != null) {
                    sb.append("AND ");
                }
            }
            if (to != null) {
                sb.append("data <= ?");
            }
        }
        String statement = sb.toString();
        try (PreparedStatement ps = conn.prepareStatement(statement);) {
            if (from != null) {
                ps.setDate(1, from);
                if (to != null) {
                    ps.setDate(2, to);
                }
            } else if (from == null && to != null) {
                ps.setDate(1, to);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ol.add(new SessioneAllenamento(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("esercizio"),
                        rs.getInt("peso"),
                        rs.getInt("ripetizioni"),
                        rs.getInt("serie"),
                        rs.getDate("data")));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        disconnect();
        return ol;
    }
/***
 * 
 * @return restituisce una lista degli esercizi distinti presenti nel database 
 */
    public ObservableList<String> getEsercizi() {
        connect();
        ObservableList<String> ol = FXCollections.observableArrayList();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT DISTINCT esercizio FROM allenamento");) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ol.add(rs.getString("esercizio"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        disconnect();
        return ol;
    }
/***
 * 
 * @return restituisce una lista distinta di utenti registrati sul database 
 */
    public ObservableList<String> getUtenti() {
        connect();
        ObservableList<String> ol = FXCollections.observableArrayList();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT DISTINCT username FROM allenamento");) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ol.add(rs.getString("username"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        disconnect();
        return ol;
    }
/***
 * aggiunge i dati desiderati al grafico
 * @param series grafico a cui aggiungere i dati
 * @param us filtro utente
 * @param es filtro esercizio
 * @param from filtro data da
 * @param to filtro data a
 */
    public void getGrafico(XYChart.Series series, String us, String es,
            java.sql.Date from, java.sql.Date to) {
        connect();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT data, SUM(peso*ripetizioni*serie) as pesototale FROM allenamento ");
        if (!isNullOrEmpty(us) || !isNullOrEmpty(es) || from != null || to != null) {
            sb.append("WHERE ");
            if (!isNullOrEmpty(us)) {
                sb.append("username = '").append(us).append("'");
                if (!isNullOrEmpty(es) || from != null || to != null) {
                    sb.append(" AND ");
                }
            }
            if (!isNullOrEmpty(es)) {
                sb.append("esercizio = '").append(es).append("'");
                if (from != null || to != null) {
                    sb.append(" AND ");
                }
            }
            if (from != null) {
                sb.append("data >= ? ");
                if (to != null) {
                    sb.append("AND ");
                }
            }
            if (to != null) {
                sb.append("data <= ?");
            }
        }
        sb.append(" GROUP BY data ORDER BY data");
        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            if (from != null) {
                ps.setDate(1, from);
                if (to != null) {
                    ps.setDate(2, to);
                }
            } else if (from == null && to != null) {
                ps.setDate(1, to);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                series.getData().add(
                        new XYChart.Data<>(
                                rs.getDate("data").toString(),
                                rs.getInt("pesototale")
                        ));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        disconnect();
    }

    private boolean isNullOrEmpty(String ss) {
        return ss == null || ss.isEmpty();
    }

}
