package com.example.practicaevaluable.Fragments_principal

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practicaevaluable.Adapter.AdapterListas
import com.example.practicaevaluable.Models.BookItem2
import com.example.practicaevaluable.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class FragmentValoracion : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var editTextTitle: EditText
    private lateinit var editTextAuthor: EditText
    private lateinit var buttonAddBook: Button
    private lateinit var recyclerViewUnreadBooks: RecyclerView
    private lateinit var recyclerViewReadBooks: RecyclerView
    private lateinit var unreadBooksAdapter: AdapterListas
    private lateinit var readBooksAdapter: AdapterListas

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_valoracion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextAuthor = view.findViewById(R.id.editTextAuthor)
        buttonAddBook = view.findViewById(R.id.buttonAddBook)
        recyclerViewUnreadBooks = view.findViewById(R.id.recyclerViewUnreadBooks)
        recyclerViewReadBooks = view.findViewById(R.id.recyclerViewReadBooks)

        buttonAddBook.setOnClickListener {
            addBook()
        }

        setupRecyclerViews()
        observeUnreadBooks()
        observeReadBooks()
    }

    private fun setupRecyclerViews() {
        recyclerViewUnreadBooks.layoutManager = LinearLayoutManager(context)
        unreadBooksAdapter = AdapterListas(
            emptyList(),
            ::onBookChecked,
            this::deleteBookFromUnread,
            ::showRatingDialog,
            requireActivity().supportFragmentManager,
            isReadList = false // Lista de libros no leídos
        )
        recyclerViewUnreadBooks.adapter = unreadBooksAdapter

        recyclerViewReadBooks.layoutManager = LinearLayoutManager(context)
        readBooksAdapter = AdapterListas(
            emptyList(),
            ::onBookChecked,
            this::deleteBookFromRead,
            ::showRatingDialog,
            requireActivity().supportFragmentManager,
            isReadList = true // Lista de libros leídos
        )
        recyclerViewReadBooks.adapter = readBooksAdapter
    }

    private fun observeUnreadBooks() {
        val user = auth.currentUser ?: return
        db.collection("unreadBooks").document(user.uid).collection("books")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar libros no leídos: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val books = snapshot.documents.mapNotNull { it.toObject(BookItem2::class.java) }
                    unreadBooksAdapter.updateBooks(books)
                }
            }
    }

    private fun observeReadBooks() {
        val user = auth.currentUser ?: return
        db.collection("readBooks").document(user.uid).collection("books")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(
                        requireContext(),
                        "Error al cargar libros leídos: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val books = snapshot.documents.mapNotNull { it.toObject(BookItem2::class.java) }
                    readBooksAdapter.updateBooks(books)
                }
            }
    }

    private fun addBook() {
        val title = editTextTitle.text.toString()
        val author = editTextAuthor.text.toString()

        if (title.isNotBlank() && author.isNotBlank()) {
            val user = auth.currentUser
            if (user != null) {
                val book = BookItem2(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    author = author,
                    read = false // Nuevo libro se agrega como no leído
                )
                db.collection("unreadBooks").document(user.uid).collection("books").document(book.id)
                    .set(book)
                    .addOnSuccessListener {
                        editTextTitle.text.clear()
                        editTextAuthor.text.clear()
                        Toast.makeText(requireContext(), "Libro agregado correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireContext(),
                            "Error al agregar el libro: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(requireContext(), "No se pudo obtener el usuario actual", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onBookChecked(book: BookItem2, isRead: Boolean) {
        if (isRead) {
            // Mostrar el diálogo para agregar opinión y valoración
            showRatingDialog(book)
        } else {
            // Manejar el caso de desmarcar como leído si es necesario
            val user = auth.currentUser ?: return
            book.read = false
            db.collection("unreadBooks").document(user.uid).collection("books").document(book.id)
                .set(book)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Libro movido a no leídos", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al mover el libro: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun deleteBookFromUnread(book: BookItem2) {
        val user = auth.currentUser ?: return

        db.collection("unreadBooks").document(user.uid).collection("books").document(book.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Libro eliminado de no leídos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al eliminar libro de no leídos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteBookFromRead(book: BookItem2) {
        val user = auth.currentUser ?: return

        db.collection("readBooks").document(user.uid).collection("books").document(book.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Libro eliminado de leídos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al eliminar libro de leídos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showRatingDialog(book: BookItem2) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_rate_book, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val editTextOpinion = dialogView.findViewById<EditText>(R.id.editTextOpinion)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        val buttonSaveRating = dialogView.findViewById<Button>(R.id.buttonSaveRating)

        buttonSaveRating.setOnClickListener {
            val opinion = editTextOpinion.text.toString()
            val rating = ratingBar.rating

            if (opinion.isNotBlank() && rating > 0) {
                saveRating(book, opinion, rating)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Por favor, agrega una opinión y una valoración", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun saveRating(book: BookItem2, opinion: String, rating: Float) {
        val user = auth.currentUser ?: return

        book.read = true
        book.opinion = opinion
        book.rating = rating

        // Eliminar el libro de la colección de no leídos
        db.collection("unreadBooks").document(user.uid).collection("books").document(book.id)
            .delete()
            .addOnSuccessListener {
                // Agregar el libro a la colección de leídos con la nueva información
                db.collection("readBooks").document(user.uid).collection("books").document(book.id)
                    .set(book)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Libro valorado y movido a leídos", Toast.LENGTH_SHORT).show()

                        // Actualizar la vista de libros leídos
                        observeReadBooks()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error al mover el libro a leídos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al eliminar libro de no leídos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}


