import java.util.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.*;
import javafx.scene.control.cell.*;
import javafx.scene.input.*;
import javafx.util.*;
/***
 * tabella in cui vengono presentati i record richiesti e permette di modificarli
 * @author andre
 */
public class TabellaAllenamenti extends TableView<SessioneAllenamento> {

    private SessioneAllenamento sessioneCorrente;

    public TabellaAllenamenti() {
        
        //crea gli header della colonna
        this.setEditable(true);
        TableColumn idCol = new TableColumn("Id");
        TableColumn userCol = new TableColumn("Username");
        TableColumn esCol = new TableColumn("Esercizio");
        TableColumn pesoCol = new TableColumn("Peso");
        TableColumn repCol = new TableColumn("Ripetizioni");
        TableColumn serieCol = new TableColumn("Serie");
        TableColumn dataCol = new TableColumn("Data");
        //aggiunge ad ogni colonna da rendere editabile il corrispondente editor
        bindColsToEditor(idCol, userCol, esCol, pesoCol, repCol, serieCol, dataCol);
        
        //aggiunge le colonne alla tabella
        this.getColumns()
                .addAll(idCol, userCol, esCol, pesoCol, repCol, serieCol, dataCol);
        //aggiunge i listener per i click del mouse
        bindMouseHandler();
       
    }

    private void bindColsToEditor(TableColumn idCol, 
            TableColumn userCol, TableColumn esCol, 
            TableColumn pesoCol, TableColumn repCol, 
            TableColumn serieCol, TableColumn dataCol) {
        //lega ogni colonna al proprio valore bean
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        esCol.setCellValueFactory(new PropertyValueFactory<>("esercizio"));
        pesoCol.setCellValueFactory(new PropertyValueFactory<>("peso"));
        repCol.setCellValueFactory(new PropertyValueFactory<>("ripetizioni"));
        serieCol.setCellValueFactory(new PropertyValueFactory<>("serie"));
        dataCol.setCellValueFactory(new PropertyValueFactory<>("data"));

        //callback che vengono richiamati quando la cella entra in editing mode
        Callback<TableColumn, TableCell> cellStringFactory
                = (TableColumn p) -> new StringEditingCell();
        Callback<TableColumn, TableCell> cellIntegerFactory
                = (TableColumn p) -> new IntegerEditingCell();
        Callback<TableColumn, TableCell> cellDateFactory
                = (TableColumn p) -> new DateEditingCell();
        
        userCol.setCellFactory(cellStringFactory);
        //comportamento da eseguire quando viene effettuato il commit nel textfield
        userCol.setOnEditCommit(
                new EventHandler<CellEditEvent<SessioneAllenamento, String>>() {
                    @Override
                    public void handle(CellEditEvent<SessioneAllenamento, String> t) {
                        ((SessioneAllenamento) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setUsername(t.getNewValue());
                    }
                }
        );

        esCol.setCellFactory(cellStringFactory);
        esCol.setOnEditCommit(
                new EventHandler<CellEditEvent<SessioneAllenamento, String>>() {
                    @Override
                    public void handle(CellEditEvent<SessioneAllenamento, String> t) {
                        ((SessioneAllenamento) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setEsercizio(t.getNewValue());
                    }
                }
        );

        pesoCol.setCellFactory(cellIntegerFactory);
        pesoCol.setOnEditCommit(
                new EventHandler<CellEditEvent<SessioneAllenamento, Integer>>() {
                    @Override
                    public void handle(CellEditEvent<SessioneAllenamento, Integer> t) {
                        ((SessioneAllenamento) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())).setPeso(t.getNewValue());
                    }
                }
        );

        repCol.setCellFactory(cellIntegerFactory);
        repCol.setOnEditCommit(
                new EventHandler<CellEditEvent<SessioneAllenamento, Integer>>() {
                    @Override
                    public void handle(CellEditEvent<SessioneAllenamento, Integer> t) {
                        ((SessioneAllenamento) t.getTableView().getItems().get(
                                t.getTablePosition().getRow()))
                        .setRipetizioni(t.getNewValue());
                    }
                }
        );
        serieCol.setCellFactory(cellIntegerFactory);
        serieCol.setOnEditCommit(
                new EventHandler<CellEditEvent<SessioneAllenamento, Integer>>() {
                    @Override
                    public void handle(CellEditEvent<SessioneAllenamento, Integer> t) {
                        ((SessioneAllenamento) t.getTableView().getItems().get(
                                t.getTablePosition().getRow()))
                        .setSerie(t.getNewValue());
                    }
                }
        );

        dataCol.setCellFactory(cellDateFactory);
        dataCol.setOnEditCommit(new EventHandler<CellEditEvent<SessioneAllenamento, Date>>() {
            @Override
            public void handle(CellEditEvent<SessioneAllenamento, Date> t) {
                ((SessioneAllenamento) t.getTableView().getItems().get(
                        t.getTablePosition().getRow()))
                        .setData(t.getNewValue());
            }
        });
    }
    
    //crea l'handler che definisce il comportamento da seguire al click del mouse per ogni riga della tabella
    private void bindMouseHandler(){
         EventHandler<MouseEvent> onClick = this::handleTableRowMouseClick;
        this.setRowFactory(param -> {
            TableRow<SessioneAllenamento> row = new TableRow<>();
            row.setOnMouseClicked(onClick);
            return row;
        });
    }
    /***
     * se viene eseguito un doppio click su una riga vuota viene creata una nuova riga che viene selezionata
     * se invece è un click singolo:
     * se la riga era già selezionata non fa niente
     * altrimenti seleziona la nuova riga
     * @param event click del mouse
     */
    private void handleTableRowMouseClick(MouseEvent event) {
        ParametriConfigurazioneXML config = ParametriConfigurazioneXML.getInstance();
        LogXML logger = LogXML.getInstance(config.logServerAddress, config.logServerPort);

        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            TableRow<Object> row = (TableRow<Object>) event.getSource();
            if (row.isEmpty() || row.getItem() == null) {
                SessioneAllenamento newses = new SessioneAllenamento();
                this.getItems().add(newses);
                int lastIndex = this.getItems().size() - 1;
                this.focus(lastIndex);
                logger.send(new EventoXML("NUOVA_RIGA"));
            }
        } else if (event.getButton().equals(MouseButton.PRIMARY)) {
            //questo evita che vengano persi i dati temporanei non ancora salvati sul db
            if (!((this.sessioneCorrente != null
                    && this.getSelectionModel().getSelectedItem() != null)
                    && this.sessioneCorrente.getId()
                    == this.getSelectionModel().getSelectedItem().getId())) {
                this.sessioneCorrente = this.getSelectionModel().getSelectedItem();
                logger.send(new EventoXML("SELEZIONE_RIGA"));
            }
        }
    }
    /***
     * 
     * @return sessione attualmente selezionata 
     */
    public SessioneAllenamento getSessioneCorrente() {
        return this.sessioneCorrente;
    }
/***
 * annulla l'eventuale selezionamento
 */
    public void resetSessioneCorrente() {
        this.sessioneCorrente = null;
    }
/***
 * ripristina gli eventuali dati non salvati dalla cache
 * @param ses valori salvati in cache
 * @param index indice della riga che era selezionata
 */
    public void restoreCachedElement(SessioneAllenamento ses, int index) {
        if (ses != null && index >= 0) {
            this.sessioneCorrente = ses;
            int size = this.getItems().size();
            if (index >= size) {
                this.getItems().add(ses);
            }
            if (!this.getItems().get(index).equals(ses)) {
                this.getItems().remove(index);
                this.getItems().add(index, ses);
            }
            this.focus(index);
        } else {
        }
    }
/***
 * seleziona la riga specificata
 * @param index riga da selezionare
 */
    private void focus(int index) {
        this.requestFocus();
        this.getSelectionModel().select(index);
        this.getFocusModel().focus(index);
    }

}
