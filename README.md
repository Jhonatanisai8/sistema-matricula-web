# Sistema de Gestión de Matrícula para Institución Educativa (Nivel Primario)

![Banner del Proyecto (Opcional)](https://via.placeholder.com/1200x300/007bff/ffffff?text=Sistema+de+Matr%C3%ADcula+Educativa)
Este proyecto es un sistema web integral diseñado para optimizar el proceso de matrícula y la gestión de usuarios dentro de una institución educativa de nivel primario. Facilita la administración de estudiantes, docentes, apoderados y usuarios del sistema, proporcionando interfaces específicas para cada rol.

---

## 🚀 Funcionalidades Principales

El sistema se centra en las siguientes áreas clave:

* **Gestión de Usuarios con Roles Definidos:**
    * **ADMIN:** Acceso completo a la administración del sistema, incluyendo la gestión de otros roles (creación, edición, listado).
    * **ESTUDIANTE:** Acceso a su información académica, horarios y notas.
    * **APODERADO:** Consulta la información de sus hijos/pupilos, seguimiento académico y comunicación con la institución.
    * **DOCENTE:** Gestión de cursos asignados, registro de notas, asistencia y comunicación con estudiantes y apoderados.

* **Proceso de Matrícula Simplificado:**
    * Registro y administración de la información personal y académica de los estudiantes.
    * Asociación de apoderados a estudiantes.

* **Administración de Personal Docente:**
    * Registro y gestión detallada de la información de los docentes, incluyendo su historial académico y laboral.

* **Dashboard Administrativo:**
    * Vista general con estadísticas clave del sistema (número total de usuarios, docentes, apoderados, estudiantes).

---

## ✨ Características Destacadas

* **Arquitectura Modular:** Diseñado con una estructura clara y separada en capas (DTOs, Entidades, Repositorios, Servicios, Controladores).
* **Validaciones Robustas:** Implementación de Jakarta Bean Validation (JSR 380) para asegurar la integridad y calidad de los datos de entrada.
* **Seguridad Básica:** Generación automática y segura de credenciales de usuario (username y password encriptada) para nuevos docentes.
* **Interfaz Intuitiva:** Vistas basadas en Thymeleaf para una experiencia de usuario clara y responsive (se asume Bootstrap).
* **Persistencia de Datos:** Utilización de Spring Data JPA para una interacción eficiente con la base de datos.

---

## 💻 Tecnologías Utilizadas

* **Backend:**
    * Java 17+
    * Spring Boot 3+
    * Spring Data JPA (para la persistencia)
    * Hibernate (como implementación de JPA)
    * Maven (gestor de dependencias)
    * Lombok (para reducir código boilerplate)
    * Jakarta Bean Validation (para validación de datos)
    * Spring Security (para seguridad y encriptación de contraseñas, si se implementa más a fondo)

* **Frontend:**
    * Thymeleaf (motor de plantillas)
    * HTML5
    * CSS3
    * JavaScript (opcional, para interacciones dinámicas)
    * Bootstrap 5 (o similar, para diseño responsive)
    * Bootstrap Icons (o Font Awesome, para iconografía)

* **Base de Datos:**
    * (Indica aquí tu base de datos, ej: MySQL, PostgreSQL, H2 Database para desarrollo)

---

## 🛠️ Cómo Configurar y Ejecutar el Proyecto

Sigue estos pasos para poner en marcha el proyecto en tu entorno local:

### Requisitos Previos

Asegúrate de tener instalado lo siguiente:

* **Java Development Kit (JDK) 17 o superior**
* **Maven 3.6.x o superior**
* **Git**
* **Tu IDE favorito** (IntelliJ IDEA, VS Code con extensiones de Java, Eclipse)
* **Base de datos** (ej: MySQL): Configura una base de datos y un usuario, y actualiza `src/main/resources/application.properties` con tus credenciales.

### Pasos de Ejecución

1.  **Clonar el Repositorio:**
    ```bash
    git clone [https://github.com/Jhonatanisai8/sistema-matricula-web.git](https://github.com/Jhonatanisai8/sistema-matricula-web.git)
    cd sistema-matricula-web
    ```

2.  **Configurar la Base de Datos:**
    * Abre `src/main/resources/application.properties`.
    * Actualiza las propiedades de conexión a tu base de datos:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/nombre_de_tu_bd?useSSL=false&serverTimezone=UTC
        spring.datasource.username=tu_usuario_bd
        spring.datasource.password=tu_contraseña_bd
        spring.jpa.hibernate.ddl-auto=update # O 'create' si es la primera vez y quieres que cree las tablas
        spring.jpa.show-sql=true
        ```
    * Si usas H2 para pruebas, puedes configurarlo así:
        ```properties
        spring.datasource.url=jdbc:h2:mem:demodb
        spring.datasource.driverClassName=org.h2.Driver
        spring.datasource.username=sa
        spring.datasource.password=
        spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
        spring.h2.console.enabled=true
        ```

3.  **Compilar y Ejecutar:**
    * Desde la raíz del proyecto en tu terminal:
        ```bash
        mvn clean install
        mvn spring-boot:run
        ```
    * También puedes ejecutar el proyecto directamente desde tu IDE como una aplicación Spring Boot.

4.  **Acceder a la Aplicación:**
    * Una vez que la aplicación esté en funcionamiento, ábrela en tu navegador:
        `http://localhost:8080` (o el puerto configurado)

---

## 🖼️ Vistas del Sistema

Aquí se muestran algunas de las interfaces clave del sistema.

### Dashboard de Administrador

Una vista general que proporciona un resumen de las principales estadísticas del sistema.

![Dashboard de Administrador](image_64ae3d.png)
### Listado de Docentes

Muestra la lista de docentes registrados con su información relevante.

![Listado de Docentes](image_650873.png)
### Registro de Docente

Formulario para añadir nuevos docentes al sistema.

![Formulario de Registro de Docente](image_afb1b7.png)
---

## 🛣️ Próximos Pasos (Roadmap)

* Implementación completa de la gestión de Apoderados y Estudiantes.
* Módulo de asignación de cursos y horarios para docentes.
* Funcionalidad de registro de notas y asistencia por parte de los docentes.
* Interacción de apoderados con la información académica de sus hijos.
* Implementación de autenticación y autorización robusta con Spring Security.
* Funcionalidad de edición y eliminación para todas las entidades.
* Generación de reportes.

---

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Si deseas colaborar, por favor sigue estos pasos:

1.  Haz un "fork" de este repositorio.
2.  Crea una nueva rama (`git checkout -b feature/nueva-funcionalidad`).
3.  Realiza tus cambios y commitea (`git commit -m 'feat: Añade nueva funcionalidad X'`).
4.  Haz "push" a tu rama (`git push origin feature/nueva-funcionalidad`).
5.  Abre un "Pull Request".

---

## 📧 Contacto

Para cualquier pregunta o sugerencia, puedes contactar a:

* **Jhonatan Isai**
* GitHub: [Jhonatanisai8](https://github.com/Jhonatanisai8)
* Email: [tu_email@example.com](mailto:tu_email@example.com) ---

## 📄 Licencia

Este proyecto está bajo la licencia [MIT](https://opensource.org/licenses/MIT). Consulta el archivo [LICENSE](LICENSE) para más detalles.
