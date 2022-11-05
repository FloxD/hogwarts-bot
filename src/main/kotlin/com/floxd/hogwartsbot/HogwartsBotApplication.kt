package com.floxd.hogwartsbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class HogwartsBotApplication

fun main(args: Array<String>) {
    runApplication<HogwartsBotApplication>(*args)
}

fun <T : Any> Optional<T>.toNullable(): T? = this.orElse(null)
