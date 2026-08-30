# Sprint 022 — Agradecimientos en SET-UP

## Estado

- Apertura: 2026-08-30.
- Cierre: 2026-08-31.
- Estado: cerrado.
- Implementación: completada y validada.
- Aplicación objetivo: PIP-SuriOS full.
- Este sprint se mantiene independiente de los procesos de los SPRINT 20 y 21.

## Objetivo

Incorporar dentro de `SET-UP` un visualizador de solo lectura para reconocer a
las personas y equipos que han prestado apoyo concreto al desarrollo de SuriOS.
Si se producen nuevas colaboraciones, la lista se ampliará editando el código
por terminal.

## Agradecimientos iniciales

- **Fenrir:** por dar la idea de crear la app gracias a la partida
  `FALLOUT_SOFT`.
- **Jesús:** por enseñar el funcionamiento de los agentes de IA, Orca y
  Android Studio.
- **Luis:** por ayudar con algunos elementos estéticos.
- **Jaime:** por la idea inicial del sónar.
- **Equipo de Navy7:** por permitir las pruebas y aportar ideas de nuevos modos
  de uso.
- **Padre:** por regalar el Watch 2, que dio las ideas para la baliza remota
  del sónar y que se está utilizando para ello.

## Implementación

- Añadir `ACKNOWLEDGEMENTS` como entrada del menú raíz de `SET-UP`.
- Mostrar una pantalla propia con nombre y aportación de cada persona o equipo.
- Mantener el contenido como una lista estática de solo lectura, clara y fácil
  de ampliar mediante código.
- Permitir el desplazamiento vertical para conservar la legibilidad cuando la
  lista crezca.
- Mantener intactos los procesos y documentos específicos de los SPRINT 20 y
  21.

## Criterios de aceptación

1. La ruta `HOME → SET-UP → ACKNOWLEDGEMENTS` está disponible.
2. Se muestran los seis agradecimientos iniciales sin truncar el contenido.
3. La pantalla admite desplazamiento vertical y `BACK` devuelve al menú de
   `SET-UP`.
4. La pantalla no ofrece edición, guardado ni controles de modificación.
5. Añadir nuevas entradas se realiza únicamente mediante cambios de código y
   no requiere modificar la navegación.
6. La compilación y las verificaciones relevantes terminan correctamente.

## Validación del cierre y regresión

- El SPRINT 21 se ha confirmado como cerrado: su implementación, pruebas,
  documentación y regresión están presentes en el árbol actual. La incidencia
  intermedia no deja una regresión observable en su alcance.
- La regresión reproducible posterior a la incidencia terminó correctamente con
  `:app:testFullDebugUnitTest`, `:app:lintFullDebug`,
  `:app:assembleFullDebug`, `:probeprotocol:test`,
  `:watchface:assembleDebug`, `:watchfacev2:assembleDebug` y
  `:probe:assembleDebug`.
- `:app:connectedFullDebugAndroidTest` ejecutó 2/2 pruebas correctamente en el
  Samsung A56 (`RZGYC07H0EX` / `SM-A566B`).
- La validación manual en el A56 confirmó `HOME → SET-UP → ACKNOWLEDGEMENTS`,
  los seis agradecimientos, el desplazamiento vertical y el retorno correcto a
  `SET-UP`. La pantalla no muestra controles `EDIT` ni `DELETE`.
- `git diff --check` termina correctamente y `USER_GUIDE` documenta el
  visualizador de solo lectura y su ampliación mediante código.

## Cierre

El SPRINT 22 queda cerrado técnica, funcional y documentalmente el 2026-08-31.
El visualizador permanece estático y de solo lectura; cualquier nuevo
agradecimiento se incorpora editando la lista en código por terminal.
