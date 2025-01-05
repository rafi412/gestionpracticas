package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.HikariCPConexion;
import model.Alumno;
import model.Practica;
import model.Visita;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class VisitaController {

    @FXML
    private BorderPane visitaBorderPane;

    @FXML
    private Button borrarButton;

    @FXML
    private Button cancelarButton;

    @FXML
    private TableColumn<Visita, String> comentarioTutorColumn;

    @FXML
    private TextField comentarioTutorField;

    @FXML
    private TableColumn<Visita, String> dniTutorColumn;

    @FXML
    private ComboBox<String> dniTutorComboBox;

    @FXML
    private Button editarButton;

    @FXML
    private TableColumn<Visita, String> fechaVisitaColumn;

    @FXML
    private DatePicker fechaVisitaDatePicker;

    @FXML
    private TableColumn<Visita, String> idPracticaColumn;

    @FXML
    private ComboBox<String> idPracticaComboBox;

    @FXML
    private TableColumn<Visita, String> idVisitaColumn;

    @FXML
    private TextField idVisitaField;

    @FXML
    private Button insertButton;

    @FXML
    private Button volverButton;

    @FXML
    private TableColumn<Visita, String> observacionesColumn;

    @FXML
    private TextField observacionesField;

    @FXML
    private TableView<Visita> tablaVisitaTable;

    private ObservableList<Visita> visitasList;
    private ObservableList<String> practicasList;
    private ObservableList<String> tutoresList;

    public VisitaController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/verTablaVisita.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    public void initialize() {
        idVisitaColumn.setCellValueFactory(new PropertyValueFactory<>("idVisita"));
        idPracticaColumn.setCellValueFactory(new PropertyValueFactory<>("idPractica"));
        dniTutorColumn.setCellValueFactory(new PropertyValueFactory<>("dniTutor"));
        fechaVisitaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaVisita"));
        observacionesColumn.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
        comentarioTutorColumn.setCellValueFactory(new PropertyValueFactory<>("comentarioTutor"));

        idVisitaField.setEditable(false);

        visitasList = FXCollections.observableArrayList();
        practicasList = FXCollections.observableArrayList();
        tutoresList = FXCollections.observableArrayList();
        tablaVisitaTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tablaVisitaTable.setItems(visitasList);

        agregarListenerCampoIdVisita();

        tablaVisitaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                llenarCampos(newSelection);
                idVisitaField.setEditable(false);
            }
        });

        visitaBorderPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            boolean esNodoDeEdicion = idVisitaField.isHover() ||
                    idPracticaComboBox.isHover() ||
                    fechaVisitaDatePicker.isHover() ||
                    comentarioTutorField.isHover();

            if (!tablaVisitaTable.isHover() && !esNodoDeEdicion) {
                tablaVisitaTable.getSelectionModel().clearSelection();

            }
        });

        tablaVisitaTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Visita>) change -> {
            // Verificar si hay más de un elemento seleccionado
            if (tablaVisitaTable.getSelectionModel().getSelectedItems().size() > 1) {
                editarButton.setDisable(true); // Desactivar el botón Editar si hay más de un elemento seleccionado
            } else {
                editarButton.setDisable(false); // Habilitar el botón Editar si solo hay un elemento seleccionado
            }
        });

        cargarDatos();
        cargarPracticas();
        cargarTutores();

        idPracticaComboBox.setItems(practicasList);
        dniTutorComboBox.setItems(tutoresList);

    }

    @FXML
    void insertButton(ActionEvent event) {
        String idPractica = idPracticaComboBox.getValue();
        String dniTutor = dniTutorComboBox.getValue();
        String fechaVisita = fechaVisitaDatePicker.getValue() != null ? fechaVisitaDatePicker.getValue().toString()
                : "";
        String observaciones = observacionesField.getText();
        String comentarioTutor = comentarioTutorField.getText();

        if (idPractica == null || dniTutor == null || fechaVisita.isEmpty() || observaciones.isEmpty()
                || comentarioTutor.isEmpty()) {
            mostrarAlerta("Error", "Completa todos los campos requeridos.", Alert.AlertType.ERROR);
            return;
        }

        String practidaId = idPractica.split(" - ")[0];
        String tutorDNI = dniTutor.split(" - ")[0];

        String query = "INSERT INTO visita_seguimiento (ID_Practica, DNI_Tutor, Fecha_Visita, Observaciones, Comentario_Tutor) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = HikariCPConexion.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, practidaId);
            statement.setString(2, tutorDNI);
            statement.setString(3, fechaVisita);
            statement.setString(4, observaciones);
            statement.setString(5, comentarioTutor);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                limpiarCampos();
                cargarDatos();
                mostrarAlerta("Éxito", "Visita insertada correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se pudo insertar la visita.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al insertar visita: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void updateButton(ActionEvent event) {

        int idVisitaEditar;
        try {
            idVisitaEditar = Integer.parseInt(idVisitaField.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El ID de la visita no existe", Alert.AlertType.ERROR);
            return;
        }
        String idPractica = idPracticaComboBox.getValue();
        String dniTutor = dniTutorComboBox.getValue();
        String observaciones = observacionesField.getText();
        String comentarioTutor = comentarioTutorField.getText();
        String fechaVisita = fechaVisitaDatePicker.getValue() != null ? fechaVisitaDatePicker.getValue().toString()
                : null;

        String practidaId = idPractica.split(" - ")[0];
        String tutorDNI = dniTutor.split(" - ")[0];

        String query = "UPDATE visita_seguimiento SET ID_Practica = ?, DNI_Tutor = ?, Fecha_Visita = ?, Observaciones = ?, Comentario_Tutor = ? WHERE ID_Visita = ?";

        try (Connection connection = HikariCPConexion.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, practidaId);
            preparedStatement.setString(2, tutorDNI);
            preparedStatement.setString(3, fechaVisita);
            preparedStatement.setString(4, observaciones);
            preparedStatement.setString(5, comentarioTutor);
            preparedStatement.setInt(6, idVisitaEditar);

            int filasActualizadas = preparedStatement.executeUpdate();

            if (filasActualizadas > 0) {
                cargarDatos();
                limpiarCampos();
                mostrarAlerta("Éxito", "Visita actualizada correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se pudo actualizar la visita.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar la visita: " + e.getMessage(), Alert.AlertType.ERROR);
        }

    }

    @FXML
    void deleteButton(ActionEvent event) {
        ObservableList<Visita> visitasSeleccionadas = tablaVisitaTable.getSelectionModel().getSelectedItems();

        if (visitasSeleccionadas.isEmpty()) {
            mostrarAlerta("Error", "Selecciona una visita", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que quieres eliminar las visitas seleccionadas?");

        if (confirmacion.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try (Connection connection = HikariCPConexion.getConnection()) {
            String query = "DELETE FROM visita_seguimiento WHERE ID_Visita = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (Visita visita : visitasSeleccionadas) {
                preparedStatement.setInt(1, visita.getIdVisita());
                preparedStatement.executeUpdate();
            }

            visitasList.removeAll(visitasSeleccionadas);

            tablaVisitaTable.getSelectionModel().clearSelection();
            limpiarCampos();
            cargarDatos();
            mostrarAlerta("Éxito", "Visitas eliminadas", AlertType.INFORMATION);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al eliminar la visita: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void actionVolverButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/mainView.fxml"));

            MainController mainController = new MainController();
            loader.setController(mainController);

            Parent mainView = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Gestión de Prácticas");
            Scene scene = new Scene(mainView);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void actionCancelarButton(ActionEvent event) {
        limpiarCampos();
    }

    private void agregarListenerCampoIdVisita() {
        idVisitaField.textProperty().addListener((observable, oldValue, newValue) -> {
            insertButton.setDisable(newValue != null && !newValue.trim().isEmpty());
        });

        insertButton.setDisable(idVisitaField.getText() != null && !idVisitaField.getText().trim().isEmpty());
    }

    private void limpiarCampos() {
        idVisitaField.clear();
        idPracticaComboBox.setValue(null);
        dniTutorComboBox.setValue(null);
        fechaVisitaDatePicker.setValue(null);
        observacionesField.clear();
        comentarioTutorField.clear();
    }

    private void llenarCampos(Visita visita) {

        idVisitaField.setText(Integer.toString(visita.getIdVisita()));
        idPracticaComboBox.setValue(String.valueOf(visita.getIdPractica()));
        dniTutorComboBox.setValue(visita.getDniTutor());
        observacionesField.setText(visita.getObservaciones());
        comentarioTutorField.setText(visita.getComentarioTutor());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (visita.getFechaVisita() != null && !visita.getFechaVisita().isEmpty()) {
            try {
                LocalDate fechaVisita = LocalDate.parse(visita.getFechaVisita(), formatter);
                fechaVisitaDatePicker.setValue(fechaVisita);
            } catch (DateTimeParseException e) {
                System.out.println("Error al convertir la fecha: " + e.getMessage());
                fechaVisitaDatePicker.setValue(null);
            }
        } else {
            fechaVisitaDatePicker.setValue(null);
        }

    }

    private void cargarDatos() {
        visitasList.clear();
        String query = "SELECT * FROM visita_seguimiento";

        try (Connection connection = HikariCPConexion.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Visita visita = new Visita(
                        resultSet.getInt("ID_Visita"),
                        resultSet.getInt("ID_Practica"),
                        resultSet.getString("DNI_Tutor"),
                        resultSet.getString("Fecha_Visita"),
                        resultSet.getString("Observaciones"),
                        resultSet.getString("Comentario_Tutor"));
                visitasList.add(visita);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarPracticas() {
        String query = "SELECT ID_Practica FROM practicas";
        String query2 = "SELECT Nombre, Apellido FROM alumno INNER JOIN practicas ON alumno.DNI_Alumno = practicas.DNI_Alumno";

        try (Connection connection = HikariCPConexion.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                PreparedStatement statement2 = connection.prepareStatement(query2);
                ResultSet resultSet = statement.executeQuery();
                ResultSet resultSet2 = statement2.executeQuery()) {

            while (resultSet.next() && resultSet2.next()) {
                practicasList.add(resultSet.getString("ID_Practica") + " - " + resultSet2.getString("Nombre") + " "
                        + resultSet2.getString("Apellido"));
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar prácticas: " + e.getMessage(), Alert.AlertType.ERROR);
        }

    }

    private void cargarTutores() {
        String query = "SELECT DNI_Tutor_Empresa, Nombre, Apellido FROM tutor_empresa";

        try (Connection connection = HikariCPConexion.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tutoresList.add(resultSet.getString("DNI_Tutor_Empresa") + " - " + resultSet.getString("Nombre") + " "
                        + resultSet.getString("Apellido"));
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar tutores: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public BorderPane getVisitaBorderPane() {
        return visitaBorderPane;
    }
}