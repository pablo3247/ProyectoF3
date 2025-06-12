# Día 1: Script de verificación de entorno Maven y estructura de proyecto

# Este archivo simula los comandos que se deberían ejecutar desde la terminal
# para configurar y validar el entorno del proyecto con Maven.

# Verificar instalación de Maven
mvn -v

# Crear estructura de carpetas tipo Maven
mkdir -p Backend/src/main/java/com/ejemplo/aplicacion
mkdir -p Backend/src/test/java/com/ejemplo/aplicacion

# Crear archivo pom.xml
touch Backend/pom.xml

# Validar archivo pom.xml (una vez completado con dependencias)
mvn validate

# Compilar y limpiar el proyecto
mvn clean compile

# Ejecutar pruebas (vacías o esqueleto)
mvn test