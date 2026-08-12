package com.suri.pipsurios.data

import java.math.BigDecimal

object OperationJsonCodec {
    fun serialize(log: OperationLog): String = buildString {
        appendLine("{")
        appendLine("  \"date\": ${string(log.date)},")
        appendLine("  \"location\": ${string(log.location)},")
        appendLine("  \"loadout\": {")
        appendLine("    \"primaryWeapon\": ${nullableString(log.loadout.primaryWeapon)},")
        appendLine("    \"secondaryWeapon\": ${nullableString(log.loadout.secondaryWeapon)},")
        appendLine("    \"accesories\": [${log.loadout.accesories.joinToString(", ") { string(it) }}],")
        appendLine("    \"headgear\": ${nullableString(log.loadout.headgear)},")
        appendLine("    \"frontPanel\": ${nullableString(log.loadout.frontPanel)},")
        appendLine("    \"uniform\": ${nullableString(log.loadout.uniform)}")
        appendLine("  },")
        appendLine("  \"consumables\": {")
        appendLine("    \"primaryMag\": ${number(log.consumables.primaryMag)},")
        appendLine("    \"secondaryMag\": ${number(log.consumables.secondaryMag)},")
        appendLine("    \"grenades40mm\": ${number(log.consumables.grenades40mm)},")
        appendLine("    \"grenades9mm\": ${number(log.consumables.grenades9mm)},")
        appendLine("    \"grenadesCo2\": ${number(log.consumables.grenadesCo2)},")
        appendLine("    \"primaryHpa\": ${number(log.consumables.primaryHpa)},")
        appendLine("    \"secondaryHpa\": ${number(log.consumables.secondaryHpa)}")
        appendLine("  }")
        append("}")
    }

    fun deserialize(json: String): OperationLog {
        val root = JsonParser(json).parseObject()
        val loadout = root.objectValue("loadout")
        val consumables = root.objectValue("consumables")
        return OperationLog(
            date = root.stringValue("date"),
            location = root.stringValue("location"),
            loadout = OperationLoadoutSnapshot(
                primaryWeapon = loadout.nullableStringValue("primaryWeapon"),
                secondaryWeapon = loadout.nullableStringValue("secondaryWeapon"),
                accesories = loadout.arrayValue("accesories").map { it as String },
                headgear = loadout.nullableStringValue("headgear"),
                frontPanel = loadout.nullableStringValue("frontPanel"),
                uniform = loadout.nullableStringValue("uniform")
            ),
            consumables = OperationConsumables(
                primaryMag = consumables.numberValue("primaryMag"),
                secondaryMag = consumables.numberValue("secondaryMag"),
                grenades40mm = consumables.numberValue("grenades40mm"),
                grenades9mm = consumables.numberValue("grenades9mm"),
                grenadesCo2 = consumables.numberValue("grenadesCo2"),
                primaryHpa = consumables.numberValue("primaryHpa"),
                secondaryHpa = consumables.numberValue("secondaryHpa")
            )
        )
    }

    private fun string(value: String): String = buildString {
        append('"')
        value.forEach { character ->
            when (character) {
                '"' -> append("\\\"")
                '\\' -> append("\\\\")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> if (character.code < 0x20) {
                    append("\\u%04x".format(character.code))
                } else {
                    append(character)
                }
            }
        }
        append('"')
    }

    private fun nullableString(value: String?): String = value?.let(::string) ?: "null"
    private fun number(value: BigDecimal): String = value.toPlainString()
}

private class JsonParser(private val source: String) {
    private var position = 0

    fun parseObject(): Map<String, Any?> {
        skipWhitespace()
        expect('{')
        val result = linkedMapOf<String, Any?>()
        skipWhitespace()
        if (consume('}')) return result
        while (true) {
            val key = parseString()
            skipWhitespace()
            expect(':')
            result[key] = parseValue()
            skipWhitespace()
            if (consume('}')) break
            expect(',')
        }
        skipWhitespace()
        if (position != source.length) error("Unexpected trailing JSON content")
        return result
    }

    private fun parseValue(): Any? {
        skipWhitespace()
        return when (source.getOrNull(position)) {
            '"' -> parseString()
            '{' -> parseNestedObject()
            '[' -> parseArray()
            'n' -> { expectLiteral("null"); null }
            else -> parseNumber()
        }
    }

    private fun parseNestedObject(): Map<String, Any?> {
        expect('{')
        val result = linkedMapOf<String, Any?>()
        skipWhitespace()
        if (consume('}')) return result
        while (true) {
            val key = parseString()
            skipWhitespace()
            expect(':')
            result[key] = parseValue()
            skipWhitespace()
            if (consume('}')) return result
            expect(',')
        }
    }

    private fun parseArray(): List<Any?> {
        expect('[')
        val result = mutableListOf<Any?>()
        skipWhitespace()
        if (consume(']')) return result
        while (true) {
            result += parseValue()
            skipWhitespace()
            if (consume(']')) return result
            expect(',')
        }
    }

    private fun parseString(): String {
        skipWhitespace()
        expect('"')
        return buildString {
            while (true) {
                val character = source.getOrNull(position++) ?: error("Unterminated JSON string")
                when (character) {
                    '"' -> return@buildString
                    '\\' -> append(parseEscape())
                    else -> append(character)
                }
            }
        }
    }

    private fun parseEscape(): Char = when (val escaped = source.getOrNull(position++)) {
        '"', '\\', '/' -> escaped
        'b' -> '\b'
        'f' -> '\u000C'
        'n' -> '\n'
        'r' -> '\r'
        't' -> '\t'
        'u' -> source.substring(position, position + 4).toInt(16).toChar().also { position += 4 }
        else -> error("Invalid JSON escape")
    }

    private fun parseNumber(): BigDecimal {
        val start = position
        while (source.getOrNull(position)?.let { it.isDigit() || it in "+-.eE" } == true) position++
        if (start == position) error("Expected JSON value at $position")
        return source.substring(start, position).toBigDecimal()
    }

    private fun expectLiteral(value: String) {
        if (!source.startsWith(value, position)) error("Expected $value at $position")
        position += value.length
    }

    private fun expect(character: Char) {
        skipWhitespace()
        if (source.getOrNull(position) != character) error("Expected $character at $position")
        position++
    }

    private fun consume(character: Char): Boolean {
        skipWhitespace()
        if (source.getOrNull(position) != character) return false
        position++
        return true
    }

    private fun skipWhitespace() {
        while (source.getOrNull(position)?.isWhitespace() == true) position++
    }
}

private fun Map<String, Any?>.stringValue(key: String): String =
    this[key] as? String ?: error("Missing string: $key")

private fun Map<String, Any?>.nullableStringValue(key: String): String? = when (val value = this[key]) {
    null -> null
    is String -> value
    else -> error("Invalid string: $key")
}

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.objectValue(key: String): Map<String, Any?> =
    this[key] as? Map<String, Any?> ?: error("Missing object: $key")

private fun Map<String, Any?>.arrayValue(key: String): List<Any?> =
    this[key] as? List<Any?> ?: error("Missing array: $key")

private fun Map<String, Any?>.numberValue(key: String): BigDecimal =
    this[key] as? BigDecimal ?: error("Missing number: $key")
