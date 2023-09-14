package com.github.moodtodie.term5_fcn.GUI;

import com.github.moodtodie.term5_fcn.serial.PortManager;
import com.github.moodtodie.term5_fcn.serial.Serial;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jssc.SerialPortException;

public class Window extends Application {
    private static TextArea output = null;
    private static Label labelByteReceived = new Label("  Byte received: 0");

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
            try {
                PortManager.getPort().write(event.getCharacter());
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

    private VBox initControlPane() {
        VBox pane = new VBox();
        pane.setMinWidth(190);
        pane.setMaxSize(190, 50);
        pane.setAlignment(Pos.TOP_CENTER);

        HBox pane1 = new HBox();
        pane1.setAlignment(Pos.CENTER_LEFT);
        pane1.setMinSize(190, 30);

        ComboBox<String> comboBox1 = new ComboBox<>();
        comboBox1.getItems().addAll(Serial.getPortList());
        comboBox1.getSelectionModel().selectFirst();
        try {
            PortManager.setPort(comboBox1.getSelectionModel().getSelectedItem());
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }
        comboBox1.setOnAction(event -> {
            try {
                PortManager.setPort(comboBox1.getSelectionModel().getSelectedItem());
            } catch (SerialPortException e) {
                throw new RuntimeException(e);
            }
        });
        comboBox1.setOnMousePressed(event -> {
            int selectedIndex = comboBox1.getSelectionModel().getSelectedIndex();
            if (selectedIndex > 0 && selectedIndex < comboBox1.getItems().size()) {
                comboBox1.getItems().remove(0, selectedIndex);
                comboBox1.getItems().remove(selectedIndex, comboBox1.getItems().size());
            } else if (selectedIndex == 0) {
                comboBox1.getItems().remove(selectedIndex + 1, comboBox1.getItems().size());
            } else {
                comboBox1.getItems().remove(0, selectedIndex - 1);
            }

            comboBox1.getItems().addAll(Serial.getPortList());
        });

        pane1.getChildren().addAll(new Label("  Port: "), comboBox1);

        HBox pane2 = new HBox();
        pane2.setAlignment(Pos.CENTER_LEFT);
        pane2.setMinSize(190, 30);

        ComboBox<String> comboBox2 = new ComboBox<>();
        comboBox2.getItems().addAll("1", "1.5", "2");
        comboBox2.getSelectionModel().selectFirst();
        PortManager.setStopBits(comboBox2.getSelectionModel().getSelectedItem());
        comboBox2.setOnAction(event -> {
            PortManager.setStopBits(comboBox2.getSelectionModel().getSelectedItem());
        });

        pane2.getChildren().addAll(new Label("  Stop bit: "), comboBox2);


        pane.getChildren().addAll(pane1, pane2);

        return pane;
    }

    private VBox initStatusPane() {
        VBox pane = new VBox();
        pane.setMinWidth(190);
        pane.setMaxSize(190, 50);

        Label label = new Label("  Baud rate: " + Serial.getBaudRate());
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        labelByteReceived.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        pane.getChildren().addAll(label, labelByteReceived);
        return pane;
    }

    @Override
    public void start(Stage stage) {
        GridPane root = new GridPane();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("COM port transmitter");
        stage.setMinWidth(400);
        stage.setMinHeight(205);

        StackPane inputPane = initInputPane();
        StackPane outputPane = initOutputPane();
        VBox controlPane = initControlPane();
        VBox statusPane = initStatusPane();

        root.add(inputPane, 0, 0);
        root.add(outputPane, 1, 0);
        root.add(controlPane, 0, 1);
        root.add(statusPane, 1, 1);

        stage.setResizable(false);
        stage.show();
    }

    public static void appendOutputText(String text) {
        if (output != null) {
            output.appendText(text);
        }
    }

    public static void setLabelByteReceived(int count) {
        Platform.runLater(() -> Window.labelByteReceived.setText("  Byte received: " + count));
    }

    public static void main(String[] args) {
        launch(args);
        try {
            PortManager.getPort().close();
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }
}
