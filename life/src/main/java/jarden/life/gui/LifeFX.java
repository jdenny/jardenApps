package jarden.life.gui;

import java.util.Collections;

import jarden.life.Cell;
import jarden.life.OnNewCellListener;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

/*!!
class CellA {
    static int currentId = 0;
    int id;
    int generation;
    int proteinCt;
    public CellA(int generation, int proteinCt) {
        id = ++currentId;
        this.generation = generation;
        this.proteinCt = proteinCt;
    }
    public String toString() {
        return "id=" + id + "; generation=" + generation +
                "; proteinCt=" + proteinCt;
    }
}
*/

/**
 * Created by john.denny@gmail.com on 15/02/2017.
 * Android Studio's embedded JDK doesn't include JavaFX, so use
 * File, Project Structure to change it; to find out location of
 * JDK on mac, in terminal window: echo $(/usr/libexec/java_home)
 * Current location is:
 * /Library/Java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home
 */

public class LifeFX extends Application implements EventHandler<ActionEvent>,
        ChangeListener<Cell>, OnNewCellListener {
    private Text statusText;
    private ObservableList<Cell> cellList;
    private ObservableList<String> resourceList;
    private ChoiceBox<String> resourceTypeChoiceBox;
    private TextField uracilQtyField;
    private TextField cytosineQtyField;
    private TextField guanineQtyField;
    private TextField adenineQtyField;
    //!! private Cell firstCell;

    private String[] cell1Proteins = {
            "c1 protein A", "c1 protein B"
    };
    private String[] cell2Proteins = {
            "c2 protein C", "c2 protein D", "c2 protein E"
    };
    private String[] cell3Proteins = {
            "c3 protein F", "c3 protein G", "c3 protein H"
    };
    private String[] aminoAcids = {
            "aminoAcid W", "aminoAcid X", "aminoAcid Y", "aminoAcid Z"
    };
    private String[] nucleotides = {
            "Uracil", "Adenine", "Cytosine", "Guanine"
    };

    public static void main(String[] args) {
        System.out.println("hello LifeFX");
        launch(args);
    }

    @Override
    public void onNewCell(Cell cell) {
        cellList.add(cell);
    }

    static class ColorRectCell extends ListCell<Cell> {
        @Override
        public void updateItem(Cell item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                int generation = item.getGeneration();
                Color colour;
                if (generation == 1) colour = Color.web("green");
                else if (generation == 2) colour = Color.web("blue");
                else colour = Color.web("red");
                Rectangle rect = new Rectangle(20 * item.getProteinCt(), 20);
                rect.setFill(colour);
                setGraphic(rect);
            }
        }
    }
    @Override
    public void start(Stage primaryStage) {

        cellList = FXCollections.observableArrayList();
        ListView<Cell> cellListView = new ListView<>(cellList);
        Cell syntheticCell = Cell.getSyntheticCell();
        syntheticCell.setOnNewCellListener(this);
        cellList.add(syntheticCell);
        /*!!
        firstCell = new Cell(1, 4);
        cellList.add(firstCell);
        cellList.add(new Cell(2, 2));
        cellList.add(new Cell(2, 3));
        */
        cellListView.setCellFactory((ListView<Cell> l) -> new ColorRectCell());
        cellListView.getSelectionModel().selectedItemProperty().addListener(this);

        resourceTypeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                "Proteins", "AminoAcids", "Nucleotides", "Threads")
        );
        resourceTypeChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue.equals("Proteins")) {
                        resetResourceList(resourceList, cell1Proteins);
                    } else if (newValue.equals("AminoAcids")) {
                        resetResourceList(resourceList, aminoAcids);
                    } else if (newValue.equals("Nucleotides")) {
                        resetResourceList(resourceList, nucleotides);
                    }
                });

        resourceList = FXCollections.observableArrayList();
        ListView<String> resourceListView = new ListView<>(resourceList);
        resourceListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    String resourceType = resourceTypeChoiceBox.getValue();
                    if (resourceType.equals("Proteins")) {
                        statusText.setText("show status of protein " + newValue);
                    } else if (resourceType.equals("AminoAcids")) {
                        statusText.setText("show status of aminoAcid " + newValue);
                    }
                });

        statusText = new Text();
        statusText.setFill(Color.FIREBRICK);

        uracilQtyField = new TextField();
        cytosineQtyField = new TextField();
        guanineQtyField = new TextField();
        adenineQtyField = new TextField();
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
        feederGrid.add(feedButton, 1, 4);

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
    }
    private void resetResourceList(ObservableList<String> list, String[] names) {
        list.clear();
        Collections.addAll(list, names);
    }

    @Override
    public void handle(ActionEvent event) {
        String message = uracilQtyField.getText() + " of Uracil, " +
                adenineQtyField.getText() + " of Adenine, " +
                guanineQtyField.getText() + " of Guanine, " +
                cytosineQtyField.getText() + " of Cytosine, ";
        statusText.setText(message);
        /*
        cellList.add(new Cell(3, 3)); // this updates listView
        firstCell.getproteinCt++; // amazingly, so does this
        */
    }

    @Override
    public void changed(ObservableValue<? extends Cell> observable,
                        Cell oldValue, Cell newValue) {
        if (newValue.getId() == 1) {
            resetResourceList(resourceList, cell1Proteins);
        } else if (newValue.getId() == 2) {
            resetResourceList(resourceList, cell2Proteins);
        } else if (newValue.getId() == 3) {
            resetResourceList(resourceList, cell3Proteins);
        } else {
            String message = "oldValue=" + oldValue + "; newValue=" +
                    newValue;
            System.out.println(message);
            statusText.setText(message);
        }

    }
}
