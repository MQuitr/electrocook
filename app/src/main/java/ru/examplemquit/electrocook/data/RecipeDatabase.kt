package ru.examplemquit.electrocook.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.examplemquit.electrocook.model.Recipe

@Database(entities = [Recipe::class], version = 2)
abstract class RecipeDatabase: RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        val migration_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Check if the isFavorite column already exists
                val cursor = database.query("SELECT name FROM sqlite_master WHERE type='table' AND name='recipe_table' AND sql LIKE '%isFavorite%'")
                val columnExists = cursor.moveToFirst()
                cursor.close()
                // If the isFavorite column does not exist, add it
                if (!columnExists) {
                    database.execSQL("ALTER TABLE recipe_table ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
                }
            }
        }

        fun getDatabase(context: Context): RecipeDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "electroCookDatabase"
                ).addMigrations(migration_1_2).createFromAsset(databaseFilePath = "database/electroCookDatabase").build()
                INSTANCE = instance
                return instance
            }
        }
    }
}