package com.github.jakobteuber

private data class ScratchCard(
    val id: Int,
    val winningNumbers: List<Int>,
    val drawnNumbers: List<Int>,
) {
    fun countWinning(): Int =
        (drawnNumbers intersect winningNumbers.toSet()).count()

}

private fun parse(input: String): Sequence<ScratchCard> {
    return input.lineSequence()
        .filter { it.isNotBlank() }
        .map { line ->
            val (header, numbers) = line.split(":")
            val id = header.findLong("""Card\s+(\d+)""")!!.toInt()
            val (winningText, drawnText) = numbers.split("|")
            ScratchCard(
                id,
                winningNumbers = winningText.split(" ").mapNotNull { it.toIntOrNull() },
                drawnNumbers = drawnText.split(" ").mapNotNull { it.toIntOrNull() }
            )
        }
}

private fun part1(input: String): Long =
    parse(input)
        .map { it.countWinning() }
        .map { if (it > 0) 1 shl (it - 1) else 0 }
        .sum()
        .toLong()


private fun part2(input: String): Long {
    val cards = parse(input)
    val cardCounter = cards.associate { it.id to 1 }.toMutableMap()
    for (card in cards) {
        val winnings = card.countWinning()
        val multiplicity = cardCounter[card.id]!!
        val doublingRange = (card.id + 1) .. (card.id + winnings)
        for (id in doublingRange) {
            cardCounter.compute(id) { _, count -> count!! + multiplicity }
        }
    }
    return cardCounter.values.sum().toLong()
}



fun main() {
    val test = """
        Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
        Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
        Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
        Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
        Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
    """.trimIndent()
    checkEq(part1(test), 13.toLong(), "Part 1")
    checkEq(part2(test), 30.toLong(), "Part 2")
    solve("04", ::part1, ::part2)
}