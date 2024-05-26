package com.example.practicaevaluable.Interface

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {

    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    val instance: GoogleBooksService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(GoogleBooksService::class.java)
    }
}

