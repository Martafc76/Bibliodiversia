package com.example.practicaevaluable.Adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practicaevaluable.Models.PdfLibro
import com.example.practicaevaluable.databinding.ItemPdfBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class AdapterPdf(private val m_context: Context, private var libros: ArrayList<PdfLibro>) :
    RecyclerView.Adapter<AdapterPdf.HolderLibro>() {

    private val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderLibro {
        val binding = ItemPdfBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderLibro(binding)
    }

    override fun getItemCount(): Int {
        return libros.size
    }

    override fun onBindViewHolder(holder: HolderLibro, position: Int) {
        val libro = libros[position]
        holder.binding.tvPdfTitulo.text = libro.titulo
        holder.binding.tvPdfDescripcion.text = libro.descripcion

        // Cargar portada del PDF
        cargarPortadaPdf(libro.pdfUrl, holder.binding.ivPortadaPdf)

        // Configurar clic para ver el PDF completo
        holder.binding.btnVerPdf.setOnClickListener {
            abrirPdfCompleto(libro.pdfUrl)
        }

        // Configurar clic para borrar el PDF
        holder.binding.btnBorrarPdf.setOnClickListener {
            borrarPdfEnStorage(libro.pdfUrl)
            // También puedes eliminar el registro del PDF en la base de datos si es necesario
            eliminarRegistroPdfEnFirebase(libro.uid) // Suponiendo que "uid" es el ID único del usuario
        }
    }

    private fun cargarPortadaPdf(pdfUrl: String, imageView: ImageView) {
        val gsReference = storageRef.child(pdfUrl)
        gsReference.getBytes(1024 * 1024) // Tamaño máximo de la imagen en bytes (1MB)
            .addOnSuccessListener { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imageView.setImageBitmap(bitmap)
            }
            .addOnFailureListener { exception ->
                // Manejar errores
            }
    }

    private fun abrirPdfCompleto(pdfUrl: String) {
        val pdfUri = Uri.parse(pdfUrl)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(pdfUri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        try {
            m_context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // No hay aplicación de visor de PDF instalada, manejar el error según sea necesario
        }
    }

    private fun borrarPdfEnStorage(pdfUrl: String) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        storageReference.delete()
            .addOnSuccessListener {
                // Borrado exitoso
            }
            .addOnFailureListener { exception ->
                // Manejar errores
            }
    }

    private fun eliminarRegistroPdfEnFirebase(uid: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Libros").child(uid)
        ref.removeValue()
            .addOnSuccessListener {
                // Eliminación exitosa
            }
            .addOnFailureListener { exception ->
                // Manejar errores
            }
    }

    fun updateLibros(libros: List<PdfLibro>) {
        this.libros.clear()
        this.libros.addAll(libros)
        notifyDataSetChanged()
    }


    inner class HolderLibro(val binding: ItemPdfBinding) : RecyclerView.ViewHolder(binding.root) {
        var tituloTv: TextView = binding.tvPdfTitulo
        var descripcionTv: TextView = binding.tvPdfDescripcion
        var portadaImageView: ImageView = binding.ivPortadaPdf
        var verButton: Button = binding.btnVerPdf
        var borrarButton: Button = binding.btnBorrarPdf
    }
}
