package com.github.jakobteuber

import kotlin.math.max

private data class Galaxy(val row: Long, val col: Long)

private fun distance(a: Long, b: Long) =
    if (a < b) { b - a } else { a - b }

private fun manhattanDistance(a: Galaxy, b: Galaxy) =
    distance(a.row, b.row) + distance(a.col, b.col)

private fun parse(input: String) =
    input.lines().withIndex()
        .flatMap { (lineNo, line) ->
            line.withIndex()
                .filter { it.value == '#' }
                .map { (col, _) -> Galaxy(row = lineNo.toLong(), col = col.toLong()) }
        }

private inline fun List<Galaxy>.expand(
    expansionFactor: Long,
    crossinline getCoordinate: Galaxy.() -> Long,
    setCoordinate: Galaxy.(Long) -> Galaxy): List<Galaxy> {
    val withStart = listOf(Galaxy(0, 0)) + this
    var offset = 0.toLong()
    return withStart.sortedBy { it.getCoordinate() }
        .zipWithNext()
        .map { (last, current) ->
            val distance = distance(last.getCoordinate(), current.getCoordinate())
            val emptySpace = max(distance - 1, 0)
            offset += emptySpace * (expansionFactor - 1)
            current.setCoordinate(current.getCoordinate() + offset)
        }
}

private fun List<Galaxy>.expand(expansionFactor: Long) =
    this.expand(expansionFactor, getCoordinate = { this.row }, setCoordinate = { copy(row = it) })
        .expand(expansionFactor, getCoordinate = { this.col }, setCoordinate = { copy(col = it) })

private fun part1(input: String): Long {
    val galaxies = parse(input)
    val expanded = galaxies.expand(expansionFactor = 2)
    val pairs = uniquePairs(expanded)
    return pairs.sumOf { (a, b) -> manhattanDistance(a, b) }
}

private fun part2(input: String): Long {
    val galaxies = parse(input)
    val expanded = galaxies.expand(expansionFactor = 1000000)
    val pairs = uniquePairs(expanded)
    return pairs.sumOf { (a, b) -> manhattanDistance(a, b) }
}

fun main() {

    val test = """
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
    """.trimIndent()
    checkEq(part1(test), 374.toLong(), "Part 1")

    solve("11", ::part1, ::part2)
}