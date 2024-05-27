package com.example.practicaevaluable.Adapter

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.practicaevaluable.Models.PdfLibro
import com.example.practicaevaluable.databinding.ItemPdfBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


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
            mostrarDialogoConfirmacion(libro, position)
        }
    }

    private fun mostrarDialogoConfirmacion(libro: PdfLibro, position: Int) {
        AlertDialog.Builder(m_context)
            .setTitle("Confirmación")
            .setMessage("¿Seguro que quieres borrar este PDF?")
            .setPositiveButton("Sí") { dialog, which ->
                borrarPdf(libro, position)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun borrarPdf(libro: PdfLibro, position: Int) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(libro.pdfUrl)
        storageReference.delete()
            .addOnSuccessListener {
                // Borrado exitoso en Storage, ahora eliminar en Realtime Database
                val ref = FirebaseDatabase.getInstance().getReference("Libros").child(libro.id) // Asegúrate de usar el ID correcto del libro
                ref.removeValue()
                    .addOnSuccessListener {
                        // Eliminación exitosa en Realtime Database
                        libros.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(m_context, "PDF borrado correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        // Manejar errores al eliminar en Realtime Database
                        Toast.makeText(m_context, "Error al borrar PDF en la base de datos: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                // Manejar errores al borrar en Storage
                Toast.makeText(m_context, "Error al borrar PDF en el almacenamiento: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarPortadaPdf(pdfUrl: String, imageView: ImageView) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        val localFile = File.createTempFile("tempPdf", "pdf")

        storageReference.getFile(localFile).addOnSuccessListener {
            val fileDescriptor = ParcelFileDescriptor.open(localFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)

            val currentPage = pdfRenderer.openPage(0)
            val bitmap = Bitmap.createBitmap(currentPage.width, currentPage.height, Bitmap.Config.ARGB_8888)
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            imageView.setImageBitmap(bitmap)

            currentPage.close()
            pdfRenderer.close()
        }.addOnFailureListener {
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
