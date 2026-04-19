CREATE DATABASE IF NOT EXISTS gestion_proyectos_db;
USE gestion_proyectos_db;

CREATE TABLE IF NOT EXISTS docentes (
    id_docente BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    area VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS estudiantes (
    id_estudiante BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    programa_academico VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS proyectos (
    id_proyecto BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    objetivos VARCHAR(500) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    id_docente BIGINT NOT NULL,
    CONSTRAINT fk_proyecto_docente FOREIGN KEY (id_docente) REFERENCES docentes(id_docente)
);

CREATE TABLE IF NOT EXISTS actividades (
    id_actividad BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    fecha_entrega DATE NOT NULL,
    id_proyecto BIGINT NOT NULL,
    CONSTRAINT fk_actividad_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto)
);

CREATE TABLE IF NOT EXISTS entregables (
    id_entregable BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_actividad BIGINT NOT NULL,
    id_estudiante BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_subida DATE NOT NULL,
    CONSTRAINT fk_entregable_actividad FOREIGN KEY (id_actividad) REFERENCES actividades(id_actividad),
    CONSTRAINT fk_entregable_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante)
);
