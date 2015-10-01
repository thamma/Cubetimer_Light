package me.thamma.newctl;

import com.thamma.guiutils.menubuilder.MenuBuilder;
import com.thamma.guiutils.menubuilder.MenuItemType;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class TestGui extends Application {

	public static void main(String... args) {
		TestGui.launch(args);
	}

	final MenuBuilder m = new MenuBuilder().append("File.Save", (event) -> {
		System.out.println("1");
	}).append("File.Load", (event) -> {
		System.out.println("2");
	}).append("File.Save As.Nix", (event) -> {
		System.out.println("3");
	}).append("File.Save As.Was", (event) -> {
		System.out.println("4");
	}).append("View.Test", MenuItemType.CHECKBOX, (event) -> {
		CheckMenuItem menu = (CheckMenuItem) event;
		System.out.println(menu.isSelected());
	}).append("Test", (event) -> {
		System.out.println("hi");
	});

	@Override
	public void start(Stage stage) throws Exception {
		
		stage.setWidth(500);
		stage.setHeight(500);
		// stage.getIcons().add(new
		// Image(this.getClass().getResourceAsStream("resources/DITTO.png")));
		Pane pane = new Pane();
		
		Scene scene = new Scene(pane);
		pane.getChildren().add(m.getMenu());

		// scene.getStylesheets().addAll(this.getClass().getResource("./resources/style.css").toExternalForm());
		stage.show();
		stage.setScene(scene);
	}
}