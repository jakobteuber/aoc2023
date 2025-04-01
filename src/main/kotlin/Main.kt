package com.github.jakobteuber

import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.time.measureTime

fun readInput(day: String): String =
    Path("./src/main/resources/problem${day}.txt")
        .readText()

fun solvePart(day: String, part: Int, solution: ((String) -> Long)?, input: String) {
    println("Part $day.$part ···················································")
    if (solution != null) {
        var result: Long = 0;
        val time = measureTime {
            result = solution(input)
        }

        print("Solution: \u001b[1m$result\u001B[0m  \uD83C\uDF89\uD83E\uDD73\t\t")
        println("${if (part == 1) "\uD83C\uDF1F" else "\uD83C\uDF1F\uD83C\uDF1F" } \u001b[2m(?)\u001b[0m")
        println("\u001b[2;3m[took $time]\u001B[0m")
    } else {
        println("\u001B[2;3mnot yet implemented\u001B[0m")
    }
}

fun solve(day: String, part1: ((String) -> Long)? = null, part2: ((String) -> Long)? = null) {
    println("\u001b[1;32mAdvent of Code, Day $day \u001B[0m \uD83C\uDF84\uD83E\uDD36\uD83C\uDF85\uD83C\uDF84\n")
    val input = readInput(day);
    solvePart(day, 1, part1, input)
    solvePart(day, 2, part2, input)
}

fun String.findLong(regex: String) =
    findString(regex)?.toLongOrNull()
fun String.findString(regex: String) =
    regex.toRegex().find(this)?.groups?.get(1)?.value

fun <T> List<T>.withSet(index: Int, element: T) =
    mapIndexed { i, t -> if (i == index) element else t }

infix fun <A, B> Iterable<A>.cartesianProduct(right: Iterable<B>) =
    flatMap { a -> right.map { b -> a to b } }

fun <T> uniquePairs(list: List<T>) =
    list.withIndex().flatMap { (i, a) ->
        (i..list.lastIndex)
            .map { j -> list[j] }
            .map { b -> a to b }
    }

infix fun <A, B> Sequence<A>.cartesianProduct(right: Sequence<B>) =
    flatMap { a -> right.map { b -> a to b } }

fun<T> checkEq(a: T, b: T, msg: String) = check(a == b) { "$a != $b ($msg)" }

