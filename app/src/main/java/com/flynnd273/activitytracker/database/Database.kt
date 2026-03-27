package com.flynnd273.activitytracker.database

import android.content.Context
import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.room.*
import com.flynnd273.activitytracker.utils.Day
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.time.LocalDateTime

@Parcelize
data class SerializedActivity(val uid: Int, val name: String) : Parcelable

@Entity
data class ActivityTask(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "goal") val goal: Int,
    @ColumnInfo(name = "progress") val progress: Int = 0,
    @ColumnInfo(name = "last_start") val lastStart: LocalDateTime? = null,
    @ColumnInfo(name = "color") val color: Color? = null,
    @ColumnInfo(name = "days") val days: List<Day> = Day.entries,
) {
    fun isCompleted(now: LocalDateTime = LocalDateTime.now()): Boolean {
        return realProgress(now) >= goal
    }

    fun realProgress(now: LocalDateTime = LocalDateTime.now()): Int {
        if (lastStart != null) {
            return progress + Duration.between(lastStart, now).toSeconds().toInt()
        }
        return progress
    }

    fun toCompleted(): ActivityTask {
        return copy(progress = goal, lastStart = null)
    }

    fun toReset(): ActivityTask {
        return copy(progress = 0, lastStart = null)
    }
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activitytask ORDER BY name")
    fun getAll(): Flow<List<ActivityTask>>

    @Query("SELECT * FROM activitytask ORDER BY name")
    suspend fun getAllOnce(): List<ActivityTask>

    @Query("SELECT * FROM activitytask WHERE uid = :id")
    suspend fun get(id: Int): ActivityTask

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

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
