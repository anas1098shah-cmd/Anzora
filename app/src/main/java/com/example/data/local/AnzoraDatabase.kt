package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.security.MessageDigest

@Database(
    entities = [
        CategoryEntity::class,
        ProductEntity::class,
        CartItemEntity::class,
        CustomerAccountEntity::class,
        OrderEntity::class,
        StoreSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AnzoraDatabase : RoomDatabase() {
    abstract fun anzoraDao(): AnzoraDao

    companion object {
        @Volatile
        private var INSTANCE: AnzoraDatabase? = null

        fun getInstance(context: Context): AnzoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnzoraDatabase::class.java,
                    "anzora_ecommerce.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun sha256Hash(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}
