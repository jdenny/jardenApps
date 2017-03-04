package jarden.life.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jarden.life.Cell;
import jarden.life.CellData;
import jarden.life.CellEnvironment;
import jarden.life.CellFood;
import jarden.life.NameCount;
import jarden.life.aminoacid.AminoAcid;
import jarden.life.nucleicacid.Nucleotide;
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
import javafx.scene.layout.GridPane;
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
    private Text statusText;
    private ListView<Cell> cellListView;
    private ObservableList<Cell> cellObservableList;
    private ListView<String> proteinListView;
    private ObservableList<String> proteinObservableList;
    private Text[] aminoAcidQtyTexts;
    private TextField[] aminoAcidQtyFields;
    private Text[] nucleotideQtyTexts;
    private TextField[] nucleotideQtyFields;
    private CellData cellData;
    private CellEnvironment cellEnvironment;

    // TODO: sliding scale from blue to green?
    private static Color[] generationColours = {
            Color.web("blue"),
            Color.web("green"),
            Color.web("yellow"),
            Color.web("orange"),
            Color.web("red")
    };

    public static void main(String[] args) {
        System.out.println("hello LifeFX");
        launch(args);
    }
    private static class ColorRectCell extends ListCell<Cell> {
        @Override
        public void updateItem(Cell item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                int generation = item.getGeneration();
                if (generation > generationColours.length) {
                    generation = generationColours.length;
                }
                Color colour = generationColours[generation - 1];
                Rectangle rect = new Rectangle(20 * item.getProteinCt(), 20);
                rect.setFill(colour);
                setGraphic(rect);
            }
        }
    }
    @Override
    public void start(Stage primaryStage) {
        /*
        0                  1                  2           3      4
        0  Cells           Proteins           AminoAcids  [Fill] [Clear]
        1  <cellListView>  <proteinListView>  <name>      <ct>   <addCt>
           ...             ...                ...

        22 ...             ...                Nucleotides
        23 ...             ...                <name>      <ct>   <addCt>
           ...             ...                ...
        27                                                       [Feed]
        28 status

        (Fill button inserts quantities for 1 cell)
         */
        cellObservableList = FXCollections.observableArrayList();
        cellListView = new ListView<>(cellObservableList);
        cellListView.setCellFactory( (listView) -> new ColorRectCell());
        cellListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> cellSelected(oldValue, newValue));

        proteinObservableList = FXCollections.observableArrayList();
        proteinListView = new ListView<>(proteinObservableList);
        proteinListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showProteinStatus());

        statusText = new Text();
        statusText.setFill(Color.FIREBRICK);

        Button fillButton = new Button("Fill");
        fillButton.setOnAction(event -> {
            for (TextField aminoAcidQtyField: aminoAcidQtyFields) {
                aminoAcidQtyField.setText("1");
            }
            for (int i = 0; i < nucleotideQtyFields.length; i++) {
                nucleotideQtyFields[i].setText("2");
            }
        });
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> {
            for (TextField aminoAcidQtyField: aminoAcidQtyFields) {
                aminoAcidQtyField.setText("");
            }
            for (TextField nucleotideQtyField: nucleotideQtyFields) {
                nucleotideQtyField.setText("");
            }
        });
        Button feedButton = new Button("Feed");
        feedButton.setOnAction(event -> {
            try {
                feedCell();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

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
        grid.add(fillButton, 3, 0);
        grid.add(clearButton, 4, 0);
        aminoAcidQtyTexts = new Text[aminoAcidCt];
        aminoAcidQtyFields = new TextField[aminoAcidCt];
        for (int i = 0; i < aminoAcidCt; i++) {
            grid.add(new Label(aminoAcidNames[i]), 2, 1 + i);
            aminoAcidQtyTexts[i] = new Text("0");
            grid.add(aminoAcidQtyTexts[i], 3, 1 + i);
            aminoAcidQtyFields[i] = new TextField();
            aminoAcidQtyFields[i].setPrefWidth(50);
            grid.add(aminoAcidQtyFields[i], 4, 1 + i);
        }

        nucleotideQtyTexts = new Text[nucleotideCt];
        nucleotideQtyFields = new TextField[nucleotideCt];
        Label nucleotideLabel = new Label("Nucleotides");
        nucleotideLabel.setFont(labelFont);
        grid.add(nucleotideLabel, 2, aminoAcidCt + 2);
        for (int i = 0; i < nucleotideCt; i++) {
            grid.add(new Label(nucleotideNames[i]), 2, aminoAcidCt + 3 + i);
            nucleotideQtyTexts[i] = new Text("0");
            grid.add(nucleotideQtyTexts[i], 3, aminoAcidCt + 3 + i);
            nucleotideQtyFields[i] = new TextField();
            nucleotideQtyFields[i].setPrefWidth(20);
            grid.add(nucleotideQtyFields[i], 4, aminoAcidCt + 3 + i);
        }
        grid.add(feedButton, 4, aminoAcidCt + nucleotideCt + 3);

        grid.add(statusText, 0, aminoAcidCt + nucleotideCt + 4, 5, 1);

        Scene scene = new Scene(grid, 850, 700);
        primaryStage.setTitle("Life is complicated!");
        primaryStage.setScene(scene);
        primaryStage.show();
        Cell syntheticCell = null;
        cellEnvironment = new CellEnvironment();
        try {
            syntheticCell = Cell.makeSyntheticCell(cellEnvironment);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void addCell(Cell cell) {
        cellObservableList.add(cell);
    }
    private void cellSelected(Cell oldValue, Cell cell) {
        System.out.println("cellSelected(" + cell + ")");
        /*
        Note: cellData is static snapshot of data in cell, and so is not being
        changed in the cell's threads
         */
        /*
        TODO: we always want to be informed of new cells, but
        do we want to monitor changes to every cell?
        If so, we need to ensure we only act on changes for currently
        selected row; if not we need two separate interfaces for
        OnNewCellListener & OnChangedCellListener
        if (oldValue != null) {
            oldValue.setCellListener(null); // stop listening for changes
        }
        This is my proposal: monitor changes on every cell; if currently
        selected cell changes, then change all relevant fields on UI; if
        not currently selected, just change the size of the cell in cellListView
        this means that we don't need to keep passing the listener to
        getCellData().
         */
        if (cell != null) {
            cellData = cell.getCellData();
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
    // TODO: replace this with CellEnvironment.setFeederRate()
    private void feedCell() throws InterruptedException {
        StringBuilder messageBuilder = new StringBuilder();
        Cell cell = this.cellListView.getSelectionModel().getSelectedItem();
        if (cell == null) {
            statusText.setText("select cell first");
        } else {
            List<Nucleotide> nucleotides = new ArrayList<>();
            List<AminoAcid> aminoAcids = new ArrayList<>();
            try {
                // nucleotides:
                int nucleotideCt = nucleotideNames.length;
                for (int i = 0; i < nucleotideCt; i++) {
                    String nucleotideQtyStr = nucleotideQtyFields[i].getText().trim();
                    if (nucleotideQtyStr.length() > 0) {
                        int nucleotideQty = Integer.parseInt(nucleotideQtyStr);
                        for (int j = 0; j < nucleotideQty; j++) {
                            nucleotides.add(CellFood.makeNucleotide(nucleotideNames[i]));
                        }
                        messageBuilder.append(nucleotideQtyStr + " of " +
                                nucleotideNames[i] + ", ");
                    }
                }
                if (nucleotides.size() > 0) {
                    cell.addNucleotides(nucleotides);
                }
                // now for amino acids:
                int aminoAcidCt = aminoAcidNames.length;
                for (int i = 0; i < aminoAcidCt; i++) {
                    String aminoAcidQtyStr = aminoAcidQtyFields[i].getText().trim();
                    if (aminoAcidQtyStr.length() > 0) {
                        int aminoAcidQty = Integer.parseInt(aminoAcidQtyStr);
                        for (int j = 0; j < aminoAcidQty; j++) {
                            aminoAcids.add(CellFood.makeAminoAcid(aminoAcidNames[i]));
                        }
                        messageBuilder.append(aminoAcidQtyStr + " of " +
                                aminoAcidNames[i] + ", ");
                    }
                }
                if (aminoAcids.size() > 0) {
                    cell.addAminoAcids(aminoAcids);
                }
                System.out.println(messageBuilder.toString());

                statusText.setText("added to cell " + cell + ": " +
                        nucleotides.size() + " nucleotides; " +
                        aminoAcids.size() + " aminoAcids");
            } catch (NumberFormatException nfe) {
                statusText.setText("supply integers only");
            }
        }
    }
}
