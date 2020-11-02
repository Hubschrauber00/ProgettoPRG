import java.time.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
/***
 * 
 * @author andre
 * classe che realizza l'interfaccia dell'applicazione
 */
public class InterfacciaGymLogger {

    private final ParametriConfigurazioneXML config;
    private final GestoreDB dbManager;
    private final InputCache cache;
    private final LogXML logger;
    private final VBox vbox;
    private final HBox comboContainer;
    private final HBox dateContainer;
    private final HBox buttonContainer;
    private final Label label;
    private final TabellaAllenamenti tabella;
    private final Label etichettaUtente;
    private final Label etichettaEsercizio;
    private final ComboBox filtroEsercizio;
    private final ComboBox filtroUtente;
    private final Label etichettaDataDa;
    private final Label etichettaDataA;
    private final DatePicker filtroDataDa;
    private final DatePicker filtroDataA;
    private final Button salva;
    private final Button elimina;
    private final LineChart andamento;
/***
 * costruttore, inizializza l'interfaccia
 */
    public InterfacciaGymLogger() {
        config = ParametriConfigurazioneXML.getInstance();
        dbManager = GestoreDB.getInstance();
        cache = InputCache.getInstance();
        logger = LogXML.getInstance(config.logServerAddress, config.logServerPort);
        logger.send(new EventoXML("AVVIO"));

        //crea etichette
        label = new Label("Gym Logger");
        etichettaUtente = new Label("Utente");
        etichettaEsercizio = new Label("Esercizio");
        etichettaDataDa = new Label("Da");
        etichettaDataA = new Label("A");

        //inizializza componenti dell'interfaccia
        vbox = new VBox();
        comboContainer = new HBox();
        dateContainer = new HBox();
        buttonContainer = new HBox();
        tabella = new TabellaAllenamenti();
        filtroEsercizio = new ComboBox();
        filtroUtente = new ComboBox();
        filtroDataDa = createDatePicker(LocalDate.now()
                .minusDays(config.defaultDatePeriod), cache.getDateFrom());
        filtroDataA = createDatePicker(LocalDate.now(), cache.getDateTo());
        salva = createSaveButton();
        elimina = createDeleteButton();
        andamento = createChart();
        buildInterface();
        updateInterface(true);
        bindComboBoxEvent(filtroEsercizio);
        bindComboBoxEvent(filtroUtente);
        tabella.restoreCachedElement(cache.getSessione(), cache.getIndex());

    }
/***
 * restituisce una nuova istanza di cache da salvare su file binario
 * @return cache da salvare alla chiusura 
 */
    public InputCache getDataToCache() {
        return InputCache.getInstance(
                this.tabella.getSessioneCorrente(),
                (String) this.filtroEsercizio.getValue(),
                (String) this.filtroUtente.getValue(),
                this.filtroDataDa.getValue(),
                this.filtroDataA.getValue(),
                this.tabella.getSelectionModel().getFocusedIndex());
    }
/***
 * 
 * @return riferimento al contenitore dell'interfaccia 
 */
    public VBox getContainer() {
        return this.vbox;
    }
/***
 * aggiorna lo stato dell'interfaccia
 * @param cached specifica se selezionare il valore presente in cache o no
 */
    private void updateInterface(boolean cached) {
        if (cached) {
            updateComboBox(filtroEsercizio, dbManager.getEsercizi(), cache.getEsercizio());
            updateComboBox(filtroUtente, dbManager.getUtenti(), cache.getUser());
        } else {
            updateComboBox(filtroEsercizio, dbManager.getEsercizi(), "");
            updateComboBox(filtroUtente, dbManager.getUtenti(), "");
        }
        updateGraph();
        updateTable();
    }
/***
 * aggiorna il contenuto della tabella in base ai filtri selezionati
 */
    private void updateTable() {
        String user = (String)filtroUtente.getValue();
        String eser = (String)filtroEsercizio.getValue();
        java.sql.Date from = null;
        java.sql.Date to = null;
        if(filtroDataDa.getValue() != null){
            from = java.sql.Date.valueOf(filtroDataDa.getValue());
        }
        if(filtroDataA.getValue() != null)
            to = java.sql.Date.valueOf(filtroDataA.getValue());
        
        tabella.setItems(dbManager.getSessioni(user, eser, from,to));
    }
/***
 * aggiorna il grafico in base ai filtri selezionati
 */
    private void updateGraph() {
        andamento.getData().clear();
        XYChart.Series series = new XYChart.Series<>();
        series.setName("Totale Peso Sollevato");
        String user = (String)filtroUtente.getValue();
        String eser = (String)filtroEsercizio.getValue();
        java.sql.Date from = null;
        java.sql.Date to = null;
        if(filtroDataDa.getValue() != null){
            from = java.sql.Date.valueOf(filtroDataDa.getValue());
        }
        if(filtroDataA.getValue() != null)
            to = java.sql.Date.valueOf(filtroDataA.getValue());
        
        dbManager.getGrafico(series, user, eser, from, to);
        andamento.getData().add(series);
    }
/***
 * ricarica il contenuto del menu a tendina
 * @param filter menu da aggiornare
 * @param list valori da inserire
 * @param cached parametro salvato nella cache, se vuoto o nullo seleziona il valore di default
 */
    private void updateComboBox(ComboBox filter, ObservableList<String> list, String cached) {
        filter.getItems().clear();
        filter.getItems().add("");
        filter.getItems().addAll(list);
        if (cached != null && !cached.isEmpty()) {
            ObservableList<String> items = filter.getItems();
            for (String item : items) {
                if (item.equals(cached)) {
                    filter.getSelectionModel().select(item);
                }
            }
        } else {
            filter.getSelectionModel().selectFirst();
        }
    }
/***
 * lega al menu a tendina il comportamento da seguire quando viene selezionato un nuovo valore
 * @param filter 
 */
    private void bindComboBoxEvent(ComboBox filter) {
        filter.setOnAction((Event e) -> {
            updateTable();
            updateGraph();
            this.logger.send(new EventoXML("FILTRO_TENDINA"));
        });
    }
/**
 * crea un nuovo datepicker e vi lega il comportamento da seguire all'aggiornamento
 * @param date valore di default
 * @param cached valore contenuto nella cache
 * @return 
 */
    private DatePicker createDatePicker(LocalDate date, LocalDate cached) {
        DatePicker dp = new DatePicker();
        if (cached != null) {
            dp.setValue(cached);
        } else {
            dp.setValue(date);
        }

        dp.setOnAction((ActionEvent event) -> {
            updateTable();
            updateGraph();
            this.logger.send(new EventoXML("FILTRO_DATA"));
        });
        return dp;
    }
/***
 * imposta per ogni elemento il font desiderato
 * @param font stile da impostare
 */
    public void setFontLabels(String font) {
        label.setFont(new Font(font, 30));
        etichettaUtente.setFont(new Font(font, 16));
        etichettaEsercizio.setFont(new Font(font, 16));
        etichettaDataDa.setFont(new Font(font, 16));
        etichettaDataA.setFont(new Font(font, 16));
        salva.setFont(new Font(font, 12));
        elimina.setFont(new Font(font, 12));
        filtroUtente.setStyle("-fx-font: 14px \"" + font + "\";");
        filtroEsercizio.setStyle("-fx-font: 14px \"" + font + "\";");
        filtroDataDa.setStyle("-fx-font: 14px \"" + font + "\";");
        filtroDataA.setStyle("-fx-font: 14px \"" + font + "\";");
        tabella.setStyle("-fx-font: 14px \"" + font + "\";");
        andamento.setStyle("-fx-font: 10px \"" + font + "\";");
    }
/***
 * crea il bottone salva e vi assegna il comportamento da seguire al click
 * @return restituisce l'istanza di Button appena creata
 */
    private Button createSaveButton() {
        Button save = new Button("salva");
        save.setOnAction((ActionEvent e) -> {
            if (tabella.getSessioneCorrente().getId() != 0) {
                dbManager.modificaSessione(tabella.getSessioneCorrente());
            } else {
                dbManager.inserisciSessione(tabella.getSessioneCorrente());
            }
            updateInterface(false);
            logger.send(new EventoXML("SALVA"));
        });
        return save;
    }
/***
 * crea il bottone elimina e vi legail comportamento da seguire al click
 * @return istanza di Button appena creata
 */
    private Button createDeleteButton() {
        Button delete = new Button("elimina");

        delete.setOnAction((ActionEvent e) -> {
            dbManager.eliminaSessione(tabella.getSessioneCorrente());
            updateInterface(false);
            this.logger.send(new EventoXML("ELIMINA"));
        });
        return delete;
    }
/***
 * crea un nuovo grafico
 * @return restituisce un'istanza del grafico appena creato
 */
    private LineChart createChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Giorni");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("kg");
        LineChart chart = new LineChart(xAxis, yAxis);
        return chart;
    }

    /***
     * lega insieme tutti i componenenti per costruire l'interfaccia
     */
    private void buildInterface() {
        comboContainer.getChildren().addAll(
                etichettaUtente, filtroUtente,
                etichettaEsercizio, filtroEsercizio);
        comboContainer.setAlignment(Pos.CENTER);

        dateContainer.getChildren().addAll(
                etichettaDataDa, filtroDataDa,
                etichettaDataA, filtroDataA
        );
        buttonContainer.getChildren().addAll(salva, elimina);
        vbox.getChildren().addAll(label,
                comboContainer, dateContainer,
                tabella, buttonContainer, andamento);

        comboContainer.setSpacing(5d);
        dateContainer.setSpacing(5d);
        dateContainer.setAlignment(Pos.CENTER);

        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setSpacing(5d);

        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(10d);
        vbox.setBackground(new Background(
                new BackgroundFill(Paint.valueOf(config.background),
                        CornerRadii.EMPTY,
                        Insets.EMPTY)
        ));
    }
}
