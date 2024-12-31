package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private Button buttonAlumno;

    @FXML
    private Button buttonComentarioEmpresa;

    @FXML
    private Button buttonEmpresa;

    @FXML
    private Button buttonPractica;

    @FXML
    private Button buttonVisitaSeguimiento;

    @FXML
    private BorderPane mainPane;

    public MainController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainView.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    void onButtonAlumnoAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/VerTablaAlumno.fxml"));

            AlumnoController alumnoController = new AlumnoController();
            loader.setController(alumnoController);

            Parent alumnoView = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            stage.setTitle("Alumnos");
            Scene scene = new Scene(alumnoView);
            stage.setScene(scene);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onButtonComentarioEmpresaAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/verTablaComentario.fxml"));

            ComentarioController comentarioController = new ComentarioController();
            loader.setController(comentarioController);

            Parent comentarioView = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            stage.setTitle("Comentarios de Empresas");
            Scene scene = new Scene(comentarioView);
            stage.setScene(scene);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onButtonEmpresaAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/verTablaEmpresa.fxml"));

            EmpresaController empresaController = new EmpresaController();
            loader.setController(empresaController);

            Parent empresaView = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            stage.setTitle("Empresas");
            Scene scene = new Scene(empresaView);
            stage.setScene(scene);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onButtonPracticaAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/verTablaPracitca.fxml"));

            PracticaController practicaController = new PracticaController();
            loader.setController(practicaController);

            Parent practicaView = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            stage.setTitle("Prácticas");
            Scene scene = new Scene(practicaView);
            stage.setScene(scene);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onButtonVisitaSeguimiento(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/verTablaVisita.fxml"));

            VisitaController visitaController = new VisitaController();
            loader.setController(visitaController);

            Parent visitaView = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            stage.setTitle("Visitas de Seguimiento");
            Scene scene = new Scene(visitaView);
            stage.setScene(scene);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BorderPane getView() {
        return mainPane;
    }

}