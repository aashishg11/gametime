package com.aashishgodambe.gametime

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.aashishgodambe.gametime.models.Schedule
import com.aashishgodambe.gametime.models.Team
import com.aashishgodambe.gametime.models.Teams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SportsRepository(private val database: TeamsDatabase) {

    suspend fun getSchedules(team: String) = SportsApi.retrofitService.getSchedules(team)

    suspend fun getTeams(team: String) = SportsApi.retrofitService.getTeams(team)

    suspend fun getTeam(id: String) = SportsApi.retrofitService.getTeam(id)

    suspend fun insert(team: Team) {
        withContext(Dispatchers.IO) {
            database.teamDao.insert(team)
        }
    }

    suspend fun delete(team: Team) {
        withContext(Dispatchers.IO) {
            database.teamDao.delete(team)
        }
    }

    suspend fun getTeamList() = database.teamDao.getTeamsList()

    val teams: LiveData<List<Team>> = database.teamDao.getTeams()

}