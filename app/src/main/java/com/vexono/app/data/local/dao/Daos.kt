package com.vexono.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vexono.app.data.local.entity.EventEntity
import com.vexono.app.data.local.entity.OccasionEntity
import com.vexono.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY jalaliYear ASC, jalaliMonth ASC, jalaliDay ASC, hour ASC, minute ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE jalaliYear = :year AND jalaliMonth = :month AND jalaliDay = :day ORDER BY hour ASC, minute ASC")
    fun getEventsForDay(year: Int, month: Int, day: Int): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE (jalaliYear = :year AND jalaliMonth = :month) OR recurrence != 'NONE' ORDER BY jalaliDay ASC, hour ASC, minute ASC")
    fun getEventsForMonth(year: Int, month: Int): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: Long)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE jalaliYear = :year AND jalaliMonth = :month AND jalaliDay = :day ORDER BY isCompleted ASC, createdAt DESC")
    fun getTasksForDay(year: Int, month: Int, day: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE jalaliYear = :year AND jalaliMonth = :month")
    fun getTasksForMonth(year: Int, month: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)
}

@Dao
interface OccasionDao {
    @Query("SELECT * FROM occasions WHERE (year IS NULL AND month = :month AND day = :day) OR (year = :year AND month = :month AND day = :day)")
    fun getOccasionsForDay(year: Int, month: Int, day: Int): Flow<List<OccasionEntity>>

    @Query("SELECT * FROM occasions WHERE (year IS NULL AND month = :month) OR (year = :year AND month = :month)")
    fun getOccasionsForMonth(year: Int, month: Int): Flow<List<OccasionEntity>>

    @Query("SELECT * FROM occasions WHERE year IS NULL OR year = :year ORDER BY month ASC, day ASC")
    fun getOccasionsForYear(year: Int): Flow<List<OccasionEntity>>

    @Query("SELECT * FROM occasions WHERE title LIKE '%' || :query || '%' ORDER BY month ASC, day ASC")
    fun searchOccasions(query: String): Flow<List<OccasionEntity>>

    @Query("SELECT COUNT(*) FROM occasions")
    suspend fun getOccasionsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(occasions: List<OccasionEntity>)
}
