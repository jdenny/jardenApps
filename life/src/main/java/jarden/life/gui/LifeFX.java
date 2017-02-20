package jarden.life.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jarden.life.Cell;
import jarden.life.Food;
import jarden.life.OnNewCellListener;
import jarden.life.Protein;
import jarden.life.aminoacid.AddAminoAcidToProtein;
import jarden.life.aminoacid.AminoAcid;
import jarden.life.aminoacid.DigestFood;
import jarden.life.aminoacid.DivideCell;
import jarden.life.aminoacid.FindNextGene;
import jarden.life.aminoacid.GetAminoAcidFromCodon;
import jarden.life.aminoacid.GetCodonFromRNA;
import jarden.life.aminoacid.GetRNAFromGene;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.Thymine;
import jarden.life.nucleicacid.Uracil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by john.denny@gmail.com on 15/02/2017.
 * Android Studio's embedded JDK doesn't include JavaFX, so use
 * File, Project Structure to change it; to find out location of
 * JDK on mac, in terminal window: echo $(/usr/libexec/java_home)
 * Current location is:
 * /Library/Java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home
 */

public class LifeFX extends Application implements EventHandler<ActionEvent>,
        OnNewCellListener {
    private Text statusText;
    private ListView<Cell> cellListView;
    private ObservableList<Cell> cellObservableList;
    private ListView<String> resourceListView;
    private ObservableList<String> resourceObservableList;
    private ChoiceBox<String> resourceTypeChoiceBox;
    private TextField uracilQtyField;
    private TextField cytosineQtyField;
    private TextField guanineQtyField;
    private TextField adenineQtyField;
    private TextField aminoAcidSetQtyField;
    private TextField cellQtyField;
    private List resourceList;

    private String[] aminoAcidNames = {
            "AddAminoAcidToProtein",
            "DigestFood", "DivideCell",
            "FindNextGene", "GetAminoAcidFromCodon",
            "GetCodonFromRNA", "GetRNAFromGene"
    };
    private String[] nucleotides = {
            "Uracil", "Adenine", "Cytosine", "Guanine"
    };
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
    @Override
    public void onNewCell(Cell cell) {
        Platform.runLater(() -> cellObservableList.add(cell));
    }
    static class ColorRectCell extends ListCell<Cell> {
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
        cellObservableList = FXCollections.observableArrayList();
        cellListView = new ListView<>(cellObservableList);
        cellListView.setCellFactory((ListView<Cell> l) -> new ColorRectCell());
        cellListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        resetResourceList());

        resourceTypeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                "Proteins", "AminoAcids", "Nucleotides")
        );
        resourceTypeChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        resetResourceList());
        resourceTypeChoiceBox.getSelectionModel().selectFirst();
        resourceObservableList = FXCollections.observableArrayList();
        resourceListView = new ListView<>(resourceObservableList);
        resourceListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showResourceStatus());

        statusText = new Text();
        statusText.setFill(Color.FIREBRICK);

        uracilQtyField = new TextField();
        cytosineQtyField = new TextField();
        guanineQtyField = new TextField();
        adenineQtyField = new TextField();
        aminoAcidSetQtyField = new TextField();
        cellQtyField = new TextField();
        Button feedButton = new Button("Feed");
        feedButton.setOnAction(this);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        GridPane feederGrid = new GridPane();
        feederGrid.setAlignment(Pos.CENTER);
        feederGrid.setHgap(10);
        feederGrid.setVgap(10);
        feederGrid.setPadding(new Insets(25, 25, 25, 25));

        feederGrid.add(new Label("Uracil"), 0, 0);
        feederGrid.add(uracilQtyField, 1, 0);
        feederGrid.add(new Label("Cytosine"), 0, 1);
        feederGrid.add(cytosineQtyField, 1, 1);
        feederGrid.add(new Label("Guanine"), 0, 2);
        feederGrid.add(guanineQtyField, 1, 2);
        feederGrid.add(new Label("Adenine"), 0, 3);
        feederGrid.add(adenineQtyField, 1, 3);
        feederGrid.add(new Label("Amino Acid Set"), 0, 4);
        feederGrid.add(aminoAcidSetQtyField, 1, 4);
        feederGrid.add(new Label("Cells"), 0, 5);
        feederGrid.add(cellQtyField, 1, 5);
        feederGrid.add(feedButton, 1, 6);

        grid.add(new Label("Cells"), 0, 0);
        grid.add(cellListView, 0, 1);

        grid.add(resourceTypeChoiceBox, 1, 0);
        grid.add(resourceListView, 1, 1);

        grid.add(feederGrid, 2, 0, 1, 2);

        grid.add(statusText, 0, 3, 3, 1);

        Scene scene = new Scene(grid, 900, 500);
        primaryStage.setTitle("Life is complicated!");
        primaryStage.setScene(scene);
        primaryStage.show();
        Cell syntheticCell = Cell.makeSyntheticCell(true);
        syntheticCell.setOnNewCellListener(this);
        cellObservableList.add(syntheticCell);
    }

    private void showResourceStatus() {
        int resourceIndex = this.resourceListView.getSelectionModel().getSelectedIndex();
        String resourceType = this.resourceTypeChoiceBox.getValue();
        if (resourceType.equals("Proteins")) {
            Protein protein = (Protein) resourceList.get(resourceIndex);
            statusText.setText("status of protein: " + protein.getStatus());
        } else {
            statusText.setText("resource waiting to be used to build proteins");
        }
    }

    private void resetResourceList() {
        Cell cell = this.cellListView.getSelectionModel().getSelectedItem();
        String resourceType = this.resourceTypeChoiceBox.getValue();
        String[] resourceNames;
        if (cell != null && resourceType != null) {
            if (resourceType.equals("Proteins")) {
                resourceList = cell.getProteinList();
                resourceNames = resourcesToNames();
            } else if (resourceType.equals("AminoAcids")) {
                resourceList = cell.getAminoAcidList();
                resourceNames = resourcesToNames();
            } else if (resourceType.equals("Nucleotides")) {
                List<Nucleotide> nucleotides = cell.getNucleotideList();
                // counts for Adenine, Cytosine, Guanine, Thymine, Uracil:
                int[] nucleotideCounts = new int[5];
                for (Nucleotide nucleotide: nucleotides) {
                    if (nucleotide instanceof Adenine) ++nucleotideCounts[0];
                    else if (nucleotide instanceof Cytosine) ++nucleotideCounts[1];
                    else if (nucleotide instanceof Guanine) ++nucleotideCounts[2];
                    else if (nucleotide instanceof Thymine) ++nucleotideCounts[3];
                    else if (nucleotide instanceof Uracil) ++nucleotideCounts[4];
                    else throw new IllegalStateException("unrecognised nucleotide: " +
                                nucleotide);
                }
                resourceNames = new String[] {
                        nucleotideCounts[0] + " Adenine",
                        nucleotideCounts[1] + " Cytosine",
                        nucleotideCounts[2] + " Guanine",
                        nucleotideCounts[3] + " Thymine",
                        nucleotideCounts[4] + " Uracil",
                };
            } else {
                throw new IllegalStateException(
                        "unrecognised resourceType: " + resourceType);
            }
            resourceObservableList.clear();
            Collections.addAll(resourceObservableList, resourceNames);
        }
    }
    private String[] resourcesToNames() {
        String[] names = new String[resourceList.size()];
        for (int i = 0; i < resourceList.size(); i++) {
            names[i] = resourceList.get(i).toString();
        }
        return names;
    }

    @Override
    public void handle(ActionEvent event) {
        String message = uracilQtyField.getText() + " of Uracil, " +
                adenineQtyField.getText() + " of Adenine, " +
                guanineQtyField.getText() + " of Guanine, " +
                cytosineQtyField.getText() + " of Cytosine, ";
        statusText.setText(message);
        Cell cell = this.cellListView.getSelectionModel().getSelectedItem();
        if (cell == null) {
            statusText.setText("select cell first");
        } else {
            List<Nucleotide> nucleotides = new ArrayList<>();
            try {
                String uracilQtyStr = uracilQtyField.getText().trim();
                if (uracilQtyStr.length() > 0) {
                    int uracilQty = Integer.parseInt(uracilQtyStr);
                    for (int i = 0; i < uracilQty; i++) {
                        nucleotides.add(new Uracil());
                    }
                }
                String adenineQtyStr = adenineQtyField.getText().trim();
                if (adenineQtyStr.length() > 0) {
                    int adenineQty = Integer.parseInt(adenineQtyStr);
                    for (int i = 0; i < adenineQty; i++) {
                        nucleotides.add(new Adenine());
                    }
                }
                String guanineQtyStr = guanineQtyField.getText().trim();
                if (guanineQtyStr.length() > 0) {
                    int guanineQty = Integer.parseInt(guanineQtyStr);
                    for (int i = 0; i < guanineQty; i++) {
                        nucleotides.add(new Guanine());
                    }
                }
                String cytosineQtyStr = cytosineQtyField.getText().trim();
                if (cytosineQtyStr.length() > 0) {
                    int cytosineQty = Integer.parseInt(cytosineQtyStr);
                    for (int i = 0; i < cytosineQty; i++) {
                        nucleotides.add(new Cytosine());
                    }
                }
                if (nucleotides.size() > 0) {
                    cell.addNucleotides(nucleotides);
                }
                // now for amino acids:
                List<AminoAcid> aminoAcids = new ArrayList<>();
                String aminoAcidSetQtyStr =
                        aminoAcidSetQtyField.getText().trim();
                if (aminoAcidSetQtyStr.length() > 0) {
                    int aminoAcidSetQty = Integer.parseInt(aminoAcidSetQtyStr);
                    for (int i = 0; i < aminoAcidSetQty; i++) {
                        aminoAcids.add(new AddAminoAcidToProtein());
                        aminoAcids.add(new DigestFood());
                        aminoAcids.add(new DivideCell());
                        aminoAcids.add(new FindNextGene());
                        aminoAcids.add(new GetAminoAcidFromCodon());
                        aminoAcids.add(new GetCodonFromRNA());
                        aminoAcids.add(new GetRNAFromGene());
                    }
                    cell.addAminoAcids(aminoAcids);
                }
                // now for the main course: cells
                String cellQtyStr = cellQtyField.getText().trim();
                List<Food> foodList = new ArrayList<>();
                if (cellQtyStr.length() > 0) {
                    int cellQty = Integer.parseInt(cellQtyStr);
                    for (int i = 0; i < cellQty; i++) {
                        // active=false, so cell doesn't use all its resources creating another cell
                        foodList.add(Cell.makeSyntheticCell(false));
                    }
                    cell.addFood(foodList);
                }
                statusText.setText("added to cell " + cell + ": " +
                        nucleotides.size() + " nucleotides; " +
                        aminoAcids.size() + " aminoAcids; " +
                        foodList.size() + " cells" );
            } catch (NumberFormatException nfe) {
                statusText.setText("supply integers only");
            }
        }
    }
}
