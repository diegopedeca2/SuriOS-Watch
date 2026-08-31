# PIP-SuriOS - Master Requirements & Project Document (MRPD)

Versión 1.1.1  
Documento maestro del proyecto

## PARTE I - PROYECTO

### 1\. Visión

PIP-SuriOS es un sistema personal inspirado en los PIP-Boy del universo Fallout.  
No pretende reproducir un videojuego, sino ofrecer una herramienta útil con identidad propia inspirada en RobCo y la Hermandad del Acero.  
El proyecto se centra actualmente en el escenario operativo de airsoft. El
perfil CIVILIAN queda retirado y no forma parte del producto.

### 2\. Filosofía

La utilidad prevalece sobre la fidelidad estética.  
Cada función debe resolver un problema real.  
La experiencia debe recordar a un terminal RobCo sin copiar literalmente el juego.  
El desarrollo será incremental y documentado.

### 3\. Objetivos

Funcionales:  
• Organizar información personal.  
• Apoyar partidas de airsoft.  
• Centralizar herramientas frecuentes.  
<br/>Visuales:  
• Estética consistente.  
• Alto contraste.  
• Interfaz limpia.  
<br/>Técnicos:  
• Arquitectura modular.  
• Código mantenible.  
• Escalabilidad.

### 4\. Lo que NO será

No será un juego.  
No será un clon del PIP-Boy.  
No tendrá publicidad, compras ni componentes sociales.  
No fomentará la competición mediante rankings o puntuaciones.

### 5\. Público objetivo

Usuario principal: el autor del proyecto.  
Todas las decisiones se tomarán pensando en un único usuario, aunque la arquitectura permita crecer en el futuro.

### 6\. Casos de uso

Caso OPERATION:
seleccionar perfil, revisar equipación, Mission Log, mapas y comunicaciones.

### 7\. Restricciones

Android únicamente.  
Orientación horizontal.  
Uso personal.  
Sin dependencia permanente de Internet.  
Una única APK para el entorno operativo actual.

### 8\. Filosofía de desarrollo

Diseñar → Documentar → Implementar → Probar → Commit → Actualizar MRPD.  
Nunca implementar una funcionalidad que no exista previamente en el MRPD.  
Toda nueva funcionalidad tendrá prioridad Alta, Media o Baja.

## PARTE II - ARQUITECTURA

Estado: 🟡 En desarrollo  
Implementación: ☐ No iniciada  
<br/>Pendiente de desarrollo detallado en la versión 1.2.

## PARTE III - DISEÑO

Paleta:  
• PipBlack  
• PipGreen (activo)  
• PipGreenDim (disponible)  
• PipAmber (advertencia)  
• PipRed (crítico)  
<br/>Interacción mediante botones.  
Encabezado institucional: BROTHERHOOD OF STEEL.

## PARTE IV - MÓDULOS

Alta:  
HOME  
INVENTORY  
MAP  
COMMS  
DATA  
RADIO  
STATS  
<br/>Media:  
MISSION LOG  
<br/>Baja:  
BIOMETRIC LINK  
Pantalla exterior Z Flip 6  
PIP-SuriOS Launcher  
Integración avanzada con CivTAK

## ECOSISTEMA PIP-SuriOS

Proyecto principal:  
• PIP-SuriOS (Android) → MRPD  
<br/>Proyectos paralelos:  
• PIP-SuriOS Watch Face (Wear OS) → WFRPD  
• PIP-SuriOS Launcher (Android) → Documentación futura  
<br/>Todos compartirán filosofía, identidad visual, guía de diseño y decisiones comunes.

## WATCH FACE (Nuevo proyecto)

Estado: Aprobado  
Prioridad: Alta  
Desarrollo: Paralelo  
Dispositivo objetivo: Xiaomi Watch 2 (Wear OS)  
<br/>Objetivo:  
Desarrollar una esfera propia integrada en el ecosistema PIP-SuriOS mediante documentación independiente (WFRPD).

## DECISIONES APROBADAS

DEC-001 Horizontal  
DEC-002 APK única  
DEC-003 Mission Log sin estadísticas competitivas  
DEC-004 Launcher independiente  
DEC-005 MRPD como fuente de verdad  
DEC-006 Perfil CIVILIAN retirado
DEC-008 Interacción mediante botones  
DEC-009 Lenguaje visual por colores  
DEC-010 Cabecera estándar de módulos  
DEC-011 Separación diseño/implementación

## PRÓXIMAS VERSIONES

v1.2 Arquitectura general  
v1.3 HOME  
v1.4 INVENTORY  
v1.5 MAP  
...
