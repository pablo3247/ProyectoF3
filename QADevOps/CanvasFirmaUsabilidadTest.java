package com.ejemplo.aplicacion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CanvasFirmaUsabilidadTest {

    @Test
    @DisplayName("Simulación de prueba manual en canvas de firma desde navegador móvil")
    void simulacionManualCanvasFirma() {
        System.out.println("1. Abrir navegador en modo responsive (Chrome DevTools).");
        System.out.println("2. Acceder a http://localhost:8080/firmar");
        System.out.println("3. Dibujar la firma en el canvas con dedo o ratón.");
        System.out.println("4. Verificar en consola del navegador que se captura JSON/SVG del trazo.");
        System.out.println("5. Confirmar que el PDF final incorpora correctamente la firma.");
    }
}