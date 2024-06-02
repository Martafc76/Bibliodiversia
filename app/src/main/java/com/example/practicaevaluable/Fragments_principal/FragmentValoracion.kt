package com.example.practicaevaluable.Fragments_principal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.practicaevaluable.R
import com.google.android.gms.location.*
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FragmentValoracion : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_valoracion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val editTextTitle: EditText = view.findViewById(R.id.editTextTitle)
        val editTextAuthor: EditText = view.findViewById(R.id.editTextAuthor)
        val editTextOpinion: EditText = view.findViewById(R.id.editTextOpinion)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val buttonSaveRating: Button = view.findViewById(R.id.buttonSaveRating)

        buttonSaveRating.setOnClickListener {
            val title = editTextTitle.text.toString()
            val author = editTextAuthor.text.toString()
            val opinion = editTextOpinion.text.toString()
            val rating = ratingBar.rating

            if (title.isNotEmpty() && author.isNotEmpty() && opinion.isNotEmpty()) {
                val user = auth.currentUser
                if (user != null) {
                    val ratingData = hashMapOf(
                        "userId" to user.uid,
                        "title" to title,
                        "author" to author,
                        "opinion" to opinion,
                        "rating" to rating
                    )

                    db.collection("bookRatings")
                        .add(ratingData)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Valoración guardada", Toast.LENGTH_SHORT).show()
                            // Clear the fields after saving the rating
                            editTextTitle.text.clear()
                            editTextAuthor.text.clear()
                            editTextOpinion.text.clear()
                            ratingBar.rating = 0f
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error al guardar la valoración: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "No se pudo obtener el usuario actual", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
