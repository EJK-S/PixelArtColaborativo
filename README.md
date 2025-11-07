# 🎨 Proyecto: Pixel Art Colaborativo

Este es el repositorio para el proyecto de Algorítmica II. El objetivo es crear una aplicación de escritorio (GUI con Swing) que funcione como un lienzo de pixel art colaborativo en tiempo real, similar a r/place pero en salas privadas.
# 👥 Equipo
 * (Nombre Alumno 1)
 * (Nombre Alumno 2)
 * (Nombre Alumno 3)
 * (Nombre Alumno 4)

# 🛠️ Arquitectura del Proyecto

Este proyecto no es una sola aplicación. Es un sistema Cliente-Servidor compuesto por dos (2) programas que deben ejecutarse al mismo tiempo:
 * El Servidor (ServerApp.java):
   * Es una aplicación de consola (sin GUI).
   * Actúa como el "cerebro" central.
   * Gestiona las cuentas de usuario, la autenticación y el estado maestro del lienzo.
   * Su main está en: src/main/java/com/equipo/pixelart/server/ServerApp.java
  
 * El Cliente (ClientApp.java):
   * Es la aplicación de escritorio GUI (Swing) que usamos todos.
   * Se conecta al Servidor para dibujar y recibir actualizaciones.
   * Su main está en: src/main/java/com/equipo/pixelart/client/ClientApp.java

# 🚀 Empezando (Guía Rápida)
Sigue estos pasos para tener el proyecto corriendo en tu máquina.
1. Prerrequisitos
 * Git: Descargar Git
 * Java JDK 21: Todos en el equipo debemos usar JDK 21 para evitar problemas. Ya lo configuramos en el pom.xml.

2. Clonar (Descargar) el Proyecto
Abre una terminal (CMD, PowerShell, Git Bash) y clona el repositorio:
git clone https://github.com/tu-usuario/PixelArtColaborativo.git
cd PIXEL_ART_COLABORATIVO

3. Importar el Proyecto (¡Importante!)
Este proyecto usa Maven (el archivo pom.xml) para gestionar la configuración y las librerías (como FlatLaf para la GUI moderna). No uses "Abrir Proyecto", usa siempre la opción de "Importar como proyecto Maven".
 * Para Eclipse:
   * Ve a File > Import...
   * Elige Maven > Existing Maven Projects.
   * Busca la carpeta que clonaste.
   * Eclipse leerá el pom.xml y lo configurará todo automáticamente.
     
 * Para IntelliJ IDEA:
   * Abre IntelliJ. En la pantalla de bienvenida, haz clic en "Open". (Si ya estás en un proyecto, ve a File > Open...).
   * Navega y selecciona la carpeta raíz que clonaste (la que contiene el archivo pom.xml).
   * IntelliJ te preguntará: "Trust and Open Maven Project?". Haz clic en "Trust Project".
   * IntelliJ leerá el pom.xml, descargará las librerías (como FlatLaf) y configurará todo automáticamente. Dale un minuto para que termine de "indexar".

 * Para VSCode:
   * Abre la carpeta que clonaste (File > Open Folder...).
   * Asegúrate de tener el "Extension Pack for Java" de Microsoft.
   * La extensión detectará el pom.xml y configurará el proyecto solo.

# 🏃 Cómo Ejecutar el Proyecto

Para probar la aplicación completa, necesitas ejecutar AMBOS programas:
 * Paso 1: Iniciar el Servidor
   * En tu IDE, busca y ejecuta el main de la clase ServerApp.java.
   * Verás un mensaje en la consola indicando que el servidor está escuchando.
 * Paso 2: Iniciar el Cliente
   * SIN CERRAR EL SERVIDOR, busca y ejecuta el main de la clase ClientApp.java.
   * Esto abrirá la ventana de la GUI (¡con el tema oscuro!).
   * Puedes ejecutar varios clientes en tu propia máquina para probar.
   
# 🌳 Flujo de Trabajo con Git (¡OBLIGATORIO!)

Para evitar conflictos y caos, seguiremos este flujo de trabajo. NUNCA hagáis push directo a la rama main.
1. Actualiza tu main
Antes de empezar a trabajar, asegúrate de tener la última versión del código:
**Regresa a la rama principal**
git checkout main

**Descarga los últimos cambios**
git pull origin main

2. Crea tu propia Rama (Branch)
Vas a crear una "copia" para trabajar sin molestar a los demás. Nombra tu rama según lo que vas a hacer (ej. feature/login-gui, bug/arreglo-chat).
**Crea tu rama y múevete a ella**
git checkout -b nombre-de-tu-rama

3. Trabaja y Guarda tus Cambios (Commit & Push)
Haz tu magia. Arregla bugs, crea la GUI, etc. Cuando tengas un progreso que quieras guardar:
  **1. Añade tus archivos al "área de espera"**
    git add .

  **2. Guarda una "foto" de tus cambios con un mensaje claro**
    git commit -m "He añadido el botón de login"

  **3. Sube tu rama a GitHub**
    git push -u origin nombre-de-tu-rama

Puedes hacer push tantas veces como quieras a tu rama.

4. Combina tu Rama (Pull Request)
Cuando tu funcionalidad esté terminada y probada, es hora de combinarla con main.
 * Ve a la página del repositorio en GitHub.
 * Verás un botón amarillo "Compare & pull request". Haz clic.
 * Escribe un resumen de lo que hiciste y asigna a tus compañeros como "Reviewers".
 * No le des a "Merge" tú mismo. El equipo debe revisar el código (¡esto es parte de la rúbrica!).
 * Una vez que el equipo dé el visto bueno, uno de ustedes puede hacer clic en "Merge pull request".
¡Y listo! Tu código ahora es parte de main y los demás pueden descargar tus cambios repitiendo el Paso 1.

# REPARTO

Persona 1: El Arquitecto del Servidor (Backend)

Misión: Construir el "cerebro" central. Esta persona se encarga de toda la lógica del servidor (la aplicación de consola).

Tareas Clave:

Crear el ServerApp.java (el main del servidor).

Programar el ClientHandler.java (la lógica multi-hilo para manejar a cada cliente).

Implementar el AuthService.java (usando FileManager y Usuario reutilizados).

Escribir el CanvasService.java (la lógica que recibe un píxel de un cliente y se lo retransmite a todos los demás).

Enfoque de Rúbrica: Arquitectura Cliente-Servidor, Persistencia.

Persona 2: El Ingeniero de GUI (Frontend - Vista Principal)

Misión: Construir la "ventana" y la interacción del usuario. Se enfoca en la apariencia y los componentes.

Tareas Clave:

Configurar el Look & Feel moderno (FlatLaf) en ClientApp.java.

Diseñar el MainFrame.java (la ventana principal con BorderLayout).

Crear el JMenuBar (para el menú "Archivo").

Crear el ToolboxPanel.java (paleta de colores, botones de lápiz, borrador).

Crear el ChatPanel.java y el UserListPanel.java (¡para usar la JTable de la rúbrica!).

Enfoque de Rúbrica: Manejo de menúes, Uso de tablas, Layout Manager, Facilidad de uso.

Persona 3: El Artista Digital (Frontend - Lienzo)

Misión: Programar la parte más compleja y "mágica" de la GUI: el lienzo de dibujo.

Tareas Clave:

Crear la clase PixelCanvasPanel.java (que extiende JPanel).

Sobrescribir el método paintComponent(Graphics g) para dibujar la cuadrícula de píxeles.

Programar la lógica de Zoom (acercar/alejar).

Crear el CanvasModel.java (la clase que guarda el Color[][] grid en el cliente).

Implementar el CanvasController.java (que usa MouseListener y MouseMotionListener para detectar dónde se hizo clic).

Enfoque de Rúbrica: Funcionalidad implementada, Clean Code.

Persona 4: El Ingeniero de Conexión y Calidad (Lógica + Pruebas)

Misión: Ser el "pegamento" que conecta todo y asegura que funcione sin errores.

Tareas Clave:

Programar el NetworkController.java (la clase que usa SwingWorker para enviar y recibir datos del servidor sin congelar la GUI).

Implementar el Patrón Command (para el "Deshacer/Rehacer" y para enviar los píxeles como objetos).

Escribir las Pruebas Unitarias (JUnit) para el proyecto (ej. AuthServiceTest.java, CanvasModelTest.java).

Enfoque de Rúbrica: Test automatizada, Aplicar patrones de diseño, Carencia de errores.
