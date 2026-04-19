# Sistema de Gestión de Proyectos Estudiantiles

Proyecto web básico hecho con **Java 21**, **Tomcat**, **JPA (Hibernate)**, **MySQL** y **JavaScript puro**.

## 1. Tecnologías usadas
- Java 21
- Apache NetBeans
- Maven Web Application
- Apache Tomcat 10+
- MySQL 8+
- Jakarta REST (JAX-RS con Jersey)
- JPA con Hibernate
- HTML, CSS y JavaScript puro

## 2. Estructura del proyecto
- `model`: entidades JPA
- `service`: lógica de negocio
- `resource`: endpoints REST
- `webapp`: interfaz HTML/CSS/JS
- `sql`: script base de la BD

## 3. Base de datos
Nombre sugerido:
`gestion_proyectos_db`

Credenciales configuradas en `persistence.xml`:
- Usuario: `root`
- Contraseña: ``

> Si en tu computador usas otras credenciales, solo cambia esos datos en:
`src/main/resources/META-INF/persistence.xml`

## 4. Cómo abrirlo en NetBeans
1. Abre NetBeans.
2. Ve a **File > Open Project**.
3. Selecciona la carpeta `gestion-proyectos-estudiantiles`.
4. Espera a que Maven cargue las dependencias.
5. En servicios, registra tu servidor **Tomcat 10+**.
6. Haz clic derecho sobre el proyecto.
7. Selecciona **Run**.

## 5. Cómo preparar MySQL
1. Abre MySQL Workbench o phpMyAdmin.
2. Ejecuta el archivo `sql/gestion_proyectos_db.sql`.
3. Verifica que el usuario y contraseña coincidan con `persistence.xml`.

## 6. URL del proyecto
Cuando despliegue correctamente, normalmente abre en:
`http://localhost:8080/gestion-proyectos-estudiantiles/`

La API REST queda así:
- `GET http://localhost:8080/gestion-proyectos-estudiantiles/api/estudiantes`
- `GET http://localhost:8080/gestion-proyectos-estudiantiles/api/docentes`
- `GET http://localhost:8080/gestion-proyectos-estudiantiles/api/proyectos`
- `GET http://localhost:8080/gestion-proyectos-estudiantiles/api/actividades`
- `GET http://localhost:8080/gestion-proyectos-estudiantiles/api/entregables`

## 7. Ejemplo de JSON para probar
### Crear estudiante
```json
{
  "nombre": "Laura Gómez",
  "correo": "laura@compensar.edu.co",
  "programaAcademico": "Ingeniería de Software"
}
```

### Crear docente
```json
{
  "nombre": "Carlos Ramírez",
  "correo": "carlos@compensar.edu.co",
  "area": "Tecnología"
}
```

### Crear proyecto
```json
{
  "nombre": "Sistema de Biblioteca",
  "descripcion": "Proyecto interdisciplinario para préstamo de libros.",
  "objetivos": "Automatizar préstamos y reportes.",
  "fechaInicio": "2026-04-01",
  "fechaFin": "2026-06-30",
  "docente": { "id": 1 }
}
```

### Crear actividad
```json
{
  "nombre": "Diseñar base de datos",
  "descripcion": "Modelo entidad relación y script SQL.",
  "fechaEntrega": "2026-04-20",
  "proyecto": { "id": 1 }
}
```

### Crear entregable
```json
{
  "actividad": { "id": 1 },
  "estudiante": { "id": 1 },
  "estado": "pendiente",
  "fechaSubida": "2026-04-18"
}
```

## 8. Orden recomendado para probar
1. Crear docentes.
2. Crear estudiantes.
3. Crear proyectos.
4. Crear actividades.
5. Crear entregables.




