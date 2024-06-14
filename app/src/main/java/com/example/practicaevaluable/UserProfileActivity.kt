package com.example.practicaevaluable

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.practicaevaluable.databinding.ActivityHomeBinding
import com.example.practicaevaluable.databinding.ActivityUserProfileBinding

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding // Utiliza View Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root) // Usa binding.root en lugar de R.layout.activity_user_profile

        supportActionBar?.title = getString(R.string.app_name)

        // Configurar la imagen y la información
        binding.imageProfile.setImageResource(R.drawable.marta)
        binding.textName.text = "Marta Fernández Carrión"
        binding.textDescription.text = "Esta aplicación ha sido creada para entregar como proyecto final por Marta Fernández Carrión de 2ºDAM en el I.E.S Al-Ándalus en Almería. Presentado el día 18 de Junio y llevada a cabo por su afición a la lectura de libros"

        // Configurar el botón Volver
        binding.btnVolver.setOnClickListener {
            onBackPressed()
        }
    }
}

