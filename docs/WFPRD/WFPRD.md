# SuriOS Watch - WFRPD v1.2

Watch Face Requirements & Project Document  
Versión 1.2  
Documento completo para implementación con Orca

## 1\. Propósito

Desarrollar una watch face para Xiaomi Watch 2 (Wear OS) integrada en el ecosistema SuriOS y alineada con el EDL.

## 2\. Alcance

Incluye únicamente Main Watch Face y Ambient Mode. Quedan fuera Tiles, app Wear OS independiente, pantallas secundarias y configuración avanzada.

## 3\. Perfiles

• CIVILIAN: Prioridad Alta. En desarrollo.  
• OPERATION: Pendiente. Se desarrollará en una fase posterior.

## 4\. Esfera principal - Perfil CIVILIAN

Objetivo:  
Mostrar de un vistazo la información esencial.  
<br/>Distribución:  
12 h -> Hora (HH:MM) + Fecha (DD/MM/AAAA)  
3 h -> Batería  
5 h -> Spotify  
7 h -> Google Wallet  
9 h -> Pasos  
<br/>Estética:  
• Fondo negro.  
• Verde terminal según EDL.  
• Tipografía monoespaciada.  
• Símbolo de la Hermandad del Acero difuminado.  
• Simetría visual entre batería y pasos.  
<br/>La imagen conceptual aprobada (Figura 4.1 de la v1.1) continúa siendo la referencia visual oficial.

## 5\. Ambient Mode

Prioridad: Baja para esta fase.  
<br/>Contenido:  
• Hora (HH:MM)  
• Fecha (DD/MM/AAAA)  
<br/>Mantendrá la identidad visual del EDL priorizando el ahorro energético.

## 6\. Integración

Comparte el Ecosystem Design Language con PIP-SuriOS y el resto de proyectos SuriOS.

## 7\. Guía de implementación

Preparación:  
• Android Studio  
• Git  
• Orca  
• Emulador Wear OS  
<br/>Crear proyecto Wear OS Watch Face, compilar sin modificaciones y realizar el primer commit.  
<br/>Sprints:  
1\. Fondo, colores, tipografía, hora y fecha.  
2\. Pasos y batería.  
3\. Spotify y Wallet.  
4\. Ambient Mode.  
5\. Optimización.

## 8\. Criterios de aceptación por sprint

Sprint 1  
☐ Fondo negro  
☐ Colores EDL  
☐ Tipografía correcta  
☐ Hora visible  
☐ Fecha visible  
☐ Compila  
☐ Funciona en emulador  
☐ Commit  
<br/>Sprint 2  
☐ Batería  
☐ Pasos  
☐ Simetría visual  
☐ Datos correctos  
☐ Commit  
<br/>Sprint 3  
☐ Spotify funcional  
☐ Wallet funcional  
☐ Áreas táctiles correctas  
☐ Commit  
<br/>Sprint 4  
☐ Ambient Mode  
☐ Hora  
☐ Fecha  
☐ Bajo consumo  
☐ Commit  
<br/>Sprint 5  
☐ Prueba en Xiaomi Watch 2  
☐ Sin errores de compilación  
☐ Versión estable

## 9\. Definition of Done

La watch face CIVILIAN se considerará finalizada cuando:  
• Hora y fecha funcionen.  
• Batería y pasos funcionen.  
• Spotify y Wallet abran correctamente.  
• Main Watch Face y Ambient Mode funcionen.  
• Respete el EDL.  
• Se pruebe en Xiaomi Watch 2.  
• No existan errores bloqueantes.  
• Exista un commit estable.

## 10\. Riesgos

• Cambios en plantillas de Android Studio.  
• Restricciones de Wear OS para accesos rápidos.  
• Limitaciones de Spotify o Google Wallet.  
• Cambios en APIs de pasos o batería.  
<br/>Si aparece una limitación técnica, Orca deberá documentarla y proponer alternativas antes de modificar el diseño.

## 11\. Backlog

• Perfil OPERATION.  
• Sincronización con PIP-SuriOS.  
• Cambio automático de perfil.  
• Batería del teléfono.  
• Tiles.  
• Complicaciones configurables.  
• Biometría.  
• Sonidos y vibración.

## 12\. Prompt para Orca

Implementa únicamente lo descrito en este WFRPD.  
Consulta siempre el EDL.  
No modifiques el diseño sin aprobación.  
Trabaja sprint a sprint.  
Realiza commits pequeños y descriptivos.