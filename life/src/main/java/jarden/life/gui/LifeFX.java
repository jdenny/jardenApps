package jarden.life.gui;

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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
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
        ChangeListener<String> {
    private Text statusText;
    private ObservableList<String> cellList;
    private ObservableList<String> resourceList;
    private ChoiceBox<String> resourceTypeChoiceBox;
    private TextField uracilQtyField;
    private TextField cytosineQtyField;
    private TextField guanineQtyField;
    private TextField adenineQtyField;

    private String[] cell1Proteins = {
            "c1 protein A", "c1 protein B"
    };
    private String[] cell2Proteins = {
            "c2 protein C", "c2 protein D", "c2 protein E"
    };
    private String[] aminoAcids = {
            "aminoAcid W", "aminoAcid X", "aminoAcid Y", "aminoAcid Z"
    };
    private String[] nucleotides = {
            "Uracil", "Adenine", "Cytosine", "Guanine"
    };

    public static void main(String[] args) {
        System.out.println("hello javafx");
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {

        cellList = FXCollections.observableArrayList();
        ListView<String> cellListView = new ListView<>(cellList);
        cellList.add("cell 1");
        cellList.add("cell 2");
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

        Scene scene = new Scene(grid, 800, 500);
        primaryStage.setTitle("Life is complicated!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void resetResourceList(ObservableList<String> list, String[] names) {
        list.clear();
        for (String name: names) list.add(name);
    }

    @Override
    public void handle(ActionEvent event) {
        String message = uracilQtyField.getText() + " of Uracil, " +
                adenineQtyField.getText() + " of Adenine, " +
                guanineQtyField.getText() + " of Guanine, " +
                cytosineQtyField.getText() + " of Cytosine, ";
        statusText.setText(message);
    }

    @Override
    public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
        if (newValue.equals("cell 1")) {
            resetResourceList(resourceList, cell1Proteins);
        } else if (newValue.equals("cell 2")) {
            resetResourceList(resourceList, cell2Proteins);
        } else {
            String message = "oldValue=" + oldValue + "; newValue=" +
                    newValue;
            System.out.println(message);
            statusText.setText(message);
        }

    }
}
