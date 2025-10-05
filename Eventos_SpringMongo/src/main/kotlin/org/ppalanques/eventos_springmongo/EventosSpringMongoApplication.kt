package org.ppalanques.eventos_springmongo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

//La aplicación que arranca el servidor SpringBoot
@SpringBootApplication
class EventosSpringMongoApplication

fun main(args: Array<String>) {
    runApplication<EventosSpringMongoApplication>(*args)
}
