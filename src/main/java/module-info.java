module com.github.arthurdeka.cedromoderndock {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.github.arthurdeka.cedromoderndock.controller to javafx.fxml;
    opens com.github.arthurdeka.cedromoderndock to javafx.fxml;
    opens com.github.arthurdeka.cedromoderndock.fxml to javafx.fxml;
    exports com.github.arthurdeka.cedromoderndock;
}