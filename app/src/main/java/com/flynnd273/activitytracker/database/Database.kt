package com.flynnd273.activitytracker.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Entity
data class ActivityTask(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "goal") val goal: Long,
    @ColumnInfo(name = "progress") val progress: Long,
    @ColumnInfo(name = "last_start") val lastStart: LocalDateTime?,
)

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activitytask ORDER BY name")
    fun getAll(): Flow<List<ActivityTask>>

    @Update
    suspend fun update(activity: ActivityTask)

    @Insert
    suspend fun insert(activity: ActivityTask)

    @Delete
    suspend fun delete(activity: ActivityTask)
}

@Database(entities = [ActivityTask::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
}
