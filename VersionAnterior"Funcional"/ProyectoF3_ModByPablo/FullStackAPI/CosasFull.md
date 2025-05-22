Dia1

Definicion de endpoints API REST son 3:

*POST /contrato/firmar

-Cliente firma el contrato

-Método: POST

-Auth: Pública o con token temporal

Body JSON(Borrador):
```
{
  "contrato_id": "abc123",
  "nombre": "Juan",
  "apellidos": "Pérez",
  "dni": "12345678X",
  "email": "juan@email.com",
  "telefono": "600112233",
  "firma_svg": "<svg xmlns='http://www.w3.org/2000/svg' width='300' height='100'><path d='M10 80 Q 95 10 180 80' stroke='black' fill='transparent' stroke-width='2'/></svg>"
}
```
#Acciones del Backend:

-Fusionar firma_svg al PDF original (última página, centro inferior)

-Guardar metadata y PDF final en el servidor

-Enviar por correo electrónico al cliente

-Marcar como firmado en la base de datos

#Respuesta (200 OK)->json:
```
{
  "mensaje": "Contrato firmado correctamente",
  "pdf_url": "/files/firmado_abc123.pdf"
}
```
*GET /contrato/view/:id(Ver contrato PDF)

-Método: GET

-Auth: Depende del tipo de contrato (público o protegido)

-Params:id → ID del contrato

Respuesta: Devuelve el archivo PDF como Content-Type: application/pdf

*GET /contrato/list

-ROOT: Listar contratos firmados

-Método: GET

-Auth: JWT con rol ROOT

-Query Params:dni, apellidos, fecha_inicio, fecha_fin, page, limit

#Respuesta (200 OK)->json:
```
[
  {
    "id": "abc123",
    "nombre": "Juan Pérez",
    "dni": "12345678X",
    "fecha_firma": "2025-05-19",
    "pdf_url": "/files/firmado_abc123.pdf"
  }
]
```

*GET /contrato/:id/pdf

-Descargar PDF firmado

-Método: GET

-Auth: JWT ROOT o URL firmada temporal

-Respuesta: Devuelve el PDF firmado

