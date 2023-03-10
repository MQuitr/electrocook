package ru.examplemquit.electrocook.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.examplemquit.electrocook.R
import ru.examplemquit.electrocook.model.Recipe

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var recipeList = emptyList<Recipe>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_label)
        val description: TextView = itemView.findViewById(R.id.description_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = recipeList[position]

        holder.name.text = currentItem.title.toString()
        holder.description.text = currentItem.description.toString()
    }

    fun setData(recipe: List<Recipe>) {
        this.recipeList = recipe
        notifyDataSetChanged()
    }
}