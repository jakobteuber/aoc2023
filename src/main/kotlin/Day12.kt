package com.github.jakobteuber

import com.github.jakobteuber.SpringStatus.*

private enum class SpringStatus {
    Operational, Damaged, Unknown;

    override fun toString(): String {
        return when(this) {
            Operational -> "."
            Damaged -> "#"
            Unknown -> "?"
        }
    }
}
private data class SpringRow(
    val springs: List<SpringStatus>,
    val damagedRunLengths: List<Int>) {

    override fun toString(): String {
        val s = springs.joinToString(separator = "")
        return "Row($s, runs = $damagedRunLengths)"
    }
}

private sealed class State {
    var currentCount = 0.toLong()
    var nextCount = 0.toLong()
    fun putIn(n: Long) { nextCount += n }

    var next: State? = null
    fun linkNext(state: State) { next = state }

    fun moveCounts() {
        currentCount = nextCount
        nextCount = 0
    }
    fun step(s: SpringStatus) {
        when (s) {
            Operational -> onOperational()
            Damaged -> onDamaged()
            Unknown -> {
                onDamaged()
                onOperational()
            }
        }
    }
    abstract fun onDamaged()
    abstract fun onOperational()
}

private class ConsumeDamaged: State() {
    override fun onDamaged() { next?.putIn(currentCount) }
    override fun onOperational() {}
    override fun toString() = "#"
}

private class ConsumeOperational: State() {
    override fun onDamaged() {}
    override fun onOperational() {
        next?.putIn(currentCount)
        this.putIn(currentCount)
    }
    override fun toString() = "."
}

private class StateMachine(runs: List<Int>) {
    val states = buildList {
        add(ConsumeOperational())
        for (run in runs) {
            repeat(run) { add(ConsumeDamaged()) }
            add(ConsumeOperational())
        }
    }.also { states ->
        states.zipWithNext()
            .onEach { (prev, next) -> prev.linkNext(next) }
    }

    fun step(s: SpringStatus) {
        states.onEach { it.step(s) }
        states.onEach { it.moveCounts() }
    }

    fun run(template: List<SpringStatus>): Long {
        states.first().currentCount = 1
        step(Operational)
        for (spring in template) {
            step(spring)
        }
        step(Operational)
        return states.last().currentCount
    }
}

private fun parse(input: String) =
    input.lineSequence()
        .filterNot { it.isBlank() }
        .map { line ->
            val (rowText,runsText) = line.split(" ")
            SpringRow(
                springs = rowText.map {
                    when (it) {
                        '.' -> Operational
                        '#' -> Damaged
                        '?' -> Unknown
                        else -> error("Bad char $it")
                    }
                },
                damagedRunLengths = runsText.split(",")
                    .map { it.toInt() }
            )
        }

private fun part1(input: String): Long {
    val springRows = parse(input)
    return springRows.sumOf { row ->
        StateMachine(row.damagedRunLengths).run(row.springs)
    }
}

private fun SpringRow.unfold(): SpringRow {
    val repeatedSprings = buildList {
        addAll(springs)
        add(Unknown)
        addAll(springs)
        add(Unknown)
        addAll(springs)
        add(Unknown)
        addAll(springs)
        add(Unknown)
        addAll(springs)
    }
    val repeatedRuns = buildList {
        repeat(5) { addAll(damagedRunLengths) }
    }
    return SpringRow(repeatedSprings, repeatedRuns)
}

private fun part2(input: String): Long {
    val springRows = parse(input).map { it.unfold() }
    return springRows.sumOf { row ->
        StateMachine(row.damagedRunLengths).run(row.springs)
    }
}

fun main() {
    val test = """
        ???.### 1,1,3
        .??..??...?##. 1,1,3
        ?#?#?#?#?#?#?#? 1,3,1,6
        ????.#...#... 4,1,1
        ????.######..#####. 1,6,5
        ?###???????? 3,2,1
    """.trimIndent()


    checkEq(part1("???.### 1,1,3"), 1.toLong(), "1.1")
    checkEq(part1(".??..??...?##. 1,1,3"), 4.toLong(), "1.2")
    checkEq(part1("?#?#?#?#?#?#?#? 1,3,1,6"), 1.toLong(), "1.3")
    checkEq(part1("????.#...#... 4,1,1"), 1.toLong(), "1.4")
    checkEq(part1("????.######..#####. 1,6,5"), 4.toLong(), "1.5")
    checkEq(part1("?###???????? 3,2,1"), 10.toLong(), "1.6")
    checkEq(part1(test), 21.toLong(), "Part 1")


    checkEq(part2("???.### 1,1,3"), 1.toLong(), "2.1")
    checkEq(part2(".??..??...?##. 1,1,3"), 16384.toLong(), "2.2")
    checkEq(part2("?#?#?#?#?#?#?#? 1,3,1,6"), 1.toLong(), "2.3")
    checkEq(part2("????.#...#... 4,1,1"), 16.toLong(), "2.4")
    checkEq(part2("????.######..#####. 1,6,5"), 2500.toLong(), "2.5")
    checkEq(part2("?###???????? 3,2,1"), 506250.toLong(), "2.6")

    solve("12", ::part1, ::part2)
}