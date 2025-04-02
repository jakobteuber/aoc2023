package com.github.jakobteuber

private typealias Grid = MutableList<MutableList<Char>>

private tailrec fun tryMoveUp(grid: Grid, row: Int, col: Int,
                              rowDelta: Int = 0, colDelta: Int = 0) {
    val here = grid.getOrNull(row)?.getOrNull(col)
    val nextRow = row + rowDelta
    val nextCol = col + colDelta
    val there = grid.getOrNull(nextRow)?.getOrNull(nextCol)
    if (here != 'O') return
    if (there != '.') return
    grid[row][col] = '.'
    grid[nextRow][nextCol] = 'O'
    tryMoveUp(grid, nextRow, nextCol, rowDelta, colDelta)
}

private fun moveAll(grid: Grid,
                    rowIndices: IntProgression, colIndices: IntProgression,
                    rowDelta: Int = 0, colDelta: Int = 0) {
    for (row in rowIndices) {
        for (col in colIndices) {
            tryMoveUp(grid, row, col, rowDelta, colDelta)
        }
    }
}

private fun moveAllNorth(grid: Grid) =
    moveAll(grid, grid.indices, grid.first().indices, rowDelta = -1)
private fun moveAllSouth(grid: Grid) =
    moveAll(grid, grid.indices.reversed(), grid.first().indices, rowDelta = +1)
private fun moveAllWest(grid: Grid) =
    moveAll(grid, grid.indices, grid.first().indices, colDelta = -1)
private fun moveAllEast(grid: Grid) =
    moveAll(grid, grid.indices, grid.first().indices.reversed(), colDelta = +1)


private fun score(grid: Grid) =
    grid.map { row -> row.count { it == 'O' }.toLong() }
        .mapIndexed { i, count -> (grid.size - i) * count }
        .sum()

private fun parse(input: String) =
    input.lines()
        .filterNot { it.isBlank() }
        .map { it.trim().toMutableList() }
        .toMutableList()

private fun part1(input: String): Long {
    val grid = parse(input)
    moveAllNorth(grid)
    return score(grid)
}

private fun cycle(grid: Grid) {
    moveAllNorth(grid)
    moveAllWest(grid)
    moveAllSouth(grid)
    moveAllEast(grid)
}

private fun Grid.stringify() = joinToString("") { it.joinToString("") }

private fun part2(input: String): Long {
    val grid = parse(input)
    val maxCycles = 1000000000
    val repetitions = mutableMapOf<String, Int>()
    for (i in 0..<maxCycles) {
        cycle(grid)
        val record = grid.stringify()
        if (record in repetitions) {
            val lastOccurrence = repetitions[record]!!
            val repetitionLength = i - lastOccurrence
            val remaining = maxCycles - i - 1
            repeat(remaining % repetitionLength) { cycle(grid) }
            break
        }
        repetitions[record] = i
    }
    return score(grid)
}

fun main() {
    val test = """
        O....#....
        O.OO#....#
        .....##...
        OO.#O....O
        .O.....O#.
        O.#..O.#.#
        ..O..#O..O
        .......O..
        #....###..
        #OO..#....
    """.trimIndent()
    checkEq(part1(test), 136.toLong(), "Part 1")
    checkEq(part2(test), 64.toLong(), "Part 2")

    solve("14", ::part1, ::part2)
}