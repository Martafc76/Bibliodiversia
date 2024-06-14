package com.example.practicaevaluable.Fragments_principal

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practicaevaluable.Adapter.AdapterListas
import com.example.practicaevaluable.Models.BookItem2
import com.example.practicaevaluable.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class FragmentValoracion : Fragment() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextAuthor: EditText
    private lateinit var editTextOpinion: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var buttonAddBook: Button
    private lateinit var recyclerViewBooks: RecyclerView
    private lateinit var textViewNoBooks: TextView
    private lateinit var bookAdapter: AdapterListas
    private var books = mutableListOf<BookItem2>()

    private val database = FirebaseDatabase.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_valoracion, container, false)

        // Initialize views
        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextAuthor = view.findViewById(R.id.editTextAuthor)
        editTextOpinion = view.findViewById(R.id.editTextOpinion)
        ratingBar = view.findViewById(R.id.ratingBar)
        buttonAddBook = view.findViewById(R.id.buttonAddBook)
        recyclerViewBooks = view.findViewById(R.id.recyclerViewBooks)
        textViewNoBooks = view.findViewById(R.id.textViewNoBooks)

        // Setup RecyclerView
        bookAdapter = AdapterListas(
            books,
            this::onDeleteBook,
            this::showRatingDialog
        )
        recyclerViewBooks.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewBooks.adapter = bookAdapter

        // Setup button click listener
        buttonAddBook.setOnClickListener {
            addBook()
        }

        // Load books from Firebase
        loadBooks()

        return view
    }

    private fun loadBooks() {
        userId?.let { uid ->
            val booksRef = database.reference.child("users").child(uid).child("books")
            booksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        books.clear() // Limpiar la lista antes de añadir libros
                        for (snapshot in dataSnapshot.children) {
                            val book = snapshot.getValue(BookItem2::class.java)
                            book?.let { books.add(it) }
                        }
                        bookAdapter.updateBooks(books) // Actualizar el adaptador después de cargar libros
                        updateUI()
                    } else {
                        showNoBooksMessage()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showNoBooksMessage()
                }
            })
        }
    }



    private fun addBook() {
        val title = editTextTitle.text.toString().trim()
        val author = editTextAuthor.text.toString().trim()
        val opinion = editTextOpinion.text.toString().trim()
        val rating = ratingBar.rating

        if (title.isNotEmpty() && author.isNotEmpty()) {
            val newBook = BookItem2(
                UUID.randomUUID().toString(),
                title,
                author,
                opinion,
                rating
            )
            saveBookToFirebase(newBook)

            // Limpiar campos de entrada después de agregar el libro
            editTextTitle.text.clear()
            editTextAuthor.text.clear()
            editTextOpinion.text.clear()
            ratingBar.rating = 0f
        } else {
            // Mostrar mensaje de campos incompletos
            Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }



    private fun saveBookToFirebase(book: BookItem2) {
        userId?.let { uid ->
            val booksRef = database.reference.child("users").child(uid).child("books")
            val bookId = booksRef.push().key ?: ""

            val bookData = hashMapOf(
                "id" to bookId,
                "title" to book.title,
                "author" to book.author,
                "opinion" to book.opinion,
                "rating" to book.rating
            )

            booksRef.child(bookId).setValue(bookData)
                .addOnSuccessListener {
                    books.add(book)
                    bookAdapter.updateBooks(books)
                    updateUI()
                }
                .addOnFailureListener { e ->
                    // Handle error
                }
        }
    }

    private fun onDeleteBook(book: BookItem2) {
        userId?.let { uid ->
            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Libro")
                .setMessage("¿Estás seguro de que quieres eliminar este libro?")
                .setPositiveButton("Eliminar") { dialog, which ->
                    deleteBookFromFirebase(book)
                }
                .setNegativeButton("Cancelar", null)
                .create()

            dialogBuilder.show()
        }
    }

    private fun deleteBookFromFirebase(book: BookItem2) {
        userId?.let { uid ->
            val booksRef = database.reference.child("users").child(uid).child("books").child(book.id)
            booksRef.removeValue()
                .addOnSuccessListener {
                    books.remove(book)
                    bookAdapter.updateBooks(books)
                    updateUI()
                    Toast.makeText(requireContext(), "Libro eliminado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al eliminar libro", Toast.LENGTH_SHORT).show()
                }
        }
    }



    private fun updateUI() {
        if (books.isEmpty()) {
            showNoBooksMessage()
        } else {
            textViewNoBooks.visibility = View.GONE
        }
    }

    private fun showNoBooksMessage() {
        textViewNoBooks.visibility = View.VISIBLE
    }

    private fun showRatingDialog(book: BookItem2) {
        // Implement rating dialog if needed
    }
}
