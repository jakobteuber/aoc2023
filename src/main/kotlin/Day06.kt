package com.github.jakobteuber

import kotlin.math.max

private data class Race(val timeAllowed: Long, val recordDistance: Long) {
    fun predictDistance(timeCharging: Long): Long {
        val speed = timeCharging
        val timeLeft = max(0, timeAllowed - timeCharging)
        return timeLeft * speed
    }
}

private fun findMarginOfError(race: Race)  = (0..race.timeAllowed)
    .count { timeCharging ->
        race.predictDistance(timeCharging) > race.recordDistance
    }


private fun findMarginOfError(races: List<Race>) =
    races.map { findMarginOfError(it).toLong() }
        .fold(1.toLong()) { acc, it -> acc * it }

private fun part1(ignore: String): Long {
    // Time:        55     82     64     90
    // Distance:   246   1441   1012   1111
    val input = listOf(
        Race(55, 246),
        Race(82, 1441),
        Race(64, 1012),
        Race(90, 1111),
    )
    return findMarginOfError(input)
}

private fun part2(ignore: String): Long {
    // Time:        55     82     64     90
    // Distance:   246   1441   1012   1111
    val input = listOf(
        Race(55826490, 246144110121111),
    )
    return findMarginOfError(input)
}

fun main() {
    val test = listOf(
        Race(7, 9),
        Race(15, 40),
        Race(30, 200),
    )
    checkEq(findMarginOfError(test), 288.toLong(), "Part 1")

    val test2 = listOf(Race(71530, 940200))
    checkEq(findMarginOfError(test2), 71503.toLong(), "Part 2")

    solve("06", ::part1, ::part2)
}