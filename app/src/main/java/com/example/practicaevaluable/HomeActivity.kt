package com.example.practicaevaluable


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.practicaevaluable.Fragments_principal.FragmentBibliotecas
import com.example.practicaevaluable.Fragments_principal.FragmentCuenta
import com.example.practicaevaluable.databinding.ActivityHomeBinding
import com.example.practicaevaluable.Fragments_principal.FragmentCategorias
import com.example.practicaevaluable.Fragments_principal.FragmentValoracion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)

        // Verifica si hay un usuario autenticado al iniciar la actividad
        val currentUser = auth.currentUser
        if (currentUser == null || !sharedPreferences.getBoolean("is_logged_in", false)) {
            // Si no hay usuario autenticado, redirige a AuthActivity
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }


        setListeners()

        // Muestra el fragmento de libros por defecto al iniciar la actividad
        if (savedInstanceState == null) {
            verLibros()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Menu_perfil -> {
                openUserProfile()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun openUserProfile() {
        try {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al abrir el perfil de usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setListeners() {
        // Configura la vista principal (toolbar, fragments, bottom navigation, etc.)
        // Aquí deberías inicializar tus vistas como la toolbar y el BottomNavigationView
        // Asegúrate de configurar el BottomNavigationView adecuadamente
        binding.navegationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Menu_libros -> {
                    verLibros()
                    true
                }
                R.id.Menu_valoraciones -> {
                    verValoracion()
                    true
                }
                R.id.Menu_biblioteca -> {
                    verBiblio()
                    true
                }
                R.id.Menu_cuenta -> {
                    verCuenta()
                    true
                }
                R.id.Menu_perfil -> {
                    openUserProfile()
                    true
                }
                else -> false
            }
        }
    }

    private fun verBiblio() {
        val nombre_titulo = "Biblioteca"
        binding.tvTitulo.text = nombre_titulo

        val fragment = FragmentBibliotecas()
        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentsPrincipal.id, fragment, "Fragment mi biblioteca")
            .commit()
    }

    private fun verCuenta() {
        val nombre_titulo = "Mi cuenta"
        binding.tvTitulo.text = nombre_titulo

        val fragment = FragmentCuenta()
        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentsPrincipal.id, fragment, "Fragment mi cuenta")
            .commit()
    }

    private fun verValoracion() {
        val nombre_titulo = "Valoraciones de libros"
        binding.tvTitulo.text = nombre_titulo

        val fragment = FragmentValoracion()
        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentsPrincipal.id, fragment, "Fragment valoraciones")
            .commit()
    }

    private fun verLibros() {
        val nombre_titulo = "Categorias"
        binding.tvTitulo.text = nombre_titulo

        val fragment = FragmentCategorias()
        supportFragmentManager.beginTransaction()
            .replace(binding.FragmentsPrincipal.id, fragment, "Fragment libro")
            .commit()
    }
}
