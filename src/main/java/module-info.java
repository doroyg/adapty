module com.pakt.adapty {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    opens com.pakt.adapty to javafx.fxml;
    opens com.pakt.adapty.controller to javafx.fxml;
    exports com.pakt.adapty;
}