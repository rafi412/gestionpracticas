package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Alumno;
import model.Empresa;
import model.Practica;
import main.HikariCPConexion;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PracticaController {

    @FXML
    private BorderPane practicaBorderPane;

    @FXML
    private TableColumn<Practica, String> dniAlumnoColumn;

    @FXML
    private TableColumn<Practica, String> fechaFinColumn;

    @FXML
    private DatePicker fechaFinDatePicker;

    @FXML
    private DatePicker fechaInicioDatePicker;

    @FXML
    private TableColumn<Practica, String> fechaInicioColumn;

    @FXML
    private TextField fechaInicioField;

    @FXML
    private TableColumn<Practica, String> idPracticaColumn;

    @FXML
    private TableColumn<Practica, String> idEmpresaColumn;

    @FXML
    private TextField idPracticaField;

    @FXML
    private Button insertarButton;

    @FXML
    private Button editarButton;

    @FXML
    private Button cancelarButton;

    @FXML
    private TableView<Practica> tablaPracticaTable;

    @FXML
    private ComboBox<String> dniAlumnoComboBox;

    @FXML
    private ComboBox<String> idEmpresaComboBox;

    @FXML
    private Button volverButton;

    @FXML
    private TextField buscarTextField;

    @FXML
    private Button buscarButton;

    private ObservableList<Practica> practicasList;
    private ObservableList<String> alumnosList;
    private ObservableList<String> empresasList;

    public PracticaController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/verTablaPracitca.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    public void initialize() {
        // Vincula las columnas con los atributos del modelo Practica
        idPracticaColumn.setCellValueFactory(new PropertyValueFactory<>("idPractica"));
        dniAlumnoColumn.setCellValueFactory(new PropertyValueFactory<>("dniAlumno"));
        idEmpresaColumn.setCellValueFactory(new PropertyValueFactory<>("idEmpresa"));
        fechaInicioColumn.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        fechaFinColumn.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));

        idPracticaField.setEditable(false);

        practicasList = FXCollections.observableArrayList();
        alumnosList = FXCollections.observableArrayList();
        empresasList = FXCollections.observableArrayList();
        tablaPracticaTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tablaPracticaTable.setItems(practicasList);

        agregarListenerCampoIdPractica();

        tablaPracticaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                llenarCampos(newSelection);
                idPracticaField.setEditable(false);
            }
        });

        practicaBorderPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            boolean esNodoDeEdicion = idPracticaField.isHover() ||
                    idEmpresaComboBox.isHover() ||
                    dniAlumnoComboBox.isHover() ||
                    fechaInicioDatePicker.isHover() ||
                    fechaFinDatePicker.isHover();

            if (!tablaPracticaTable.isHover() && !esNodoDeEdicion) {
                tablaPracticaTable.getSelectionModel().clearSelection();
            }
        });

        //Desactivar botón Editar si hay 2 o más elementos seleccionados
        tablaPracticaTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Practica>) change -> {
            // Verificar si hay más de un elemento seleccionado
            if (tablaPracticaTable.getSelectionModel().getSelectedItems().size() > 1) {
                editarButton.setDisable(true); // Desactivar el botón Editar si hay más de un elemento seleccionado
            } else {
                editarButton.setDisable(false); // Habilitar el botón Editar si solo hay un elemento seleccionado
            }
        });

        //Evitar la selección de rangos eliminando los seleccionados no deseados
        tablaPracticaTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Practica>) change -> {
            while (change.next()) {
                if (change.wasAdded() && change.getAddedSubList().size() > 1) {
                    // Evitar la selección de rangos eliminando los seleccionados no deseados
                    Practica ultimoSeleccionado = change.getAddedSubList().get(change.getAddedSubList().size() - 1);
                    tablaPracticaTable.getSelectionModel().clearSelection();
                    tablaPracticaTable.getSelectionModel().select(ultimoSeleccionado);
                }
            }
        });

        cargarDatos();
        cargarAlumnos();
        cargarEmpresas();

        dniAlumnoComboBox.setItems(alumnosList);
        idEmpresaComboBox.setItems(empresasList);

        //Listener en tiempo real para buscar por ID en el textField
        buscarTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                cargarDatos(); // Si el campo está vacío, cargar todos los datos.
            } else {
                realizarBusqueda(newValue); // Realizar búsqueda en tiempo real.
            }
        });

    }

    @FXML
    void insertarButton(ActionEvent event) {
        String dniAlumno = dniAlumnoComboBox.getValue();
        String idEmpresa = idEmpresaComboBox.getValue();
        String fechaInicio = fechaInicioDatePicker.getValue() != null ? fechaInicioDatePicker.getValue().toString()
                : "";
        String fechaFin = fechaFinDatePicker.getValue() != null ? fechaFinDatePicker.getValue().toString() : "";

        if (fechaInicio.isEmpty() || fechaFin.isEmpty() || dniAlumno == null || idEmpresa == null) {
            mostrarAlerta("Error", "Por favor, completa todos los campos antes de insertar.", Alert.AlertType.ERROR);
            return;
        }

        if (fechaInicioDatePicker.getValue().isAfter(fechaFinDatePicker.getValue())) {
            mostrarAlerta("Error", "La fecha de inicio no puede ser mayor que la fecha de fin.", Alert.AlertType.ERROR);
            return;
        }

        // obtener solo el dni del alumno y el id de la empresa del combobox para evitar
        // errores de inserción
        String empresaId = idEmpresa.split(" - ")[0];
        String alumnoDNI = dniAlumno.split(" - ")[0];

        String query = "INSERT INTO practicas (dni_Alumno, id_Empresa, Fecha_Inicio, Fecha_Fin) VALUES (?, ?, ?, ?)";

        try (Connection connection = HikariCPConexion.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, alumnoDNI);
            preparedStatement.setString(2, empresaId);
            preparedStatement.setString(3, fechaInicio);
            preparedStatement.setString(4, fechaFin);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                cargarDatos();
                limpiarCampos();
                mostrarAlerta("Éxito", "Práctica insertada correctamente.", Alert.AlertType.INFORMATION);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al insertar práctica: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void updateButton(ActionEvent event) {

        int idPractica;
        try {
            idPractica = Integer.parseInt(idPracticaField.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID de práctica no es un número válido.", Alert.AlertType.ERROR);
            return;
        }
        String dniAlumno = dniAlumnoComboBox.getValue();
        String idEmpresa = idEmpresaComboBox.getValue();
        String fechaInicio = fechaInicioDatePicker.getValue() != null ? fechaInicioDatePicker.getValue().toString()
                : null;
        String fechaFin = fechaFinDatePicker.getValue() != null ? fechaFinDatePicker.getValue().toString() : null;

        if (dniAlumno == null || dniAlumno.isEmpty()) {
            mostrarAlerta("Error", "Seleccione un curso.", Alert.AlertType.WARNING);
            return;
        }

        if (fechaInicioDatePicker.getValue() == null) {
            mostrarAlerta("Error", "Seleccione un curso.", Alert.AlertType.WARNING);
            return;
        }

        if (fechaFinDatePicker.getValue() == null) {
            mostrarAlerta("Error", "Seleccione un curso.", Alert.AlertType.WARNING);
            return;
        }

        if (fechaInicioDatePicker.getValue().isAfter(fechaFinDatePicker.getValue())) {
            mostrarAlerta("Error", "La fecha de inicio no puede ser mayor que la fecha de fin.", Alert.AlertType.ERROR);
            return;
        }

        String empresaID = idEmpresa.split(" - ")[0];
        String alumnoDNI = dniAlumno.split(" - ")[0];

        String query = "UPDATE practicas SET DNI_Alumno = ?, ID_Empresa = ?, Fecha_Inicio = ?, Fecha_Fin = ? WHERE ID_Practica = ?";

        try (Connection connection = HikariCPConexion.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, alumnoDNI);
            preparedStatement.setString(2, empresaID);
            preparedStatement.setString(3, fechaInicio);
            preparedStatement.setString(4, fechaFin);
            preparedStatement.setInt(5, idPractica);

            int filasActualizadas = preparedStatement.executeUpdate();

            if (filasActualizadas > 0) {
                limpiarCampos();
                cargarDatos();
                mostrarAlerta("Éxito", "Páctica actualizada correctamente.", Alert.AlertType.INFORMATION);

            } else {
                mostrarAlerta("Error", "No se pudo actualizar al alumno.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar la practica: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        limpiarCampos();
        practicasList.clear();
        cargarDatos();

    }

    @FXML
    void deleteButton(ActionEvent event) {
        ObservableList<Practica> seleccionados = FXCollections.observableArrayList(
                tablaPracticaTable.getSelectionModel().getSelectedItems());

        if (seleccionados.isEmpty()) {
            mostrarAlerta("Error", "Por favor, selecciona una práctica para eliminar.", Alert.AlertType.ERROR);
            return;
        }

        // Confirmación de eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar las prácticas seleccionadas?");

        if (confirmacion.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try (Connection con = HikariCPConexion.getConnection()) {
            String query = "DELETE FROM practicas WHERE ID_Practica = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);

            for (Practica practica : seleccionados) {
                preparedStatement.setInt(1, practica.getIdPractica());
                preparedStatement.executeUpdate();
            }

            // Eliminar elementos seleccionados de la lista de datos
            practicasList.removeAll(seleccionados);

            // Limpiar la selección
            tablaPracticaTable.getSelectionModel().clearSelection();
            limpiarCampos();
            cargarDatos();
            mostrarAlerta("Éxito", "Prácticas eliminadas correctamente.", Alert.AlertType.INFORMATION);

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al eliminar las prácticas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void actionCancelarButton(ActionEvent event) {
        limpiarCampos();
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

   
    // Método para cargar los datos de la tabla de prácticas en el tableView
    private void cargarDatos() {
        practicasList.clear();
        String query = "SELECT * FROM practicas";

        try (Connection connection = HikariCPConexion.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Practica practica = new Practica(
                        resultSet.getInt("ID_Practica"),
                        resultSet.getString("dni_Alumno"),
                        resultSet.getInt("id_Empresa"),
                        resultSet.getString("Fecha_Inicio"),
                        resultSet.getString("Fecha_Fin"));
                practicasList.add(practica);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar datos de la tabla Práctica: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    // Método para rellenar los campos de la práctica seleccionada en la tabla
    private void llenarCampos(Practica practica) {

        // Rellenar campo de ID de práctica
        idPracticaField.setText(Integer.toString(practica.getIdPractica()));
    
        String query = "SELECT DNI_Alumno, Nombre, Apellido FROM alumno WHERE DNI_Alumno = ?";
    
        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            // Establece el valor del parámetro ? en la consulta
            statement.setString(1, practica.getDniAlumno());
    
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Construye el valor para la ComboBox
                    dniAlumnoComboBox.setValue(
                            resultSet.getString("DNI_Alumno") + " - " +
                            resultSet.getString("Nombre") + " " +
                            resultSet.getString("Apellido")
                    );
                } else {
                    dniAlumnoComboBox.setValue("Alumno no encontrado");
                }
            }
    
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar datos de alumnos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    
        String query2 = "SELECT ID_Empresa, Nombre FROM empresa WHERE ID_Empresa = ?";

        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(query2)) {

            // Establece el valor del parámetro ? en la consulta
            statement.setInt(1, practica.getIdEmpresa());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Construye el valor para la ComboBox
                    idEmpresaComboBox.setValue(
                            resultSet.getInt("ID_Empresa") + " - " +
                            resultSet.getString("Nombre")
                    );
                } else {
                    idEmpresaComboBox.setValue("Empresa no encontrada");
                }
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar datos de empresas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    
        // Formateador para las fechas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
        // Rellenar fecha de inicio
        if (practica.getFechaInicio() != null && !practica.getFechaInicio().isEmpty()) {
            try {
                LocalDate fechaInicio = LocalDate.parse(practica.getFechaInicio(), formatter);
                fechaInicioDatePicker.setValue(fechaInicio);
            } catch (DateTimeParseException e) {
                System.out.println("Error al convertir la fecha de inicio: " + e.getMessage());
                fechaInicioDatePicker.setValue(null);
            }
        } else {
            fechaInicioDatePicker.setValue(null);
        }
    
        // Rellenar fecha de fin
        if (practica.getFechaFin() != null && !practica.getFechaFin().isEmpty()) {
            try {
                LocalDate fechaFin = LocalDate.parse(practica.getFechaFin(), formatter);
                fechaFinDatePicker.setValue(fechaFin);
            } catch (DateTimeParseException e) {
                System.out.println("Error al convertir la fecha de fin: " + e.getMessage());
                fechaFinDatePicker.setValue(null);
            }
        } else {
            fechaFinDatePicker.setValue(null);
        }
    }
    
    // Método para cargar los datos de los alumnos en la ComboBox
    private void cargarAlumnos() {
        String query = "SELECT DNI_Alumno, Nombre, Apellido FROM alumno";

        try (Connection connection = HikariCPConexion.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                alumnosList.add(resultSet.getString("DNI_Alumno") + " - " + resultSet.getString("Nombre") + " "
                        + resultSet.getString("Apellido"));
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar datos de alumnos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Método para cargar los datos de las empresas en la ComboBox
    private void cargarEmpresas() {
        String query = "SELECT ID_Empresa, Nombre FROM empresa";

        try (Connection connection = HikariCPConexion.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                empresasList.add(resultSet.getInt("ID_Empresa") + " - " + resultSet.getString("Nombre"));
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar datos de empresas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    //Realizar búsqueda por ID de práctica
    private void realizarBusqueda(String termino) {
        practicasList.clear();
        String query = "SELECT * FROM practicas WHERE ID_Practica LIKE ?";
    
        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            // Usa LIKE con comodines para búsquedas parciales
            statement.setString(1, "%" + termino + "%");
    
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Practica practica = new Practica(
                            resultSet.getInt("ID_Practica"),
                            resultSet.getString("DNI_Alumno"),
                            resultSet.getInt("ID_Empresa"),
                            resultSet.getString("Fecha_Inicio"),
                            resultSet.getString("Fecha_Fin"));
                    practicasList.add(practica);
                }
            }
    
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar prácticas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void agregarListenerCampoIdPractica() {
        idPracticaField.textProperty().addListener((observable, oldValue, newValue) -> {
            insertarButton.setDisable(newValue != null && !newValue.trim().isEmpty());
        });

        insertarButton.setDisable(idPracticaField.getText() != null && !idPracticaField.getText().trim().isEmpty());
    }

    private void limpiarCampos() {
        idPracticaField.clear();
        fechaFinDatePicker.setValue(null);
        fechaInicioDatePicker.setValue(null);
        dniAlumnoComboBox.setValue(null);
        idEmpresaComboBox.setValue(null);
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public BorderPane getpracticaBorderPane() {
        return practicaBorderPane;
    }

}