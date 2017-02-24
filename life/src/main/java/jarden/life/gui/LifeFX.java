package jarden.life.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import jarden.life.Cell;
import jarden.life.CellData;
import jarden.life.CellFood;
import jarden.life.Food;
import jarden.life.CellListener;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static jarden.life.Cell.nucleotidesFor1Cell;
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

public class LifeFX extends Application implements /*!!EventHandler<ActionEvent>,*/
        CellListener {
    private Text statusText;
    private ListView<Cell/*!!KeyFields*/> cellListView;
    private ObservableList<Cell/*!!KeyFields*/> cellObservableList;
    private ListView<String> proteinListView; //!! resourceListView;
    private ObservableList<String> proteinObservableList; //!! resourceObservableList;
    /*!!
    private ChoiceBox<String> resourceTypeChoiceBox;
    private TextField adenineQtyField;
    private TextField cytosineQtyField;
    private TextField guanineQtyField;
    private TextField thymineQtyField;
    private TextField uracilQtyField;
      */
    private Text[] aminoAcidQtyTexts;
    private TextField[] aminoAcidQtyFields;
    private Text[] nucleotideQtyTexts;
    private TextField[] nucleotideQtyFields;
    //!! private TextField aminoAcidSetQtyField;
    //!! private TextField cellQtyField;
    private List resourceList;

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
    @Override
    public void onNewCell(Cell cell) {
        Platform.runLater(() -> addCell(cell));
    }
    @Override
    public void onCellUpdated(CellData cellData) {
        Platform.runLater(() -> {
            /*??
            MultipleSelectionModel<Cell> selectionModel =
                    this.cellListView.getSelectionModel();
            int selectedIndex = selectionModel.getSelectedIndex();
            cellObservableList.remove(selectedIndex);
            cellObservableList.add(selectedIndex, cell);
            */

        });
        System.out.println("LifeFX.onCellUpdated: " + cellData);
    }
    @Override
    public void onProteinStatusUpdated(int proteinId, String status) {
        System.out.println("LifeFX.onProteinStatusUpdated(" +
                proteinId + ", " + status + ")");
    }
    private static class ColorRectCell extends ListCell<Cell/*!!KeyFields*/> {
        @Override
        public void updateItem(Cell/*!!KeyFields*/ item, boolean empty) {
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
    /*!!
    static class CellKeyFields {
        int id;
        int generation;
        int proteinCt;

        CellKeyFields(int id, int generation, int proteinCt) {
            this.id = id;
            this.generation = generation;
            this.proteinCt = proteinCt;
        }
    }
    */
    @Override
    public void start(Stage primaryStage) {
        /*
        0                  1                  2      3    4
        0  Cells           Proteins           AminoAcids  [Fill]
        1  <cellListView>  <proteinListView>  <name> <ct> <addCt>
           ...             ...                ...

        22 ...             ...                Nucleotides
        23 ...             ...                <name> <ct> <addCt>
           ...             ...                ...
        27                                                [Feed]

        (Fill button inserts quantities for 1 cell)
         */
        cellObservableList = FXCollections.observableArrayList();
        cellListView = new ListView<>(cellObservableList);
        cellListView.setCellFactory((/*!!ListView<Cell> */l) -> new ColorRectCell());
        cellListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        cellSelected());

        /*!!
        resourceTypeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                "Proteins", "AminoAcids", "RNA", "Nucleotides")
        );
        resourceTypeChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        resetResourceList());
        resourceTypeChoiceBox.getSelectionModel().selectFirst();
        */
        /*!!resource*/proteinObservableList = FXCollections.observableArrayList();
        /*!!resource*/proteinListView = new ListView<>(/*!!resource*/proteinObservableList);
        /*!!resource*/proteinListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showProteinStatus());

        statusText = new Text();
        statusText.setFill(Color.FIREBRICK);

        /*!!
        uracilQtyField = new TextField("" + Cell.uracilFor1Cell);
        cytosineQtyField = new TextField("" + Cell.cytosineFor1Cell);
        guanineQtyField = new TextField("" + Cell.guanineFor1Cell);
        adenineQtyField = new TextField();
        thymineQtyField = new TextField();
        aminoAcidSetQtyField = new TextField("1");
        cellQtyField = new TextField();
        */
        Button fillButton = new Button("Fill");
        fillButton.setOnAction(event -> {
            for (TextField aminoAcidQtyField: aminoAcidQtyFields) {
                aminoAcidQtyField.setText("1");
            }
            for (int i = 0; i < nucleotideQtyFields.length; i++) {
                nucleotideQtyFields[i].setText(nucleotidesFor1Cell[i]);
            }
        });
        Button feedButton = new Button("Feed");
        feedButton.setOnAction(event -> feedCell() );

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        /*!!
        GridPane feederGrid = new GridPane();
        feederGrid.setAlignment(Pos.CENTER);
        feederGrid.setHgap(10);
        feederGrid.setVgap(10);
        feederGrid.setPadding(new Insets(25, 25, 25, 25));
        */

        grid.add(new Label("Cells"), 0, 0);
        grid.add(cellListView, 0, 1);

        grid.add(new Label("Proteins"), 1, 0);
        grid.add(proteinListView, 1, 1);

        int aminoAcidCt = aminoAcidNames.length;
        aminoAcidQtyTexts = new Text[aminoAcidCt];
        aminoAcidQtyFields = new TextField[aminoAcidCt];
        for (int i = 0; i < aminoAcidCt; i++) {
            grid.add(new Label(aminoAcidNames[i]), 2, 1 + i);
            aminoAcidQtyTexts[i] = new Text();
            grid.add(aminoAcidQtyTexts[i], 3, 1 + i);
            aminoAcidQtyFields[i] = new TextField();
            grid.add(aminoAcidQtyFields[i], 4, 1 + i);
        }

        int nucleotideCt = nucleotideNames.length; // should be 5
        nucleotideQtyTexts = new Text[nucleotideCt];
        nucleotideQtyFields = new TextField[nucleotideCt];
        grid.add(new Label("Nucleotides"), 2, 22);
        for (int i = 0; i < nucleotideCt; i++) {
            grid.add(new Label(nucleotideNames[i]), 2, 23 + i);
            nucleotideQtyTexts[i] = new Text();
            grid.add(nucleotideQtyTexts[i], 3, 23 + i);
            nucleotideQtyFields[i] = new TextField();
            grid.add(nucleotideQtyFields[i], 4, 23 + i);
        }

        /*!!
        grid.add(new Label("Uracil"), 0, 0);
        grid.add(uracilQtyField, 1, 0);
        grid.add(new Label("Cytosine"), 0, 1);
        grid.add(cytosineQtyField, 1, 1);
        grid.add(new Label("Guanine"), 0, 2);
        grid.add(guanineQtyField, 1, 2);
        grid.add(new Label("Adenine"), 0, 3);
        grid.add(adenineQtyField, 1, 3);
        grid.add(new Label("Amino Acid Set"), 0, 4);
        grid.add(aminoAcidSetQtyField, 1, 4);
        grid.add(new Label("Cells"), 0, 5);
        grid.add(cellQtyField, 1, 5);
        grid.add(feedButton, 1, 6);

        grid.add(resourceTypeChoiceBox, 1, 0);
        grid.add(resourceListView, 1, 1);

        grid.add(feederGrid, 2, 0, 1, 2);
        */

        grid.add(statusText, 0, 28, 5, 1);

        Scene scene = new Scene(grid, 900, 500);
        primaryStage.setTitle("Life is complicated!");
        primaryStage.setScene(scene);
        primaryStage.show();
        Cell syntheticCell = Cell.makeSyntheticCell(true);
        syntheticCell.setCellListener(this);
        addCell(syntheticCell);
    }
    private void addCell(Cell cell) {
        cellObservableList.add(cell /*!!new CellKeyFields(cell.getId(),
                cell.getGeneration(), cell.getProteinCt())*/);
    }
    private void cellSelected() {
        System.out.println("cellSelected() not yet implemented!");
    }

    private void showProteinStatus() {
        int proteinIndex = this.proteinListView.getSelectionModel().getSelectedIndex();
        if (proteinIndex >= 0) {
            Protein protein = (Protein) resourceList.get(proteinIndex);
            statusText.setText("status of protein: " + protein.getStatus());
        }
    }
    /*!!
    private void resetResourceList() {
        CellKeyFields cellKF = this.cellListView.getSelectionModel().getSelectedItem();
        String resourceType = this.resourceTypeChoiceBox.getValue();
        String[] resourceNames;
        // TODO: could all resources (Protein, RNA, DNA, AminoAcid, NucleicAcid)
        // implement Resource?
        if (cell != null && resourceType != null) {
            if (resourceType.equals("Proteins")) {
                resourceList = cell.getProteinList();
            } else if (resourceType.equals("AminoAcids")) {
                resourceList = cell.getAminoAcidList();
            } else if (resourceType.equals("RNA")) {
                resourceList = cell.getRNAList();
            } else if (resourceType.equals("Nucleotides")) {
                resourceList = cell.getNucleotideList();
            } else {
                throw new IllegalStateException(
                        "unrecognised resourceType: " + resourceType);
            }
            HashMap<String, Integer> nameCountMap = new HashMap<>();
            for (Object resource: resourceList) {
                String name = resource.toString();
                Integer ct = nameCountMap.get(name);
                ct = (ct==null)?1:(ct+1);
                nameCountMap.put(name, ct);
            }
            resourceNames = new String[nameCountMap.size()];
            Set<String> keys = nameCountMap.keySet();
            int i = 0;
            for (String key: keys) {
                resourceNames[i++] = nameCountMap.get(key) + " " + key;
            }
            resourceObservableList.clear();
            Collections.addAll(resourceObservableList, resourceNames);
        }
    }
    @Override
    public void handle(ActionEvent event) {
        String message = uracilQtyField.getText() + " of Uracil, " +
                adenineQtyField.getText() + " of Adenine, " +
                guanineQtyField.getText() + " of Guanine, " +
                cytosineQtyField.getText() + " of Cytosine, ";
        statusText.setText(message);
        CellKeyFields cell = this.cellListView.getSelectionModel().getSelectedItem();
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
                Platform.runLater(() -> {
                    resetResourceList();
                    MultipleSelectionModel<Cell> selectionModel =
                            this.cellListView.getSelectionModel();
                    int selectedIndex = selectionModel.getSelectedIndex();
                    cellObservableList.remove(selectedIndex);
                    cellObservableList.add(selectedIndex, cell);
                });
            } catch (NumberFormatException nfe) {
                statusText.setText("supply integers only");
            }
        }
    }
    */
    private void feedCell() {
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
