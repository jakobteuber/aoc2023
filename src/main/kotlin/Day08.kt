package com.github.jakobteuber

import kotlin.math.max
import kotlin.math.min

private data class Navigation(val directions: List<Char>, val nodes: List<MapNode>) {
    val lookup = nodes.associateBy { it.name }
    val first get() = lookup["AAA"]!!
    val last get() = lookup["ZZZ"]!!
}
private data class MapNode(val name: String, val left: String, val right: String)

private fun parse(input: String): Navigation {
    val (directionText, nodesText) = input.split("\n\n")
    val directions = directionText.trim().toList()
        .filter { it in listOf('L', 'R') }
    val nodes = nodesText.lineSequence().filterNot { it.isBlank() }
        .map { line ->
            MapNode(
                name = line.findString("""([A-Z0-9]{3}) = \([A-Z0-9]{3}, [A-Z0-9]{3}\)""")!!,
                left = line.findString("""[A-Z0-9]{3} = \(([A-Z0-9]{3}), [A-Z0-9]{3}\)""")!!,
                right = line.findString("""[A-Z0-9]{3} = \([A-Z0-9]{3}, ([A-Z0-9]{3})\)""")!!,
            )
        }.toList()
    return Navigation(directions, nodes)
}

private fun part1(input: String): Long {
    val navigation = parse(input)
    var steps = 0
    var current = navigation.first
    while (current != navigation.last) {
        val index = steps % navigation.directions.size
        val direction = navigation.directions[index]
        when(direction) {
            'L' -> current = navigation.lookup[current.left]!!
            'R' -> current = navigation.lookup[current.right]!!
        }
        steps += 1
    }
    return steps.toLong()
}

private data class CycleCount(val start: Int, val length: Int)

private fun findCycle(navigation: Navigation, start: MapNode): CycleCount {
    var steps = 0
    var current = start
    val seenEnds = mutableMapOf<String, Int>()
    while (current.name !in seenEnds) {
        if (current.name.endsWith("Z")) {
            seenEnds.put(current.name, steps)
        }
        val index = steps % navigation.directions.size
        val direction = navigation.directions[index]
        when(direction) {
            'L' -> current = navigation.lookup[current.left]!!
            'R' -> current = navigation.lookup[current.right]!!
        }
        steps += 1
    }
    val cycleStart = seenEnds[current.name]!!
    return CycleCount(cycleStart, steps - cycleStart)
}

private fun leastCommonMultiple(a: Long, b: Long): Long {
    val greatest = max(a, b)
    val smallest = min(a, b)
    var i = greatest
    while (true) {
        if (i % smallest == 0.toLong()) return i
        i += greatest
    }
}

private fun leastCommonMultiple(numbers: List<Long>): Long {
    var result = 1.toLong()
    for (n in numbers) result = leastCommonMultiple(result, n)
    return result
}

private fun part2(input: String): Long {
    val navigation = parse(input)
    var steps = 0
    val starts = navigation.nodes.filter { it.name.endsWith("A") }

    // This seems wrong. Couldn’t there be a start offset before we hit the cycle?
    // But it works for the AoC problem
    val cycles = starts.map { findCycle(navigation, it) }
    cycles.onEach { checkEq(it.start, it.length, "cycle has no different offset") }
    return leastCommonMultiple(cycles.map { it.length.toLong() })
}

fun main() {
    val test = """
        RL

        AAA = (BBB, CCC)
        BBB = (DDD, EEE)
        CCC = (ZZZ, GGG)
        DDD = (DDD, DDD)
        EEE = (EEE, EEE)
        GGG = (GGG, GGG)
        ZZZ = (ZZZ, ZZZ)
    """.trimIndent()
    val test2 = """
        LLR

        AAA = (BBB, BBB)
        BBB = (AAA, ZZZ)
        ZZZ = (ZZZ, ZZZ)
    """.trimIndent()

    checkEq(part1(test), 2.toLong(), "Part 1, 1")
    checkEq(part1(test2), 6.toLong(), "Part 1, 2")

    val test3 = """
        LR

        11A = (11B, XXX)
        11B = (XXX, 11Z)
        11Z = (11B, XXX)
        22A = (22B, XXX)
        22B = (22C, 22C)
        22C = (22Z, 22Z)
        22Z = (22B, 22B)
        XXX = (XXX, XXX)
    """.trimIndent()
    checkEq(part2(test3), 6.toLong(), "Part 2")

    solve("08", ::part1, ::part2)

}