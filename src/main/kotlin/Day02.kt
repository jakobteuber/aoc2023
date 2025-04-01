package com.github.jakobteuber

import kotlin.math.max

private data class CubeSet(val red: Long, val green: Long, val blue: Long) {
    fun isPossibleWith(max: CubeSet) =
        red <= max.red && green <= max.green && blue <= max.blue

    fun power() = red * green * blue
}

private data class Game(val id: Long, val sets: List<CubeSet>) {
    fun isPossibleWith(max: CubeSet) =
        sets.all { it.isPossibleWith(max) }

    fun minimumSet() = sets.reduce { minSet, currentSet ->
        CubeSet(red = max(minSet.red, currentSet.red),
            green = max(minSet.green, currentSet.green),
            blue = max(minSet.blue, currentSet.blue))
    }
}

private fun parse(line: String): Game {
    val (header, lists) = line.split(":")
    val id = line.findLong("""Game (\d+):""")!!
    val cubeSets = lists.split(";")
        .map { part ->
            val red = part.findLong("""(\d+) red""") ?: 0
            val green = part.findLong("""(\d+) green""") ?: 0
            val blue = part.findLong("""(\d+) blue""") ?: 0
            CubeSet(red, green, blue)
        }
    return Game(id, cubeSets)
}

private fun part1(input: String): Long {
    val maxCubes = CubeSet(red = 12, green = 13, blue = 14)
    return input.lineSequence()
        .filter { it.isNotBlank() }
        .map { parse(it) }
        .filter { it.isPossibleWith(maxCubes) }
        .map { it.id }
        .sum()
}

private fun part2(input: String): Long {
    return input.lineSequence()
        .filter { it.isNotBlank() }
        .map { parse(it) }
        .map { it.minimumSet().power() }
        .sum()

}

fun main() {
    val test = """
        Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
        Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
        Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
        Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
    """.trimIndent()
    check(part1(test) == 8.toLong()) { "Part 1" }
    check(part2(test) == 2286.toLong()) { "Part 2" }

    solve("02", ::part1, ::part2)
}