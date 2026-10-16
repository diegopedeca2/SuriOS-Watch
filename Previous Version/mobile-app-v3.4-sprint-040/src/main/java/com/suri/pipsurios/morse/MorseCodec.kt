package com.suri.pipsurios.morse

object MorseCodec {
    private val symbols = linkedMapOf(
        'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".",
        'F' to "..-.", 'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---",
        'K' to "-.-", 'L' to ".-..", 'M' to "--", 'N' to "-.", 'O' to "---",
        'P' to ".--.", 'Q' to "--.-", 'R' to ".-.", 'S' to "...", 'T' to "-",
        'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-", 'Y' to "-.--",
        'Z' to "--..", '0' to "-----", '1' to ".----", '2' to "..---",
        '3' to "...--", '4' to "....-", '5' to ".....", '6' to "-....",
        '7' to "--...", '8' to "---..", '9' to "----."
    )
    private val decodedSymbols = symbols.entries.associate { (character, morse) -> morse to character }

    fun encode(text: String): String = text
        .trim()
        .uppercase()
        .split(Regex("\\s+"))
        .filter(String::isNotEmpty)
        .joinToString("__") { word ->
            word.mapNotNull(symbols::get).joinToString("_")
        }

    fun decode(morse: String): String = morse
        .trim('_', ' ')
        .split("__")
        .filter(String::isNotEmpty)
        .joinToString(" ") { word ->
            word.split('_')
                .filter(String::isNotEmpty)
                .joinToString("") { symbol -> decodedSymbols[symbol.trim()]?.toString() ?: "?" }
        }
}
