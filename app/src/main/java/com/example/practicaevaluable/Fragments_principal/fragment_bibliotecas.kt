package com.example.practicaevaluable.Fragments_principal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practicaevaluable.Adapter.BiblioAdapter
import com.example.practicaevaluable.Interface.ApiService
import com.example.practicaevaluable.Interface.GoogleBooksService
import com.example.practicaevaluable.Models.BooksApiResponse
import com.example.practicaevaluable.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class fragment_bibliotecas : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: BiblioAdapter
    private lateinit var googleBooksService: GoogleBooksService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bibliotecas, container, false)
        googleBooksService = ApiService.instance

        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)

        adapter = BiblioAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchBooks(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return view
    }

    private fun searchBooks(query: String?) {
        if (query != null) {
            googleBooksService.searchBooks(query, "AIzaSyAvn-gZPaZyEqT4BpNWVXEuDT8r7frCnPM")
                .enqueue(object : Callback<BooksApiResponse> {
                    override fun onFailure(call: Call<BooksApiResponse>, t: Throwable) {
                        // Handle error
                    }

                    override fun onResponse(call: Call<BooksApiResponse>, response: Response<BooksApiResponse>) {
                        val books = response.body()?.items?.map { it.volumeInfo } ?: emptyList()
                        adapter.updateData(books)
                    }
                })
        }
    }
}
