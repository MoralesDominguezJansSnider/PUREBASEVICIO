-- Crear base de datos
CREATE DATABASE IF NOT EXISTS nutricional_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE nutricional_db;

-- -----------------------------------------------------
-- Tabla: usuario
-- Roles: PADRE, DOCENTE, SALUD
-- -----------------------------------------------------
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol ENUM('PADRE', 'DOCENTE', 'SALUD') NOT NULL,
    codigo_vinculacion VARCHAR(10) UNIQUE,   -- solo para padres
    activo BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tabla: estudiante (solo puede ser registrado por un PADRE)
-- -----------------------------------------------------
CREATE TABLE estudiante (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(150) NOT NULL,
    edad INT NOT NULL,
    sexo ENUM('M', 'F') NOT NULL,
    grado VARCHAR(50),
    nivel_socioeconomico ENUM('BAJO', 'MEDIO_BAJO', 'MEDIO', 'MEDIO_ALTO', 'ALTO'),
    situacion_laboral ENUM('UNO_TRABAJA', 'AMBOS_TRABAJAN', 'NINGUNO_TRABAJA'),
    acceso_alimentos ENUM('FACIL', 'MEDIO', 'DIFICIL'),
    miembros_hogar INT,
    ubicacion ENUM('URBANO', 'RURAL'),
    cultura_alimenticia ENUM('COSTA', 'SIERRA', 'SELVA'),
    tipo_cocina ENUM('COMPLETA', 'BASICA', 'LENIA'),
    tiene_refrigeradora BOOLEAN,
    tiene_agua_potable BOOLEAN,
    observaciones TEXT,
    padre_id BIGINT NOT NULL,
    FOREIGN KEY (padre_id) REFERENCES usuario(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tabla: analisis_hematologico (historial hematológico por estudiante)
-- -----------------------------------------------------
CREATE TABLE analisis_hematologico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estudiante_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hemoglobina DECIMAL(5,2),
    hematocrito DECIMAL(5,2),
    ferritina_serica DECIMAL(7,2),
    hierro_serico DECIMAL(7,2),
    registrado_por_salud BOOLEAN DEFAULT FALSE,
    observaciones TEXT,
    FOREIGN KEY (estudiante_id) REFERENCES estudiante(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tabla: comida (registro de comidas)
-- -----------------------------------------------------
CREATE TABLE comida (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estudiante_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    tipo ENUM('DESAYUNO', 'ALMUERZO', 'CENA') NOT NULL,
    alimento VARCHAR(150) NOT NULL,
    porciones DECIMAL(5,2),
    FOREIGN KEY (estudiante_id) REFERENCES estudiante(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tabla: analisis_nutricional (cálculo automático de nutrientes por comida)
-- -----------------------------------------------------
CREATE TABLE analisis_nutricional (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comida_id BIGINT NOT NULL,
    hierro_pct DECIMAL(5,2),
    vitamina_c_pct DECIMAL(5,2),
    proteinas_pct DECIMAL(5,2),
    calcio_pct DECIMAL(5,2),
    vitamina_a_pct DECIMAL(5,2),
    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (comida_id) REFERENCES comida(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tabla: alimento_nutritivo (biblioteca de alimentos)
-- -----------------------------------------------------
CREATE TABLE alimento_nutritivo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    categoria ENUM('FRUTAS', 'VERDURAS', 'CARNES', 'CEREALES', 'LACTEOS', 'LEGUMBRES', 'PESCADOS'),
    hierro DECIMAL(5,2),
    proteinas DECIMAL(5,2),
    calcio DECIMAL(5,2),
    vitamina_a DECIMAL(5,2),
    vitamina_c DECIMAL(5,2),
    imagen_url VARCHAR(255)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tabla: mensaje (comunicación entre usuarios)
-- -----------------------------------------------------
CREATE TABLE mensaje (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    remitente_id BIGINT NOT NULL,
    destinatario_id BIGINT,
    estudiante_id BIGINT,  -- opcional, para contextualizar
    contenido TEXT NOT NULL,
    leido BOOLEAN DEFAULT FALSE,
    respondido BOOLEAN DEFAULT FALSE,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (remitente_id) REFERENCES usuario(id),
    FOREIGN KEY (destinatario_id) REFERENCES usuario(id),
    FOREIGN KEY (estudiante_id) REFERENCES estudiante(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tabla: vinculacion (relación entre padre y docente/salud mediante código)
-- -----------------------------------------------------
CREATE TABLE vinculacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    padre_id BIGINT NOT NULL,
    usuario_vinculado_id BIGINT NOT NULL,
    codigo VARCHAR(10) NOT NULL,
    FOREIGN KEY (padre_id) REFERENCES usuario(id),
    FOREIGN KEY (usuario_vinculado_id) REFERENCES usuario(id),
    UNIQUE KEY uk_vinculacion (padre_id, usuario_vinculado_id)
) ENGINE=InnoDB;

DELETE FROM alimento_nutritivo;

-- Un padre de familia (tiene código de vinculación)
INSERT INTO usuario (nombre, email, password, rol, codigo_vinculacion, activo)
VALUES ('María López', 'padre@test.com', '1234', 'PADRE', 'ABDU', 1);

-- Un docente
INSERT INTO usuario (nombre, email, password, rol, codigo_vinculacion, activo)
VALUES ('Profesor Juan', 'docente@test.com', '1234', 'DOCENTE', NULL, 1);

-- Personal de salud
INSERT INTO usuario (nombre, email, password, rol, codigo_vinculacion, activo)
VALUES ('Dr. Carlos', 'salud@test.com', '1234', 'SALUD', NULL, 1);


