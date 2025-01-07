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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Empresa;
import model.Practica;
import main.HikariCPConexion;
import java.io.IOException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmpresaController {

    @FXML
    private BorderPane empresaBorderPane;

    @FXML
    private Button borrarButton;

    @FXML
    private Button cancelarButton;

    @FXML
    private TableColumn<Empresa, String> correoEmpresaColumn;

    @FXML
    private TextField correoEmpresaField;

    @FXML
    private TableColumn<Empresa, String> direccionColumn;

    @FXML
    private TextField direccionEmpresaField;

    @FXML
    private Button editarButton;

    @FXML
    private Button volverButton;

    @FXML
    private TableColumn<Empresa, String> especialidadColumn;

    @FXML
    private ComboBox<String> especialidadComboBox;

    @FXML
    private TableColumn<Empresa, String> horarioColumn;

    @FXML
    private TextField horarioEmpresaField;

    @FXML
    private TableColumn<Empresa, Integer> idEmpresaColumn;

    @FXML
    private TextField idEmpresaField;

    @FXML
    private Button insertButton;

    @FXML
    private TableColumn<Empresa, String> nombreEmpresaColumn;

    @FXML
    private TextField nombreEmpresaField;

    @FXML
    private TableColumn<Empresa, Integer> plazasColumn;

    @FXML
    private TextField plazasField;

    @FXML
    private TableView<Empresa> tablaEmpresaTable;

    private ObservableList<Empresa> empresasList;
    private ObservableList<String> especialidadesList;

    public EmpresaController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VerTablaEmpresa.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    public void initialize() {
        // Vincula las columnas con los atributos del modelo Empresa
        idEmpresaColumn.setCellValueFactory(new PropertyValueFactory<>("idEmpresa"));
        especialidadColumn.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        nombreEmpresaColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        correoEmpresaColumn.setCellValueFactory(new PropertyValueFactory<>("correo"));
        horarioColumn.setCellValueFactory(new PropertyValueFactory<>("horario"));
        plazasColumn.setCellValueFactory(new PropertyValueFactory<>("plazasDisponibles"));

        empresasList = FXCollections.observableArrayList();
        especialidadesList = FXCollections.observableArrayList();
        tablaEmpresaTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        cargarDatos();
        cargarEspecialidades();

        tablaEmpresaTable.setItems(empresasList);

        especialidadComboBox.setItems(especialidadesList);

        idEmpresaField.setEditable(false);

        agregarListenerCampoIDEmpresa();

        tablaEmpresaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                llenarCampos(newSelection);
            }
        });

        empresaBorderPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            boolean esNodoDeEdicion =
                    idEmpresaField.isHover() ||
                            nombreEmpresaField.isHover() ||
                            direccionEmpresaField.isHover() ||
                            correoEmpresaField.isHover() ||
                            horarioEmpresaField.isHover() ||
                            plazasField.isHover() ||
                            especialidadComboBox.isHover();


            if (!tablaEmpresaTable.isHover() && !esNodoDeEdicion) {
                tablaEmpresaTable.getSelectionModel().clearSelection();

            }
        });

        //Desactivar botón Editar si hay 2 o más elementos seleccionados
        tablaEmpresaTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Empresa>) change -> {
            // Verificar si hay más de un elemento seleccionado
            if (tablaEmpresaTable.getSelectionModel().getSelectedItems().size() > 1) {
                editarButton.setDisable(true); // Desactivar el botón Editar si hay más de un elemento seleccionado
            } else {
                editarButton.setDisable(false); // Habilitar el botón Editar si solo hay un elemento seleccionado
            }
        });

        //Evitar la selección de rangos eliminando los seleccionados no deseados
        tablaEmpresaTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Empresa>) change -> {
            while (change.next()) {
                if (change.wasAdded() && change.getAddedSubList().size() > 1) {
                    // Evitar la selección de rangos eliminando los seleccionados no deseados
                    Empresa ultimoSeleccionado = change.getAddedSubList().get(change.getAddedSubList().size() - 1);
                    tablaEmpresaTable.getSelectionModel().clearSelection();
                    tablaEmpresaTable.getSelectionModel().select(ultimoSeleccionado);
                }
            }
        });

    }

    @FXML
    void insertButton(ActionEvent event) {
        String especialidad = especialidadComboBox.getValue();
        String nombre = nombreEmpresaField.getText();
        String direccion = direccionEmpresaField.getText();
        String correo = correoEmpresaField.getText();
        String horarioEmpresa = horarioEmpresaField.getText();
        String plazasText = plazasField.getText();

        if (especialidad == null || nombre.isEmpty() || direccion.isEmpty() || correo.isEmpty() || horarioEmpresa.isEmpty() || plazasText.isEmpty()) {
            mostrarAlerta("Error", "Por favor, completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        if (!isValidEmail(correo)) {
            mostrarAlerta("Error", "El correo electrónico no tiene el formato correcto.", Alert.AlertType.ERROR);
            return;
        }

        if (isValidHorario(horarioEmpresa)) {
            mostrarAlerta("Error", "El horario introducido no tiene un formato válido.", Alert.AlertType.ERROR);
            return;
        }

        int plazas;
        int idEspecialidad;
        try {
            plazas = Integer.parseInt(plazasText);
            idEspecialidad = Integer.parseInt(especialidad.split(" - ")[0]);
        } catch (NumberFormatException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR, "El número de plazas debe ser un número entero.");
            alerta.show();
            return;
        }

        String insertQuery = "INSERT INTO Empresa (Especialidad, Nombre, Direccion, Correo_E, Horario, Plazas_Disp) " +
                "VALUES (" + idEspecialidad + ", '" + nombre + "', '" + direccion + "', '" + correo + "', '" + horarioEmpresa + "', " + plazas + ")";

        try (Connection connection = HikariCPConexion.getConnection();
             Statement statement = connection.createStatement()) {

            int filasAfectadas = statement.executeUpdate(insertQuery);

            if (filasAfectadas > 0) {
                Alert alerta = new Alert(Alert.AlertType.INFORMATION, "¡Empresa agregada exitosamente!");
                alerta.show();

                limpiarCampos();

                empresasList.clear();
                cargarDatos();
            }
        } catch (SQLException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR, "Error al insertar los datos: " + e.getMessage());
            alerta.show();
        }
    }

    @FXML
    void updateButton(ActionEvent event) {
        if (idEmpresaField.getText().isEmpty() || nombreEmpresaField.getText().isEmpty() || direccionEmpresaField.getText().isEmpty()
                || correoEmpresaField.getText().isEmpty() || horarioEmpresaField.getText().isEmpty() || plazasField.getText().isEmpty()
                || especialidadComboBox.getValue().isEmpty()){
            mostrarAlerta("Campos Vacíos", "Por favor, complete todos los campos obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        String idEmpresa = idEmpresaField.getText();
        String nombreEmpresa = nombreEmpresaField.getText();
        String direccionEmpresa = direccionEmpresaField.getText();
        String correoEmpresa = correoEmpresaField.getText();
        String horarioEmpresa = horarioEmpresaField.getText();
        String plazasDisponibles = plazasField.getText();
        String escpecialidad = especialidadComboBox.getValue();

        if (!isValidEmail(correoEmpresa)) {
            mostrarAlerta("Error", "El correo electrónico introducido no tiene un formato válido.", Alert.AlertType.ERROR);
            return;
        }

        if (!isValidHorario(horarioEmpresa)) {
            mostrarAlerta("Error", "El horario introducido no tiene un formato válido.", Alert.AlertType.ERROR);
            return;
        }


        String query = "UPDATE empresa SET Especialidad = ?, Nombre = ?, Direccion = ?, Correo_E = ?, Horario = ?, Plazas_Disp = ? WHERE ID_Empresa = ?";

        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, escpecialidad.split(" - ")[0]);
            preparedStatement.setString(2, nombreEmpresa);
            preparedStatement.setString(3, direccionEmpresa);
            preparedStatement.setString(4, correoEmpresa);
            preparedStatement.setString(5, horarioEmpresa);
            preparedStatement.setString(6, plazasDisponibles);
            preparedStatement.setString(7, idEmpresa);

            int filasActualizadas = preparedStatement.executeUpdate();

            if (filasActualizadas > 0) {
                mostrarAlerta("Éxito", "Empresa actualizada correctamente.", Alert.AlertType.INFORMATION);
                cargarDatos();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar la empresa.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al actualizar la empresa: " + e.getMessage(), Alert.AlertType.ERROR);
        }



        limpiarCampos();
        empresasList.clear();
        cargarDatos();
    }

    @FXML
    void deleteButton(ActionEvent event) {
        Empresa empresaSeleccionada = tablaEmpresaTable.getSelectionModel().getSelectedItem();

        if (empresaSeleccionada == null){
            mostrarAlerta("Error", "Por favor, selecciona una empresa para eliminiar", Alert.AlertType.ERROR);
            return;
        }

        int idEliminar = empresaSeleccionada.getIdEmpresa();

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("Estas seguro de que deseas eliminar la empresa con ID " + idEliminar + "?");

        if (confirmacion.showAndWait().get() != ButtonType.OK) {
            return;
        }

        String query = "DELETE FROM empresa WHERE ID_Empresa = ?";

        try (Connection connection = HikariCPConexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idEliminar);
            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                empresasList.remove(empresaSeleccionada);
                empresasList.remove(empresaSeleccionada);
                tablaEmpresaTable.getSelectionModel().clearSelection();
                limpiarCampos();
                mostrarAlerta("Éxito", "Empresa eliminada correctamente.", Alert.AlertType.INFORMATION);

            } else {
                mostrarAlerta("Error", "No se pudo eliminar la empresa. Por favor, inténtalo de nuevo.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al eliminar la empresa: " + e.getMessage(), Alert.AlertType.ERROR);
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

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidHorario(String schedule) {
        Pattern pattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]-([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
        Matcher matcher = pattern.matcher(schedule);
        return matcher.matches();
    }

    private void llenarCampos(Empresa empresa) {
        idEmpresaField.setText(Integer.toString(empresa.getIdEmpresa()));
        nombreEmpresaField.setText(empresa.getNombre());
        direccionEmpresaField.setText(empresa.getDireccion());
        correoEmpresaField.setText(empresa.getCorreo());
        horarioEmpresaField.setText(empresa.getHorario());
        plazasField.setText(Integer.toString(empresa.getPlazasDisponibles()));
        
        String query = "SELECT ID_Especialidad, Nombre FROM Especialidad WHERE ID_Especialidad = " + empresa.getEspecialidad();

        try(Connection connection = HikariCPConexion.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                String especialidad = resultSet.getInt("ID_Especialidad") + " - " + resultSet.getString("Nombre");
                especialidadComboBox.setValue(especialidad);
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar datos de la tabla Especialidad: " + e.getMessage());
        }

    }

    private void cargarDatos() {
        String query = "SELECT * FROM Empresa";

        try (Connection connection = HikariCPConexion.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Empresa empresa = new Empresa(
                        resultSet.getInt("ID_Empresa"),
                        resultSet.getInt("Especialidad"),
                        resultSet.getString("Nombre"),
                        resultSet.getString("Direccion"),
                        resultSet.getString("Correo_E"),
                        resultSet.getString("Horario"),
                        resultSet.getInt("Plazas_Disp")
                );
                empresasList.add(empresa);
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar datos de la tabla Empresa: " + e.getMessage());
        }
    }

    private void cargarEspecialidades() {
        String query = "SELECT ID_Especialidad, Nombre FROM Especialidad";

        try (Connection connection = HikariCPConexion.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String especialidad = resultSet.getInt("ID_Especialidad") + " - " + resultSet.getString("Nombre");
                especialidadesList.add(especialidad);
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar datos de la tabla Especialidad: " + e.getMessage());
        }
    }

    private void agregarListenerCampoIDEmpresa() {
        idEmpresaField.textProperty().addListener((observable, oldValue, newValue) -> {
            insertButton.setDisable(newValue != null && !newValue.trim().isEmpty());
        });

        insertButton.setDisable(idEmpresaField.getText() != null && !idEmpresaField.getText().trim().isEmpty());
    }

    private void limpiarCampos() {
        idEmpresaField.clear();
        especialidadComboBox.setValue(null);
        nombreEmpresaField.clear();
        direccionEmpresaField.clear();
        correoEmpresaField.clear();
        horarioEmpresaField.clear();
        plazasField.clear();
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }



    public BorderPane getEmpresaMainPane() {
        return empresaBorderPane;
    }
}