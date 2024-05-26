package com.example.practicaevaluable.Models

data class BooksApiResponse(
    val items: List<BookItem>
)

data class BookItem(
    val volumeInfo: Book
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

