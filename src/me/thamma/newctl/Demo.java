package me.thamma.newctl;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import me.thamma.guiutils.menubuilder.*;

public class Demo extends Application {

	public static void main(String... args) {
		Demo.launch(args);
	}

	final MenuBuilder menuBuilder = new MenuBuilder().append("Path.Subpath.Some item", (item) -> {
		System.out.println("You hit some item!");
	}).append("Path.Subpath.Some other item.Nested is fun!", (item) -> {
		System.out.println("You hit some other item!");
	}).append("Path.Toggleable", MenuItemType.CHECKBOX, (item) -> {
		System.out.println("The checkbox is " + (((CheckMenuItem) item).isSelected() ? "" : "not ") + "checked");
	}).append("Other.Subpath", (item) -> {
		System.out.println("Hello, good Sir!");
	});

	@Override
	public void start(Stage stage) throws Exception {
		Pane pane = new Pane();

		MenuBar menuBar = new MenuBar();

		Menu menuPath = new Menu("Path");
		menuBar.getMenus().add(menuPath);
		Menu subPath = new Menu("Subpath");
		menuPath.getItems().add(subPath);
		MenuItem someItem = new MenuItem("Some item");
		Menu someOther = new Menu("Some other item");
		subPath.getItems().addAll(someItem, someOther);
		MenuItem nested = new MenuItem("Nested is totally fun...");
		someOther.getItems().add(nested);
		CheckMenuItem toggle = new CheckMenuItem("Toggleable");
		Menu menuOther = new Menu("Other");
		menuBar.getMenus().add(menuOther);

		pane.getChildren().add(menuBar);

		stage.setWidth(500);
		stage.setHeight(500);

		// This is where all the magic happens!
		MenuBar menu = menuBuilder.getMenu();
		pane.getChildren().add(menu);

		stage.show();
		stage.setScene(new Scene(pane));
	}
}