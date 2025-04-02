package com.github.jakobteuber

fun hash(s: String) =
    s.fold(0) { acc, c -> (acc + c.code) * 17 % 256 }

private fun parse(input: String) =
    input.filterNot { it.isWhitespace() }
        .split(",")

private fun part1(input: String): Long {
    val initSequence = parse(input)
    return initSequence.sumOf { hash(it).toLong() }
}

private data class Lens(val name: String, val focalLength: Int)

private fun part2(input: String): Long {
    val initSequence = parse(input)
    val boxes = List(256) { mutableListOf<Lens>() }
    for (step in initSequence) {
        val name = step.takeWhile { it in 'a'..'z' }
        val rest = step.drop(name.length)
        when (rest.firstOrNull()) {
            '-' -> {
                boxes[hash(name)].removeIf { it.name == name }
            }
            '=' -> {
                val lens = Lens(name, focalLength = rest.drop(1).toInt())
                val box = boxes[hash(name)]
                val index = box.indexOfFirst { it.name == name }
                if (index in box.indices) {
                    box[index] = lens
                } else {
                    box.addLast(lens)
                }
            }
            else -> error("bad operator `${rest.firstOrNull()}` in $step")
        }
    }
    return boxes.withIndex()
        .sumOf { (i, box) ->
            val forBox = box.withIndex()
                .sumOf { (j, lens) -> (1 + j) * lens.focalLength.toLong() }
            (i + 1) * forBox
        }
}

fun main() {
    checkEq(hash("HASH"), 52, "Holiday ASCII String Helper algorithm")
    val test = "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7"
    checkEq(part1(test), 1320.toLong(), "Part 1")
    checkEq(part2(test), 145.toLong(), "Part 2")

    solve("15", ::part1, ::part2)
}