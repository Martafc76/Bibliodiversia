package com.example.practicaevaluable.Models

import java.util.UUID

data class BooksApiResponse(
    val items: List<BookItem>
)

data class BookItem(
    val volumeInfo: Book
)

data class BookItem2(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    var opinion: String = "",
    var rating: Float = 0f,
    val image: String = ""
)



data class Book(
    val title: String,
    val authors: List<String>?,
    val description: String?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String?
)

data class VolumeInfo(
    val imageLinks: ImageLinks?
)

