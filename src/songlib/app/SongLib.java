package songlib.app;

import javafx.application.Application;

/**
 * @author      Sahej Bansal
 * @author		Arian Baoas
 */

import javafx.stage.Stage;
import songlib.view.ListController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class SongLib extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);

	}

	@Override
	public void start(Stage PrimaryStage) throws Exception {
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/songlib/view/songlib.fxml"));
		
		GridPane root = (GridPane) loader.load();
		ListController listcontroller = loader.getController();
		listcontroller.start(PrimaryStage);
		
		Scene scene = new Scene(root);
		
		PrimaryStage.setScene(scene);
		PrimaryStage.setResizable(false);
		PrimaryStage.show();
	}

}