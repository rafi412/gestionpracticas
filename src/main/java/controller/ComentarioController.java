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
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.HikariCPConexion;
import model.Comentario;
import model.Practica;

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
        tablaComentarioTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        cargarDatosComentarios();
        cargarDatosEmpresas();

        tablaComentarioTable.setItems(comentariosList);
        idEmpresaComboBox.setItems(empresasList);

        agregarListenerCampoIdComentario();

        tablaComentarioTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        llenarCampos(newSelection);
                        idComentarioField.setEditable(false);
                    }
                });

        idComentarioField.textProperty().addListener((observable, oldValue, newValue) -> {
            insertButton.setDisable(!newValue.isEmpty());
        });

        // Limpiar selección de la tabla al hacer clic en cualquier parte del BorderPane
        comentarioBorderPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            boolean esNodoDeEdicion = idComentarioField.isHover() ||
                    idEmpresaComboBox.isHover() ||
                    fechaComentarioDatePicker.isHover() ||
                    notaField.isHover();

            if (!tablaComentarioTable.isHover() && !esNodoDeEdicion) {
                tablaComentarioTable.getSelectionModel().clearSelection();
            }
        });

        tablaComentarioTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Comentario>) change -> {
            // Verificar si hay más de un elemento seleccionado
            if (tablaComentarioTable.getSelectionModel().getSelectedItems().size() > 1) {
                editarButton.setDisable(true);  // Desactivar el botón Editar si hay más de un elemento seleccionado
            } else {
                editarButton.setDisable(false); // Habilitar el botón Editar si solo hay un elemento seleccionado
            }
        });

        tablaComentarioTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Comentario>) change -> {
            while (change.next()) {
                if (change.wasAdded() && change.getAddedSubList().size() > 1) {
                    // Evitar la selección de rangos eliminando los seleccionados no deseados
                    Comentario ultimoSeleccionado = change.getAddedSubList().get(change.getAddedSubList().size() - 1);
                    tablaComentarioTable.getSelectionModel().clearSelection();
                    tablaComentarioTable.getSelectionModel().select(ultimoSeleccionado);
                }
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
                limpiarCampos();
                cargarDatosComentarios();
                mostrarAlerta("Éxito", "Comentario insertado correctamente", AlertType.INFORMATION);
                ;
            
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
        try {
            idComentario = Integer.parseInt(idComentarioField.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID de comentario no es un número válido.", Alert.AlertType.ERROR);
            return;
        }

        String idEmpresa = idEmpresaComboBox.getValue();
        String fechaComentario = fechaComentarioDatePicker.getValue() != null
                ? fechaComentarioDatePicker.getValue().toString()
                : null;
        String nota = notaField.getText();

        String empresaID = idEmpresa.split(" - ")[0];

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
        ObservableList<Comentario> seleccionados = FXCollections.observableArrayList(
                tablaComentarioTable.getSelectionModel().getSelectedItems());

        if (seleccionados.isEmpty()) {
            mostrarAlerta("Error", "Por favor, selecciona uno o más comentarios para eliminar.", Alert.AlertType.ERROR);
            return;
        }

        // Confirmación de eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar los comentarios seleccionados?");

        if (confirmacion.showAndWait().get() != ButtonType.OK) {
            return;
        }

        String query = "DELETE FROM comentario_empresa WHERE ID_Comentario = ?";

        try (Connection connection = HikariCPConexion.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Comentario comentario : seleccionados) {
                preparedStatement.setInt(1, comentario.getIdComentario());
                preparedStatement.executeUpdate();
            }

            // Eliminar elementos seleccionados de la lista de datos
            comentariosList.removeAll(seleccionados);

            // Limpiar la selección
            tablaComentarioTable.getSelectionModel().clearSelection();
            limpiarCampos();
            mostrarAlerta("Éxito", "Comentarios eliminados correctamente.", Alert.AlertType.INFORMATION);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al eliminar los comentarios: " + e.getMessage(), Alert.AlertType.ERROR);
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

    private void agregarListenerCampoIdComentario() {
        idComentarioField.textProperty().addListener((observable, oldValue, newValue) -> {
            insertButton.setDisable(newValue != null && !newValue.trim().isEmpty());
        });

        idComentarioField
                .setDisable(idComentarioField.getText() != null && !idComentarioField.getText().trim().isEmpty());
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
                        resultSet.getString("Nota"));
                comentariosList.add(comentario);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar datos de la tabla Comentario_Empresa: " + e.getMessage());
        }
    }

    private void llenarCampos(Comentario comentario) {
        idComentarioField.setText(Integer.toString(comentario.getIdComentario()));
        
        String query = "SELECT ID_Empresa, Nombre FROM empresa WHERE ID_Empresa = ?";

        try(Connection connection = HikariCPConexion.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, comentario.getIdEmpresa());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String empresa = resultSet.getInt("ID_Empresa") + " - " + resultSet.getString("Nombre");
            idEmpresaComboBox.setValue(empresa);
        } catch (SQLException e) {
            System.out.println("Error al cargar datos de la tabla Empresa: " + e.getMessage());
        }

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
