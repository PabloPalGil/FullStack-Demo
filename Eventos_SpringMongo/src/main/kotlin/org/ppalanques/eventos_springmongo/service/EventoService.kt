package org.ppalanques.eventos_springmongo.service

import org.ppalanques.eventos_springmongo.model.Evento
import org.ppalanques.eventos_springmongo.repository.EventoRepository
import org.springframework.stereotype.Service

//Lógica de las operaciones con eventos
@Service
class EventoService(private val repository: EventoRepository) {

    fun agregarEvento(evento: Evento): Evento {
        return repository.save(evento)
    }

    fun obtenerTodosLosEventos(): List<Evento> {
        return repository.findAll()
    }


    fun actualizarEvento(id: String, eventoActualizado: Evento): Evento {
        val eventoExistente = repository.findById(id).orElseThrow { Exception("Evento no encontrado.") }
        val eventoModificado = eventoExistente.copy(
            titulo = eventoActualizado.titulo,
            descripcion = eventoActualizado.descripcion,
            categoria = eventoActualizado.categoria,
            fechaRealizacion = eventoActualizado.fechaRealizacion,
            favorito = eventoActualizado.favorito
        )
        return repository.save(eventoModificado)
    }

    fun eliminarEvento(id: String) {
        repository.deleteById(id)
    }
}