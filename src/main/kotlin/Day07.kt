package com.github.jakobteuber

private enum class HandType { FiveOfKind, FourOfKind, FullHouse, ThreeOfKind, TwoPair, OnePair, HighCard }

private fun List<Int>.pairCount() = this.count { it == 2 }

fun <I, K> compareKeys(keyComparator: Comparator<K>, kexExtractor: (I) -> K) =
    Comparator.nullsLast<I> { a, b ->
        keyComparator.compare(kexExtractor(a), kexExtractor(b))
    }

private fun classifyHand(hand: String): HandType {
    val counts = hand.groupBy { it }.map { (_,list) -> list.size }
    return when {
        5 in counts -> HandType.FiveOfKind
        4 in counts -> HandType.FourOfKind
        3 in counts && 2 in counts -> HandType.FullHouse
        3 in counts -> HandType.ThreeOfKind
        counts.pairCount() == 2 -> HandType.TwoPair
        2 in counts -> HandType.OnePair
        else -> HandType.HighCard
    }
}

private fun symbolWorth(card: Char, alphabet: String): Int {
    for ((i, c) in alphabet.withIndex()) {
        if (c == card) return i
    }
    error("unknown card character")
}

private fun compareFirstDiffering(alphabet: String) =
    Comparator.nullsLast { a: String, b: String ->
        val (c1, c2) = (a zip b).dropWhile { (c1, c2) -> c1 == c2 }
            .first()
        val cmp = Comparator.comparing<Char, Int> { symbolWorth(it, alphabet) }
        return@nullsLast cmp.compare(c1, c2)
    }

private fun compareHands(alphabet: String) =
    Comparator.comparing<String, HandType> { classifyHand(it) }
        .then(compareFirstDiffering(alphabet))


private data class CamelGame(val hand: String, val bidding: Long)

private fun parse(input: String): List<CamelGame> =
    input.lineSequence()
        .filter { it.isNotBlank() }
        .map { line ->
            val (hand, bidding) = line.split(" ")
            CamelGame(hand, bidding.toLong())
        }.toList()

private fun part1(input: String): Long {
    val alphabet = "AKQJT98765432"
    val games = parse(input)
    val gameCmp = compareKeys<CamelGame, String>(compareHands(alphabet)) { it.hand }
    return games
        .sortedWith(gameCmp)
        .asReversed()
        .mapIndexed { i, game -> (i + 1) * game.bidding }
        .sum()
}

private fun promote(handType: HandType) =
    when(handType) {
        HandType.FiveOfKind -> HandType.FiveOfKind
        HandType.FourOfKind -> HandType.FiveOfKind
        HandType.FullHouse -> HandType.FourOfKind
        HandType.ThreeOfKind -> HandType.FourOfKind
        HandType.TwoPair -> HandType.FullHouse
        HandType.OnePair -> HandType.ThreeOfKind
        HandType.HighCard -> HandType.OnePair
    }

private fun compareHandsWithJoker(alphabet: String) =
    Comparator.comparing<String, HandType> { hand ->
        val withoutJokers = hand.filter { it != 'J' }
        val jokerCount = hand.count { it == 'J' }
        var type = classifyHand(withoutJokers)
        repeat(jokerCount) { type = promote(type) }
        type
    }.then(compareFirstDiffering(alphabet))

private fun part2(input: String): Long {
    val alphabet = "AKQT98765432J"
    val games = parse(input)
    val gameCmp = compareKeys<CamelGame, String>(compareHandsWithJoker(alphabet)) { it.hand }
    return games
        .sortedWith(gameCmp)
        .asReversed()
        .mapIndexed { i, game -> (i + 1) * game.bidding }
        .sum()
}

fun main() {
    val test = """
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483
    """.trimIndent()

    checkEq(part1(test), 6440.toLong(), "Part 1")
    checkEq(part2(test), 5905.toLong(), "Part 2")

    solve("07", ::part1, ::part2)
}