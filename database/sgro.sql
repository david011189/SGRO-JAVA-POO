-- ============================================================
--  SGRO — Sistema de Gestión de Reservas Online
--  Hotel Barlen | Anco Huallo, Apurímac, Perú
-- ============================================================
--  INSTRUCCIONES:
--  1. Abrir MySQL Workbench
--  2. File → Open SQL Script → seleccionar este archivo
--  3. Presionar el rayo ⚡ (Execute All)
--  4. Listo. La base de datos "sgro" quedará creada.
-- ============================================================

-- Crear y seleccionar la base de datos
CREATE DATABASE IF NOT EXISTS sgro;
USE sgro;

-- ============================================================
-- TABLA: clientes
-- Almacena usuarios del sistema con sus roles
-- ============================================================
CREATE TABLE IF NOT EXISTS clientes (
    id       INT          AUTO_INCREMENT PRIMARY KEY,
    dni      VARCHAR(20)  NOT NULL UNIQUE,
    nombre   VARCHAR(100) NOT NULL,
    correo   VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    rol      ENUM('cliente','admin','recepcionista') NOT NULL DEFAULT 'cliente'
);

INSERT INTO clientes (dni, nombre, correo, password, rol) VALUES
    ('12345678', 'David Calderón', 'david@gmail.com',  '12345', 'admin'),
    ('98745632', 'Laura Zarate',   'laura@gmail.com',  '12345', 'cliente'),
    ('87654321', 'Maria Lopez',    'maria@barlen.com', '12345', 'recepcionista');

-- ============================================================
-- TABLA: habitaciones
-- Las 5 habitaciones del Hotel Barlen a S/20.00 c/u
-- ============================================================
CREATE TABLE IF NOT EXISTS habitaciones (
    id          INT           AUTO_INCREMENT PRIMARY KEY,
    numero      INT           NOT NULL UNIQUE,
    tipo        VARCHAR(50)   NOT NULL,
    capacidad   INT           NOT NULL,
    descripcion VARCHAR(200)  NOT NULL,
    estado      ENUM('disponible','ocupada','mantenimiento') NOT NULL DEFAULT 'disponible',
    tarifa      DECIMAL(10,2) NOT NULL
);

INSERT INTO habitaciones (numero, tipo, capacidad, descripcion, estado, tarifa) VALUES
    (101, 'Simple', 1, 'Habitación simple con cama individual y baño privado',  'disponible', 20.00),
    (102, 'Simple', 1, 'Habitación simple con cama individual y baño privado',  'disponible', 20.00),
    (103, 'Doble',  2, 'Habitación doble con cama matrimonial y baño privado',  'disponible', 20.00),
    (104, 'Doble',  2, 'Habitación doble con cama matrimonial y baño privado',  'disponible', 20.00),
    (105, 'Triple', 3, 'Habitación triple con tres camas y baño privado',        'disponible', 20.00);

-- ============================================================
-- TABLA: reserva
-- Registro de todas las reservas realizadas
-- ============================================================
CREATE TABLE IF NOT EXISTS reserva (
    id             INT           AUTO_INCREMENT PRIMARY KEY,
    cliente        VARCHAR(100)  NOT NULL,
    fecha_ingreso  DATE          NOT NULL,
    fecha_salida   DATE          NOT NULL,
    habitacion     INT           NOT NULL,
    costo          DECIMAL(10,2) NOT NULL,
    dias           INT           NOT NULL DEFAULT 1,
    metodo_pago    VARCHAR(30)   NOT NULL DEFAULT 'efectivo',
    estado_reserva VARCHAR(20)   NOT NULL DEFAULT 'confirmada'
);

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================
