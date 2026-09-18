package com.vexono.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.vexono.app.data.local.dao.EventDao
import com.vexono.app.data.local.dao.OccasionDao
import com.vexono.app.data.local.dao.TaskDao
import com.vexono.app.data.local.entity.EventEntity
import com.vexono.app.data.local.entity.OccasionEntity
import com.vexono.app.data.local.entity.TaskEntity
import com.vexono.app.domain.model.OccasionCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [EventEntity::class, TaskEntity::class, OccasionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class VexonoDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun taskDao(): TaskDao
    abstract fun occasionDao(): OccasionDao

    companion object {
        @Volatile
        private var INSTANCE: VexonoDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): VexonoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VexonoDatabase::class.java,
                    "vexono_database"
                )
                    .addCallback(DatabaseCallback(context.applicationContext, scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context,
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                scope.launch(Dispatchers.IO) {
                    INSTANCE?.let { database ->
                        populateOccasionsFromAssets(context, database.occasionDao())
                    }
                }
            }
        }

        suspend fun populateOccasionsFromAssets(context: Context, occasionDao: OccasionDao) {
            try {
                if (occasionDao.getOccasionsCount() > 0) return

                val jsonString = context.assets.open("occasions_data.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val root = gson.fromJson(jsonString, JsonObject::class.java)

                val entities = mutableListOf<OccasionEntity>()

                // 1. Solar Fixed Occasions
                if (root.has("solar_occasions")) {
                    val solarList = root.getAsJsonArray("solar_occasions")
                    for (element in solarList) {
                        val obj = element.asJsonObject
                        val month = obj.get("month").asInt
                        val day = obj.get("day").asInt
                        val title = obj.get("title").asString
                        val isHoliday = obj.get("is_holiday").asBoolean
                        val categoryStr = obj.get("category").asString.uppercase()
                        val category = when (categoryStr) {
                            "OFFICIAL" -> OccasionCategory.OFFICIAL
                            "RELIGIOUS" -> OccasionCategory.RELIGIOUS
                            "INTERNATIONAL" -> OccasionCategory.INTERNATIONAL
                            else -> OccasionCategory.NATIONAL
                        }
                        entities.add(
                            OccasionEntity(
                                id = "solar_${month}_${day}_${title.hashCode()}",
                                title = title,
                                isHoliday = isHoliday,
                                category = category.name,
                                month = month,
                                day = day,
                                year = null
                            )
                        )
                    }
                }

                // 2. Yearly Mapped Religious / Variable Holidays (1394 - 1406)
                if (root.has("yearly_mapped_religious_holidays")) {
                    val yearsObj = root.getAsJsonObject("yearly_mapped_religious_holidays")
                    for (yearKey in yearsObj.keySet()) {
                        val year = yearKey.toIntOrNull() ?: continue
                        val holidaysArray = yearsObj.getAsJsonArray(yearKey)
                        for (element in holidaysArray) {
                            val obj = element.asJsonObject
                            val month = obj.get("month").asInt
                            val day = obj.get("day").asInt
                            val title = obj.get("title").asString
                            val isHoliday = obj.get("is_holiday").asBoolean
                            entities.add(
                                OccasionEntity(
                                    id = "religious_${year}_${month}_${day}_${title.hashCode()}",
                                    title = title,
                                    isHoliday = isHoliday,
                                    category = OccasionCategory.RELIGIOUS.name,
                                    month = month,
                                    day = day,
                                    year = year
                                )
                            )
                        }
                    }
                }

                if (entities.isNotEmpty()) {
                    occasionDao.insertAll(entities)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
