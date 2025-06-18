#!/bin/bash

# 1. Navegar al directorio del proyecto (si es necesario)
# cd /ruta/al/proyecto

# 2. Limpiar, compilar y generar reportes
mvn clean test jacoco:report

# 3. Verificar si los reportes existen
if [ ! -f "target/site/jacoco/index.html" ]; then
    echo "❌ Error: No se encontró el reporte de JaCoCo. Asegúrate de que:"
    echo "   - El proyecto tiene dependencia JaCoCo en el pom.xml"
    echo "   - Las pruebas se ejecutaron correctamente"
    exit 1
fi

# 4. Extraer métricas (usando herramientas alternativas si grep falla)
COBERTURA=$(awk -F'|' '/instruction/ {print $4}' target/site/jacoco/jacoco.csv | head -1)
TESTS_TOTAL=$(find target/surefire-reports -name "TEST-*.xml" | xargs grep -h "testsuite" | awk -F'"' '{print $2}')
TESTS_FALLIDOS=$(find target/surefire-reports -name "TEST-*.xml" | xargs grep -h "failure" | wc -l)

# 5. Generar informe
cat <<EOF > informe_calidad.md
# Informe de Calidad

## 📊 Métricas
- **Cobertura:** ${COBERTURA:-"N/A"}
- **Pruebas totales:** ${TESTS_TOTAL:-"N/A"}
- **Pruebas fallidas:** ${TESTS_FALLIDOS:-"N/A"}

## 🔧 Recomendaciones
- Verifica los fallos en: target/surefire-reports/
- Revisa la cobertura detallada en: target/site/jacoco/index.html
EOF

echo "✅ Informe generado: informe_calidad.md"