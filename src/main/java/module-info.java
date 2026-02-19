module sn.mouhammad.isi.candidats {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires jakarta.persistence;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires jakarta.validation;

    opens sn.mouhammad.isi.candidats to javafx.fxml;
    opens sn.mouhammad.isi.candidats.entity to javafx.fxml, org.hibernate.orm.core, javafx.base;
    exports sn.mouhammad.isi.candidats;
}