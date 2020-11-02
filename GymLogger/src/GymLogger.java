import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;

public class GymLogger extends Application {

    private Stage stage;
    private Scene scene;
    private ParametriConfigurazioneXML config;
    private GestoreDB dbManager;
    private InterfacciaGymLogger interfaccia;
    private LogXML logger;
    private InputCache cache;

    @Override
    //metodo principale che inizializza l'applicazione
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        config = ParametriConfigurazioneXML.getInstance();
        dbManager = GestoreDB.getInstance();
        cache = InputCache.getInstance();
        logger = LogXML.getInstance(config.logServerAddress, config.logServerPort);
        interfaccia = new InterfacciaGymLogger();
        interfaccia.setFontLabels(config.font);
        this.scene = new Scene(interfaccia.getContainer(), config.windowSizeX, config.windowSizeY);
        this.stage.setOnCloseRequest((WindowEvent event) -> {
            InputCache.salva(interfaccia.getDataToCache());
            this.logger.send(new EventoXML("TERMINE"));
        });

        this.stage.setScene(scene);
        this.stage.setTitle("Gym Logger");
        this.stage.setMinHeight(config.windowSizeY);
        this.stage.setMinWidth(config.windowSizeX);

        this.stage.show();
    }

}
