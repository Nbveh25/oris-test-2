module ru.kpfu.itis.bikmukhametov.oristest2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens ru.kpfu.itis.bikmukhametov.oristest2 to javafx.fxml;
    exports ru.kpfu.itis.bikmukhametov.oristest2;
}