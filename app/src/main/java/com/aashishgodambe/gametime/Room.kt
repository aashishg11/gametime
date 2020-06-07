package com.aashishgodambe.gametime

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.aashishgodambe.gametime.models.Team

@Dao
interface TeamsDao {
    @Query("select * from team")
    fun getTeams(): LiveData<List<Team>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: Team)

    @Delete
    suspend fun delete(team: Team)

    @Query("select * from team")
    suspend fun getTeamsList(): List<Team>
}

@Database(entities = [Team::class], version = 1)
abstract class TeamsDatabase : RoomDatabase() {
    abstract val teamDao: TeamsDao
}

private lateinit var INSTANCE: TeamsDatabase

fun getDatabase(context: Context): TeamsDatabase {
    synchronized(TeamsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                TeamsDatabase::class.java,
                    "teams").build()
        }
    }
    return INSTANCE
}
