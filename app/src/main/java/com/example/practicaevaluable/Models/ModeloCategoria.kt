package com.example.practicaevaluable.Models

class ModeloCategoria {
    var id : String = ""
    var categoria : String = ""
    var tiempo : Long = 0
    var uid : String = ""

    constructor()

    constructor(id: String, categoria: String, tiempo: Long, uid: String) {
        this.id = id
        this.categoria = categoria
        this.tiempo = tiempo
        this.uid = uid
    }


}
class ModeloArchivo {
    var id: String = ""
    var titulo: String = ""
    var descripcion: String = ""
    var pdfUri: String = ""

    constructor()

    constructor(id: String, titulo: String, descripcion: String, pdfUri: String) {
        this.id = id
        this.titulo = titulo
        this.descripcion = descripcion
        this.pdfUri = pdfUri
    }
}

class ModeloLibro {
    var id: String = ""
    var titulo: String = ""
    var descripcion: String = ""
    var pdfUrl: String = ""
    var categoria: String = ""
    var uid: String = ""  // Agrega el campo uid

    constructor()

    constructor(id: String, titulo: String, descripcion: String, pdfUrl: String, categoria: String, uid: String) {
        this.id = id
        this.titulo = titulo
        this.descripcion = descripcion
        this.pdfUrl = pdfUrl
        this.categoria = categoria
        this.uid = uid
    }
}