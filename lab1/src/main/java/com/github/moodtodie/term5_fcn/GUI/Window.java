package com.github.moodtodie.term5_fcn.GUI;

import com.github.moodtodie.term5_fcn.bytestuffing.ByteStuffing;
import com.github.moodtodie.term5_fcn.bytestuffing.Packet;
import com.github.moodtodie.term5_fcn.serial.PortManager;
import com.github.moodtodie.term5_fcn.serial.Serial;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jssc.SerialPortException;

import java.nio.charset.StandardCharsets;

public class Window extends Application {
  private static TextArea output = null;
  private boolean canSend = true;
  private static final Label labelByteReceived = new Label("Byte received: 0");
  //  private static final TextArea textSentPacket = new TextArea("The package hasn't been sent yet");
  private static final HBox panelSentPacket = new HBox(new Label("The package hasn't been sent yet"));
  double padding = 3;

  //  Minimum size for panel
  private final int minWidth = 200;
  private final int minHeight = 180;

  //  Maximum size for panel
  private final int maxWidth = 400;
  private final int maxHeight = 300;
  private final int maxHeightSmall = 70;

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
          ByteStuffing.addData(event.getCharacter());
          if (ByteStuffing.getDataByteSize() >= 19) {
            Packet packet = new Packet( //  Create Packet
                //  Port number
                (byte) PortManager.getPort().getPortName().charAt(PortManager.getPort().getPortName().length() - 1),
                //  Data
                ByteStuffing.getData().getBytes(StandardCharsets.UTF_8)
            );
            setLabelPacket(packet.toString());
            PortManager.getPort().write(packet.getBytes());
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

//    panelSentPacket
    ScrollPane scrollPane = new ScrollPane(panelSentPacket);
    scrollPane.setMinSize(minWidth, 46);

    pane.getChildren().addAll(label, labelByteReceived, scrollPane);
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

  public static void setLabelPacket(String packet) {
    Platform.runLater(() -> panelSentPacket.getChildren().clear());
    Platform.runLater(() -> panelSentPacket.getChildren().add(new Label("Sent package: ")));
    Platform.runLater(() -> panelSentPacket.getChildren().add(new Label(packet.substring(packet.indexOf("{"), packet.indexOf("data=[") + 6))));

    String style = "-fx-font-weight: bold; -fx-underline: true";
    String data = packet.substring(packet.indexOf("data=[") + 6, packet.indexOf("], fcs="));

    int firstIndex = 0;
    String numder = data;
    int counter = 0;

    while (firstIndex < numder.length()) {
      Label label = new Label();
      numder = numder.substring(firstIndex);

      String p = numder.substring(0, getLastIndex(numder));

      if (p.equals("35") && isStaffing(numder))
        counter = 4;

      label.setText(p);

      if (counter > 0) {
        label.setStyle(style);
        Platform.runLater(() -> panelSentPacket.getChildren().add(label));
        counter--;
      } else
        Platform.runLater(() -> panelSentPacket.getChildren().add(label));

      firstIndex = getLastIndex(numder) + 2;

      if (firstIndex < numder.length())
        Platform.runLater(() -> panelSentPacket.getChildren().add(new Label(", ")));
    }

    Platform.runLater(() -> panelSentPacket.getChildren().add(new Label(packet.substring(packet.indexOf("], fcs=")))));
  }

  private static boolean isStaffing(String source) {
    String string = source;
    for (int i = 0; i < 3; i++) {
      String p = string.substring(0, getLastIndex(string));
      if (p.equals("36") && i == 2)
        return true;
      string = string.substring(getLastIndex(string) + 2);
    }
    return false;
  }

  private static int getLastIndex(String tail) {
    int indexOfEnd = 0;
    for (int j = 0; j < tail.length() && tail.charAt(j) != ','; j++)
      indexOfEnd = j + 1;
    return indexOfEnd;
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
