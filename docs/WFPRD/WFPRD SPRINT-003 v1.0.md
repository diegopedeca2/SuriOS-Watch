# WFPRD – Diseño completo de la Watch Face v1.0

---

## Objetivo

Definir la distribución visual definitiva de SuriOS Watch.

Este documento establece la posición, jerarquía visual y comportamiento esperado de todos los componentes que formarán parte de la versión 1.0 de la Watch Face.

No implica su implementación inmediata.

Cada componente será desarrollado en el Sprint correspondiente.

---

# Distribución general

La Watch Face mantiene un diseño limpio, simétrico y fácilmente legible.

Toda la información debe poder consultarse de un vistazo.

La prioridad visual será:

1. Hora
2. Fecha
3. Batería
4. Pasos
5. Accesos rápidos

---

# Componentes

## Hora

Estado:

Implementada.

Elemento principal de la Watch Face.

Mantendrá siempre la máxima prioridad visual.

No se modificará su posición salvo aprobación expresa.

---

## Fecha

Estado:

Implementada.

Debe permanecer inmediatamente bajo la hora.

Mantendrá menor peso visual.

---

## Indicador de batería

Estado:

Pendiente de implementación.

Información mostrada:

- porcentaje
- icono de batería

Pendiente de definir:

- posición exacta
- dimensiones
- estados gráficos
- representación durante la carga
- comportamiento con batería baja

Será desarrollado en Sprint 004.

---

## Indicador de pasos

Estado:

Pendiente de implementación.

Información mostrada:

- número de pasos
- icono identificativo

Pendiente de definir:

- posición
- formato
- frecuencia de actualización
- comportamiento cuando el dato no esté disponible

Será desarrollado en Sprint 004.

---

## Spotify

Estado:

Pendiente de implementación.

Mostrará un acceso rápido a Spotify.

No mostrará información de reproducción.

No actuará como reproductor.

Será desarrollado en Sprint 005.

---

## Google Wallet

Estado:

Pendiente de implementación.

Mostrará un acceso rápido a Google Wallet.

No mostrará tarjetas ni información adicional.

Será desarrollado en Sprint 005.

---

# Ambient Mode

Estado:

Pendiente de implementación.

Se desarrollará íntegramente según ADR-002 y el apartado correspondiente del WFPRD.

No modifica la distribución de la esfera.

Únicamente modifica el comportamiento del estado ambiente.

Será desarrollado en Sprint 003.

---

# Distribución visual

La organización general será:

Hora

↓

Fecha

↓

Zona de información secundaria

- batería
- pasos

↓

Zona inferior

- Spotify
- Google Wallet

Todos los componentes deberán respetar la cuadrícula definida por el EDL.

---

# Criterios visuales

Todos los componentes deberán:

- mantener alineación horizontal;
- respetar los márgenes definidos;
- utilizar la paleta oficial;
- utilizar la tipografía aprobada;
- mantener coherencia con el resto de SuriOS.

---

# Restricciones

No introducir elementos decorativos.

No añadir texto innecesario.

No utilizar colores fuera del EDL.

No alterar la jerarquía visual.

No modificar componentes ya aprobados sin autorización expresa.

---

# Dependencias

EDL

ADR-002

ADR-003

WFPRD

Cada Sprint implementará únicamente los componentes autorizados para ese Sprint.

---

# Observaciones

Este documento constituye la referencia visual de la Watch Face completa.

La implementación se realizará de forma incremental.

La existencia de un componente en este documento no implica que deba desarrollarse antes del Sprint autorizado correspondiente.