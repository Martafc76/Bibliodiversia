package com.example.practicaevaluable.Fragments_principal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.practicaevaluable.AuthActivity
import com.example.practicaevaluable.R
import com.example.practicaevaluable.databinding.FragmentCuentaBinding
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class FragmentCuenta : Fragment() {

    private lateinit var binding: FragmentCuentaBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCuentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.CerrarSesion.setOnClickListener {
            mostrarDialogoConfirmacionCerrarSesion()
        }

        binding.EliminarCuenta.setOnClickListener {
            mostrarDialogoConfirmacionEliminarCuenta()
        }
        binding.CambiarContrasena.setOnClickListener {
            cambiarContrasena()
        }
    }

    private fun mostrarDialogoConfirmacionCerrarSesion() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cerrar Sesión")
        builder.setMessage("¿Estás seguro de que deseas cerrar sesión?")
        builder.setPositiveButton("Sí") { _, _ ->
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            activity?.finishAffinity()
        }
        builder.setNegativeButton("No") { _, _ ->
            // No hacer nada, cerrar el diálogo
        }
        builder.create().show()

    }

    private fun mostrarDialogoConfirmacionEliminarCuenta() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Cuenta")
        builder.setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción es irreversible.")
        builder.setPositiveButton("Sí") { _, _ ->
            val user = firebaseAuth.currentUser
            user?.delete()
                ?.addOnSuccessListener {
                    Toast.makeText(requireContext(), "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), AuthActivity::class.java))
                    activity?.finishAffinity()
                }
                ?.addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al eliminar la cuenta: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        builder.setNegativeButton("No") { _, _ ->
            // No hacer nada, cerrar el diálogo
        }
        builder.create().show()
    }

    private fun cambiarContrasena() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_cambiar_contrasena, null)
            val etContrasenaActual = dialogView.findViewById<EditText>(R.id.etContrasenaActual)
            val etNuevaContrasena = dialogView.findViewById<EditText>(R.id.etNuevaContrasena)
            val etConfirmarContrasena = dialogView.findViewById<EditText>(R.id.etConfirmarContrasena)
            val btnConfirmar = dialogView.findViewById<Button>(R.id.btnConfirmar)

            val builder = AlertDialog.Builder(requireContext())
            builder.setView(dialogView)
            builder.setTitle("Cambiar Contraseña")
            val dialog = builder.create()

            btnConfirmar.setOnClickListener {
                val contrasenaActual = etContrasenaActual.text.toString()
                val nuevaContrasena = etNuevaContrasena.text.toString()
                val confirmarContrasena = etConfirmarContrasena.text.toString()

                if (nuevaContrasena != confirmarContrasena) {
                    Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val credential = EmailAuthProvider.getCredential(user.email!!, contrasenaActual)
                user.reauthenticate(credential)
                    .addOnSuccessListener {
                        user.updatePassword(nuevaContrasena)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Error al actualizar contraseña: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error al reautenticar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            dialog.show()
        }
    }





}
