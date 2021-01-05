package accse.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application 
{
	
	private ClientPane pane = null;
	
	public static void main(String[] args) 
	{
		launch(args);
	}


	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		//create the ClientPane, set up the Scene and Stage
		primaryStage.setTitle("Network Project");
		//Creates the ClientPane
		pane = new ClientPane();
		//Set the Scene
		Scene scene = new Scene(pane);
	//	scene.getStylesheets().add("file:data/stylesheet.css"); style sheet attempt
		
		primaryStage.setWidth(1200);
		primaryStage.setHeight(750);
		primaryStage.setScene(scene);
		//Shows GUI
		primaryStage.show();	
	}

}
