module com.example.electricfieldproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.electricfieldproject to javafx.fxml;
    exports com.example.electricfieldproject;
    exports com.example.electricfieldproject.electricharges;
    opens com.example.electricfieldproject.electricharges to javafx.fxml;
    exports com.example.electricfieldproject.commons;
    opens com.example.electricfieldproject.commons to javafx.fxml;
}