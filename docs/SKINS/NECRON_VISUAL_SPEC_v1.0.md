# NECRON — Especificación visual v1.0

Estado: Implementada y validada — Sprint 018
Aplicación: PIP-SuriOS Android
Ámbito: Toda la aplicación, incluyendo P.R.S. mientras conserve legibilidad

Validación: Samsung A56 (`SM_A566B`), Home y P.R.S. sin distorsión apreciable

## Dirección

NECRON utiliza una lectura de terminal táctica inspirada en la estética de los Necrones y la dinastía Szarekhan: blackstone, metal envejecido, energía teal y acentos de bronce. La función siempre prevalece sobre la decoración.

Las referencias de dirección son [Ancient Dynasties of the Necrons](https://www.warhammer-community.com/en-gb/articles/5Sz5J6wE/), [Designing and Painting the Silent King](https://www.warhammer-community.com/en-gb/articles/wE77ZMK0/designing-the-silent-king/) y [Necrons](https://warhammer40000.com/games/faction/necrons/). Se utilizan como inspiración; no se incorporan assets externos.

## Tokens

| Token | HEX | Uso |
|---|---|---|
| Fondo | `#000000` | Fondo AMOLED |
| Primario | `#9DFFE9` | Texto activo, selección y controles |
| Primario tenue | `#48BFAF` | Información secundaria y bordes |
| Realce | `#D4FFF6` | Destacados puntuales |
| Bronce | `#E7B86A` | Advertencias y acento dinástico |
| Crítico | `#FF5F62` | Errores y estados críticos |
| P.R.S. | `#63D8F2` | Nodo remoto y enlace |
| Neutral | `#DCEBE7` | Datos de alto contraste |
| Neutral tenue | `#A8BCB7` | Información contextual |
| Panel | `#071211` | Superficies técnicas sutiles |

No se utilizan degradados ni sombras. Las texturas y scanlines quedan limitadas a tratamientos sutiles donde no compitan con la información.

## Componentes

La geometría, navegación, tipografía monoespaciada, jerarquía, estados y acciones se heredan de Brotherhood of Steel. NECRON solo cambia tokens visuales, emblema y tratamiento de superficies.

## P.R.S. / REMOTE PROBE

Se aplica la paleta NECRON a P.R.S., incluidos paneles, textos, mapa de densidad y estados de enlace. El emblema de fondo se sustituye por el emblema vectorial NECRON. Si la prueba física muestra pérdida de lectura, se revertirá únicamente el tratamiento visual de P.R.S. a Brotherhood sin modificar su funcionamiento.

## Alcance de publicación

Este documento y la implementación son de uso privado conforme a [PROJECT_SCOPE_POLICY_v1.0](../PROJECT_GUIDE/PROJECT_SCOPE_POLICY_v1.0.md). Una eventual publicación requerirá una variante original sin nombres, símbolos ni assets de propiedad intelectual de terceros.
