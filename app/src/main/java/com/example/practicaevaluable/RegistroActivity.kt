package com.example.practicaevaluable

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.practicaevaluable.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var auth: FirebaseAuth

    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegistroFinal.setOnClickListener {
            registrarUsuario()
        }

        binding.btnVolver.setOnClickListener {
            onBackPressed()
        }

        // Configurar el botón para alternar la visibilidad de la contraseña
        binding.btnTogglePasswordVisibility.setOnClickListener {
            togglePasswordVisibility()
        }

        // Configurar también para el campo de confirmar contraseña
        binding.btnTogglePasswordVisibilityConfirm.setOnClickListener {
            togglePasswordVisibilityConfirm()
        }
    }

    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible

        if (passwordVisible) {
            // Mostrar la contraseña
            binding.etPasswordRegistro.transformationMethod = null
            binding.btnTogglePasswordVisibility.setImageResource(R.drawable.password_icon)
        } else {
            // Ocultar la contraseña
            binding.etPasswordRegistro.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.btnTogglePasswordVisibility.setImageResource(R.drawable.password_icon)
        }

        // Mover el cursor al final del texto
        binding.etPasswordRegistro.setSelection(binding.etPasswordRegistro.text.length)
    }

    private fun togglePasswordVisibilityConfirm() {
        passwordVisible = !passwordVisible

        if (passwordVisible) {
            // Mostrar la contraseña
            binding.etConfirmarPassword.transformationMethod = null
            binding.btnTogglePasswordVisibilityConfirm.setImageResource(R.drawable.password_icon)
        } else {
            // Ocultar la contraseña
            binding.etConfirmarPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.btnTogglePasswordVisibilityConfirm.setImageResource(R.drawable.password_icon)
        }

        // Mover el cursor al final del texto
        binding.etConfirmarPassword.setSelection(binding.etConfirmarPassword.text.length)
    }

    private fun registrarUsuario() {
        val email = binding.etEmailRegistro.text.toString().trim()
        val password = binding.etPasswordRegistro.text.toString().trim()
        val confirmPassword = binding.etConfirmarPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmailRegistro.error = "Introduce un email"
            binding.etEmailRegistro.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailRegistro.error = "Introduce un email válido"
            binding.etEmailRegistro.requestFocus()
            return
        }

        if (password.isEmpty() || password.length < 6) {
            binding.etPasswordRegistro.error = "La contraseña debe tener al menos 6 caracteres"
            binding.etPasswordRegistro.requestFocus()
            return
        }

        if (confirmPassword.isEmpty() || confirmPassword != password) {
            binding.etConfirmarPassword.error = "Las contraseñas no coinciden"
            binding.etConfirmarPassword.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        sendEmailVerification(user)
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Error al registrar usuario: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("RegistroActivity", "Error al registrar usuario", task.exception)
                }
            }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Email de verificación enviado a ${user.email}. Por favor verifica tu email antes de hacer login.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e("RegistroActivity", "sendEmailVerification", task.exception)
                    Toast.makeText(
                        this,
                        "Error al enviar el email de verificación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // Always finish the registration activity, whether email was sent successfully or not
                finish()
            }
    }
}
