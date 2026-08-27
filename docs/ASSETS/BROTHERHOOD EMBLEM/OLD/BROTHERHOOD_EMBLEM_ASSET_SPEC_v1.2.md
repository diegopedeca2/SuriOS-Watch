# BROTHERHOOD_EMBLEM_ASSET_SPEC

---

document: ASSET_SPEC
asset: Brotherhood Emblem
asset_id: BROTHERHOOD_EMBLEM_MASTER
version: 1.2
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

El recurso gráfico maestro oficial del proyecto será:

```
brotherhood_emblem_master.png
```

Este archivo constituye la referencia absoluta del emblema de la Hermandad del Acero para todo el ecosistema SuriOS.

Todas las variantes oficiales deberán derivarse exclusivamente de este recurso.

El recurso maestro nunca deberá modificarse directamente.

Ninguna implementación podrá utilizar imágenes descargadas nuevamente de Internet ni versiones diferentes del emblema.

---

# 4. Ubicación oficial

El repositorio oficial del emblema será:

```
D:\WristOS\assets\branding\brotherhood
```

Este directorio constituye la ubicación canónica del recurso gráfico durante todo el ciclo de vida del proyecto.

Todas las implementaciones de SuriOS Watch y PIP-SuriOS deberán utilizar exclusivamente archivos procedentes de este repositorio oficial.

---

# 5. Inventario oficial de recursos

El repositorio oficial contendrá los siguientes archivos:

```
assets/
└── branding/
    └── brotherhood/
        ├── brotherhood_emblem_master.png
        ├── brotherhood_emblem_pipgreen.png
        ├── brotherhood_emblem_pipgreendim.png
        └── brotherhood_emblem_monoblack.png
```

No deberán existir variantes oficiales fuera de este directorio.

---

# 6. Formato maestro

El formato oficial del recurso maestro será:

- PNG.
- Fondo completamente transparente.
- Resolución suficiente para permitir escalado sin pérdida apreciable.

Otros formatos (WEBP, SVG, JPG u otros) podrán conservarse únicamente como material de referencia, respaldo o documentación.

El PNG transparente será siempre el recurso oficial utilizado por el proyecto.

---

# 7. Características del recurso maestro

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

# 8. Variantes oficiales

Únicamente se consideran oficiales las siguientes variantes.

## 8.1 Recurso maestro

Archivo:

```
brotherhood_emblem_master.png
```

Uso:

Origen de todas las variantes oficiales.

No se utilizará directamente en la interfaz salvo que un Sprint lo autorice expresamente.

---

## 8.2 Variante principal

Archivo:

```
brotherhood_emblem_pipgreen.png
```

Uso:

- Interfaces activas.
- Pantallas principales.
- Branding del proyecto.

Color:

PipGreen.

Será la variante utilizada por defecto.

---

## 8.3 Variante tenue

Archivo:

```
brotherhood_emblem_pipgreendim.png
```

Uso:

- Ambient Mode.
- Elementos de fondo.
- Interfaces de bajo consumo.

Color:

PipGreenDim.

Su finalidad es reducir el consumo energético manteniendo la identidad visual del proyecto.

---

## 8.4 Variante monocromática

Archivo:

```
brotherhood_emblem_monoblack.png
```

Uso:

Casos excepcionales donde la plataforma limite el color disponible.

Color:

Un único color sólido.

No podrán añadirse degradados, sombras ni efectos gráficos.

---

# 9. Colores

El emblema utilizará exclusivamente la paleta oficial definida por el proyecto.

Como norma:

- PipGreen.
- PipGreenDim.
- Transparente.
- Negro cuando forme parte del diseño de la interfaz.

Los valores exactos estarán definidos por la guía gráfica oficial del proyecto.

---

# 10. Intensidad

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

# 11. Escalado

El emblema podrá escalarse libremente siempre que:

- conserve las proporciones originales;
- no se deforme;
- no se estire;
- no se comprima;
- no se recorte.

No existe un tamaño único obligatorio.

Cada módulo determinará el tamaño adecuado según su diseño.

---

# 12. Posicionamiento

Este documento no define posiciones concretas.

La ubicación del emblema será responsabilidad del documento de diseño correspondiente.

Por ejemplo:

- WATCHFACE_LAYOUT.
- Layout de PIP-SuriOS.
- Splash Screen.
- Pantallas específicas.

---

# 13. Orden de dibujo

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

# 14. Transparencia

El porcentaje de opacidad dependerá del contexto.

Como norma:

El emblema deberá permanecer claramente reconocible sin dificultar la lectura de la información principal.

Cada módulo establecerá el valor exacto necesario.

---

# 15. Reglas de uso

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

# 16. Fuera del alcance

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

# 17. Implementación

La incorporación del emblema en cada módulo se realizará mediante el Sprint correspondiente.

La presente especificación no autoriza por sí misma ninguna implementación.

Cada integración deberá quedar registrada en:

- WFPRD correspondiente.
- Sprint correspondiente.
- Historial del proyecto.

---

# 18. Compatibilidad

La geometría oficial del emblema constituye la identidad visual del ecosistema SuriOS.

Las futuras versiones de este documento podrán:

- añadir nuevas variantes oficiales;
- ampliar documentación;
- mejorar especificaciones.

No podrán modificar el diseño base del recurso maestro aprobado.

---

# 19. Dependencias documentales

Este documento se complementa con:

- PROJECT_GUIDE.
- WFPRD.
- WATCHFACE_LAYOUT.
- Sprint correspondiente.
- ACTIVE_SPRINT.
- SPRINT_HISTORY.

---

# 20. Control de versiones

| Versión | Estado | Descripción |
|----------|--------|-------------|
| 1.0 | Aprobada | Primera especificación oficial del emblema de la Hermandad del Acero como recurso gráfico único del ecosistema SuriOS. |
| 1.1 | Aprobada | Se incorpora el identificador único del recurso, la ubicación oficial, la definición del formato maestro PNG, el alcance funcional del documento y la política de compatibilidad futura. |
| 1.2 | Aprobada | Se adopta el repositorio oficial de assets, se define el recurso maestro `brotherhood_emblem_master.png`, se documenta el inventario oficial de variantes y se identifican explícitamente los archivos gráficos autorizados. |