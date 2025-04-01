package com.github.jakobteuber

private data class Number(val value: Long, val range: IntRange, val row: Int) {
    fun adjacentRange() = (range.first - 1) .. (range.last + 1)
}

private data class Symbol(val symbol: Char, val index: Int, val row: Int)

private data class Line(val numbers: List<Number>, val symbols: List<Symbol>)

private fun parse(line: String, lineNumber: Int): Line {
    val symbols = line
        .mapIndexed { i, c -> i to c }
        .filter { (_, c) -> c != '.' && !c.isDigit() }
        .map { (i, c) -> Symbol(symbol = c, index = i, row = lineNumber) }
    val numbers = """(\d++)""".toRegex()
        .findAll(line)
        .map { match ->
            Number(
                value = match.value.toLong(),
                range = match.range,
                row = lineNumber
            )
        }.toList()
    return Line(numbers, symbols)
}

private fun part1(input: String) =
    input.lineSequence()
        .filter { it.isNotBlank() }
        .mapIndexed { i, line -> parse(line, i) }
        .zipWithNext { prev, next ->
            val symbols = prev.symbols + next.symbols
            val numbers = prev.numbers + next.numbers
            numbers.filter { num ->
                symbols.any { it.index in num.adjacentRange() }
            }
        }.flatMap { it }
        .distinct()
        .sumOf { it.value }


private fun part2(input: String) =
    input.lineSequence()
        .filter { it.isNotBlank() }
        .mapIndexed { i, line -> parse(line, i) }
        .zipWithNext { prev, next ->
            val gears = (prev.symbols + next.symbols).filter { it.symbol == '*' }
            val numbers = prev.numbers + next.numbers
            gears to numbers
        }.flatMap { (gears, numbers) ->
            gears.flatMap { gear ->
                numbers.filter { gear.index in it.adjacentRange() }
                    .map { gear to it }
            }
        }.distinct()
        .groupBy { (gear, _) -> gear }
        .map { (_, list) -> list.map { (_, number) -> number } }
        .filter { it.size == 2 }
        .sumOf { (a, b) -> a.value * b.value }

fun main() {
    val test = """
        467..114..
        ...*......
        ..35..633.
        ......#...
        617*......
        .....+.58.
        ..592.....
        ......755.
        ...${'$'}.*....
        .664.598..
    """.trimIndent()
    checkEq(part1(test), 4361.toLong(), "Part 1")
    checkEq(part2(test), 467835.toLong(), "Part 2")

    solve("03", ::part1, ::part2)
}