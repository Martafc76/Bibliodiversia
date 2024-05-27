package com.example.practicaevaluable.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.practicaevaluable.DetalleCategoria
import com.example.practicaevaluable.Models.ModeloCategoria
import com.example.practicaevaluable.databinding.ItemCategoriaBinding
import com.google.firebase.database.FirebaseDatabase
import com.example.practicaevaluable.Fragments_principal.FragmentCategorias


class AdapterCategoria(
    private var mContext: Context,
    var categoriaArrayList: ArrayList<ModeloCategoria>,
    private val recyclerView: RecyclerView,
    private val fragmentInstance: FragmentCategorias
) : RecyclerView.Adapter<AdapterCategoria.HolderCategoria>(), Filterable {

    private lateinit var binding: ItemCategoriaBinding
    private val originalList = ArrayList(categoriaArrayList)
    var filteredList = ArrayList<ModeloCategoria>()

    init {
        filteredList.addAll(originalList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
        binding = ItemCategoriaBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HolderCategoria(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        val modelo = filteredList[position]
        holder.categoriaTv.text = modelo.categoria

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, DetalleCategoria::class.java)
            fragmentInstance.mostrarLibrosCategoria(modelo)
            intent.putExtra("categoria", modelo.categoria)
            mContext.startActivity(intent)
        }

        holder.eliminarCat.setOnClickListener {
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Eliminar Categoria")
                .setMessage("¿Seguro que quiere eliminar esta categoria?")
                .setPositiveButton("Confirmar") { a, d ->
                    eliminarCategoria(modelo, holder)
                }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            builder.show()
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    private fun eliminarCategoria(modelo: ModeloCategoria, holder: HolderCategoria) {
        val id = modelo.id
        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(mContext, "Categoría eliminada", Toast.LENGTH_SHORT).show()
                categoriaArrayList.remove(modelo)
                notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(mContext, "No se pudo eliminar la categoría", Toast.LENGTH_SHORT).show()
            }
    }

    inner class HolderCategoria(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoriaTv: TextView = binding.itemNombreCat
        var eliminarCat: ImageButton = binding.eliminarCat
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                filteredList = if (charString.isEmpty()) {
                    originalList
                } else {
                    val filtered = ArrayList<ModeloCategoria>()
                    originalList
                        .filter {
                            it.categoria.contains(charString, true)
                        }
                        .forEach {
                            filtered.add(it)
                        }
                    filtered
                }
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = if (results?.values == null) ArrayList() else results.values as ArrayList<ModeloCategoria>
                notifyDataSetChanged()
            }
        }
    }
}
