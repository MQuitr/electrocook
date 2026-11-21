
package ru.mquitr.electrocookv2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        InstalledPackageEntity::class,
        RecipeEntity::class,
        IngredientEntity::class,
        CookingStepEntity::class,
        AllergenEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class ElectroCookDatabase : RoomDatabase() {

    abstract fun recipesDao(): RecipesDao

    companion object {

        @Volatile
        private var INSTANCE: ElectroCookDatabase? = null

        fun getInstance(context: Context): ElectroCookDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ElectroCookDatabase::class.java,
                    "electrocook_v2.db"
                )
                    .build()
                    .also { database ->
                        INSTANCE = database
                    }
            }
        }
    }
}
