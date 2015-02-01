package org.jzy3d.javafx;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.jzy3d.chart.Chart;

public class JavaFXHelper {

    public StackPane makeSceneInPane(Chart chart, Stage stage) {
        StackPane pane = new StackPane();
        Scene scene = makeScene(chart, stage, pane);
        return pane;
    }

    public Scene makeScene(Chart chart, Stage stage, StackPane sp) {
        Scene scene = new Scene(sp);
        stage.setScene(scene);
        stage.show();
        return scene;
    }
}
