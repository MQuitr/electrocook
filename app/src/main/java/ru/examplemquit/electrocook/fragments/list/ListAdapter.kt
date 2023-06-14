package ru.examplemquit.electrocook.fragments.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.examplemquit.electrocook.R
import ru.examplemquit.electrocook.fragments.recipe.FavoriteFragmentDirections
import ru.examplemquit.electrocook.model.Recipe

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var recipeList = emptyList<Recipe>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_label)
        val shortDescription: TextView = itemView.findViewById(R.id.description_label)
        val rowLayout: View = itemView.findViewById(R.id.rowLayout_main)
        val imageView: ImageView = itemView.findViewById(R.id.imageView_image) // Добавление окна изображения из custom row
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = recipeList[position]

        holder.name.text = currentItem.title
        holder.shortDescription.text = currentItem.shortDescription
        val resourceImageId = when (currentItem.imageResourceId) {
            1 -> R.drawable.american_pancake
            2 -> R.drawable.slice_blin
            3 -> R.drawable.brusketta_s_pomidorami
            4 -> R.drawable.classic_sharlotka
            5 -> R.drawable.oyakodon
            6 -> R.drawable.crem_sup_so_slivkamy
            7 -> R.drawable.kotleety_s_morkovkoy
            8 -> R.drawable.lepeshka_na_moloke
            9 -> R.drawable.onigiri
            10 -> R.drawable.pasta_karbonara_na_slivkah
            11 -> R.drawable.imbirnui_lemonad
            12 -> R.drawable.salat_tcezar
            13 -> R.drawable.shokolat_maffin_s_kakao
            14 -> R.drawable.azu_po_tatarsky
            15 -> R.drawable.solyanka
            16 -> R.drawable.beze
            17 -> R.drawable.crabovo_sirnyi_salat_sharikami
            18 -> R.drawable.praga
            19 -> R.drawable.tonkoe_testo_dlya_pizza
            20 -> R.drawable.salat_s_krevetkami_i_kyunshytom
            else -> R.drawable.ic_launcher_foreground
        }
        holder.imageView.setImageResource(resourceImageId)
        holder.rowLayout.setOnClickListener {
            val action = when (holder.itemView.findNavController().currentDestination?.id) {
                R.id.listFragment -> ListFragmentDirections.actionListFragmentToRecipeFragment(currentItem)
                R.id.favoriteFragment -> FavoriteFragmentDirections.actionFavoriteFragmentToRecipeFragment(currentItem)
                else -> null
            }
            if (action != null) {
                holder.itemView.findNavController().navigate(action)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(recipe: List<Recipe>) {
        this.recipeList = recipe
        notifyDataSetChanged()
    }
}