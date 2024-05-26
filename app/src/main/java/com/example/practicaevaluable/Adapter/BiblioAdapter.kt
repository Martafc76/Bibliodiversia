package com.example.practicaevaluable.Adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.practicaevaluable.Models.Book
import com.example.practicaevaluable.R
import com.bumptech.glide.request.target.Target


class BiblioAdapter(mutableListOf: MutableList<Any>) : RecyclerView.Adapter<BiblioAdapter.BookViewHolder>() {
    private val books: MutableList<Book> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_biblio, parent, false)
        return BookViewHolder(view)
    }




    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size



    fun updateData(newBooks: List<Book>) {
        books.clear()
        books.addAll(newBooks)
        notifyDataSetChanged()
    }


    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView = itemView.findViewById<TextView>(R.id.tv_librotitulo)
        private val authorsTextView = itemView.findViewById<TextView>(R.id.tv_autor)
        private val descriptionTextView = itemView.findViewById<TextView>(R.id.tv_descripcion)
        private val thumbnailImageView = itemView.findViewById<ImageView>(R.id.iv_libro)


        fun bind(book: Book) {
            titleTextView.text = book.title
            authorsTextView.text = book.authors?.joinToString(", ") ?: "Unknown Authors"
            descriptionTextView.text = book.description

            Glide.with(itemView.context)
                .load(book.imageLinks?.thumbnail)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        Log.e("Glide", "Error al cargar la imagen: $e")
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        Log.d("Glide", "Imagen cargada correctamente")
                        return false
                    }
                })
                .into(thumbnailImageView)

        }

    }
}