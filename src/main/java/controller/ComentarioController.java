package controller;

import javafx.collections.FXCollections;
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
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.HikariCPConexion;
import model.Comentario;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ComentarioController {

    @FXML
    private Button borrarButton;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private DatePicker fechaComentarioDatePicker;

    @FXML
    private BorderPane comentarioBorderPane;

    @FXML
    private Button editarButton;

    @FXML
    private TableColumn<Comentario, String> fechaComentarioColumn;

    @FXML
    private TextField fechaComentarioField;

    @FXML
    private Button volverButton;

    @FXML
    private TableColumn<Comentario, String> idComentarioColumn;

    @FXML
    private TextField idComentarioField;

    @FXML
    private TableColumn<Comentario, String> idEmpresaColumn;

    @FXML
    private ComboBox<String> idEmpresaComboBox;

    @FXML
    private Button insertButton;

    @FXML
    private TableColumn<Comentario, String> notaColumn;

    @FXML
    private TextField notaField;

    @FXML
    private TableView<Comentario> tablaComentarioTable;

    private ObservableList<Comentario> comentariosList;
    private ObservableList<String> empresasList;

    public ComentarioController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/verTablaComentario.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    public void initialize() {
        // Vincula las columnas con los atributos del modelo Comentario
        idComentarioColumn.setCellValueFactory(new PropertyValueFactory<>("idComentario"));
        idEmpresaColumn.setCellValueFactory(new PropertyValueFactory<>("idEmpresa"));
        fechaComentarioColumn.setCellValueFactory(new PropertyValueFactory<>("fechaComentario"));
        notaColumn.setCellValueFactory(new PropertyValueFactory<>("nota"));

        idComentarioField.setEditable(false);

        comentariosList = FXCollections.observableArrayList();
        empresasList = FXCollections.observableArrayList();

        cargarDatosComentarios();
        cargarDatosEmpresas();

        tablaComentarioTable.setItems(comentariosList);
        idEmpresaComboBox.setItems(empresasList);

        agregarListenerCampoIdComentario();

        tablaComentarioTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                llenarCampos(newSelection);
                idComentarioField.setEditable(false);
            }
        });

        idComentarioField.textProperty().addListener((observable, oldValue, newValue) -> {
            insertButton.setDisable(!newValue.isEmpty());
        });

        comentarioBorderPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            boolean esNodoDeEdicion = idComentarioField.isHover() ||
                    idEmpresaComboBox.isHover() ||
                    fechaComentarioDatePicker.isHover() ||
                    notaField.isHover();

            if (!tablaComentarioTable.isHover() && !esNodoDeEdicion) {
                tablaComentarioTable.getSelectionModel().clearSelection();

            }
        });

    }

    @FXML
    void insertButton(ActionEvent event) {
        String idEmpresaText = idEmpresaComboBox.getValue();
        String nota = notaField.getText().trim();

        if (idEmpresaText == null || idEmpresaText.isEmpty()) {
            mostrarAlerta("Error", "El campo 'ID Empresa' debe contener un ID válido.", Alert.AlertType.ERROR);
            return;
        }
        if (fechaComentarioDatePicker.getValue() == null) {
            mostrarAlerta("Error", "El campo Fecha no puede estar vacio", Alert.AlertType.ERROR);
            return;
        }

        String fechaComentario = fechaComentarioDatePicker.getValue().toString();

        if (nota.isEmpty()) {
            mostrarAlerta("Error", "El campo Nota no puede estar vacio", Alert.AlertType.ERROR);
            return;
        }

        String[] idEmpresaParts = idEmpresaText.split(" - ");
        int idEmpresa;
        try {
            idEmpresa = Integer.parseInt(idEmpresaParts[0]);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El campo 'ID Empresa' debe contener un ID válido.", Alert.AlertType.ERROR);
            return;
        }

        String query = "INSERT INTO comentario_empresa (ID_Empresa, Fecha_Comentario, Nota) VALUES (?, ?, ?)";
        try (Connection connection = HikariCPConexion.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idEmpresa);
            preparedStatement.setString(2, fechaComentario);
            preparedStatement.setString(3, nota);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                mostrarAlerta("Éxito", "Comentario insertado correctamente", AlertType.INFORMATION);;
                comentariosList.clear();
                cargarDatosComentarios();
            } else {
                mostrarAlerta("Error", "No se pudo insertar el comentario", AlertType.ERROR);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
        limpiarCampos();

    }

    @FXML
    void updateButton(ActionEvent event) {
        int idComentario;
        try{
            idComentario = Integer.parseInt(idComentarioField.getText());
        } catch(NumberFormatException e){
            mostrarAlerta("Error", "ID de comentario no es un número válido.", Alert.AlertType.ERROR);
            return;
        }



        String idEmpresa = idEmpresaComboBox.getValue();
        String fechaComentario = fechaComentarioDatePicker.getValue() != null ? fechaComentarioDatePicker.getValue().toString() : null;
        String nota = notaField.getText();

        String empresaID =idEmpresa.split(" - ")[0];

        String query = "UPDATE comentario_empresa SET ID_Empresa = ?, Fecha_Comentario = ?, Nota = ? WHERE ID_Comentario = ? ";

        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, empresaID);
            preparedStatement.setString(2, fechaComentario);
            preparedStatement.setString(3, nota);
            preparedStatement.setInt(4, idComentario);

            int filasActualizadas = preparedStatement.executeUpdate();

            if (filasActualizadas > 0) {
                cargarDatosComentarios();
                limpiarCampos();
                mostrarAlerta("Éxito", "Comentario actualizado correctamente.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el comentario.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar el comentario: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        limpiarCampos();
        comentariosList.clear();
        cargarDatosComentarios();

    }

    @FXML
    void deleteButton(ActionEvent event) {
        Comentario comentarioSeleccionado = tablaComentarioTable.getSelectionModel().getSelectedItem();

        if (comentarioSeleccionado == null) {
            mostrarAlerta("Error", "Selecciona un comentario para eliminar", Alert.AlertType.ERROR);
            return;
        }

        int idEliminar = comentarioSeleccionado.getIdComentario();

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("Estas seguro de que deseas eliminar el comentario con ID " + idEliminar + "?");

        if (confirmacion.showAndWait().get() != ButtonType.OK) {
            return;
        }

        String query = "DELETE FROM comentario_empresa WHERE ID_Comentario = ?";

        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idEliminar);
            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                comentariosList.remove(comentarioSeleccionado);
                tablaComentarioTable.getSelectionModel().clearSelection();
                limpiarCampos();
                mostrarAlerta("Éxito", "Comentario eliminada correctamente.", Alert.AlertType.INFORMATION);

            } else {
                mostrarAlerta("Error", "No se pudo eliminar el comentario. Por favor, inténtalo de nuevo.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al eliminar la práctica: " + e.getMessage(), Alert.AlertType.ERROR);
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

    private void agregarListenerCampoIdComentario () {
        idComentarioField.textProperty().addListener((observable, oldValue, newValue) -> {
            insertButton.setDisable(newValue != null && !newValue.trim().isEmpty());
        });

        idComentarioField.setDisable(idComentarioField.getText() != null && !idComentarioField.getText().trim().isEmpty());
    }

    private void limpiarCampos() {
        idComentarioField.clear();
        idEmpresaComboBox.setValue(null);
        fechaComentarioDatePicker.setValue(null);
        notaField.clear();
    }

    private void cargarDatosComentarios() {
        comentariosList.clear();
        String query = "SELECT * FROM comentario_empresa";

        try (Connection connection = HikariCPConexion.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Comentario comentario = new Comentario(
                        resultSet.getInt("ID_Comentario"),
                        resultSet.getInt("ID_Empresa"),
                        resultSet.getString("Fecha_Comentario"),
                        resultSet.getString("Nota")
                );
                comentariosList.add(comentario);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar datos de la tabla Comentario_Empresa: " + e.getMessage());
        }
    }

    private void llenarCampos(Comentario comentario) {
        idComentarioField.setText(Integer.toString(comentario.getIdComentario()));
        idEmpresaComboBox.setValue(Integer.toString(comentario.getIdEmpresa()));
        notaField.setText(comentario.getNota());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (comentario.getFechaComentario() != null && !comentario.getFechaComentario().isEmpty()) {
            try {
                LocalDate fechaComentario = LocalDate.parse(comentario.getFechaComentario(), formatter);
                fechaComentarioDatePicker.setValue(fechaComentario);
            } catch (DateTimeParseException e) {
                System.out.println("Error al convertir la fecha: " + e.getMessage());
                fechaComentarioDatePicker.setValue(null);
            }
        } else {
            fechaComentarioDatePicker.setValue(null);
        }

    }

    private void cargarDatosEmpresas() {
        String query = "SELECT ID_Empresa, Nombre FROM empresa";

        try (Connection connection = HikariCPConexion.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String empresa = resultSet.getInt("ID_Empresa") + " - " + resultSet.getString("Nombre");
                empresasList.add(empresa);
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar datos de la tabla Empresa: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public BorderPane getComentarioAnchorPane() {
        return comentarioBorderPane;
    }
}
