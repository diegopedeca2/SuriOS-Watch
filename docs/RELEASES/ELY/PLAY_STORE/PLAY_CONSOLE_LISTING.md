# ELY — Ficha de Google Play

## Identidad

- Nombre: ELY
- Paquete: `com.suri.surioswatch.ely`
- Tipo: Aplicación Wear OS / esfera WFF
- Precio: Gratuita
- Versión: `2.0` (`versionCode 5`)
- Artefacto: `watch/watchfaceely/build/outputs/bundle/release/watchfaceely-release.aab`

## Texto de la ficha

### Descripción breve

Una esfera de cuento para Wear OS, con una rosa, libros y detalles dorados.

### Descripción completa

ELY es una esfera de Wear OS inspirada en los cuentos clásicos de magia,
romance y aventura.

Su composición presenta una rosa bajo una cúpula de cristal, libros
ornamentados y una ambientación cálida de biblioteca. Incluye hora, fecha,
batería y pasos, con una tipografía de inspiración clásica.

Los libros funcionan como accesos directos transparentes: el libro izquierdo
abre Google Wallet y el derecho abre el reproductor de música disponible en el
reloj.

ELY funciona de forma independiente en el reloj. No incluye anuncios, cuentas
ni conexión propia a Internet.

### Categoría sugerida

Personalización.

## Material preparado

- Captura Wear OS: `ely_watchface_screenshot_480.png`
- Vista previa incluida en la aplicación: `@drawable/ely_background`
- AAB firmado: `watch/watchfaceely/build/outputs/bundle/release/watchfaceely-release.aab`

## Declaraciones técnicas sugeridas

- Aplicación standalone: sí.
- Recopilación de datos propia: ninguna.
- Compartición de datos propia: ninguna.
- Anuncios: no.
- Compras integradas: no.
- Inicio de sesión: no.

La ficha debe revisarse en Play Console antes de enviarla. Los accesos a
Google Wallet y al reproductor delegan la acción en aplicaciones externas y no
recopilan datos desde ELY.

## Pendiente de Play Console

1. Crear la aplicación con el paquete indicado.
2. Añadir el formulario Wear OS.
3. Crear una pista de pruebas internas.
4. Subir el AAB y añadir testers.
5. Completar contacto, contenido, clasificación y privacidad.
