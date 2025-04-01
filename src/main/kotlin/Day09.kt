package com.github.jakobteuber

private fun parse(input: String) =
    input.lineSequence()
        .filterNot { it.isBlank() }
        .map { line ->
            line.split("\\s+".toRegex())
                .map { it.toLong() }
        }

private fun differences(numbers: List<Long>) = numbers.zipWithNext { a, b -> b - a }

private fun expandUntilAllZero(initial: List<Long>) =
    generateSequence(initial) { prev ->
        if(prev.all { it == 0.toLong() }) {
            null
        } else {
            differences(prev)
        }
    }.toList()

private fun predictNext(numbers: List<Long>) =
    expandUntilAllZero(numbers).sumOf { it.last() }


private fun part1(input: String): Long {
    val lines = parse(input)
    return lines.sumOf { predictNext(it) }
}

private fun part2(input: String): Long {
    val lines = parse(input)
    return lines.sumOf { predictNext(it.asReversed()) }
}

fun main() {
    val test = """
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """.trimIndent()

    checkEq(part1(test), 114.toLong(), "Part 1")

    solve("09", ::part1, ::part2)
}