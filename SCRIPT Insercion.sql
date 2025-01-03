-- Inserción en Especialidad
INSERT INTO Especialidad (Nombre) VALUES
('Informática'),
('Electrónica'),
('Administración'),
('Mecánica');

-- Inserción en Empresa
INSERT INTO Empresa (Especialidad, Nombre, Direccion, Correo_E, Horario, Plazas_Disp) VALUES
(1, 'TechCorp', 'Calle Innovación 123', 'contacto@techcorp.com', '9:00-18:00', 10),
(2, 'ElectroWorks', 'Av. Circuito 456', 'info@electroworks.com', '8:00-16:00', 5),
(3, 'AdminPlus', 'Calle Oficina 789', 'admin@adminplus.com', '10:00-19:00', 3),
(4, 'MechaLab', 'Paseo Industria 101', 'info@mechalab.com', '7:00-15:00', 8);

-- Inserción en Curso
INSERT INTO Curso (Nombre) VALUES
('Grado Superior en Informática'),
('Grado Superior en Electrónica'),
('Grado Superior en Administración'),
('Grado Superior en Mecánica');

-- Inserción en Alumno
INSERT INTO Alumno (DNI_Alumno, Curso, Nombre, Apellido, Fecha_Nacimiento, Direccion, Correo_E) VALUES
('12345678A', 1, 'Luis', 'Pérez', '2003-05-14', 'Calle Uno 1', 'luis.perez@example.com'),
('23456789B', 2, 'Ana', 'Gómez', '2002-11-23', 'Calle Dos 2', 'ana.gomez@example.com'),
('34567890C', 3, 'Carlos', 'Martínez', '2001-03-12', 'Calle Tres 3', 'carlos.martinez@example.com'),
('45678901D', 4, 'Laura', 'López', '2004-07-18', 'Calle Cuatro 4', 'laura.lopez@example.com');

-- Inserción en Tutor_Docente
INSERT INTO Tutor_Docente (DNI_Tutor_Docente, Nombre, Apellido, Correo_E) VALUES
('11111111A', 'María', 'Fernández', 'maria.fernandez@docente.com'),
('22222222B', 'José', 'Ruiz', 'jose.ruiz@docente.com'),
('33333333C', 'Elena', 'Díaz', 'elena.diaz@docente.com'),
('44444444D', 'Pedro', 'Sánchez', 'pedro.sanchez@docente.com');

-- Inserción en Tutor_Empresa
INSERT INTO Tutor_Empresa (DNI_Tutor_Empresa, ID_Empresa, Nombre, Apellido, Correo_E) VALUES
('55555555E', 1, 'Raúl', 'Muñoz', 'raul.munoz@techcorp.com'),
('66666666F', 2, 'Sonia', 'Hernández', 'sonia.hernandez@electroworks.com'),
('77777777G', 3, 'Diego', 'Vargas', 'diego.vargas@adminplus.com'),
('88888888H', 4, 'Clara', 'Gil', 'clara.gil@mechalab.com');

-- Inserción en Asignar_Tutor
INSERT INTO Asignar_Tutor (DNI_Alumno, DNI_Tutor_Empresa, DNI_Tutor_Docente) VALUES
('12345678A', '55555555E', '11111111A'),
('23456789B', '66666666F', '22222222B'),
('34567890C', '77777777G', '33333333C'),
('45678901D', '88888888H', '44444444D');

-- Inserción en Practicas
INSERT INTO Practicas (DNI_Alumno, ID_Empresa, Fecha_Inicio, Fecha_Fin) VALUES
('12345678A', 1, '2024-01-10', '2024-04-10'),
('23456789B', 2, '2024-02-01', '2024-05-01'),
('34567890C', 3, '2024-03-15', '2024-06-15'),
('45678901D', 4, '2024-04-20', '2024-07-20');

-- Inserción en Comentario_Empresa
INSERT INTO Comentario_Empresa (ID_Empresa, Fecha_Comentario, Nota) VALUES
(1, '2024-01-15', 'Buen inicio con los alumnos'),
(2, '2024-02-20', 'Necesitamos mejorar la organización'),
(3, '2024-03-25', 'Excelente desempeño'),
(4, '2024-04-30', 'Requiere supervisión más frecuente');

-- Inserción en Visita_Seguimiento
INSERT INTO Visita_Seguimiento (ID_Practica, DNI_Tutor, Fecha_Visita, Observaciones, Comentario_Tutor) VALUES
(1, '55555555E', '2024-01-20', 'Alumno adaptado', 'Buena proactividad'),
(2, '66666666F', '2024-02-28', 'Alumno necesita apoyo', 'Recomendado mentor adicional'),
(3, '77777777G', '2024-03-30', 'Excelente progreso', 'Muy comprometido'),
(4, '88888888H', '2024-04-25', 'Requiere más supervisión', 'Mejorar técnicas de trabajo');