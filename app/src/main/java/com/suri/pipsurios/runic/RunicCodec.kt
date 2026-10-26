package com.suri.pipsurios.runic

import java.text.Normalizer
import java.util.Locale

/** Transliteration for the 24-rune Elder Futhark alphabet. */
object RunicCodec {
    enum class Direction {
        LATIN_TO_RUNES,
        RUNES_TO_LATIN
    }

    private val latinToRune = mapOf(
        'a' to "ᚨ",
        'b' to "ᛒ",
        'c' to "ᚲ",
        'd' to "ᛞ",
        'e' to "ᛖ",
        'f' to "ᚠ",
        'g' to "ᚷ",
        'h' to "ᚺ",
        'i' to "ᛁ",
        'j' to "ᛃ",
        'k' to "ᚲ",
        'l' to "ᛚ",
        'm' to "ᛗ",
        'n' to "ᚾ",
        'o' to "ᛟ",
        'p' to "ᛈ",
        'q' to "ᚲ",
        'r' to "ᚱ",
        's' to "ᛊ",
        't' to "ᛏ",
        'u' to "ᚢ",
        'v' to "ᚹ",
        'w' to "ᚹ",
        'x' to "ᚲᛊ",
        'y' to "ᛃ",
        'z' to "ᛉ",
        'þ' to "ᚦ"
    )

    private val runeToLatin = mapOf(
        'ᚠ' to "F",
        'ᚢ' to "U",
        'ᚦ' to "TH",
        'ᚨ' to "A",
        'ᚱ' to "R",
        'ᚲ' to "K",
        'ᚷ' to "G",
        'ᚹ' to "W",
        'ᚺ' to "H",
        'ᚾ' to "N",
        'ᛁ' to "I",
        'ᛃ' to "J",
        'ᛇ' to "EI",
        'ᛈ' to "P",
        'ᛉ' to "Z",
        'ᛊ' to "S",
        'ᛏ' to "T",
        'ᛒ' to "B",
        'ᛖ' to "E",
        'ᛗ' to "M",
        'ᛚ' to "L",
        'ᛜ' to "NG",
        'ᛞ' to "D",
        'ᛟ' to "O"
    )

    fun translate(text: String, direction: Direction): String = when (direction) {
        Direction.LATIN_TO_RUNES -> toRunes(text)
        Direction.RUNES_TO_LATIN -> toLatin(text)
    }

    fun toRunes(text: String): String {
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .lowercase(Locale.ROOT)
        val result = StringBuilder()
        var index = 0
        while (index < normalized.length) {
            val character = normalized[index]
            if (character == 't' && normalized.getOrNull(index + 1) == 'h') {
                result.append("ᚦ")
                index += 2
            } else {
                result.append(latinToRune[character] ?: character)
                index++
            }
        }
        return result.toString()
    }

    fun toLatin(text: String): String = buildString {
        text.forEach { character ->
            append(runeToLatin[character] ?: character)
        }
    }
}
