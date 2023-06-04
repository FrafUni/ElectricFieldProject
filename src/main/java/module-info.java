module com.example.efpsprite {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.efpsprite to javafx.fxml;
    exports com.example.efpsprite;
    exports com.example.efpsprite.electricharges;
    opens com.example.efpsprite.electricharges to javafx.fxml;
    exports com.example.efpsprite.commons;
    opens com.example.efpsprite.commons to javafx.fxml;
}