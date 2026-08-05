# PROJECT_GUIDE.md

---
document: PROJECT_GUIDE
project: SuriOS Ecosystem
version: 1.0
status: Active
source: Markdown
---

# SuriOS Ecosystem
## Project Guide

## 1. Propósito
Este documento define la metodología oficial de trabajo para todos los proyectos del ecosistema SuriOS.

No describe funcionalidades ni sustituye a los PRD. Su objetivo es establecer una única forma de trabajar para todos los miembros del proyecto (humanos o IA).

## 2. Documentación oficial

### EDL
Fuente de verdad para:
- Diseño
- Arquitectura visual
- Componentes
- Interacción
- Metodología común

### MRPD
Fuente de verdad de PIP-SuriOS.

### WFRPD
Fuente de verdad de SuriOS Watch.

### Futuros documentos
- Launcher PRD
- Biometric Link PRD
- Otros proyectos del ecosistema.

## 3. Fuente de verdad

Los documentos maestros del proyecto serán siempre archivos Markdown (`.md`).

Los documentos Word (`.docx`) y PDF serán documentos derivados para revisión, distribución o copia de seguridad. Nunca se editarán directamente.

## 4. Flujo documental

Idea

↓

Discusión

↓

Aprobación

↓

Actualizar Markdown

↓

Generar Word

↓

Backup

↓

Commit

## 5. Flujo de desarrollo

EDL

↓

PRD correspondiente

↓

Sprint

↓

Implementación

↓

Pruebas

↓

Commit

## 6. Roles

### Propietario del proyecto
Responsable: Diego Pérez de Camino

Funciones:
- Visión del proyecto.
- Priorización.
- Validación.
- Commits.

### ChatGPT
Funciones:
- Arquitectura.
- Diseño.
- UX.
- Documentación.
- Revisión.
- Propuesta de nuevas funcionalidades.

### Orca
Funciones:
- Implementación.
- Refactorización.
- Android Studio.
- Actualización de documentación.
- Programación.

## 7. Flujo de trabajo con Orca

Antes de comenzar un sprint, Orca deberá consultar, por este orden:

1. PROJECT_GUIDE.md
2. EDL.md
3. PRD correspondiente

Solo entonces comenzará la implementación.

## 8. Convenciones

- Un commit = una funcionalidad.
- Documentación antes que código.
- Nunca implementar algo que no exista previamente en un PRD.
- No modificar el diseño sin aprobación.
- Toda funcionalidad tendrá una prioridad (Alta, Media o Baja).

## 9. Definition of Done

Una funcionalidad únicamente se considera terminada cuando:

- Está implementada.
- Ha sido probada.
- No presenta errores bloqueantes.
- Existe un commit estable.
- La documentación está actualizada.

## 10. Gestión de versiones

- Cada versión será completa y autocontenida.
- Nunca resumirá la anterior.
- Siempre partirá de la versión inmediatamente anterior.
- Nunca se sobrescribirá un documento aprobado.

## 11. Estructura recomendada del repositorio

```
docs/
    EDL.md
    MRPD.md
    WFRPD.md
    PROJECT_GUIDE.md

app/
watch/
launcher/
assets/
```

## 12. Próximos proyectos

- PIP-SuriOS
- SuriOS Watch
- PIP-SuriOS Launcher
- Biometric Link
- Otros proyectos del ecosistema

## 13. Filosofía

Construir poco.

Construir bien.

Documentar siempre.

Mantener un ecosistema coherente.

Priorizar la mantenibilidad sobre la velocidad de desarrollo.
