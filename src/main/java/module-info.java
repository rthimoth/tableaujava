module com.example.tableau {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tableau to javafx.fxml;
    exports com.example.tableau;
}