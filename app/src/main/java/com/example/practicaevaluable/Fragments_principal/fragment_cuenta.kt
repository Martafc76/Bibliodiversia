package com.example.practicaevaluable.Fragments_principal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.practicaevaluable.AuthActivity
import com.example.practicaevaluable.databinding.FragmentCuentaBinding
import com.google.firebase.auth.FirebaseAuth


class fragment_cuenta : Fragment() {

    private lateinit var binding: FragmentCuentaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var miContext: Context

    override fun onAttach(context: android.content.Context) {
        miContext = context
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCuentaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.CerrarSesion.setOnClickListener{
            mostrarDialogoConfirmacionCerrarSesion()
        }
    }

    private fun mostrarDialogoConfirmacionCerrarSesion() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cerrar Sesión")
        builder.setMessage("¿Estás seguro de que deseas cerrar sesión?")
        builder.setPositiveButton("Sí") { _, _ ->
            // Cerrar sesión
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            activity?.finishAffinity()
        }
        builder.setNegativeButton("No") { _, _ ->
            // No hacer nada, cerrar el diálogo
        }
        builder.create().show()
    }


}