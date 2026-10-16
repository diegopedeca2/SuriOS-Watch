# Sprint 038 — SuriOS Watch: esfera oficial

---
document: SPRINT
project: SuriOS Watch
version: 1.0
status: Cerrado; completado
owner: Diego Pérez de Camino
date: 2026-10-16
predecessor: Sprint 037
watchface_version: 1.4
watchface_version_code: 8
adb_endpoint: 192.168.1.63:43083
---

## Preparación

El Sprint 038 queda abierto para continuar el trabajo sobre la esfera oficial
del Samsung Galaxy Watch Ultra.

Antes de modificarla se ha conservado la versión funcional anterior en:

- `Previous Version/watchface-v1.3-sprint-038/`

La copia incluye el código fuente, los recursos y la configuración, pero no las
carpetas generadas de compilación.

## Estado inicial

- El repositorio está accesible en `D:\WristOS`.
- El repositorio Git estaba limpio antes de abrir este Sprint.
- La versión instalada y validada al inicio era v1.3, `versionCode 7`.
- La esfera preparada para este Sprint es v1.4, `versionCode 8`.
- Las pruebas `test`, `lint` y `:watchface:assembleDebug` han finalizado
  correctamente antes de iniciar los cambios funcionales.
- El Watch Ultra está conectado por ADB Wi-Fi en `192.168.1.63:43083`.

## Alcance completado

- [x] Convertir el progreso de pasos de una variación de brillo a un recorte
      vertical de abajo hacia arriba.
- [x] Sustituir las dos líneas circulares por una rueda dentada exterior.
- [x] Alinear los dientes para conseguir simetría izquierda/derecha.
- [x] Eliminar el anillo interior y las marcas de las posiciones horarias.
- [x] Añadir el emblema Brotherhood como fondo verde difuminado en modo normal.
- [x] Mantener el emblema oculto en modo ambiente.
- [x] Mantener la armadura, botones, batería, fecha, línea de refresco y
      funcionamiento de `TRAINING` validados anteriormente.

## Validación y cierre

- La validación visual del propietario fue aprobada después de cada cambio.
- `gradlew.bat test lint :watchface:assembleDebug`: correcto.
- Parseo de `watchface.xml`: correcto.
- `git diff --check`: correcto.
- No hay carpetas `build` dentro de `Previous Version`.
- No se encontraron marcadores de conflicto Git.
- Instalación ADB Wi-Fi en `192.168.1.63:43083`: `Success`.
- Versión instalada: v1.4, `versionCode 8`.

El Sprint 038 queda **CLOSED** y sin incidencias técnicas abiertas dentro de
su alcance.

