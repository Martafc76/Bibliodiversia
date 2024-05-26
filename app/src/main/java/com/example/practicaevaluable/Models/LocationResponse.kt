package com.example.practicaevaluable.Models

data class LocationResponse(
    val results: List<LocationResult>
)

data class LocationResult(
    val lat: String,
    val lon: String,
    val display_name: String
)
