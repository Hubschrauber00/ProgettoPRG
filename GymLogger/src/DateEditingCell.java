import java.text.*;
import java.util.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
/***
 * questa classe viene utilizzata per ridefinire il comportamento di default 
 * delle celle di tableview in modo da permettere di poter editare dei valori
 * di tipo Date.
 * La maggior parte di questo codice è stato adattato da un esempio preso dalla pagina
 * https://docs.oracle.com/javafx/2/ui_controls/table-view.htm
 */
class DateEditingCell extends TableCell<SessioneAllenamento, Date> {
 
        private TextField textField;
 
        public DateEditingCell() {
        }
 
        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }
 
        @Override
        public void cancelEdit() {
            super.cancelEdit();
 
            setText((String) getItem().toString());
            setGraphic(null);
        }
 
        @Override
        public void updateItem(Date item, boolean empty) {
            super.updateItem(item, empty);
 
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    setText(formatter.format(item));
                    setGraphic(null);
                }
            }
        }
 
        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
            textField.focusedProperty()
                    .addListener((ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) -> {
                if (!arg2) {
                    try {
                        // quando viene tolto il focus dalla casella editabile
                        //fa il parsing del testo contenuto nella casella e lo salva 
                        //come data nel modello
                        commitEdit(new SimpleDateFormat("yyyy-MM-dd")
                                .parse(textField.getText()));
                    } catch (ParseException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            });
             textField.setOnKeyPressed((KeyEvent t) -> {
            if (t.getCode() == KeyCode.ENTER) {
                try {
                    //comportamento simile al caso precedente alla pressione del tasto invio
                    commitEdit(new SimpleDateFormat("yyyy-MM-dd")
                            .parse(textField.getText()));
                } catch (ParseException ex) {
                   System.err.println(ex.getMessage());
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                //la pressione del tasto esc annulla le modifiche
                cancelEdit(); 
            }
        });
        }
 
        private String getString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return getItem() == null ? "" : formatter.format(getItem());
        }
 }