package com.github.moodtodie.term5_fcn.GUI;

import com.github.moodtodie.term5_fcn.bytestuffing.ByteStuffing;
import com.github.moodtodie.term5_fcn.serial.PortManager;
import com.github.moodtodie.term5_fcn.serial.Serial;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jssc.SerialPortException;

public class Window extends Application {
    private static TextArea output = null;
    private boolean canSend = true;
    private static final Label labelByteReceived = new Label("Byte received: 0");
    double padding = 3;

    //  Minimum size for panel
    private final int minWidth = 200;
    private final int minHeight = 180;

    //  Maximum size for panel
    private final int maxWidth = 400;
    private final int maxHeight = 300;
    private final int maxHeightSmall = 70;

    byte[] data = new byte[19];

    private StackPane initInputPane() {
        StackPane pane = new StackPane();
        pane.setMinSize(minWidth, minHeight);
        pane.setMaxSize(maxWidth, maxHeight);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(padding));

        TextArea input = new TextArea();
        input.setWrapText(true);

        input.setMinSize(minWidth, minHeight);

        input.setPromptText("Input field");

        // Устанавливаем обработчик события ContextMenuRequested
        input.setContextMenu(new ContextMenu());

        input.setOnKeyPressed(event -> {
            // Перемещаем курсор в конец строки
            input.positionCaret(input.getLength());

            // Проверяем, была ли нажата клавиша Ctrl
            if (event.isControlDown()) {
                // Игнорируем событие
                canSend = false;
                event.consume();
                return;
            }

            // Проверяем, какая клавиша была нажата
            switch (event.getCode()) {
                case TAB:
                case BACK_SPACE:
                case DELETE:
                    // Игнорируем событие
                    canSend = false;
                    event.consume();
                    break;
                default:
            }
        });

        input.setOnKeyTyped(event -> {
            if (canSend)
                try {
//                    data[] =  // <-------------------------------------------------------------------------------------------
//                    PortManager.getPort().write(event.getCharacter());

                    ByteStuffing.addData(event.getCharacter());
                    if (ByteStuffing.getDataByteSize() >= 19) {
                        PortManager.getPort().write(
                                ByteStuffing.convert(
                                        PortManager.getPort().getPortName(),
                                        ByteStuffing.getData())
                        );
                        ByteStuffing.clearData();
                    }

                } catch (SerialPortException e) {
                    throw new RuntimeException(e);
                }
            canSend = true;
        });

        pane.getChildren().add(input);
        return pane;
    }

    private StackPane initOutputPane() {
        StackPane pane = new StackPane();
        pane.setMinSize(minWidth, minHeight);
        pane.setMaxSize(maxWidth, maxHeight);
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(padding));

        output = new TextArea();
        output.setEditable(false);
        output.setMouseTransparent(true);
        output.setWrapText(true);
        output.setMinSize(minWidth, minHeight);
        output.setPromptText("Output field");

        pane.getChildren().add(output);

        return pane;
    }

    private VBox initControlPane() {
        VBox pane = new VBox();
        pane.setMinWidth(minWidth);
        pane.setMaxSize(maxWidth, maxHeightSmall);
        pane.setAlignment(Pos.BASELINE_CENTER);
        pane.setSpacing(padding * 2);
        pane.setPadding(new Insets(padding));

        //  ComboBox #1: Выбор последовательных портов
        HBox subPane1 = new HBox();
        subPane1.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> serialPortBox = new ComboBox<>();
        serialPortBox.getItems().addAll(Serial.getPortList());
        serialPortBox.getSelectionModel().selectFirst();

        //  Автоматический выбор свободного порта
        try {
            PortManager.setPort(serialPortBox.getSelectionModel().getSelectedItem());
        } catch (SerialPortException e) {
            throw new RuntimeException(e);
        }

        //  Выбор порта
        serialPortBox.setOnAction(event -> {
            try {
                PortManager.setPort(serialPortBox.getSelectionModel().getSelectedItem());
            } catch (SerialPortException e) {
                throw new RuntimeException(e);
            }
        });

        //  Динамическое обновление выпадающего списка
        serialPortBox.setOnMousePressed(event -> {
            int selectedIndex = serialPortBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex == serialPortBox.getItems().size() - 1) {
                serialPortBox.getItems().remove(0, selectedIndex);
            } else if (selectedIndex == 0) {
                serialPortBox.getItems().remove(1, serialPortBox.getItems().size());
            } else {
                serialPortBox.getItems().remove(0, selectedIndex);
                serialPortBox.getItems().remove(1, serialPortBox.getItems().size());
            }
            serialPortBox.getItems().addAll(Serial.getPortList());
        });

        subPane1.getChildren().addAll(new Label("Port: "), serialPortBox);

        //  ComboBox #2: Выбор стоп-бит
        HBox subPane2 = new HBox();
        subPane2.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> stopBitBox = new ComboBox<>();
        stopBitBox.getItems().addAll("1", "1.5", "2");
        stopBitBox.getSelectionModel().selectFirst();
        PortManager.setStopBits(stopBitBox.getSelectionModel().getSelectedItem());
        stopBitBox.setOnAction(event -> PortManager.setStopBits(stopBitBox.getSelectionModel().getSelectedItem()));

        subPane2.getChildren().addAll(new Label("Stop bit: "), stopBitBox);

        pane.getChildren().addAll(subPane1, subPane2);
        return pane;
    }

    private VBox initStatusPane() {
        VBox pane = new VBox();
        pane.setMinWidth(minWidth);
        pane.setMaxSize(maxWidth, maxHeightSmall);
        pane.setAlignment(Pos.TOP_LEFT);
        pane.setSpacing(padding * 2);
        pane.setPadding(new Insets(padding * 2));

        Label label = new Label("Baud rate: " + Serial.getBaudRate());
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        labelByteReceived.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        pane.getChildren().addAll(label, labelByteReceived);
        return pane;
    }

    @Override
    public void start(Stage stage) {
        GridPane root = new GridPane();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("style.css");

        stage.setScene(scene);
        stage.setTitle("COM port transmitter");
        stage.setResizable(false);

        root.setMinSize(minWidth * 2, minHeight);
        root.setMaxSize(maxWidth * 2, maxHeight + maxHeightSmall);

        root.add(initInputPane(), 0, 0);
        root.add(initOutputPane(), 1, 0);
        root.add(initControlPane(), 0, 1);
        root.add(initStatusPane(), 1, 1);

        stage.show();
    }

    public static void appendOutputText(String text) {
        if (output != null) {
            output.appendText(text);
        }
    }

    public static void setLabelByteReceived(int count) {
        if (count > 1)
            Platform.runLater(() -> Window.labelByteReceived.setText("Bytes received: " + count));
        else
            Platform.runLater(() -> Window.labelByteReceived.setText("Byte received: " + count));
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
