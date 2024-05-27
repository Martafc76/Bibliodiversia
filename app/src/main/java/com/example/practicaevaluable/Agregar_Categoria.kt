package com.example.practicaevaluable

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import com.example.practicaevaluable.databinding.ActivityAgregarCategoriaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Agregar_Categoria : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarCategoriaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var categoria = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.AgregarCategoriaBD.setOnClickListener {
            validarDatos()
        }

        binding.EtCategoria.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
    }

    private fun validarDatos() {
        categoria = binding.EtCategoria.text.toString().trim()
        if (categoria.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese una categoría", Toast.LENGTH_SHORT).show()
        } else {
            agregarCategoria()
        }
    }

    private fun agregarCategoria() {
        progressDialog.setMessage("Agregando categoría")
        progressDialog.show()

        val tiempo = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$tiempo"
        hashMap["categoria"] = categoria
        hashMap["tiempo"] = tiempo
        hashMap["uid"] = firebaseAuth.uid!!  // Agrega el UID del usuario

        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.child("$tiempo")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Se agregó la categoría a la BD", Toast.LENGTH_SHORT).show()
                binding.EtCategoria.setText("")
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se agregó la categoría debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
