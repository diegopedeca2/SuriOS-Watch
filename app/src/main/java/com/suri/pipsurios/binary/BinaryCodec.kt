package com.suri.pipsurios.binary

/** Converts the same simple A-Z/0-9 text range used by the MORSE terminal. */
object BinaryCodec {
    fun encode(text: String): String = text
        .trim()
        .uppercase()
        .mapNotNull { character -> character.takeIf { it in ' '..'~' } }
        .joinToString(" ") { character ->
            character.code.toString(radix = 2).padStart(8, '0')
        }

    fun decode(binary: String): String {
        val normalized = binary.trim()
        if (normalized.isEmpty()) return ""

        val bytes = if (normalized.any(Char::isWhitespace)) {
            normalized.split(Regex("\\s+"))
        } else {
            normalized.chunked(8)
        }

        return bytes.joinToString("") { byte ->
            if (byte.length != 8 || byte.any { it != '0' && it != '1' }) {
                "?"
            } else {
                byte.toInt(radix = 2).toChar()
                    .takeIf { it in ' '..'~' }
                    ?.toString()
                    ?: "?"
            }
        }
    }
}
