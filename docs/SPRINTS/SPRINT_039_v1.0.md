# Sprint 039 — SuriOS Watch: meteorología y WFF v2

---
document: SPRINT
project: SuriOS Watch
version: 1.0
status: Cerrado; completado
owner: Diego Pérez de Camino
date: 2026-10-16
predecessor: Sprint 038
watchface_version: 1.5
watchface_version_code: 9
watchface_format: 2
adb_endpoint: 192.168.1.63:43083
---

## Preparación

El Sprint 039 queda abierto para incorporar información meteorológica a la
esfera oficial del Samsung Galaxy Watch Ultra.

Antes de modificarla se ha conservado la versión funcional anterior en:

- `Previous Versions/watchface-v1.4-sprint-039/`

La copia incluye el código fuente, los recursos y la configuración, pero no las
carpetas generadas de compilación.

## Estado inicial

- El Sprint 038 está cerrado con auditoría aprobada y commit enviado al remoto.
- La versión funcional anterior era v1.4, `versionCode 8`, WFF v1.
- La esfera preparada para este Sprint es v1.5, `versionCode 9`, WFF v2.
- La migración eleva el `minSdk` del módulo de 33 a 34, requisito de WFF v2.
- El Watch Ultra está conectado por ADB Wi-Fi en `192.168.1.63:43083`.

## Alcance

- Mostrar la temperatura actual.
- Mostrar la probabilidad actual de precipitación.
- Mantener el diseño y las funciones validadas del Sprint 038.
- Gestionar la falta de datos meteorológicos mostrando un estado neutro.

## Alcance completado

- [x] Migrar la esfera a WFF v2.
- [x] Añadir los indicadores de temperatura y probabilidad de lluvia en modo
      normal.
- [x] Convertir la armadura en un indicador cíclico del objetivo personal de
      pasos.
- [x] Añadir hasta diez celdas consumidas bajo la batería, una por objetivo
      superado.
- [x] Configurar el modo ambiente con solo emblema, hora centrada y batería.
- [x] Mantener el funcionamiento validado de los cuatro botones.

## Validación y cierre

- `gradlew.bat test lint :watchface:assembleDebug`: correcto.
- Parseo de `watchface.xml`: correcto.
- Instalación ADB Wi-Fi en `192.168.1.63:43083`: `Success`.
- Versión instalada: v1.5, `versionCode 9`, WFF v2.
- La auditoría queda documentada en [AUDIT_SPRINT_039](../AUDIT_SPRINT_039.md).
- La validación visual del modo ambiente queda pendiente de activación natural.
- La validación del primer ciclo de pasos queda pendiente de superar el
  objetivo diario real.

El Sprint 039 queda **CLOSED** y sin incidencias técnicas abiertas dentro de
su alcance.
