import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
/***
 * vedi doc DateEditingCell
 * stessa cosa ma per campi stringa
 */
public class StringEditingCell extends TableCell<SessioneAllenamento, String> {

    private TextField textField;

    public StringEditingCell() {
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

        setText((String) getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
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
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.focusedProperty()
                .addListener((ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) -> {
                    if (!arg2) {
                        commitEdit(textField.getText());
                    }
                });
        textField.setOnKeyPressed((KeyEvent t) -> {
            if (t.getCode() == KeyCode.ENTER) {
                commitEdit(textField.getText());
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit(); 
            }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem();
    }
}
