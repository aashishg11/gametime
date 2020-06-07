package com.aashishgodambe.gametime.ui.schedules

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.aashishgodambe.gametime.SportsApi
import com.aashishgodambe.gametime.SportsRepository
import com.aashishgodambe.gametime.getDatabase
import com.aashishgodambe.gametime.models.Schedule
import com.aashishgodambe.gametime.models.Team
import com.aashishgodambe.gametime.ui.teamSearch.SportsApiStatus
import com.aashishgodambe.gametime.ui.teamSearch.TeamsViewmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class SchedulesViewmodel(application: Application,team: String) : AndroidViewModel(application) {


    private val database = getDatabase(application)
    val repository: SportsRepository = SportsRepository(database)

    private val _results = MutableLiveData<List<Schedule>>()
    val results: LiveData<List<Schedule>>
        get() = _results

    private val _status = MutableLiveData<SportsApiStatus>()
    val status: LiveData<SportsApiStatus>
        get() = _status

    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getTeam(team)
    }

    fun getTeam(team: String) {
        coroutineScope.launch {
            try{
            _status.value = SportsApiStatus.LOADING
            var results = repository.getSchedules(team).results
            for (result in results){
                val homeId = result.idHomeTeam
                homeId?.let{
                    val homeTeam = repository.getTeam(homeId).teams?.get(0)
                    result.homeTeamLogo = homeTeam?.strTeamBadge
                }
                val awayId = result.idAwayTeam
                awayId?.let{
                    val awayTeam = repository.getTeam(awayId).teams?.get(0)
                    result.awayTeamLogo = awayTeam?.strTeamBadge
                }
            }
            _results.value = results
            _status.value = SportsApiStatus.DONE
            }catch (e: Exception){
                Log.e("ScheduleViewmodel",e.toString())
                _status.value = SportsApiStatus.ERROR
            }

        }
    }
    class Factory(val app: Application,val team: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SchedulesViewmodel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SchedulesViewmodel(app,team) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}