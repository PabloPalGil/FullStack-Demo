package org.ppalanques.eventos_springmongo.controller

import org.ppalanques.eventos_springmongo.model.Evento
import org.ppalanques.eventos_springmongo.service.EventoService
import org.springframework.web.bind.annotation.*

//Controlador para el Endpoint /eventos
@RestController
@RequestMapping("/eventos")
class EventoController(private val service: EventoService) {

    //Agregar un evento
    @PostMapping
    fun agregarEvento(@RequestBody evento: Evento): Evento {
        return service.agregarEvento(evento)
    }

    //Obtener todos los eventos:
    @GetMapping
    fun obtenerEventos(): List<Evento> {
        return service.obtenerTodosLosEventos()
    }

    //Actualizar un evento por su Id
    @PutMapping("/{id}")
    fun actualizarEvento(@PathVariable id: String, @RequestBody evento: Evento): Evento {
        return service.actualizarEvento(id, evento)
    }

    //Eliminar un evento por su Id
    @DeleteMapping("/{id}")
    fun eliminarEvento(@PathVariable id: String) {
        service.eliminarEvento(id)
    }
}