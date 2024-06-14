package com.example.practicaevaluable.Fragments_principal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.practicaevaluable.Adapter.AdapterCategoria
import com.example.practicaevaluable.Adapter.AdapterPdf
import com.example.practicaevaluable.Agregar_Categoria
import com.example.practicaevaluable.Agregar_pdf
import com.example.practicaevaluable.Models.ModeloCategoria
import com.example.practicaevaluable.Models.PdfLibro
import com.example.practicaevaluable.databinding.FragmentCategoriasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class FragmentCategorias : Fragment() {

    private lateinit var binding: FragmentCategoriasBinding
    private lateinit var miContext: Context
    private lateinit var categoriaArrayList: ArrayList<ModeloCategoria>
    private lateinit var adaptadorCategoria: AdapterCategoria
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onAttach(context: Context) {
        miContext = context
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoriasBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listarCategorias()

        binding.BuscarCategoria.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(categoria: CharSequence?, start: Int, before: Int, count: Int) {
                adaptadorCategoria.filter.filter(categoria.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnAgregarCategoria.setOnClickListener {
            startActivity(Intent(miContext, Agregar_Categoria::class.java))
        }
        binding.AgregarPdf.setOnClickListener {
            startActivity(Intent(miContext, Agregar_pdf::class.java))
        }
    }

    private fun listarCategorias() {
        categoriaArrayList = ArrayList()
        val uid = firebaseAuth.uid
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("uid").equalTo(uid)
        binding.progressBar.visibility = View.VISIBLE
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriaArrayList.clear()
                for (ds in snapshot.children) {
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriaArrayList.add(modelo!!)
                }
                adaptadorCategoria = AdapterCategoria(miContext, categoriaArrayList, binding.categoriasRv, this@FragmentCategorias)
                binding.categoriasRv.adapter = adaptadorCategoria
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Verificar si el usuario está autenticado antes de mostrar el mensaje de error
                if (firebaseAuth.currentUser != null) {
                    // Usuario autenticado pero ocurrió un error al cargar categorías
                    Toast.makeText(miContext, "Error al cargar categorías: ${error.message}", Toast.LENGTH_SHORT).show()
                }
                binding.progressBar.visibility = View.GONE
            }
        })
    }


    private fun mostrarLibrosEnRecyclerView(libros: ArrayList<PdfLibro>) {
        val adapter = AdapterPdf(miContext, libros)
        binding.librosRv.adapter = adapter
    }

    fun mostrarLibrosCategoria(categoria: ModeloCategoria) {
        val uid = firebaseAuth.uid
        val ref = FirebaseDatabase.getInstance().getReference("Libros")
        ref.orderByChild("categoria").equalTo(categoria.categoria).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val libros = ArrayList<PdfLibro>()
                for (ds in snapshot.children) {
                    val libro = ds.getValue(PdfLibro::class.java)
                    if (libro != null && libro.uid == uid) {  // Filtrar por uid
                        libros.add(libro)
                    }
                }
                mostrarLibrosEnRecyclerView(libros)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

