module org.example.vocabularyjc25 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.vocabularyjc25 to javafx.fxml;
    exports org.example.vocabularyjc25;
}