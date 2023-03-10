package ru.examplemquit.electrocook.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_table")
data class Recipe (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val description: String,
    val ingredient: String,
    val steps: String,
    val pathImage: String
    )
