package org.ppalanques.eventos_springmongo.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

//Definimos el objeto Evento a partir de los documentos de la colección mongo "eventos"
@Document(collection = "eventos")
data class Evento(
    @Id val id: String? = null,
    val titulo: String,
    val descripcion: String? = null,
    val categoria: String,
    val fechaRealizacion: LocalDateTime,
    val favorito: Boolean
)
