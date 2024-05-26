package com.example.practicaevaluable


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.practicaevaluable.Fragments_principal.fragment_bibliotecas
import com.example.practicaevaluable.Fragments_principal.fragment_cuenta
import com.example.practicaevaluable.databinding.ActivityHomeBinding
import com.example.practicaevaluable.Fragments_principal.fragment_categorias
import com.example.practicaevaluable.Fragments_principal.fragment_mapa
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth= Firebase.auth
        //pintarEmail()
        setListeners()

    }


    private fun setListeners() {

        verLibros()
        binding.navegationView.setOnNavigationItemSelectedListener{ item ->
            when(item.itemId){
                R.id.Menu_libros ->{
                    verLibros()
                    true
                }
                R.id.Menu_mapa ->{
                    verMapa()
                    true
                }
                R.id.Menu_cuenta->{
                    verCuenta()
                    true
                }
                R.id.Menu_biblioteca->{
                    verBiblio()
                    true
                }
                else-> {
                    false
                }
            }
        }
    }

    private fun verBiblio() {
        val nombre_titulo = "Biblioteca"
        binding.tvTitulo.text=nombre_titulo

        val fragment = fragment_bibliotecas()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsPrincipal.id, fragment, "Fragment mi biblioteca")
        fragmentTransaction.commit()
    }

    private fun verCuenta(){
        val nombre_titulo = "Mi cuenta"
        binding.tvTitulo.text=nombre_titulo

        val fragment = fragment_cuenta()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsPrincipal.id, fragment, "Fragment mi cuenta")
        fragmentTransaction.commit()

    }

    private fun verMapa(){
        val nombre_titulo = "Mapa"
        binding.tvTitulo.text=nombre_titulo

        val fragment = fragment_mapa()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsPrincipal.id, fragment, "Fragment mapa")
        fragmentTransaction.commit()

    }

    private fun verLibros(){
        val nombre_titulo = "Categorias"
        binding.tvTitulo.text=nombre_titulo

        val fragment = fragment_categorias()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragmentsPrincipal.id, fragment, "Fragment libro")
        fragmentTransaction.commit()
    }

}