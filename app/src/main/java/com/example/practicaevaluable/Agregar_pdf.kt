package com.example.practicaevaluable

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practicaevaluable.databinding.ActivityAgregarPdfBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class Agregar_pdf : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarPdfBinding
    private val PICK_PDF_REQUEST = 1

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private val categoriasList: MutableList<String> = ArrayList()

    private lateinit var pdfUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Cargar categorías desde Firebase
        cargarCategorias()

        binding.btnInsertarPdf.setOnClickListener {
            seleccionarPDF()
        }

        configurarBotonSubirLibro()
    }

    private fun cargarCategorias() {
        val uid = firebaseAuth.uid
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("uid").equalTo(uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriasList.clear()
                for (ds in snapshot.children) {
                    val categoria = ds.child("categoria").value as String
                    categoriasList.add(categoria)
                }
                // Configurar el adaptador del AutoCompleteTextView después de cargar las categorías
                configurarTextViewCategoria()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores
                Toast.makeText(
                    this@Agregar_pdf,
                    "Error al cargar categorías: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun configurarTextViewCategoria() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoriasList)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.tv_categoria)
        autoCompleteTextView.setAdapter(adapter)

        // Configurar el AutoCompleteTextView para mostrar la lista desplegable al recibir foco
        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                autoCompleteTextView.showDropDown()
            }
        }

        // Asegurar que el AutoCompleteTextView muestre el menú desplegable al hacer clic después de haber perdido el foco
        autoCompleteTextView.setOnClickListener {
            if (!autoCompleteTextView.isPopupShowing) {
                autoCompleteTextView.showDropDown()
            }
        }
    }

    private fun configurarBotonSubirLibro() {
        binding.btnSubirLibro.setOnClickListener {
            val titulo = binding.etTituloLibro.text.toString()
            val descripcion = binding.etDescripcionLibro.text.toString()
            val categoria = binding.tvCategoria.text.toString()

            if (titulo.isNotEmpty() && descripcion.isNotEmpty() && categoria.isNotEmpty() && ::pdfUri.isInitialized) {
                subirLibro(titulo, descripcion, categoria, pdfUri)
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun subirLibro(titulo: String, descripcion: String, categoria: String, pdfUri: Uri) {
        progressDialog.setMessage("Subiendo libro...")
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().reference.child("libros")
            .child("${System.currentTimeMillis()}.pdf")

        storageReference.putFile(pdfUri)
            .addOnSuccessListener { taskSnapshot ->
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val ref = FirebaseDatabase.getInstance().getReference("Libros")
                    val libroId = ref.push().key
                    val libroMap = HashMap<String, Any>()
                    libroMap["titulo"] = titulo
                    libroMap["descripcion"] = descripcion
                    libroMap["pdfUrl"] = uri.toString()
                    libroMap["categoria"] = categoria
                    libroMap["uid"] = firebaseAuth.uid!!  // Agrega el UID del usuario

                    if (libroId != null) {
                        ref.child(libroId).setValue(libroMap)
                            .addOnSuccessListener {
                                progressDialog.dismiss()
                                Toast.makeText(applicationContext, "Libro subido correctamente", Toast.LENGTH_SHORT).show()
                                limpiarCampos()
                            }
                            .addOnFailureListener { e ->
                                progressDialog.dismiss()
                                Toast.makeText(applicationContext, "Error al subir libro: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Error al subir PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun limpiarCampos() {
        binding.etTituloLibro.text.clear()
        binding.etDescripcionLibro.text.clear()
        binding.tvCategoria.text.clear()
    }

    private fun seleccionarPDF() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(intent, PICK_PDF_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            pdfUri = data.data!!
            val nombreArchivo = obtenerNombreArchivo(pdfUri)
            Toast.makeText(this, "Archivo seleccionado: $nombreArchivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerNombreArchivo(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return if (cursor != null && cursor.moveToFirst()) {
            val nombreIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val nombreArchivo = cursor.getString(nombreIndex)
            cursor.close()
            nombreArchivo
        } else {
            "Archivo PDF"
        }
    }
}