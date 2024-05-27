package com.example.practicaevaluable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practicaevaluable.Adapter.AdapterPdf
import com.example.practicaevaluable.Models.PdfLibro
import com.example.practicaevaluable.databinding.ActivityDetalleCategoriaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class DetalleCategoria : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleCategoriaBinding
    private lateinit var adapter: AdapterPdf
    val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoria = intent.getStringExtra("categoria") ?: ""

        adapter = AdapterPdf(this, arrayListOf())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Cargar libros de la categoría
        obtenerLibrosCategoria(categoria)
    }

    private fun obtenerLibrosCategoria(categoria: String) {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.orderByChild("categoria").equalTo(categoria).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val libros = arrayListOf<PdfLibro>()
                for (ds in snapshot.children) {
                    val libro = ds.getValue(PdfLibro::class.java)
                    if (libro != null && libro.uid == uid) {  // Filtrar por uid
                        libro.id = ds.key ?: ""
                        libros.add(libro)
                    }
                }
                adapter.updateLibros(libros)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores
            }
        })
    }
}
