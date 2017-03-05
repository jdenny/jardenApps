package jarden.life.gui;

import java.util.Collections;
import java.util.List;

import jarden.life.CellData;
import jarden.life.CellEnvironment;
import jarden.life.CellShortData;
import jarden.life.NameCount;
import javafx.application.Application;
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

public class LifeFX extends Application {
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
    private TextField feederRateField;
    private TextField nucleotideFeedField;
    private TextField aminoAcidFeedField;
    private Text[] aminoAcidQtyTexts;
    private Text[] nucleotideQtyTexts;
    private CellData cellData;
    private CellEnvironment cellEnvironment;
    private Text liveCellCt;
    private Text deadCellCt;

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
    /*
    What do we want to see?
        count of all live cells; count of dead cells
        list of most recent 20 live cells; show id, generation, proteinCt
        refresh button for above
        setFeederRate(); setNucleotideFeedCt(); setAminoAcidFeedCt()
        select cell gets cell data
     */
    @Override
    public void start(Stage primaryStage) {
        /*
        feeder rate: <ct> aminoAcids: <ct> nucleotides: <ct> [set] [start] [stop]
        live cells: <ct> dead cells: <ct> [refresh]

        0                  1                  2           3
        0  Cells           Proteins           AminoAcids
        1  <cellListView>  <proteinListView>  <name>      <ct>
           ...             ...                ...

        22 ...             ...                Nucleotides
        23 ...             ...                <name>      <ct>
           ...             ...                ...
        28
        29 status

        (Fill button inserts quantities for 1 cell)
         */
        cellObservableList = FXCollections.observableArrayList();
        cellListView = new ListView<>(cellObservableList);
        cellListView.setCellFactory( (listView) -> new ColorRectCell());
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
        statusText.setFill(Color.FIREBRICK);
        feederRateField = new TextField();
        feederRateField.setPrefWidth(50);
        nucleotideFeedField = new TextField();
        nucleotideFeedField.setPrefWidth(50);
        aminoAcidFeedField = new TextField();
        aminoAcidFeedField.setPrefWidth(50);
        liveCellCt = new Text();
        deadCellCt = new Text();

        Button setButton = new Button("Set");
        setButton.setOnAction(event -> setFeederRates());
        Button startButton = new Button("Start");
        startButton.setOnAction(event -> cellEnvironment.startFeeding());
        Button stopButton = new Button("Stop");
        stopButton.setOnAction(event -> cellEnvironment.stopFeeding());
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refresh());

        BorderPane borderPane = new BorderPane();
        VBox controlVBox = new VBox();
        HBox feedRateHBox = new HBox();
        feedRateHBox.setPadding(new Insets(15, 12, 15, 12));
        feedRateHBox.setSpacing(10);
        HBox cellCtsHBox = new HBox();
        cellCtsHBox.setPadding(new Insets(15, 12, 15, 12));
        cellCtsHBox.setSpacing(10);

        feedRateHBox.getChildren().addAll(
                new Label("Feeder Rate:"),
                feederRateField,
                new Label("Amino Acids:"),
                aminoAcidFeedField,
                new Label("Nucleotides:"),
                nucleotideFeedField,
                setButton,
                startButton,
                stopButton);
        cellCtsHBox.getChildren().addAll(
                new Label("Live cells:"),
                liveCellCt,
                new Label("Dead cells:"),
                deadCellCt,
                refreshButton);
        controlVBox.getChildren().addAll(feedRateHBox, cellCtsHBox);
        borderPane.setTop(controlVBox);


        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(6);
        grid.setPadding(new Insets(25, 25, 25, 25));

        int aminoAcidCt = aminoAcidNames.length;
        int nucleotideCt = nucleotideNames.length;
        Label cellLabel = new Label("Cells");
        Font font = cellLabel.getFont();
        Font labelFont = Font.font(font.getFamily(), FontWeight.EXTRA_BOLD, font.getSize() + 4);
        cellLabel.setFont(labelFont);
        grid.add(cellLabel, 0, 0);
        grid.add(cellListView, 0, 1, 1, aminoAcidCt + nucleotideCt + 1);

        Label proteinLabel = new Label("Proteins");
        proteinLabel.setFont(labelFont);
        grid.add(proteinLabel, 1, 0);
        grid.add(proteinListView, 1, 1, 1, aminoAcidCt + nucleotideCt + 1);

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

        grid.add(statusText, 0, aminoAcidCt + nucleotideCt + 4, 5, 1);
        borderPane.setCenter(grid);

        Scene scene = new Scene(borderPane, 850, 700);
        primaryStage.setTitle("Life is complicated!");
        primaryStage.setScene(scene);
        primaryStage.show();
        boolean startLife = true;
        try {
            cellEnvironment = new CellEnvironment(startLife);
            initialiseFeederRates();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void refresh() {
        liveCellCt.setText(String.valueOf(cellEnvironment.getCellCount()));
        deadCellCt.setText(
                String.valueOf(cellEnvironment.getDeadCellCt()));
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
    }

    private void showProteinStatus() {
        System.out.println("not yet implemented!");
        /*?? TODO: fix this
        int proteinIndex = this.proteinListView.getSelectionModel().getSelectedIndex();
        if (proteinIndex >= 0) {
            Protein protein = (Protein) proteinObservableList.get(proteinIndex);
            statusText.setText("status of protein: " + protein.getStatus());
        }
        */
    }
    private void initialiseFeederRates() {
        feederRateField.setText(String.valueOf(cellEnvironment.getFeederRate()));
        nucleotideFeedField.setText(String.valueOf(cellEnvironment.getNucleotideFeedCt()));
        aminoAcidFeedField.setText(String.valueOf(cellEnvironment.getAminoAcidFeedCt()));
    }
    private void setFeederRates() {
        try {
            int feederRate = Integer.parseInt(feederRateField.getText());
            int nucleotideFeedCt = Integer.parseInt(nucleotideFeedField.getText());
            int aminoAcidFeedCt = Integer.parseInt(aminoAcidFeedField.getText());
            cellEnvironment.setFeederRate(feederRate);
            cellEnvironment.setAminoAcidFeedCt(aminoAcidFeedCt);
            cellEnvironment.setNucleotideFeedCt(nucleotideFeedCt);
        } catch (NumberFormatException nfe) {
            statusText.setText("supply integers only");
        }
    }
}
