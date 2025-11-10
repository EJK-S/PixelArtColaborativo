# Proyecto: Pixel Art Colaborativo

Este es el repositorio del proyecto del curso **Algorítmica II**.  
El objetivo es crear una aplicación de escritorio (GUI con Swing) que funcione como un **lienzo de pixel art colaborativo en tiempo real**, similar a *r/place*, pero en **salas privadas y seguras**.

---

## Equipo

- Tecsi Alanoca Jean Carlo  
- Sinche Vega Moises Pedro  
- Adrián Ángel  
- (Integrante pendiente)

---

## Cumplimiento de la Rúbrica

Este proyecto fue diseñado para cumplir con los criterios de la **Rúbrica 2**, enfocándose en la **calidad de la arquitectura** y las **buenas prácticas** sobre la cantidad de funciones.

### Criterio: Uso de GitHub
- Se utiliza este repositorio, siguiendo un flujo de trabajo con ramas y Pull Requests.  
  (Ver sección *Flujo de Trabajo con Git*)

### Criterio: Metodología (Kanban)
- Se utiliza un tablero Kanban (Task Pool) para la gestión de tareas.  
  (Ver sección *Metodología*)

### Criterio: Funcionalidades (GUI)
- Menús: `MainFrame.java` implementa una `JMenuBar` completa.  
- Tablas/Listas: `UserListPanel.java` usa `JList` para mostrar usuarios.  
- Layout Managers: se emplean `BorderLayout`, `GridLayout` y `BoxLayout` para organizar los paneles.

### Criterio: Persistencia
- Implementada en el servidor (`server/persistence/`).  
- `FileManager.java` usa serialización (`ObjectOutputStream`) para guardar `users.dat` y `canvas.dat`.

### Criterio: Calidad de la Solución
- Pruebas unitarias con **JUnit 5** en `src/test/java/`.  
  Ejemplos: `AuthServiceTest.java`, `CanvasModelTest.java`.  
- Arquitectura: Cliente-Servidor y Modelo-Vista-Controlador (MVC).  
- Patrones de diseño:  
  - Command (acciones de red y deshacer)  
  - Observer (NetworkListener)  
  - Strategy (herramientas)  
  - Singleton (servicios)
- Clean Code: código estructurado en paquetes claros (`mvc`, `service`, `network`, `persistence`).

---

## Funcionalidades Principales

- Arquitectura Cliente-Servidor: múltiples clientes se conectan a un servidor central.  
- Lienzo colaborativo: dibujo de píxeles en tiempo real visible por todos los usuarios conectados.  
- Autenticación: sistema de registro e inicio de sesión con contraseñas cifradas.  
- Herramientas de dibujo:  
  - Lápiz (con tamaño de pincel)  
  - Borrador  
  - Bote de pintura (Flood Fill)  
  - Gotero (selector de color)
- Chat en tiempo real: panel de chat (`ChatPanel`) integrado.  
- Lista de usuarios: panel (`UserListPanel`) que muestra quién está conectado.  
- Persistencia y autoguardado: el servidor guarda `canvas.dat` y `users.dat` periódicamente.  
- Consola de administrador (`ServerApp`): comandos `stats`, `users`, `save`, `clear`.

---

## Tech Stack (Tecnologías Usadas)

| Categoría | Tecnología |
|------------|-------------|
| Lenguaje | Java 21 |
| Interfaz Gráfica | Swing |
| Gestión de Proyecto | Maven |
| Networking | java.net.Socket (TCP) |
| Hilos | Thread y SwingWorker |
| Pruebas | JUnit 5 |
| Look & Feel (opcional) | FlatLaf |

---

## Metodología (Kanban)

El proyecto se gestiona mediante un tablero **Kanban** (Task Pool) en GitHub Projects.

**Flujo de trabajo:**
1. Un miembro elige una tarea en *To Do*.  
2. La mueve a *In Progress* y se asigna.  
3. Crea una rama (`feature/...`).  
4. Realiza cambios, commits y push.  
5. Crea un Pull Request.  
6. Una vez aprobado, se fusiona y se mueve a *Done*.

**Tablero del proyecto:**  
[Enlace a tu GitHub Project Board]

---

## Empezando (Guía Rápida)

### 1. Prerrequisitos
- **Git:** [Descargar aquí](https://git-scm.com/downloads)  
- **Java JDK 21:** todos los miembros deben usar la misma versión (configurada en `pom.xml`).

### 2. Clonar el Proyecto
```bash
git clone https://github.com/tu-usuario/PixelArtColaborativo.git
cd PixelArtColaborativo

### 3. Importar el Proyecto (Maven)

#### 🧩 En Eclipse
1. File > Import...
2. Selecciona Maven > Existing Maven Projects  
3. Elige la carpeta del proyecto.  
4. Espera a que se carguen las dependencias de `pom.xml`.

#### 💡 En IntelliJ IDEA
1. File > Open...  
2. Selecciona la carpeta raíz (la que contiene `pom.xml`).  
3. Acepta el mensaje “Trust Project”.  
4. IntelliJ configurará automáticamente las dependencias.

#### ⚙️ En VSCode
1. File > Open Folder...  
2. Instala la extensión Extension Pack for Java.  
3. VSCode detectará `pom.xml` y configurará el proyecto automáticamente.

---

## 🚀 Cómo Ejecutar el Proyecto

### 1. Iniciar el Servidor
- Ejecuta la clase `ServerApp.java`.  
- Espera el mensaje:
  ```
  Servidor escuchando en el puerto 12345...
  ```

### 2. Iniciar el Cliente
- Sin cerrar el servidor, ejecuta la clase `ClientApp.java`.  
- Se abrirá la interfaz gráfica.  
- Puedes abrir múltiples clientes para probar la colaboración en tiempo real.

---

## 🧭 Flujo de Trabajo con Git (Obligatorio)

> ⚠️ Nunca hagas push directo a main.

### 1. Actualiza tu rama principal
```bash
git checkout main
git pull origin main
```

### 2. Crea una nueva rama
```bash
git checkout -b feature/nueva-funcion
```

### 3. Guarda y sube tus cambios
```bash
git add .
git commit -m "Agregada nueva herramienta de dibujo"
git push -u origin feature/nueva-funcion
```

### 4. Crea un Pull Request
1. Ve al repositorio en GitHub.  
2. Crea un Pull Request (comparando tu rama con main).  
3. Asigna revisores y espera aprobación.  
4. Una vez aprobado, realiza el merge.

---

## 🧱 Estructura del Proyecto
```
PixelArtColaborativo/
├── src/
│   ├── main/java/
│   │   ├── client/
│   │   ├── server/
│   │   ├── model/
│   │   ├── service/
│   │   ├── network/
│   │   └── persistence/
│   └── test/java/
│       └── tests/...
├── pom.xml
└── README.md
```

---

## 🧩 Próximas Mejoras (Backlog)

- [ ] Implementar salas privadas con contraseña  
- [ ] Agregar capas de dibujo  
- [ ] Exportar lienzo como imagen (.png)  
- [ ] Sistema de roles (admin, invitado)  

---

## 🪪 Licencia

Proyecto desarrollado con fines académicos para el curso **Algorítmica II**.  
Uso libre para aprendizaje y contribuciones no comerciales.

---

✅ **Nota:**  
Puedes copiar este bloque tal cual, pegarlo en tu archivo `README.md`, y al previsualizarlo en GitHub o VSCode, **todo el formato se verá igual que aquí** — listas, títulos, bloques de código, y emojis incluidos.

