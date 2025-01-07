package controller;

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
import model.Alumno;
import model.Practica;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AlumnoController {

    @FXML
    private TableView<Alumno> alumnoTableView;
    @FXML
    private BorderPane alumnoBorderPane;

    @FXML
    private TableColumn<Alumno, String> dniColumn;
    @FXML
    private TableColumn<Alumno, String> cursoColumn;
    @FXML
    private TableColumn<Alumno, String> nombreColumn;
    @FXML
    private TableColumn<Alumno, String> apellidosColumn;
    @FXML
    private TableColumn<Alumno, String> fechaColumn;
    @FXML
    private TableColumn<Alumno, String> direccionColumn;
    @FXML
    private TableColumn<Alumno, String> correoColumn;
    @FXML
    private DatePicker fechaNacimientoDatePicker;

    @FXML
    private TextField dniAlumnoTextField;
    @FXML
    private TextField nombreTextField;
    @FXML
    private TextField apellidosTextField;

    @FXML
    private TextField direccionTextField;
    @FXML
    private TextField correoTextField;
    @FXML
    private ComboBox<String> cursoComboBox;
    @FXML
    private Button cancelarButton;
    @FXML
    private Button volverButton;
    @FXML
    private Button editarButton;
    @FXML
    private Button insertButton;
    @FXML
    private TextField buscarTextField;

    private ObservableList<Alumno> alumnosList;
    private ObservableList<String> cursosList;

    public AlumnoController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/verTablaAlumno.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    public void initialize() {
        dniColumn.setCellValueFactory(new PropertyValueFactory<>("dniAlumno"));
        cursoColumn.setCellValueFactory(new PropertyValueFactory<>("curso"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidosColumn.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        correoColumn.setCellValueFactory(new PropertyValueFactory<>("correo"));

        alumnosList = FXCollections.observableArrayList();
        cursosList = FXCollections.observableArrayList();
        alumnoTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        cargarDatos();
        cargarCursos();

        alumnoTableView.setItems(alumnosList);
        cursoComboBox.setItems(cursosList);

        agregarListenerInsertar();

        // Detectar clics fuera de la tabla
        alumnoTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                llenarCampos(newSelection);
                dniAlumnoTextField.setEditable(false);
            }
        });

        alumnoBorderPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            // Listar los nodos de edición que no deben activar la limpieza
            boolean esNodoDeEdicion = dniAlumnoTextField.isHover() ||
                    nombreTextField.isHover() ||
                    apellidosTextField.isHover() ||
                    direccionTextField.isHover() ||
                    correoTextField.isHover() ||
                    fechaNacimientoDatePicker.isHover() ||
                    cursoComboBox.isHover();

            if (!alumnoTableView.isHover() && !esNodoDeEdicion) {
                alumnoTableView.getSelectionModel().clearSelection();

            }
        });

        //Desactivar botón Editar si hay 2 o más elementos seleccionados
        alumnoTableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Alumno>) change -> {
            // Verificar si hay más de un elemento seleccionado
            if (alumnoTableView.getSelectionModel().getSelectedItems().size() > 1) {
                editarButton.setDisable(true); // Desactivar el botón Editar si hay más de un elemento seleccionado
            } else {
                editarButton.setDisable(false); // Habilitar el botón Editar si solo hay un elemento seleccionado
            }
        });

        //Evitar la selección de rangos eliminando los seleccionados no deseados
        alumnoTableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Alumno>) change -> {
            while (change.next()) {
                if (change.wasAdded() && change.getAddedSubList().size() > 1) {
                    // Evitar la selección de rangos eliminando los seleccionados no deseados
                    Alumno ultimoSeleccionado = change.getAddedSubList().get(change.getAddedSubList().size() - 1);
                    alumnoTableView.getSelectionModel().clearSelection();
                    alumnoTableView.getSelectionModel().select(ultimoSeleccionado);
                }
            }
        });

        //Listener en tiempo real para buscar DNI en el TextField
        buscarTextField.textProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue == null || newValue.isEmpty()) {
               cargarDatos();
           } else {
               realizarBusqueda();
           }
        });
        

    }

    @FXML
    void insertButton(ActionEvent event) {
        String dni = dniAlumnoTextField.getText();
        String nombre = nombreTextField.getText();
        String apellidos = apellidosTextField.getText();
        String direccion = direccionTextField.getText();
        String correo = correoTextField.getText();
        String curso = cursoComboBox.getSelectionModel().getSelectedItem();

        if (!dni.matches("\\d{8}[A-Za-z]")) {
            mostrarAlerta("Error", "El DNI debe tener 8 números seguidos de una letra.", Alert.AlertType.ERROR);
            return;
        }

        // Validación de correo electrónico
        if (!correo.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            mostrarAlerta("Error", "El correo electrónico no tiene un formato válido.", Alert.AlertType.ERROR);
            return;
        }

        if (fechaNacimientoDatePicker.getValue() == null) {
            mostrarAlerta("Error", "Debes seleccionar una fecha de nacimiento", Alert.AlertType.ERROR);
            return;
        }
        String fechaNacimiento = fechaNacimientoDatePicker.getValue().toString();
        if (dni.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || direccion.isEmpty() || correo.isEmpty() || curso == null) {
            System.out.println("Todos los campos deben estar completos.");
            return;
        }

        // Obtener solo el ID del curso para evitar errores de inserción
        String cursoId = curso.split(" - ")[0];

        String query = "INSERT INTO Alumno (DNI_Alumno, Curso, Nombre, Apellido, Fecha_Nacimiento, Direccion, Correo_E) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, dni);
            preparedStatement.setString(2, cursoId);
            preparedStatement.setString(3, nombre);
            preparedStatement.setString(4, apellidos);
            preparedStatement.setString(5, fechaNacimiento);
            preparedStatement.setString(6, direccion);
            preparedStatement.setString(7, correo);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                limpiarCampos();
                cargarDatos();
                mostrarAlerta("Éxito", "Alumno insertado correctamente", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al insertar el alumno" + e.getMessage(), AlertType.ERROR);
        }

        alumnosList.clear();
        cargarDatos();
    }

    @FXML
    void updateButton(ActionEvent event) {
        if (dniAlumnoTextField.getText().isEmpty() || nombreTextField.getText().isEmpty() || apellidosTextField.getText().isEmpty()) {
            mostrarAlerta("Error", "Por favor, complete todos los campos obligatorios.", Alert.AlertType.ERROR);
            dniAlumnoTextField.setEditable(true);
            return;
        }

        String dni = dniAlumnoTextField.getText();
        String nombre = nombreTextField.getText();
        String apellidos = apellidosTextField.getText();
        String direccion = direccionTextField.getText();
        String correo = correoTextField.getText();
        String curso = cursoComboBox.getValue();
        String fechaNacimiento = fechaNacimientoDatePicker.getValue() != null ? fechaNacimientoDatePicker.getValue().toString() : null;

        if (curso == null || curso.isEmpty()) {
            mostrarAlerta("Error", "Seleccione un curso.", Alert.AlertType.WARNING);
            dniAlumnoTextField.setEditable(true);
            return;
        }

        if (!correo.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            mostrarAlerta("Error", "El correo electrónico no tiene un formato válido.", Alert.AlertType.ERROR);
            dniAlumnoTextField.setEditable(true);
            return;
        }

        String query = "UPDATE Alumno SET Nombre = ?, Apellido = ?, Direccion = ?, Correo_E = ?, Fecha_Nacimiento = ?, Curso = ? WHERE DNI_Alumno = ?";

        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, apellidos);
            preparedStatement.setString(3, direccion);
            preparedStatement.setString(4, correo);
            preparedStatement.setString(5, fechaNacimiento);
            preparedStatement.setString(6, curso.split(" - ")[0]);
            preparedStatement.setString(7, dni);

            int filasActualizadas = preparedStatement.executeUpdate();

            if (filasActualizadas > 0) {
                cargarDatos();
                mostrarAlerta("Éxito", "Alumno actualizado correctamente.", Alert.AlertType.INFORMATION);
                
            } else {
                mostrarAlerta("Error", "No se pudo actualizar al alumno.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar el alumno: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        dniAlumnoTextField.setEditable(true);

        limpiarCampos();
        alumnosList.clear();
        cargarDatos();
    }

    @FXML
    void deleteButton(ActionEvent event) {
        Alumno alumnoSeleccionado = alumnoTableView.getSelectionModel().getSelectedItem();

        if (alumnoSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, selecciona un alumno para eliminar.", Alert.AlertType.ERROR);
            return;
        }

        String dni = alumnoSeleccionado.getDniAlumno();

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que quieres eliminar al alumno con DNI " + dni + "?");

        if (confirmacion.showAndWait().get() != ButtonType.OK) {
            return;
        }

        String query = "DELETE FROM Alumno WHERE DNI_Alumno = ?";

        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, dni);
            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                alumnosList.remove(alumnoSeleccionado);
                alumnoTableView.getSelectionModel().clearSelection();
                limpiarCampos();
                mostrarAlerta("Éxito", "Alumno eliminado correctamente.", Alert.AlertType.INFORMATION);

            } else {
                mostrarAlerta("Error", "No se pudo eliminar al alumno. Por favor, inténtalo de nuevo.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al eliminar el alumno: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void actionCancelarButton(ActionEvent event) {
        limpiarCampos();
        dniAlumnoTextField.setEditable(true);
        insertButton.setDisable(false);
    }

    @FXML
    void actionVolverButton(ActionEvent event) {
        try {
            // Crear un FXMLLoader
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/mainView.fxml"));

            // Establecer el controlador ANTES de cargar
            MainController mainController = new MainController();
            loader.setController(mainController);

            // Cargar la vista
            Parent mainView = loader.load();

            // Obtener el stage actual y cambiar la escena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Gestión de Prácticas");
            Scene scene = new Scene(mainView);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void agregarListenerInsertar() {
        alumnoTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                llenarCampos(newSelection);
                insertButton.setDisable(true);
            }
        });
    }

    private void limpiarCampos() {
        dniAlumnoTextField.clear();
        nombreTextField.clear();
        apellidosTextField.clear();
        direccionTextField.clear();
        correoTextField.clear();
        cursoComboBox.setValue(null);
        fechaNacimientoDatePicker.setValue(null); // Reiniciar el DatePicker
    }

    private void llenarCampos(Alumno alumno) {
        dniAlumnoTextField.setText(alumno.getDniAlumno());
        nombreTextField.setText(alumno.getNombre());
        apellidosTextField.setText(alumno.getApellido());
        direccionTextField.setText(alumno.getDireccion());
        correoTextField.setText(alumno.getCorreo());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (alumno.getFechaNacimiento() != null && !alumno.getFechaNacimiento().isEmpty()) {
            try {
                LocalDate fechaNacimiento = LocalDate.parse(alumno.getFechaNacimiento(), formatter);
                fechaNacimientoDatePicker.setValue(fechaNacimiento);
            } catch (DateTimeParseException e) {
                System.out.println("Error al convertir la fecha: " + e.getMessage());
                fechaNacimientoDatePicker.setValue(null);
            }
        } else {
            fechaNacimientoDatePicker.setValue(null);
        }

        String query = "SELECT ID_curso, Nombre FROM Curso WHERE ID_Curso = ?";

        try(Connection connection = HikariCPConexion.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, alumno.getCurso());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String curso = resultSet.getInt("ID_Curso") + " - " + resultSet.getString("Nombre");
                cursoComboBox.setValue(curso);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar el curso: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarDatos() {
        alumnosList.clear();
        String query = "SELECT * FROM Alumno";
        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                // Modificación: solo agregar el ID del curso (no el nombre)
                String cursoId = resultSet.getString("Curso");  // Obtiene solo el ID
                Alumno alumno = new Alumno(
                        resultSet.getString("DNI_Alumno"),
                        cursoId,  // Solo el ID del curso
                        resultSet.getString("Nombre"),
                        resultSet.getString("Apellido"),
                        resultSet.getString("Fecha_Nacimiento"),
                        resultSet.getString("Direccion"),
                        resultSet.getString("Correo_E")
                );
                alumnosList.add(alumno);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar alumnos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarCursos() {
        String query = "SELECT ID_Curso, Nombre FROM Curso";
        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String curso = resultSet.getInt("ID_Curso") + " - " + resultSet.getString("Nombre");
                cursosList.add(curso);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar cursos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void realizarBusqueda() {
        alumnosList.clear();
        String busqueda = buscarTextField.getText();
        String query = "SELECT * FROM Alumno WHERE DNI_Alumno LIKE ?";

        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + busqueda + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String cursoId = resultSet.getString("Curso");
                Alumno alumno = new Alumno(
                        resultSet.getString("DNI_Alumno"),
                        cursoId,
                        resultSet.getString("Nombre"),
                        resultSet.getString("Apellido"),
                        resultSet.getString("Fecha_Nacimiento"),
                        resultSet.getString("Direccion"),
                        resultSet.getString("Correo_E")
                );
                alumnosList.add(alumno);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar el alumno: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null); // Opcional, si no quieres un encabezado
        alerta.setContentText(contenido);
        alerta.showAndWait(); // Muestra la alerta y espera a que el usuario la cierre
    }

    public BorderPane getAlumnoBorderPane() {
        return alumnoBorderPane;
    }
}