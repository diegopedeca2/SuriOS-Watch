# Sprint 032 — Seguimiento de Alpha y feedback de testers

---
document: SPRINT
project: SuriOS Ecosystem / PIP-SuriOS
version: 1.0
status: Activo
owner: Diego Pérez de Camino
date: 2026-09-04
predecessor: Sprint 031
---

## Objetivo

Recoger el resultado de las pruebas de los testers Alpha y usar datos reales
para decidir las próximas correcciones del proyecto.

## Alcance inicial

- recibir y revisar los informes de FENRIR, ALTAMIRA y CHECHU;
- comprobar si `AUD-031-01` se reproduce en otros dispositivos;
- conservar la diferencia funcional entre SENTRY y TRACKER;
- mantener PROBE fuera de las APK tester;
- no cambiar la firma de versión ni modificar código hasta tener feedback
  suficiente, salvo que el propietario autorice una incidencia concreta;
- actualizar las guías dinámicas con los resultados confirmados.

## Trabajo pendiente

- [ ] Recibir los formularios CSV de los tres testers.
- [ ] Revisar instalación, permisos, identidad, mapas, SENTRY y TRACKER.
- [ ] Comparar los resultados del mapa `TESTING` de ALTAMIRA y CHECHU con
      FENRIR.
- [ ] Decidir si `AUD-031-01` se corrige, se mantiene pausada o se cierra.
- [ ] Registrar nuevas incidencias reproducibles y priorizarlas.
- [ ] Actualizar las tres guías de tester y las tres guías de funcionamiento
      si el feedback cambia el comportamiento documentado.
- [ ] Cerrar el Sprint 032 solo cuando exista una decisión documentada y el
      propietario lo autorice.

## Estado inicial

El Sprint 032 está abierto. Las APK Alpha v3.0 ya están preparadas en
`output/SPRINT_031_APK`, dentro de tres ZIP independientes. La validación del
Samsung A56 se completó en el Sprint 031; este Sprint queda a la espera de las
pruebas externas.
