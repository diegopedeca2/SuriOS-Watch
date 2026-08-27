# BROTHERHOOD_EMBLEM_ASSET_SPEC

---

document: ASSET_SPEC
asset: Brotherhood Emblem
asset_id: BROTHERHOOD_EMBLEM_MASTER
version: 1.1
project: Ecosistema SuriOS
type: Recurso gráfico oficial
document_status: Aprobado
implementation_status: Pendiente
priority: Alta
owner: Diego Pérez de Camino
date: 2026-08-06

---

# 1. Objetivo

Este documento define la especificación oficial del emblema de la Hermandad del Acero utilizado en el ecosistema SuriOS.

Su finalidad es garantizar que todos los proyectos compartan exactamente la misma identidad visual, evitando diferencias de forma, color, proporciones o tratamiento gráfico entre aplicaciones, módulos o dispositivos.

Este documento constituye la referencia única para cualquier implementación futura del emblema.

---

# 2. Alcance

Esta especificación es aplicable a:

- SuriOS Watch.
- PIP-SuriOS.
- Pantallas de carga.
- Pantallas de inicio.
- Splash Screens.
- Widgets.
- Iconografía interna.
- Material gráfico del proyecto.
- Documentación técnica cuando proceda.

No afecta a logotipos de terceros ni a otros recursos gráficos independientes del proyecto.

---

# 3. Recurso maestro

El emblema oficial del proyecto será un único archivo maestro.

Este archivo será la fuente de todas las variantes utilizadas posteriormente.

Ninguna implementación podrá utilizar imágenes descargadas nuevamente de Internet ni versiones diferentes del emblema.

Todas las adaptaciones deberán derivarse exclusivamente del recurso maestro aprobado.

---

# 4. Ubicación oficial

El recurso maestro y todas sus variantes oficiales deberán almacenarse exclusivamente en:

```
D:\WristOS\docs\ASSETS\BROTHERHOOD EMBLEM
```

Este directorio constituirá la ubicación canónica del recurso gráfico durante todo el ciclo de vida del proyecto.

Las implementaciones de SuriOS Watch y PIP-SuriOS deberán utilizar únicamente archivos procedentes de esta ubicación o derivados directamente del recurso maestro aquí almacenado.

---

# 5. Formato maestro

El formato oficial del emblema será:

- PNG.
- Fondo transparente.
- Resolución suficiente para permitir escalado sin pérdida apreciable.

Otros formatos (WEBP, SVG, JPG u otros) podrán conservarse únicamente como material de referencia, respaldo o documentación.

El PNG transparente será siempre el recurso oficial utilizado por el proyecto.

---

# 6. Características del recurso maestro

El archivo maestro deberá cumplir las siguientes características:

- Fondo completamente transparente.
- Proporciones originales conservadas.
- Sin deformaciones.
- Sin recortes.
- Sin sombras añadidas.
- Sin efectos tridimensionales adicionales.
- Sin desenfoques.
- Sin texto integrado.
- Sin marcos externos.

El emblema conservará siempre su geometría original.

---

# 7. Variantes oficiales

Se autorizan únicamente las siguientes variantes.

## 7.1 Variante principal

Uso:

Interfaces activas.

Color:

PipGreen.

Será la variante utilizada por defecto en la mayoría de pantallas del proyecto.

---

## 7.2 Variante tenue

Uso:

Ambient Mode.

Color:

PipGreenDim.

Su finalidad es reducir el consumo energético y mantener la coherencia visual con el resto de elementos del modo ambiente.

---

## 7.3 Variante monocromática

Uso:

Casos excepcionales donde la plataforma limite el color disponible.

Color:

Un único color sólido.

No podrán añadirse degradados, sombras ni efectos gráficos.

---

# 8. Colores

El emblema utilizará exclusivamente la paleta oficial definida por el proyecto.

Como norma:

- PipGreen.
- PipGreenDim.
- Transparente.
- Negro cuando forme parte del diseño de la interfaz.

Los valores exactos de la paleta estarán definidos por la guía gráfica oficial del proyecto.

---

# 9. Intensidad

La intensidad visual dependerá del contexto.

Como norma general:

- Elemento protagonista:
  intensidad alta.

- Elemento secundario:
  intensidad media.

- Elemento de fondo:
  intensidad baja.

El objetivo será mantener la identidad visual sin competir con la información funcional de la interfaz.

---

# 10. Escalado

El emblema podrá escalarse libremente siempre que:

- conserve las proporciones originales;
- no se deforme;
- no se estire;
- no se comprima;
- no se recorte.

No existe un tamaño único obligatorio.

Cada módulo determinará el tamaño adecuado según su diseño.

---

# 11. Posicionamiento

Este documento no define posiciones concretas.

La ubicación del emblema será responsabilidad del documento de diseño correspondiente.

Por ejemplo:

- WATCHFACE_LAYOUT.
- Layout de PIP-SuriOS.
- Splash Screen.
- Pantallas específicas.

---

# 12. Orden de dibujo

Cuando el emblema actúe como elemento de fondo deberá dibujarse siempre por detrás de:

- hora;
- fecha;
- batería;
- pasos;
- indicadores;
- botones;
- controles;
- texto.

Nunca podrá ocultar información funcional.

---

# 13. Transparencia

El porcentaje de opacidad dependerá del contexto.

Como norma:

El emblema deberá permanecer claramente reconocible sin dificultar la lectura de la información principal.

Cada módulo establecerá el valor exacto necesario.

---

# 14. Reglas de uso

Está permitido:

- cambiar el tamaño;
- ajustar la intensidad;
- utilizar PipGreen;
- utilizar PipGreenDim;
- utilizar transparencia;
- utilizar la variante monocromática.

No está permitido:

- modificar la geometría;
- eliminar elementos;
- añadir elementos;
- cambiar proporciones;
- aplicar rotaciones arbitrarias;
- aplicar perspectivas;
- añadir efectos gráficos;
- sustituir el recurso maestro por otro diferente.

---

# 15. Fuera del alcance

Este documento no define:

- posiciones concretas;
- tamaños específicos;
- porcentajes de opacidad;
- animaciones;
- comportamiento funcional;
- integración en pantallas concretas.

Estos aspectos se documentarán en:

- WFPRD.
- WATCHFACE_LAYOUT.
- Sprint correspondiente.

---

# 16. Implementación

La incorporación del emblema en cada módulo se realizará mediante el Sprint correspondiente.

La presente especificación no autoriza por sí misma ninguna implementación.

Cada integración deberá quedar registrada en:

- WFPRD correspondiente.
- Sprint correspondiente.
- Historial del proyecto.

---

# 17. Compatibilidad

La geometría oficial del emblema constituye la identidad visual del ecosistema SuriOS.

Las futuras versiones de este documento podrán:

- añadir variantes oficiales;
- ampliar documentación;
- mejorar especificaciones.

No podrán modificar el diseño base del recurso maestro aprobado.

---

# 18. Dependencias documentales

Este documento se complementa con:

- PROJECT_GUIDE.
- WFPRD.
- WATCHFACE_LAYOUT.
- Sprint correspondiente.
- ACTIVE_SPRINT.
- SPRINT_HISTORY.

---

# 19. Control de versiones

| Versión | Estado | Descripción |
|----------|--------|-------------|
| 1.0 | Aprobada | Primera especificación oficial del emblema de la Hermandad del Acero como recurso gráfico único del ecosistema SuriOS. |
| 1.1 | Aprobada | Se incorpora el identificador único del recurso, la ubicación oficial, la definición del formato maestro PNG, el alcance funcional del documento y la política de compatibilidad futura. |