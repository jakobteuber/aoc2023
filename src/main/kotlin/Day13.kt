package com.github.jakobteuber

data class Pattern(val str: String) {
    val rows = str.trim().lines()
    val cols = rows.first().indices.map { i ->
        rows.map { it[i] }.joinToString("") }
}

fun isMirror(line: String, afterCol: Int): Boolean {
    for (offset in 0..line.length) {
        val a = line.getOrNull(afterCol - offset - 1)
        val b = line.getOrNull(afterCol + offset)
        if (a == null) continue
        if (b == null) continue
        if (a != b) return false
    }
    return true
}

fun Pattern.findRowMirrors(): List<Int> {
    val colAxes = (1..<cols.size).toList()
    return rows.fold(colAxes) { axes, row ->
        axes.filter { isMirror(row, it) }
    }
}

fun Pattern.findColMirrors(): List<Int> {
    val rowAxes = (1..<rows.size).toList()
    return cols.fold(rowAxes) { axes, col ->
        axes.filter { isMirror(col, it) }
    }
}

private fun parse(input: String) =
    input.split("\n\n")
        .filterNot { it.isBlank() }
        .map { Pattern(it) }

private fun part1(input: String): Long {
    val patterns = parse(input)
    return patterns.sumOf { pattern ->
        val colMirrors = pattern.findColMirrors().sum().toLong()
        val rowMirrors = pattern.findRowMirrors().sum().toLong()
        100 * colMirrors + rowMirrors
    }
}

private fun Pattern.changeExactlyOne() =
    str.indices.asSequence()
        .filter { i -> str[i] in ".#" }
        .map { i ->
            val s = str.withSet(index = i,
                when(str[i]) {
                    '.' -> '#'
                    '#' -> '.'
                    else -> error("bad char: `${str[i]}`")
                })
            Pattern(s)
        }

private fun part2(input: String): Long {
    val patterns = parse(input)
    return patterns.sumOf { pattern ->
        val oldColMirrors = pattern.findColMirrors()
        val oldRowMirrors = pattern.findRowMirrors()
        pattern.changeExactlyOne()
            .map { p ->
                val colMirrors = p.findColMirrors()
                    .filter { it !in oldColMirrors }
                    .sum().toLong()
                val rowMirrors = p.findRowMirrors()
                    .filter { it !in oldRowMirrors }
                    .sum().toLong()
                100 * colMirrors + rowMirrors
            }.dropWhile { it == 0.toLong() }
            .first()
    }
}

fun main() {
    val test = """
        #.##..##.
        ..#.##.#.
        ##......#
        ##......#
        ..#.##.#.
        ..##..##.
        #.#.##.#.

        #...##..#
        #....#..#
        ..##..###
        #####.##.
        #####.##.
        ..##..###
        #....#..#
    """.trimIndent()

    checkEq(part1(test), 405.toLong(), "Part 1")
    checkEq(part2(test), 400.toLong(), "Part 2")


    solve("13", ::part1, ::part2)
}