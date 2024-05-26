package com.example.practicaevaluable.Interface

import com.example.practicaevaluable.Models.BooksApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksService {

    @GET("volumes")
    fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): Call<BooksApiResponse>
}
