package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

import application_v2.AddPress;
import application_v2.Locations;
import application_v2.Press;
import application_v2.PressManager;
import application_v2.TransferTime;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Controller implements Initializable {
	
	@FXML
    private Button addPress; //Add press button in Accordion
	@FXML
    private Button addTransferLocation; //Add Transfer Location '+'
	@FXML
    private Label editTransferLocation; //Add Transfer Location '+'
	@FXML
    private Button addTransferTime; //Add Transfer Time '+'
	@FXML
    private VBox pressList; //Press list in GUI
	@FXML
    private VBox transferLocList; //Location list in GUI
	@FXML
    private ScrollPane scrollpane; //ScrollPane
	@FXML
    private TextField connectionName; //name of connection in dialog
	@FXML
	private TableView<TransferTime> timeTable; //Table
	@FXML
	private TableColumn<TransferTime, String> startTime; //Columns
	@FXML
	private TableColumn<TransferTime, String> endTime;
	
	private Press selectedPress;
	
	
//	Parent root;
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Always show vertical scroll bar
		scrollpane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		VBox.setVgrow(scrollpane, Priority.ALWAYS);
		addTransferLocation.setDisable(true); //Disables adding
		addTransferTime.setDisable(true);
		addPress.setOnAction(this::handleNewPress);
		addTransferLocation.setOnAction(this::handleNewTranferLocation);
		addTransferTime.setOnAction(this::handleNewTranferTime);
		
		//Applies the objects to the actual cells in the table
		startTime.setCellValueFactory(new PropertyValueFactory<TransferTime, String>("startTime"));
		endTime.setCellValueFactory(new PropertyValueFactory<TransferTime, String>("stopTime"));
	}
	
	@FXML
	private void handleNewPress(ActionEvent event) {
		addTransferLocation.setDisable(true); //Disables adding
		addTransferTime.setDisable(true);
		try {
			VBox root = FXMLLoader.load(getClass().getResource("/resources/AddPressWindow.fxml"));
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.getIcons().add(new Image("/resources/icon.png"));
			window.setTitle("Add New Press");
			window.setScene(new Scene(root));
			window.showAndWait(); //Wait until window closes
			if (PressManager.getRecentAdded()) { //If recently added press
				addPressToScene();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleEditTranferLocation() {
		try {
			VBox root = FXMLLoader.load(getClass().getResource("/resources/TransferLocationWindow.fxml"));
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.getIcons().add(new Image("/resources/icon.png"));
			window.setTitle("Edit Location");
			window.setScene(new Scene(root));
			window.showAndWait(); //Wait until window closes
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Handles event for adding a new transfer location
	 */
	@FXML
	private void handleNewTranferLocation(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/TransferLocationWindow.fxml"));
			VBox root = (VBox) loader.load();
			LocationController mainController = loader.<LocationController>getController();
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.getIcons().add(new Image("/resources/icon.png"));
			window.setTitle("Edit Location");
			window.setScene(new Scene(root));
			window.showAndWait(); //Wait until window closes
			if (!mainController.getName().trim().isEmpty()) {
				createComponent(mainController.getName(), mainController.getSourceLoc(), mainController.getTargetLoc());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*
	 * Creates new ui component for Transfer Location
	 */
	private void createComponent(String name, String sourceLoc, String targetLoc) {
		Pane pane = new Pane();
		VBox vbox = new VBox();
		pane.getStyleClass().add("locationDiv");
		pane.setPrefHeight(146.00);
		pane.prefWidth(424.00);
		pane.setMinHeight(146);
		Label label = new Label(name);
		TextField textfield1 = new TextField();
		TextField textfield2 = new TextField();
		textfield1.setText(sourceLoc);
		textfield2.setText(targetLoc);
		textfield1.getStyleClass().add("textboxtext");
		textfield2.getStyleClass().add("textboxtext");
		textfield1.setPrefWidth(400);
		textfield2.setPrefWidth(400);
		textfield1.setEditable(false);
		textfield2.setEditable(false);

		label.getStyleClass().add("connections");
		label.setPadding(new Insets(25, 0, 5, 25));
		label.setPrefHeight(35);
		label.setOnMouseClicked(event -> handleEditTranferLocation());
		vbox.getChildren().add(0,label);
		vbox.getChildren().add(1,textfield1);
		vbox.getChildren().add(2,textfield2);
		pane.getChildren().add(0,vbox);
		transferLocList.getChildren().add(transferLocList.getChildren().size(),pane);
		
		addLocationToPress(name, sourceLoc, targetLoc);
	}
	/*
	 * Adds the new loation to the press
	 */
	private void addLocationToPress(String name, String sourceLoc, String targetLoc) {
		ArrayList<Locations> locations = selectedPress.getLocations();
		if (locations != null) {
			locations.add(new Locations(name, sourceLoc, targetLoc));
		}
		else { //If location array is null lets create a new one
			locations = new ArrayList<Locations>();
			locations.add(new Locations(name, sourceLoc, targetLoc));
		}
		selectedPress.setLocations(locations);
		PressManager.updatePress(selectedPress); //Update the press in the hashmap
	}

	/*
	 * Handles event for adding a new transfer time
	 */
	@FXML
	private void handleNewTranferTime(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/TransferTimeWindow.fxml"));
			VBox root = (VBox) loader.load();
			TimeController mainController = loader.<TimeController>getController();
			Stage window = new Stage();
			window.initModality(Modality.APPLICATION_MODAL);
			window.getIcons().add(new Image("/resources/icon.png"));
			window.setTitle("Edit Location");
			window.setScene(new Scene(root));
			window.showAndWait(); //Wait until window closes
			ObservableList<TransferTime> list = mainController.getTimes();
			if (mainController.ifSaved() && !list.isEmpty()) { //If list is not empty lets add them to the main table
				addTimesToTable(list);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*
	 * This method add the new transfer times to the main table
	 */
	private void addTimesToTable(ObservableList<TransferTime> list) {
		for (int i = 0; i < list.size(); i++) {
			TransferTime time = list.get(i);
			//Add times to table
			timeTable.getItems().add(time);
		}
		
	}

	@FXML
	private void handlePress(ActionEvent event) {
		selectedPress = PressManager.getPress(event.getSource().hashCode()); //Get the press info
		addTransferLocation.setDisable(false); //Enables adding
		addTransferTime.setDisable(false);
		updateGUI(); //Lets update the UI with the presses current info
	}
	
	/*
	 * Adds press name to GUI
	 */
	@FXML
	public void addPressToScene() {
		//Get the most recent press added to hashmap
		Press press = PressManager.getRecentPress();
		PressManager.removePress(press.getKey()); //Remove temp obj in hashmap
		
		Button newPress = new Button(press.getName());
		newPress.getStyleClass().add("addPress");
		newPress.setMinWidth(addPress.getWidth());
		newPress.setUserData(press);
		
		press.setKey(newPress.hashCode());
		PressManager.addPress(press.getName(), newPress.hashCode()); //Add new press with the same hash code as the button
		//Set action to handle the new press button
		newPress.setOnAction(this::handlePress);
		
		pressList.getChildren().add(0,newPress); //Add button to top of children

	}

}
