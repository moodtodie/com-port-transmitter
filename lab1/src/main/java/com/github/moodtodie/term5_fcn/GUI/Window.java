package com.github.moodtodie.term5_fcn.GUI;

import com.github.moodtodie.term5_fcn.serial.PortListener;
import com.github.moodtodie.term5_fcn.serial.PortManager;
import com.github.moodtodie.term5_fcn.serial.Serial;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import jssc.SerialPortException;

public class Window extends Application {
	private TextArea output = null;

	private StackPane initInputPane() {
		StackPane pane = new StackPane();
		pane.setMinSize(200, 100);
		pane.setAlignment(Pos.CENTER);

		TextArea input = new TextArea();
		input.setWrapText(true);

		input.setMinSize(180, 90);
		input.setMaxSize(190, 90);

		input.setPromptText("Input field");

		input.setOnKeyTyped(event -> {
			appendOutputText(String.format("code: %d; key: %s%n", (int) event.getCharacter().charAt(0),event.getCharacter()));
			try {
				new Serial(PortManager.getPort()).write(event.getCharacter());
			} catch (SerialPortException e) {
				throw new RuntimeException(e);
			}
		});

		pane.getChildren().add(input);
		return pane;
	}

	private StackPane initOutputPane() {
		StackPane pane = new StackPane();
		pane.setMinSize(200, 100);
		pane.setAlignment(Pos.CENTER);

		output = new TextArea();
		output.setEditable(false);
		output.setMouseTransparent(true);
		output.setWrapText(true);

		output.setMinSize(180, 90);
		output.setMaxSize(190, 90);

		output.setPromptText("Output field");

		pane.getChildren().add(output);
		return pane;
	}

	private StackPane initControlPane() {
		StackPane pane = new StackPane();
		pane.setMinSize(200, 200);
		return pane;
	}

	private StackPane initStatusPane() {
		StackPane pane = new StackPane();
		pane.setMinSize(200, 200);

		// Создаем метку
		Label label = new Label("Это окно JavaFX на Java 20");
		label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

		pane.getChildren().add(label);
		return pane;
	}

	@Override
	public void start(Stage stage) {
		GridPane root = new GridPane();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("lab");
		stage.setMinWidth(400);
		stage.setMinHeight(200);

		StackPane inputPane = initInputPane();
		StackPane outputPane = initOutputPane();
		StackPane controlPane = initControlPane();
		StackPane statusPane = initStatusPane();

		root.add(inputPane, 0, 0);
		root.add(outputPane, 1, 0);
		root.add(controlPane, 0, 1);
		root.add(statusPane, 1, 1);

		//	--------------------------------------------

		PortListener listener = new PortListener(this, PortManager.getPort());

		// Выполняем задачу в отдельном потоке
		Thread thread = new Thread(listener::listen);

		// Запускаем поток
		thread.start();

		//	--------------------------------------------

//		stage.setResizable(false);
		stage.show();
	}

	public void appendOutputText(String text) {
		if (output != null) {
			output.appendText(text);
		}
	}

	public static void main(String[] args) {
		PortManager.init("/dev/pts/1");
		launch(args);
	}

}
