package com.github.jakobteuber

private fun part1(input: String) =
    input.lineSequence()
        .filter { it.isNotBlank() }
        .map { line ->
            val firstNum = line.first { it.isDigit() }.digitToInt().toLong()
            val lastNum = line.last { it.isDigit() }.digitToInt().toLong()
            firstNum * 10 + lastNum
        }.sum()

private val numberNames: Map<String, Long> = mapOf(
    "zero" to 0,
    "1" to 1,
    "one" to 1,
    "2" to 2,
    "two" to 2,
    "3" to 3,
    "three" to 3,
    "4" to 4,
    "four" to 4,
    "5" to 5,
    "five" to 5,
    "6" to 6,
    "six" to 6,
    "7" to 7,
    "seven" to 7,
    "8" to 8,
    "eight" to 8,
    "9" to 9,
    "nine" to 9,
)

private fun part2(input: String) =
    input.lineSequence()
        .filter { it.isNotBlank() }
        .map { line ->
            val first = numberNames[line.findAnyOf(numberNames.keys)!!.second]!!
            val second = numberNames[line.findLastAnyOf(numberNames.keys)!!.second]!!
            first * 10 + second
        }.sum()
fun main() {

    val test = """1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet"""

    val input = readInput("01")

    check(part1(test) == 142.toLong()) { "Part1" }

    val test2 = """two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen"""

    check(part2(test2) == 281.toLong()) { "Part2" }

    solve("01", ::part1, ::part2)
}