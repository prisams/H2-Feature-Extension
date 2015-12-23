import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * @author Shashank
 *
 */
public class LaunchCassy extends Application{

	private DatePicker checkInDatePicker;
	private final String pattern = "yyyy-MM-dd";
	static public String selectedDate;

	public static void main(String[] args) {
		System.out.println("Application begins...");
		launch();
	}

	@Override
	public void start(Stage firstStage) throws Exception {
		firstStage.setTitle("Powered by Cassandra");

		//ADD APPLICATION NAME

		HBox hboxt = new HBox(40);
		hboxt.setSpacing(100);
		Text title = new Text();
		title.setCache(true);
		title.setText("         STOCK TRACK($) : The trend is your friend...");
		title.setFill(Color.BLUE);
		title.setFont(Font.font(null, FontWeight.BOLD, 30));
		hboxt.getChildren().add(title);

		ArrayList<String> tickerList = new ArrayList<String>();
		//Populate ticker list here
		TickerTableMethods tm= new TickerTableMethods();
		tickerList=tm.getListofTickers();



		final TextArea information = new TextArea("Powered by Cassandra");

		information.setPrefSize(150,100);
		information.setEditable(false);
		information.setWrapText(true);
		final ChoiceBox tickerdropDown = new ChoiceBox(FXCollections.observableArrayList(
				tickerList) );

		tickerdropDown.getSelectionModel().selectedIndexProperty().
		addListener(new javafx.beans.value.ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {

			}
		});
		tickerdropDown.setPrefWidth(100);

		tickerdropDown.getSelectionModel().selectedItemProperty().addListener(new javafx.beans.value.ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				System.out.println("TICKER selection changed from oldValue = "
						+ oldValue + " to newValue = " + newValue);

				information.setText("Ticker selected : "+newValue);

			}
		});


		GridPane grid0 = new GridPane();
		grid0.setVgap(2);
		grid0.setHgap(2);
		grid0.setPadding(new Insets(5, 5, 5, 5));
		Label  tickerSelect=new Label("Select Ticker");
		tickerSelect.setFont(Font.font(null, FontWeight.BOLD, 15));
		grid0.add(tickerSelect,0,0);
		grid0.add(tickerdropDown,0,4);
		Label  info=new Label("Information");
		info.setFont(Font.font(null, FontWeight.BOLD, 15));
		grid0.add(info,0,10);
		grid0.add(information,0,12);

		//Create Horizontal box
		HBox hbox = new HBox(40);
		hbox.setSpacing(100);
		ArrayList<String> sectorList = new ArrayList<String>();

		//Create Sector list here
		sectorList=tm.getListofSectors();

		final ListView<String> list = new ListView<String>();
		ObservableList<String> items =FXCollections.observableArrayList (
				sectorList);
		list.setItems(items);
		list.setPrefWidth(200);
		list.setPrefHeight(200);

		final VBox vbox2 = new VBox();
		list.getSelectionModel().selectedItemProperty().addListener(new javafx.beans.value.ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				System.out.println("SECTOR changed from oldValue = "
						+ oldValue + " to newValue = " + newValue);

				//List of tickers per sector
				ArrayList<String> tickerListPerSector = new ArrayList<String>();
				TickerTableMethods tm1= new TickerTableMethods();
				tickerListPerSector=tm1.getListofTickersOfSector(newValue);

				ListView<String> nlist = new ListView<String>();
				ObservableList<String> nitems =
						FXCollections.observableArrayList (tickerListPerSector);
				//Create list of tickers belonging to a sector here and update nitems to display all tickers
				vbox2.setLayoutX(30);
				vbox2.setLayoutY(30);
				nlist.setItems(nitems);
				nlist.setPrefWidth(30);
				nlist.setPrefHeight(70);
				vbox2.getChildren().add(nlist);
				StackPane root1 = new StackPane();
				root1.getChildren().add(nlist);
				// root.getChildren().add(list);
				Stage firstStage1 = new Stage();
				firstStage1.setTitle("Available Tickers ");
				firstStage1.setScene(new Scene(root1, 400, 400));
				firstStage1.show();
			}
		});

		GridPane gridList = new GridPane();
		gridList.setVgap(2);
		gridList.setHgap(2);
		gridList.setPadding(new Insets(5, 5, 5, 5));
		Label  catSelect=new Label("Select Sector ");
		catSelect.setFont(Font.font(null, FontWeight.BOLD, 15));
		gridList.add(catSelect,0,0);
		gridList.add(list,0,4);

		//Date
		checkInDatePicker = new DatePicker();
		final  StringConverter converter = new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter =
					DateTimeFormatter.ofPattern(pattern);
			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					selectedDate= dateFormatter.format(date);
					System.out.println("Selected date is "+(selectedDate));
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}
			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		};

		checkInDatePicker.setConverter(converter);
		checkInDatePicker.setPromptText(pattern.toLowerCase());
		final TextArea warningmsg = new TextArea("");

		warningmsg.setPrefSize(200,100);
		warningmsg.setEditable(false);
		warningmsg.setWrapText(true);
		GridPane gridPane = new GridPane();
		gridPane.setHgap(2);
		gridPane.setVgap(2);
		Label checkDate = new Label("Select Date");

		checkDate.setFont(Font.font(null, FontWeight.BOLD, 15));
		gridPane.add(checkDate, 0, 8);
		GridPane.setHalignment(checkDate, HPos.LEFT);
		gridPane.add(checkInDatePicker, 0, 10);
		Label  warninfo=new Label("Warning");
		warninfo.setFont(Font.font(null, FontWeight.BOLD, 15));
		gridPane.add(warninfo,0,15);
		gridPane.add(warningmsg,0,17);

		hbox.getChildren().add(grid0);
		hbox.getChildren().add(gridList);
		hbox.getChildren().add(gridPane);


		//Insert Button Implementation
		ArrayList<String> dateList = new ArrayList<String>();
		for (int i=1;i<=10;i++)
		{
			dateList.add(new String(""+i));
		}
		final ChoiceBox dateDropdown = new ChoiceBox(FXCollections.observableArrayList(
				dateList) );

		dateDropdown.getSelectionModel().selectedIndexProperty().
		addListener(new javafx.beans.value.ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {

			}
		});
		dateDropdown.setPrefWidth(50);

		dateDropdown.getSelectionModel().selectedItemProperty().addListener(new javafx.beans.value.ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				System.out.println("Day selection changed from oldValue = "
						+ oldValue + " to newValue = " + newValue);

				information.setText("Day selected : "+newValue);

			}
		});

		Button insertButton = new Button();
		insertButton.setText("Insert");
		insertButton.setMaxWidth(Double.MAX_VALUE);
		Label  labelInsert=new Label("Insert ticker Data");
		labelInsert.setTextFill(Color.web("GREEN"));
		labelInsert.setFont(Font.font(null, FontWeight.BOLD, 20));
		Label  labelDays=new Label("no. of Days ");
		labelDays.setTextFill(Color.web("GREEN"));
		labelDays.setFont(Font.font(null, FontWeight.BOLD, 20));
		GridPane gridInsert = new GridPane();
		gridInsert.setPadding(new Insets(10,10,10,10));
		gridInsert.add(labelDays,0,0);
		gridInsert.add(dateDropdown,0,4);
		gridInsert.add(labelInsert,0,8);
		gridInsert.add(insertButton,0,12);

		insertButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("INSERT Button Clicked");
				System.out.println(checkInDatePicker.getValue());
				//    System.out.println( converter.toString());
				if (tickerdropDown.getValue()==null  || dateDropdown.getValue()==null){
					System.out.println("Please select no of days for insertion");
					information.setText("");
					warningmsg.setText("Please select valid no of days and ticker values to insert !!!");
				}
				else{
					//Dates needs to be changed to integers
					//Do Insert activities here with tickerdropDown.getValue() and checkInDatePicker.getValue()
					GetStockData getStockData = new GetStockData();
					try {
						getStockData.getStreamingData(tickerdropDown.getValue().toString(), Integer.parseInt((dateDropdown.getValue().toString())));
					} catch (Exception e) {
						e.printStackTrace();
					}
					warningmsg.setText("");
					information.setText("Data inserted for "+tickerdropDown.getValue()+" for "+ dateDropdown.getValue().toString() + " days.");
				}
			}
		});


		//Update Button Implementation
		Button updateButton = new Button();
		updateButton.setText("Update");
		updateButton.setMaxWidth(Double.MAX_VALUE);
		final  TextField updateDescription = new TextField() ;
		GridPane gridUpdate = new GridPane();
		Label  labelUpdate=new Label("Update Ticker Description");
		labelUpdate.setTextFill(Color.web("ORANGE"));
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(3.0);
		dropShadow.setOffsetY(3.0);
		dropShadow.setColor(Color.color(0, 0, 0));
		labelUpdate.setEffect(dropShadow);
		labelUpdate.setFont(Font.font(null, FontWeight.BOLD, 25));
		gridUpdate.setPadding(new Insets(5,5,5,5));
		gridUpdate.add(labelUpdate,0,0);
		gridUpdate.add(updateDescription,0,1);
		gridUpdate.add(updateButton,0,2);


		updateButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("Update Button Clicked");
				System.out.println("DECSCRIPTION IS : "+updateDescription.getText());
				if (updateDescription.getText().isEmpty() || tickerdropDown.getValue()==null){
					information.setText("");
					warningmsg.setText("Please select a ticker and enter valid description to update !!!");
				}
				else{
					//Do update activity here

					UpdateTicker update = new UpdateTicker();
					String updateResult=update.updateTicker(tickerdropDown.getValue().toString(),
							updateDescription.getText());
					warningmsg.setText("");
					information.setText("Data description updated for "+tickerdropDown.getValue() + " as " + updateResult );
				}
			}
		});


		//Delete button
		Button deleteButton = new Button();
		deleteButton.setText("Delete");
		deleteButton.setMaxWidth(Double.MAX_VALUE);
		Label  labelDelete=new Label("Delete ticker Data");
		labelDelete.setTextFill(Color.web("RED"));
		labelDelete.setFont(Font.font(null, FontWeight.BOLD, 20));
		GridPane gridDelete = new GridPane();
		gridDelete.setPadding(new Insets(5,5,5,5));
		gridDelete.add(labelDelete,0,0);
		gridDelete.add(deleteButton,0,8);
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("Delete Button Clicked");
				System.out.println( "VALUE OF CB IS"+tickerdropDown.getValue()+"HELLO");
				if (tickerdropDown.getValue()==null  ){
					information.setText("");
					System.out.println("Please select ticker for deletion");
					warningmsg.setText("Please select valid  ticker values to delete !!!");
				}
				else{
					//Delete activity here
					DeleteData delete = new DeleteData();
					delete.deleteData(tickerdropDown.getValue().toString());
					warningmsg.setText("");
					information.setText("Data deleted for "+tickerdropDown.getValue());
				}
			}
		});

		HBox hbox2 = new HBox();
		hbox2.setSpacing(50);
		hbox2.getChildren().add(gridInsert);
		hbox2.getChildren().add(gridUpdate);
		hbox2.getChildren().add(gridDelete);

		//Last row buttons
		//get-Extreme button
		Button lowhigh = new Button();
		lowhigh.setText("Get-Extremes");
		final  TextArea lowDescription = new TextArea() ;
		lowDescription.setPrefWidth(70);
		lowDescription.setPrefHeight(60);
		lowDescription.setEditable(false);
		GridPane gridlow = new GridPane();
		Label  labellow=new Label("Ticker day's Performance");
		labellow.setTextFill(Color.web("BLACK"));
		labellow.setFont(Font.font(null, FontWeight.BOLD, 15));
		gridlow.setPadding(new Insets(5,5,5,5));
		gridlow.add(labellow,0,0);
		gridlow.add(lowDescription,0,1);
		gridlow.add(lowhigh,0,62);

		lowhigh.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("get-extremes Button Clicked");
				System.out.println("TICKER IS : "+tickerdropDown.getValue());
				if ( tickerdropDown.getValue()==null || checkInDatePicker.getValue()==null){
					warningmsg.setText("Please select a valid ticker and date !!!");
				}
				else{
					//Do Ticker performance button activity here
					TickerPerformanceforADay tickerPerformance = new TickerPerformanceforADay();
					ArrayList<String> result = new ArrayList<String>();
					result=tickerPerformance.getTickerPerformance(tickerdropDown.getValue().toString(),
							checkInDatePicker.getValue().toString());
					warningmsg.setText("");
					//lowDescription.setText("MIN : X  , MAX : Y");
					//need to handle exception
					lowDescription.setText(result.get(0) + "\n" + result.get(1));
				}
			}
		});

		//Max volume button
		Button maxVol = new Button();
		maxVol.setText("Get-Volume");
		final  TextField volDescription = new TextField() ;
		volDescription.setEditable(false);
		GridPane gridVol = new GridPane();
		Label  labelVol=new Label("MAXIMUM Volume ");

		labelVol.setTextFill(Color.web("BLACK"));

		labelVol.setFont(Font.font(null, FontWeight.BOLD, 20));
		DropShadow dropShadow1 = new DropShadow();
		dropShadow1.setRadius(5.0);
		dropShadow1.setOffsetX(3.0);
		dropShadow1.setOffsetY(3.0);
		dropShadow1.setColor(Color.color(1, 1, 1));
		labelVol.setEffect(dropShadow1);
		gridVol.setPadding(new Insets(5,5,5,5));
		gridVol.add(labelVol,0,0);
		gridVol.add(volDescription,0,1);
		gridVol.add(maxVol,0,2);


		maxVol.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("maxVol Button Clicked");
				System.out.println("TICKER IS : "+tickerdropDown.getValue());
				if ( tickerdropDown.getValue()==null || checkInDatePicker.getValue()==null){
					warningmsg.setText("Please select a valid ticker and date !!!");
				}
				else{
					//Do max volume button activity here
					//Handle exception
					TickerMaxVolumeForADay tMax = new TickerMaxVolumeForADay();
					String result=tMax.getTickerMaxVolume(tickerdropDown.getValue().toString(),
							checkInDatePicker.getValue().toString());
					warningmsg.setText("");
					volDescription.setText(result);
				}
			}
		});


		//Best ticker button for a sector

		Button bestTicker = new Button();
		bestTicker.setText("Get-BestTicker");
		final  TextField bestDesc = new TextField() ;
		bestDesc.setEditable(false);
		GridPane gridBest = new GridPane();
		Label  labelBest=new Label("Best ticker from Sector");

		labelBest.setTextFill(Color.web("BLACK"));

		labelBest.setFont(Font.font(null, FontWeight.BOLD, 20));

		gridBest.setPadding(new Insets(5,5,5,5));
		gridBest.add(labelBest,0,0);
		gridBest.add(bestDesc,0,1);
		gridBest.add(bestTicker,0,2);


		bestTicker.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("bestTicker Button Clicked");
				System.out.println("Sector is : "+list.getSelectionModel().getSelectedItem());
				if ( list.getSelectionModel().getSelectedItem()==null){
					warningmsg.setText("Please select a valid Sector !!!");
				}
				if ( tickerdropDown.getValue()==null || checkInDatePicker.getValue()==null){
					warningmsg.setText("Please select a valid ticker and date !!!");
				}
				else{
					//Do get best ticker for a sector here
					BestTickerPerformanceInASector bestTicker =
							new BestTickerPerformanceInASector();
					String result=bestTicker.bestTickerInSector(list.getSelectionModel().getSelectedItem(),
							checkInDatePicker.getValue().toString());
					warningmsg.setText("");
					bestDesc.setText(result);
				}
			}
		});

		HBox hbox3 = new HBox();
		hbox3.setSpacing(50);
		hbox3.getChildren().add(gridlow);
		hbox3.getChildren().add(gridVol);
		hbox3.getChildren().add(gridBest);

		//Add all layers
		VBox vbox = new VBox();
		vbox.setSpacing(20);
		vbox.setLayoutX(200);
		vbox.setLayoutY(200);
		StackPane root = new StackPane();

		//Add elements to Vertical Box
		vbox.getChildren().add(hboxt);
		vbox.getChildren().add(hbox);
		vbox.getChildren().add(hbox2);
		vbox.getChildren().add(hbox3);

		//Add to root
		root.getChildren().add(vbox);
		//Upload image
		root.setStyle("-fx-background-image: url('stock.jpg')");
		Scene oneScene = new Scene(root, 800, 600);
		firstStage.setScene(oneScene);
		firstStage.show();
	}

}

