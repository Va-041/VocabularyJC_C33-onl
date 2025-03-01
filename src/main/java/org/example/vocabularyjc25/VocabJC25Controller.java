// 1. Разобраться с кодом;
// 2. Посмотреть как идет связь между бэком и графикой (вопрос контроллера)
// 3. Привести все форму в нормальное состояние и все правильно расположить (можно просто через SceneBuilder)
// 4. Добавить кнопку (четвертую) "Закрыть", которое будет ЗАКРЫВТЬ окно, т.е. прекращать программу;
// 5. Все данные хранить в коллекции, сами выберите какую коллекцию хотите, Map
// 6. Если в документе, где хранятся значения есть данные, все надо сохранить в коллекции и вывести мне на экран
// 7. Если ничего нет, то просто пусто
// 8. Нажав на кнопку Добавить я должен иметь возможность добавлять в конец
// 9. Все добавленное также должно храниться уже в обновленном списке
// 10. Нажав на кнопку Save весь новый список должен сохраниться в документе
// 11. Если я нажму на кнопку Удалить, из списка должен быть удален элемент, также и коллекции и соответственно
// его не должно быть в документ
// 12. Каждый раз, когда я буду закрывать окно через кнопку Закрыть, он также должен сохранить все изменения в документе


// все пункты были выполнены, было добавлено предупреждение под самой строкой ввода если там пустое значение, также
// модальное окно при закрытии без сохранения.
package org.example.vocabularyjc25;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class VocabJC25Controller implements Initializable {
    @FXML
    public ListView<String> listView;
    @FXML
    public Button delete;
    @FXML
    public Button save;
    @FXML
    private TextField textValue;
    @FXML
    private Button add;
    @FXML
    private Label warningLabel;

    ArrayList<String> storeValues;
    private ArrayList<String> originalValues;

    // Create File & Read / Write
    private final File file;
    BufferedReader reader;

    // Not everything can be by default created and sent to a constructor
    // We must keep in mind timing and the way constructors are build and objects are created
    public VocabJC25Controller() {

        listView = new ListView<>();
        file = new File("src/main/resources/vocabulary.txt");
        storeValues = new ArrayList<>();
        originalValues = new ArrayList<>();
    }

    @FXML
    public void addWord(ActionEvent actionEvent) {

        if (textValue.getText().isBlank() || textValue.getText().isEmpty()) {
            System.out.println("Your field is either blank or empty...");

            warningLabel.setVisible(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> warningLabel.setVisible(false));
            pause.play();

            return;
        }

        String newWord = textValue.getText();
        listView.getItems().add(newWord);
        storeValues.add(newWord);           //добавление в коллекцию
        textValue.clear();                  //очистка поля ввода
    }

    @FXML
    public void deleteWord(ActionEvent actionEvent) {

        String selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            listView.getItems().remove(selectedItem);
            storeValues.remove(selectedItem);
        } else {
            System.out.println("No word selected to delete.");
        }
    }

    @FXML
    public void saveVocabulary(ActionEvent actionEvent) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String word : storeValues) {
                writer.write(word);
                writer.newLine();
            }
            originalValues.clear();
            originalValues.addAll(storeValues);     //обновление оригинальных значений после сохранения
            System.out.println("\nVocabulary saved successfully!");
        } catch (IOException e) {
            System.out.println("IOException error: " + e.getMessage());
        }
    }

    private boolean changesExist() {
        return !storeValues.equals(originalValues);
    }

    @FXML
    public void closeWindow(ActionEvent actionEvent) {

        //модальное окно с подтверждением выхода без сохранения
        if (changesExist()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes.");
            alert.setContentText("Do you want to save your changes before exiting?");
            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No");
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeYes) {
                saveVocabulary(actionEvent);
            }
        }
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // method which create a code once some object have been initialized
        // Read document and save values to ArrayList
        // Then add all ArrayList values to listview -> listview.getItems().addAll(_array);
        try {
            String line;
            reader = new BufferedReader(new FileReader(file));

            while ((line = reader.readLine()) != null) {
                listView.getItems().add(line);
                storeValues.add(line);      //cохранение в коллекцию ArrayList<String>
                originalValues.add(line);   //начальные значения
            }

        } catch (FileNotFoundException e) {
            System.out.println("File was not found!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //добавление обработчика события на нажатие клавиши Enter (добавление слова по клавише Enter)
        textValue.setOnAction(this::addWord);

        //добавление обработчика события на нажатие клавиши Delete (удаление слова по клавише Del)
        listView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DELETE) {
                deleteWord(new ActionEvent());
            }

        });
    }
}
