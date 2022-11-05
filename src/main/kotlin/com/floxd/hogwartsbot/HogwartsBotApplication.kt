package com.floxd.hogwartsbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HogwartsBotApplication

fun main(args: Array<String>) {
    runApplication<HogwartsBotApplication>(*args)
}
