module fr.expenses.tableau {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;


    opens fr.expenses.tableau to javafx.fxml;
    exports fr.expenses.tableau;
}