# Guía de Pruebas QA - Proyecto Firma de Contratos

Este proyecto contiene pruebas automatizadas y de verificación manual realizadas por el rol QA/DevOps durante los días 1 a 6 del desarrollo.

## 📦 Contenido del paquete

- `Dia1_Setup_Entorno_Maven.sh`: Script con comandos para crear la estructura de proyecto y validar Maven.
- `ControladorAutenticacionTest.java`: Pruebas al endpoint `/login` con credenciales válidas e inválidas.
- `ContratoControladorTest.java`: Pruebas al endpoint `/contrato/view/{id}`.
- `FormularioValidacionTest.java`: Validaciones de campos del formulario de firma.
- `CanvasFirmaUsabilidadTest.java`: Prueba manual para validar la firma en dispositivos móviles.

## 🧪 Requisitos

- Java 11+
- Apache Maven instalado (`mvn -v`)
- Proyecto Spring Boot configurado con `pom.xml`
- Las rutas de API deben coincidir con las usadas en los tests

## 🚀 Instrucciones

### Día 1: Configuración del entorno

```bash
sh Dia1_Setup_Entorno_Maven.sh
```

### Días 2 a 6: Ejecutar pruebas

Ubica los archivos `.java` en tu proyecto dentro de:

```
src/test/java/com/ejemplo/aplicacion/
```

Luego ejecuta:

```bash
mvn clean test
```

### Día 6: Prueba manual de firma

1. Abre Google Chrome y accede a `http://localhost:8080/firmar`
2. Usa DevTools (Ctrl+Shift+M) para activar modo responsive
3. Firma en el canvas y observa la consola (JSON/SVG)
4. Verifica que el trazo se guarda y se incluye en el PDF final

## ✅ Notas

- Las pruebas utilizan `WebTestClient`, asegúrate de tenerlo en tu `pom.xml`
- Las pruebas requieren que la aplicación esté corriendo en entorno local

---

**Autor:** QA / DevOps - Proyecto 3 FCT