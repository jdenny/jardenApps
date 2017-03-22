package jarden.life.gui;

import java.util.Collections;
import java.util.List;

import jarden.life.CellData;
import jarden.life.CellEnvironment;
import jarden.life.CellListener;
import jarden.life.CellShortData;
import jarden.life.NameCount;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static jarden.life.CellData.aminoAcidNames;
import static jarden.life.CellData.nucleotideNames;

/**
 * Created by john.denny@gmail.com on 15/02/2017.
 * Android Studio's embedded JDK doesn't include JavaFX, so use
 * File, Project Structure to change it; to find out location of
 * JDK on mac, in terminal window: echo $(/usr/libexec/java_home)
 * Current location is:
 * /Library/Java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home
 */

public class LifeFX extends Application implements CellListener, CellEnvironment.FoodListener {
    // TODO: sliding scale from blue to green?
    private static Color[] generationColours = {
            Color.web("blue"),
            Color.web("green"),
            Color.web("yellow"),
            Color.web("orange"),
            Color.web("red")
    };

    private Text statusText;
    private ListView<CellShortData> cellListView;
    private ObservableList<CellShortData> cellObservableList;
    private ListView<String> proteinListView;
    private ObservableList<String> proteinObservableList;
    private TextField feedIntervalField;
    private Text foodCtText;
    private Text[] aminoAcidQtyTexts;
    private Text[] nucleotideQtyTexts;
    private Text rnaCtText;
    private CellData cellData;
    private CellEnvironment cellEnvironment;
    private Text liveCellCtText;
    private Text deadCellCtText;

    public static void main(String[] args) {
        System.out.println("hello LifeFX");
        launch(args);
    }
    private static class ColorRectCell extends ListCell<CellShortData> {
        @Override
        public void updateItem(CellShortData item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                int generation = item.generation;
                if (generation > generationColours.length) {
                    generation = generationColours.length;
                }
                Color colour = generationColours[generation - 1];
                Rectangle rect = new Rectangle(20 * item.proteinCt, 20);
                rect.setFill(colour);
                setGraphic(rect);
            }
        }
    }
    @Override
    public void start(Stage primaryStage) {
        /*
        feed interval (sec/10): <ct> [set] [start] [stop] Food store: <ct>
        live cells: <ct> dead cells: <ct> [refresh] [restart life]

        0                  1                  2           3
        0  Cells           Proteins           AminoAcids
        1  <cellListView>  <proteinListView>  <name>      <ct>
           ...             ...                ...

        22 ...             ...                Nucleotides
        23 ...             ...                <name>      <ct>
           ...             ...                ...
        28                                    RNA         <ct>
        29
        30 status
         */
        cellObservableList = FXCollections.observableArrayList();
        cellListView = new ListView<>(cellObservableList);
        cellListView.setCellFactory((listView) -> new ColorRectCell());
        cellListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        cellSelected(oldValue, newValue));

        proteinObservableList = FXCollections.observableArrayList();
        proteinListView = new ListView<>(proteinObservableList);
        proteinListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showProteinStatus());

        statusText = new Text();
        foodCtText = new Text();
        statusText.setFill(Color.FIREBRICK);
        feedIntervalField = new TextField();
        feedIntervalField.setPrefWidth(50);
        rnaCtText = new Text();
        liveCellCtText = new Text();
        deadCellCtText = new Text();

        Button setButton = new Button("Set");
        setButton.setOnAction(event -> setFeedInterval());
        Button startButton = new Button("Start");
        startButton.setOnAction(event -> cellEnvironment.startFeeding());
        Button stopButton = new Button("Stop");
        stopButton.setOnAction(event -> cellEnvironment.stopFeeding());
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refresh());
        Button restartLifeButton = new Button("Restart Life");
        restartLifeButton.setOnAction(event -> restart());

        BorderPane borderPane = new BorderPane();
        VBox controlVBox = new VBox();
        HBox feedIntervalHBox = new HBox();
        // padding means gap between nodes and edges of box:
        feedIntervalHBox.setPadding(new Insets(10, 10, 10, 10));
        feedIntervalHBox.setSpacing(10); // gap between nodes
        HBox cellCtsHBox = new HBox();
        cellCtsHBox.setPadding(new Insets(10, 10, 10, 10));
        cellCtsHBox.setSpacing(10);

        feedIntervalHBox.getChildren().addAll(
                new Label("Feed Interval (sec/10):"),
                feedIntervalField,
                setButton,
                startButton,
                stopButton,
                new Label("Food Store:"),
                foodCtText);
        cellCtsHBox.getChildren().addAll(
                new Label("Live cells:"),
                liveCellCtText,
                new Label("Dead cells:"),
                deadCellCtText,
                refreshButton,
                restartLifeButton);
        controlVBox.getChildren().addAll(feedIntervalHBox, cellCtsHBox);
        borderPane.setTop(controlVBox);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(6);
        grid.setPadding(new Insets(10, 10, 10, 10));

        int aminoAcidCt = aminoAcidNames.length;
        int nucleotideCt = nucleotideNames.length;
        Label cellLabel = new Label("Cells");
        Font font = cellLabel.getFont();
        Font labelFont = Font.font(font.getFamily(), FontWeight.EXTRA_BOLD, font.getSize() + 4);
        cellLabel.setFont(labelFont);
        grid.add(cellLabel, 0, 0);
        int cellListHeight = aminoAcidCt + nucleotideCt + 4;
        grid.add(cellListView, 0, 1, 1, cellListHeight);

        Label proteinLabel = new Label("Proteins");
        proteinLabel.setFont(labelFont);
        grid.add(proteinLabel, 1, 0);
        grid.add(proteinListView, 1, 1, 1, cellListHeight);

        Label aminoAcidLabel = new Label("Amino Acids");
        aminoAcidLabel.setFont(labelFont);
        grid.add(aminoAcidLabel, 2, 0);
        aminoAcidQtyTexts = new Text[aminoAcidCt];
        for (int i = 0; i < aminoAcidCt; i++) {
            grid.add(new Label(aminoAcidNames[i]), 2, 1 + i);
            aminoAcidQtyTexts[i] = new Text("0");
            grid.add(aminoAcidQtyTexts[i], 3, 1 + i);
        }

        nucleotideQtyTexts = new Text[nucleotideCt];
        Label nucleotideLabel = new Label("Nucleotides");
        nucleotideLabel.setFont(labelFont);
        grid.add(nucleotideLabel, 2, aminoAcidCt + 2);
        for (int i = 0; i < nucleotideCt; i++) {
            grid.add(new Label(nucleotideNames[i]), 2, aminoAcidCt + 3 + i);
            nucleotideQtyTexts[i] = new Text("0");
            grid.add(nucleotideQtyTexts[i], 3, aminoAcidCt + 3 + i);
        }
        Label rnaLabel = new Label("RNA");
        rnaLabel.setFont(labelFont);
        grid.add(rnaLabel, 2, aminoAcidCt + nucleotideCt + 4);
        grid.add(rnaCtText, 3, aminoAcidCt + nucleotideCt + 4);

        grid.add(statusText, 0, aminoAcidCt + nucleotideCt + 4, 5, 1);
        borderPane.setCenter(grid);

        Scene scene = new Scene(borderPane, 850, 700);
        primaryStage.setTitle("Life is complicated!");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            if (cellEnvironment != null) cellEnvironment.exit();
            Platform.exit();
        });
        primaryStage.show();
        restart();
    }
    @Override
    public void onCellCountChanged(int liveCellCt, int deadCellCt) {
        Platform.runLater(() -> refresh(liveCellCt, deadCellCt));
    }
    @Override
    public void onUpdateFood(int foodCount) {
        Platform.runLater(() -> foodCtText.setText(String.valueOf(foodCount)));
    }
    private void restart() {
        boolean startLife = true;
        try {
            cellEnvironment = new CellEnvironment(startLife);
            cellEnvironment.setCellListener(this);
            cellEnvironment.setFoodListener(this);
            initialiseFeedFields();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void refresh() {
        refresh(cellEnvironment.getCellCount(), cellEnvironment.getDeadCellCt());
    }
    private void refresh(int liveCellCt, int deadCellCt) {
        liveCellCtText.setText(String.valueOf(liveCellCt));
        deadCellCtText.setText(String.valueOf(deadCellCt));
        List<CellShortData> cellShortDataList = cellEnvironment.getCellShortDataList();
        cellObservableList.clear();
        cellObservableList.addAll(cellShortDataList);
    }
    private void cellSelected(CellShortData oldValue, CellShortData cellShortData) {
        System.out.println("cellSelected(" + cellShortData + ")");
        /*
        Note: cellShortData is static snapshot of data in cell, and so is not being
        changed in the cell's threads
         */
        if (cellShortData != null) {
            cellData = cellEnvironment.getCellData(cellShortData.id);
            showCellData(cellData);
        }
    }
    private void showCellData(CellData cellData) {
        int proteinCt = cellData.proteinNameCts.length;
        String[] proteinData = new String[proteinCt];
        for (int i = 0; i < proteinCt; i++) {
            NameCount proteinNameCount = cellData.proteinNameCts[i];
            proteinData[i] = proteinNameCount.count + " " + proteinNameCount.name;
        }
        proteinObservableList.clear();
        Collections.addAll(proteinObservableList, proteinData);

        int aminoAcidCt = cellData.aminoAcidCts.length;
        for (int i = 0; i < aminoAcidCt; i++) {
            aminoAcidQtyTexts[i].setText(String.valueOf(cellData.aminoAcidCts[i]));
        }

        int nucleotideCt = cellData.nucleotideCts.length;
        for (int i = 0; i < nucleotideCt; i++) {
            nucleotideQtyTexts[i].setText(String.valueOf(cellData.nucleotideCts[i]));
        }
        rnaCtText.setText(String.valueOf(cellData.rnaCt));
    }

    private void showProteinStatus() {
        System.out.println("not yet implemented!");
        /* TODO: proteinObservableList is type String, so cannot cast to protein
           perhaps pass state of all proteins in cellData/
        int proteinIndex = this.proteinListView.getSelectionModel().getSelectedIndex();
        if (proteinIndex >= 0) {
            Protein protein = (Protein) proteinObservableList.get(proteinIndex);
            statusText.setText("status of protein: " + protein.getState());
        }
        */
    }
    private void initialiseFeedFields() {
        feedIntervalField.setText(String.valueOf(cellEnvironment.getFeedInterval()));
    }
    private void setFeedInterval() {
        try {
            int feedInterval = Integer.parseInt(feedIntervalField.getText());
            cellEnvironment.setFeedInterval(feedInterval);
        } catch (NumberFormatException nfe) {
            statusText.setText("supply integers only");
        }
    }
}
