package org.ppalanques.eventos_springmongo.repository

import org.ppalanques.eventos_springmongo.model.Evento
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


//Declaramos el repositorio mongo
@Repository
interface EventoRepository : MongoRepository<Evento, String> {}