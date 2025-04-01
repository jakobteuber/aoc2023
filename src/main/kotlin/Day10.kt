package com.github.jakobteuber

private val nicerChars = mapOf(
    '|' to '┃',
    '-' to '━',
    'L' to '┗',
    'J' to '┛',
    '7' to '┓',
    'F' to '┏',
    '.' to ' ',
    'S' to 'S',
    '\n' to '\n',
)

private fun String.prettify() =
    this.map { nicerChars[it]!! }.joinToString(separator = "")

private data class Coordinate(val x: Int, val y: Int) {
    fun up() = Coordinate(x, y - 1)
    fun down() = Coordinate(x, y + 1)
    fun left() = Coordinate(x - 1, y)
    fun right() = Coordinate(x + 1, y)
}
private fun List<List<Char>>.at(c: Coordinate) =
    if (c.y in this.indices && c.x in this.first().indices) {
        this[c.y][c.x]
    } else {
        '.'
    }


private fun next(grid: List<List<Char>>, current: Coordinate, previous: Coordinate) =
    when (grid.at(current)) {
        '|','┃' -> listOf(current.up(), current.down())
        '-','━' -> listOf(current.left(), current.right())
        'L','┗' -> listOf(current.up(), current.right())
        'J','┛' -> listOf(current.up(), current.left())
        '7','┓' -> listOf(current.left(), current.down())
        'F','┏' -> listOf(current.right(), current.down())
        '.' -> error("fell out of loop ${grid.at(current)} at $current")
        'S' -> listOf()
        else -> error("bad char ${grid.at(current)} at $current")
    }.firstOrNull { it != previous }

private fun firstStep(grid: List<List<Char>>, current: Coordinate) =
    when {
        grid.at(current.up()) in "|7F" -> current.up()
        grid.at(current.right()) in "-J7" -> current.right()
        grid.at(current.down()) in "|LJ" -> current.down()
        grid.at(current.left()) in "-LF" -> current.left()
        else -> error("cannot find start")
    }

private fun part1(input: String): Long {
    val grid = input.lines().map { it.toList() }
    val yStart = grid.withIndex().find { (_, line) -> 'S' in line }!!.index
    val xStart = grid.find { line -> 'S' in line }!!
        .withIndex().find {(_, c) -> c == 'S' }!!.index
    val start = Coordinate(xStart, yStart)

    val steps = generateSequence(
        start to firstStep(grid, start)
    ) { (prev, curr) ->
        val next = next(grid, curr, prev)
        if (next == null) null else curr to next
    }.withIndex().toList()

    return (steps.size / 2).toLong()
}

private fun isolateLoop(grid: List<MutableList<Char>>, start: Coordinate): List<MutableList<Char>> {
    var prev = start
    var curr = firstStep(grid, start)
    while (true) {
        grid[curr.y][curr.x] = nicerChars[grid.at(curr)]!!
        val next = next(grid, curr, prev) ?: break
        prev = curr
        curr = next
    }
    return grid
}

private fun connectors(grid: List<List<Char>>, start: Coordinate) = buildList {
    if (grid.at(start.up()) in "|┃7┓F┏") add(start.up())
    if (grid.at(start.left()) in "-━L┗F┏") add(start.left())
    if (grid.at(start.right()) in "-━J┛7┓") add(start.right())
    if (grid.at(start.down()) in "|┃J┛L┗") add(start.down())
}

private fun findStart(grid: List<List<Char>>, start: Coordinate) =
    when(connectors(grid, start).toSet()) {
        setOf(start.up(), start.down()) -> '┃'
        setOf(start.up(), start.left()) -> '┛'
        setOf(start.up(), start.right()) -> '┗'
        setOf(start.left(), start.right()) -> '━'
        setOf(start.left(), start.down()) -> '┓'
        setOf(start.right(), start.down()) -> '┏'
        else -> error("Bad connector: ${connectors(grid, start)}")
    }

private fun part2(input: String): Long {
    val grid = input.lines().map { it.toMutableList() }
    val yStart = grid.withIndex().find { (_, line) -> 'S' in line }!!.index
    val xStart = grid.find { line -> 'S' in line }!!
        .withIndex().find {(_, c) -> c == 'S' }!!.index
    val start = Coordinate(xStart, yStart)
    val withLoop = isolateLoop(grid, start)
    withLoop[yStart][xStart] = findStart(withLoop, start)

    var innerSquares = 0.toLong()
    for (line in withLoop) {
        /*
         * Idea: shoot a beam towards the edge count crossings, if the number is odd,
         * the point is inside the loop.
         * Note: the point, from which the beam originates, can be chosen arbitrarily
         * within the square. Here we use a lower point so that ...┓... is an intersection
         * but ...┛... is not.
         */
        var crossings = 0
        for (square in line) {
            when(square) {
                '┃' -> ++crossings
                '━' -> {}
                '┗' -> {}
                '┛' -> {}
                '┓' -> ++crossings
                '┏' -> ++crossings
                else -> {}
            }
            if (crossings % 2 == 1 && square !in "┃━┗┛┏┓") {
                ++innerSquares
            }
        }
    }
    return innerSquares
}

fun main() {
    val test = """
        7-F7-
        .FJ|7
        SJLL7
        |F--J
        LJ.LJ
    """.trimIndent()
    checkEq(part1(test), 8.toLong(), "Part 1")

    val test2 = """
        FF7FSF7F7F7F7F7F---7
        L|LJ||||||||||||F--J
        FL-7LJLJ||||||LJL-77
        F--JF--7||LJLJ7F7FJ-
        L---JF-JLJ.||-FJLJJ7
        |F|F-JF---7F7-L7L|7|
        |FFJF7L7F-JF7|JL---7
        7-L-JL7||F7|L7F-7F7|
        L.L7LFJ|||||FJL7||LJ
        L7JLJL-JLJLJL--JLJ.L
    """.trimIndent()
    checkEq(part2(test2), 10.toLong(), "Part 2")

    val test3 = """
        .F----7F7F7F7F-7....
        .|F--7||||||||FJ....
        .||.FJ||||||||L7....
        FJL7L7LJLJ||LJ.L-7..
        L--J.L7...LJS7F-7L7.
        ....F-J..F7FJ|L7L7L7
        ....L7.F7||L7|.L7L7|
        .....|FJLJ|FJ|F7|.LJ
        ....FJL-7.||.||||...
        ....L---J.LJ.LJLJ...
    """.trimIndent()
    println("\n\n\nPart 2.2")
    checkEq(part2(test3), 8.toLong(), "Part 2, 2")

    solve("10", ::part1, ::part2)
}