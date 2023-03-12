package ru.examplemquit.electrocook.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "recipe_table")
data class Recipe (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val description: String,
    val ingredient: String,
    val steps: String,
    val pathImage: String
): Parcelable
