package com.example.practicaevaluable.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practicaevaluable.Models.BookItem2
import com.example.practicaevaluable.R
import kotlin.reflect.KFunction1


class AdapterListas(
    private var books: List<BookItem2>,
    private val onDeleteBook: (BookItem2) -> Unit,
    private val showRatingDialog: (BookItem2) -> Unit
) : RecyclerView.Adapter<AdapterListas.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int {
        return books.size
    }

    fun updateBooks(newBooks: List<BookItem2>) {
        books = newBooks
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val textViewAuthor: TextView = itemView.findViewById(R.id.textViewAuthor)
        private val textViewOpinion: TextView = itemView.findViewById(R.id.textViewOpinion)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)

        init {
            buttonDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val book = books[position]
                    onDeleteBook(book)
                }
            }
        }

        fun bind(book: BookItem2) {
            textViewTitle.text = book.title
            textViewAuthor.text = book.author
            textViewOpinion.text = book.opinion
            ratingBar.rating = book.rating
        }
    }

}
