package com.floxd.hogwartsbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.*

@SpringBootApplication
@EnableScheduling
class HogwartsBotApplication

fun main(args: Array<String>) {
    runApplication<HogwartsBotApplication>(*args)
}
