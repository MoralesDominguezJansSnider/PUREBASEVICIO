-- 1. Eliminar la base de datos si existe y crearla de nuevo (opcional, para empezar limpio)
DROP DATABASE IF EXISTS proyecto_infantes;
CREATE DATABASE proyecto_infantes;
USE proyecto_infantes;

-- 2. Tabla estudiante (usando BIGINT para coincidir con Long de Java)
CREATE TABLE estudiante (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    edad INT NOT NULL,
    nivel_socioeconomico VARCHAR(20) NOT NULL,
    num_hermanos INT NOT NULL
);

-- 3. Tabla comida (foreign key con BIGINT)
CREATE TABLE comida (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    estudiante_id BIGINT,
    FOREIGN KEY (estudiante_id) REFERENCES estudiante(id) ON DELETE CASCADE
);

-- 4. Insertar los 5 estudiantes
INSERT INTO estudiante (nombre, apellido, edad, nivel_socioeconomico, num_hermanos) VALUES
('Juan', 'Pérez', 7, 'Pobre', 2),
('María', 'López', 9, 'Medio', 1),
('Luis', 'García', 6, 'Rico', 0),
('Ana', 'Rodríguez', 8, 'Pobre', 3),
('Carlos', 'Mendoza', 10, 'Medio', 2);

-- 5. Insertar las comidas de cada estudiante
-- Juan Pérez (id=1)
INSERT INTO comida (descripcion, estudiante_id) VALUES
('Sopa de fideos sin carne', 1),
('Pan con mermelada', 1);

-- María López (id=2)
INSERT INTO comida (descripcion, estudiante_id) VALUES
('Arroz con huevo', 2),
('Jugo de caja', 2);

-- Luis García (id=3)
INSERT INTO comida (descripcion, estudiante_id) VALUES
('Pollo a la plancha con puré', 3),
('Helado de chocolate', 3);

-- Ana Rodríguez (id=4)
INSERT INTO comida (descripcion, estudiante_id) VALUES
('Menestras sin carne', 4),
('Agua', 4);

-- Carlos Mendoza (id=5)
INSERT INTO comida (descripcion, estudiante_id) VALUES
('Lentejas con arroz', 5),
('Manzana', 5);


-- Ver los estudiantes
SELECT * FROM estudiante;

-- Ver un estudiante con sus comidas (ejemplo Juan Pérez)
SELECT e.nombre, e.apellido, c.descripcion 
FROM estudiante e 
JOIN comida c ON e.id = c.estudiante_id 
WHERE e.id = 1;