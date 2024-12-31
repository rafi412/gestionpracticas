-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 01-12-2024 a las 19:55:22
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `gestionpracticas`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `alumno`
--

CREATE TABLE `alumno` (
  `DNI_Alumno` varchar(9) NOT NULL,
  `Curso` int(11) DEFAULT NULL,
  `Nombre` varchar(30) NOT NULL,
  `Apellido` varchar(60) NOT NULL,
  `Fecha_Nacimiento` date DEFAULT NULL,
  `Direccion` varchar(150) DEFAULT NULL,
  `Correo_E` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `alumno`
--

INSERT INTO `alumno` (`DNI_Alumno`, `Curso`, `Nombre`, `Apellido`, `Fecha_Nacimiento`, `Direccion`, `Correo_E`) VALUES
('12345678A', 1, 'Luis', 'Pérez', '2003-05-14', 'Calle Uno 1', 'luis.perez@example.com'),
('23456789B', 2, 'Ana', 'Gómez', '2002-11-23', 'Calle Dos 2', 'ana.gomez@example.com'),
('34567890C', 3, 'Carlos', 'Martínez', '2001-03-12', 'Calle Tres 3', 'carlos.martinez@example.com'),
('45678901D', 4, 'Laura', 'López', '2004-07-18', 'Calle Cuatro 4', 'laura.lopez@example.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asignar_tutor`
--

CREATE TABLE `asignar_tutor` (
  `DNI_Alumno` varchar(9) NOT NULL,
  `DNI_Tutor_Empresa` varchar(9) DEFAULT NULL,
  `DNI_Tutor_Docente` varchar(9) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `asignar_tutor`
--

INSERT INTO `asignar_tutor` (`DNI_Alumno`, `DNI_Tutor_Empresa`, `DNI_Tutor_Docente`) VALUES
('12345678A', '55555555E', '11111111A'),
('23456789B', '66666666F', '22222222B'),
('34567890C', '77777777G', '33333333C'),
('45678901D', '88888888H', '44444444D');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comentario_empresa`
--

CREATE TABLE `comentario_empresa` (
  `ID_Comentario` int(11) NOT NULL,
  `ID_Empresa` int(11) NOT NULL,
  `Fecha_Comentario` date NOT NULL,
  `Nota` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `comentario_empresa`
--

INSERT INTO `comentario_empresa` (`ID_Comentario`, `ID_Empresa`, `Fecha_Comentario`, `Nota`) VALUES
(1, 1, '2024-01-15', 'Buen inicio con los alumnos'),
(2, 2, '2024-02-20', 'Necesitamos mejorar la organización'),
(3, 3, '2024-03-25', 'Excelente desempeño'),
(4, 4, '2024-04-30', 'Requiere supervisión más frecuente');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `curso`
--

CREATE TABLE `curso` (
  `ID_Curso` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `curso`
--

INSERT INTO `curso` (`ID_Curso`, `Nombre`) VALUES
(1, 'Grado Superior en Informática'),
(2, 'Grado Superior en Electrónica'),
(3, 'Grado Superior en Administración'),
(4, 'Grado Superior en Mecánica');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empresa`
--

CREATE TABLE `empresa` (
  `ID_Empresa` int(11) NOT NULL,
  `Especialidad` int(11) DEFAULT NULL,
  `Nombre` varchar(30) NOT NULL,
  `Direccion` varchar(150) NOT NULL,
  `Correo_E` varchar(50) NOT NULL,
  `Horario` varchar(50) DEFAULT NULL,
  `Plazas_Disp` int(11) NOT NULL CHECK (`Plazas_Disp` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `empresa`
--

INSERT INTO `empresa` (`ID_Empresa`, `Especialidad`, `Nombre`, `Direccion`, `Correo_E`, `Horario`, `Plazas_Disp`) VALUES
(1, 1, 'TechCorp', 'Calle Innovación 123', 'contacto@techcorp.com', '9:00-18:00', 10),
(2, 2, 'ElectroWorks', 'Av. Circuito 456', 'info@electroworks.com', '8:00-16:00', 5),
(3, 3, 'AdminPlus', 'Calle Oficina 789', 'admin@adminplus.com', '10:00-19:00', 3),
(4, 4, 'MechaLab', 'Paseo Industria 101', 'info@mechalab.com', '7:00-15:00', 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `especialidad`
--

CREATE TABLE `especialidad` (
  `ID_Especialidad` int(11) NOT NULL,
  `Nombre` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `especialidad`
--

INSERT INTO `especialidad` (`ID_Especialidad`, `Nombre`) VALUES
(1, 'Informática'),
(2, 'Electrónica'),
(3, 'Administración'),
(4, 'Mecánica');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `practicas`
--

CREATE TABLE `practicas` (
  `ID_Practica` int(11) NOT NULL,
  `DNI_Alumno` varchar(15) DEFAULT NULL,
  `ID_Empresa` int(11) DEFAULT NULL,
  `Fecha_Inicio` date NOT NULL,
  `Fecha_Fin` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `practicas`
--

INSERT INTO `practicas` (`ID_Practica`, `DNI_Alumno`, `ID_Empresa`, `Fecha_Inicio`, `Fecha_Fin`) VALUES
(1, '12345678A', 1, '2024-01-10', '2024-04-10'),
(2, '23456789B', 2, '2024-02-01', '2024-05-01'),
(3, '34567890C', 3, '2024-03-15', '2024-06-15'),
(4, '45678901D', 4, '2024-04-20', '2024-07-20');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tutor_docente`
--

CREATE TABLE `tutor_docente` (
  `DNI_Tutor_Docente` varchar(15) NOT NULL,
  `Nombre` varchar(30) NOT NULL,
  `Apellido` varchar(60) NOT NULL,
  `Correo_E` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tutor_docente`
--

INSERT INTO `tutor_docente` (`DNI_Tutor_Docente`, `Nombre`, `Apellido`, `Correo_E`) VALUES
('11111111A', 'María', 'Fernández', 'maria.fernandez@docente.com'),
('22222222B', 'José', 'Ruiz', 'jose.ruiz@docente.com'),
('33333333C', 'Elena', 'Díaz', 'elena.diaz@docente.com'),
('44444444D', 'Pedro', 'Sánchez', 'pedro.sanchez@docente.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tutor_empresa`
--

CREATE TABLE `tutor_empresa` (
  `DNI_Tutor_Empresa` varchar(9) NOT NULL,
  `ID_Empresa` int(11) NOT NULL,
  `Nombre` varchar(30) NOT NULL,
  `Apellido` varchar(60) NOT NULL,
  `Correo_E` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tutor_empresa`
--

INSERT INTO `tutor_empresa` (`DNI_Tutor_Empresa`, `ID_Empresa`, `Nombre`, `Apellido`, `Correo_E`) VALUES
('55555555E', 1, 'Raúl', 'Muñoz', 'raul.munoz@techcorp.com'),
('66666666F', 2, 'Sonia', 'Hernández', 'sonia.hernandez@electroworks.com'),
('77777777G', 3, 'Diego', 'Vargas', 'diego.vargas@adminplus.com'),
('88888888H', 4, 'Clara', 'Gil', 'clara.gil@mechalab.com');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `visita_seguimiento`
--

CREATE TABLE `visita_seguimiento` (
  `ID_Visita` int(11) NOT NULL,
  `ID_Practica` int(11) DEFAULT NULL,
  `DNI_Tutor` varchar(9) DEFAULT NULL,
  `Fecha_Visita` date NOT NULL,
  `Observaciones` varchar(200) DEFAULT NULL,
  `Comentario_Tutor` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `visita_seguimiento`
--

INSERT INTO `visita_seguimiento` (`ID_Visita`, `ID_Practica`, `DNI_Tutor`, `Fecha_Visita`, `Observaciones`, `Comentario_Tutor`) VALUES
(1, 1, '55555555E', '2024-01-20', 'Alumno adaptado', 'Buena proactividad'),
(2, 2, '66666666F', '2024-02-28', 'Alumno necesita apoyo', 'Recomendado mentor adicional'),
(3, 3, '77777777G', '2024-03-30', 'Excelente progreso', 'Muy comprometido'),
(4, 4, '88888888H', '2024-04-25', 'Requiere más supervisión', 'Mejorar técnicas de trabajo');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `alumno`
--
ALTER TABLE `alumno`
  ADD PRIMARY KEY (`DNI_Alumno`),
  ADD UNIQUE KEY `Correo_E` (`Correo_E`),
  ADD KEY `FK_Alumno_Curso` (`Curso`);

--
-- Indices de la tabla `asignar_tutor`
--
ALTER TABLE `asignar_tutor`
  ADD PRIMARY KEY (`DNI_Alumno`),
  ADD KEY `FK_Asignar_Tutor_Tutor_Empresa` (`DNI_Tutor_Empresa`),
  ADD KEY `FK_Asignar_Tutor_Tutor_Docente` (`DNI_Tutor_Docente`);

--
-- Indices de la tabla `comentario_empresa`
--
ALTER TABLE `comentario_empresa`
  ADD PRIMARY KEY (`ID_Comentario`),
  ADD KEY `FK_Comentario_Empresa_Empresa` (`ID_Empresa`);

--
-- Indices de la tabla `curso`
--
ALTER TABLE `curso`
  ADD PRIMARY KEY (`ID_Curso`);

--
-- Indices de la tabla `empresa`
--
ALTER TABLE `empresa`
  ADD PRIMARY KEY (`ID_Empresa`),
  ADD UNIQUE KEY `Correo_E` (`Correo_E`),
  ADD KEY `FK_Empresa_Especialidad` (`Especialidad`);

--
-- Indices de la tabla `especialidad`
--
ALTER TABLE `especialidad`
  ADD PRIMARY KEY (`ID_Especialidad`);

--
-- Indices de la tabla `practicas`
--
ALTER TABLE `practicas`
  ADD PRIMARY KEY (`ID_Practica`),
  ADD KEY `FK_Practicas_Alumno` (`DNI_Alumno`),
  ADD KEY `FK_Practicas_Empresa` (`ID_Empresa`);

--
-- Indices de la tabla `tutor_docente`
--
ALTER TABLE `tutor_docente`
  ADD PRIMARY KEY (`DNI_Tutor_Docente`),
  ADD UNIQUE KEY `Correo_E` (`Correo_E`);

--
-- Indices de la tabla `tutor_empresa`
--
ALTER TABLE `tutor_empresa`
  ADD PRIMARY KEY (`DNI_Tutor_Empresa`),
  ADD UNIQUE KEY `Correo_E` (`Correo_E`),
  ADD KEY `FK_Tutor_Empresa_Empresa` (`ID_Empresa`);

--
-- Indices de la tabla `visita_seguimiento`
--
ALTER TABLE `visita_seguimiento`
  ADD PRIMARY KEY (`ID_Visita`),
  ADD KEY `FK_Visita_Seguimiento_Practica` (`ID_Practica`),
  ADD KEY `FK_Visita_Seguimiento_Tutor` (`DNI_Tutor`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `comentario_empresa`
--
ALTER TABLE `comentario_empresa`
  MODIFY `ID_Comentario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `curso`
--
ALTER TABLE `curso`
  MODIFY `ID_Curso` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `empresa`
--
ALTER TABLE `empresa`
  MODIFY `ID_Empresa` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `especialidad`
--
ALTER TABLE `especialidad`
  MODIFY `ID_Especialidad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `practicas`
--
ALTER TABLE `practicas`
  MODIFY `ID_Practica` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `visita_seguimiento`
--
ALTER TABLE `visita_seguimiento`
  MODIFY `ID_Visita` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `alumno`
--
ALTER TABLE `alumno`
  ADD CONSTRAINT `FK_Alumno_Curso` FOREIGN KEY (`Curso`) REFERENCES `curso` (`ID_Curso`) ON DELETE SET NULL;

--
-- Filtros para la tabla `asignar_tutor`
--
ALTER TABLE `asignar_tutor`
  ADD CONSTRAINT `FK_Asignar_Tutor_Alumno` FOREIGN KEY (`DNI_Alumno`) REFERENCES `alumno` (`DNI_Alumno`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_Asignar_Tutor_Tutor_Docente` FOREIGN KEY (`DNI_Tutor_Docente`) REFERENCES `tutor_docente` (`DNI_Tutor_Docente`) ON DELETE SET NULL,
  ADD CONSTRAINT `FK_Asignar_Tutor_Tutor_Empresa` FOREIGN KEY (`DNI_Tutor_Empresa`) REFERENCES `tutor_empresa` (`DNI_Tutor_Empresa`) ON DELETE SET NULL;

--
-- Filtros para la tabla `comentario_empresa`
--
ALTER TABLE `comentario_empresa`
  ADD CONSTRAINT `FK_Comentario_Empresa_Empresa` FOREIGN KEY (`ID_Empresa`) REFERENCES `empresa` (`ID_Empresa`) ON DELETE CASCADE;

--
-- Filtros para la tabla `empresa`
--
ALTER TABLE `empresa`
  ADD CONSTRAINT `FK_Empresa_Especialidad` FOREIGN KEY (`Especialidad`) REFERENCES `especialidad` (`ID_Especialidad`) ON DELETE SET NULL;

--
-- Filtros para la tabla `practicas`
--
ALTER TABLE `practicas`
  ADD CONSTRAINT `FK_Practicas_Alumno` FOREIGN KEY (`DNI_Alumno`) REFERENCES `alumno` (`DNI_Alumno`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_Practicas_Empresa` FOREIGN KEY (`ID_Empresa`) REFERENCES `empresa` (`ID_Empresa`) ON DELETE CASCADE;

--
-- Filtros para la tabla `tutor_empresa`
--
ALTER TABLE `tutor_empresa`
  ADD CONSTRAINT `FK_Tutor_Empresa_Empresa` FOREIGN KEY (`ID_Empresa`) REFERENCES `empresa` (`ID_Empresa`) ON DELETE CASCADE;

--
-- Filtros para la tabla `visita_seguimiento`
--
ALTER TABLE `visita_seguimiento`
  ADD CONSTRAINT `FK_Visita_Seguimiento_Practica` FOREIGN KEY (`ID_Practica`) REFERENCES `practicas` (`ID_Practica`) ON DELETE CASCADE,
  ADD CONSTRAINT `FK_Visita_Seguimiento_Tutor` FOREIGN KEY (`DNI_Tutor`) REFERENCES `tutor_empresa` (`DNI_Tutor_Empresa`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
