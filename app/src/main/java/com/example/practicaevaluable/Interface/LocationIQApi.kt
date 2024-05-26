package com.example.practicaevaluable.Interface

    import com.example.practicaevaluable.Models.LocationResponse
    import retrofit2.Call
    import retrofit2.http.GET
    import retrofit2.http.Headers
    import retrofit2.http.Query

    interface LocationIQApi {
        @Headers("Accept: application/json")
        @GET("v1/search.php")
        fun search(@Query("key") apiKey: String, @Query("q") query: String): Call<LocationResponse>
    }
