package com.github.jakobteuber

import kotlin.math.max
import kotlin.math.min
import kotlin.streams.asStream

private data class MapSegment(
    val destinationStart: Long,
    val sourceStart: Long,
    val length: Long
) {
    val sourceRange get() = sourceStart ..< sourceStart + length
    val delta get() = destinationStart - sourceStart

    fun transform(valueRange: LongRange): List<LongRange> {
        if (valueRange.last < sourceRange.first) return listOf(valueRange);
        if (valueRange.first > sourceRange.last) return listOf(valueRange);
        val clampedStart = max(valueRange.first, sourceRange.first)
        val clampedEnd = min(valueRange.last, sourceRange.last)
        return listOf(
            valueRange.first ..< clampedStart,
            (clampedStart + delta) .. (clampedEnd + delta),
            (clampedEnd + 1) .. valueRange.last,
        ).filterNot { it.isEmpty() }
    }
}

private data class AlmanacMap(
    val from: String,
    val to: String,
    val segments: List<MapSegment>
) {
    fun transform(value: Long): Long {
        for (s in segments) {
            if (value in s.sourceRange) {
                return value + s.delta
            }
        }
        return value
    }

    fun transform(valueRanges: List<LongRange>) =
        segments.fold(valueRanges) { ranges, segment ->
            ranges.flatMap { segment.transform(it) }
        }
}

private data class Almanac(val seeds: List<Long>, val maps: List<AlmanacMap>) {
    fun transform(value: Long) = maps.fold(value) { v, map ->
        map.transform(v)
    }

    fun transform(valueRanges: List<LongRange>) = maps.fold(valueRanges) { ranges, map ->
        map.transform(ranges)
    }
}

private fun parse(input: String): Almanac {
    val seedLine = input.lineSequence().first()
    val mapBlocks = input.split("\n\n")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .drop(1)
    val seeds = seedLine.substringAfter(":")
        .split(" ")
        .mapNotNull { it.toLongOrNull() }
    val maps = mapBlocks.map { block ->
        val header = block.lineSequence().first().findString("""([\w-]+) map:""")!!
        val (from, to) = header.split("-to-")
        val segments = block.lineSequence().drop(1)
            .map { line ->
                val (destStart, srcStart, length) = line.split(" ")
                MapSegment(destStart.toLong(), srcStart.toLong(), length.toLong())
            }.toList()
        AlmanacMap(from, to, segments)
    }
    return Almanac(seeds, maps)
}

private fun part1(input: String): Long {
    val almanac = parse(input)
    checkEq(almanac.maps.first().from, "seed", "sanity check, start")
    almanac.maps.zipWithNext { prev, next -> checkEq(prev.to, next.from, "sanity check, chain") }
    checkEq(almanac.maps.last().to, "location", "sanity check, end")

    return almanac.seeds.minOf { seed ->
        almanac.transform(seed)
    }
}

private fun part2(input: String): Long {
    val almanac = parse(input)
    val seedRanges = almanac.seeds.chunked(2)
        .map { (start, length) ->
            (start..< start + length)
        }

    // This is ugly, it takes 40s
    val min = seedRanges.asSequence()
        .flatMap { it }
        .asStream()
        .parallel()
        .mapToLong { it }
        .map { almanac.transform(it) }
        .min().orElse(Long.MAX_VALUE)
    return min
}

fun main() {
    val test = """
        seeds: 79 14 55 13

        seed-to-soil map:
        50 98 2
        52 50 48

        soil-to-fertilizer map:
        0 15 37
        37 52 2
        39 0 15

        fertilizer-to-water map:
        49 53 8
        0 11 42
        42 0 7
        57 7 4

        water-to-light map:
        88 18 7
        18 25 70

        light-to-temperature map:
        45 77 23
        81 45 19
        68 64 13

        temperature-to-humidity map:
        0 69 1
        1 0 69

        humidity-to-location map:
        60 56 37
        56 93 4
    """.trimIndent()

    checkEq(part1(test), 35.toLong(), "Part 1")
    checkEq(part2(test), 46.toLong(), "Part 2")

    solve("05", ::part1, ::part2)
}