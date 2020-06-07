package com.aashishgodambe.gametime.ui.teamSearch

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.aashishgodambe.gametime.SportsApi
import com.aashishgodambe.gametime.SportsRepository
import com.aashishgodambe.gametime.getDatabase
import com.aashishgodambe.gametime.models.Team
import kotlinx.coroutines.*
import java.lang.Exception

enum class SportsApiStatus { LOADING, ERROR, DONE, NOTFOUND }

class TeamsViewmodel(application: Application) : AndroidViewModel(application) {

    private val _status = MutableLiveData<SportsApiStatus>()
    val status: LiveData<SportsApiStatus>
        get() = _status

    private val _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>>
        get() = _teams

    private var viewModelJob = Job()

    fun isFavourite(id: String): Boolean = (currFavs.contains(id))

    private var currFavs = mutableSetOf<String>()

    private val database = getDatabase(application)
    val repository: SportsRepository = SportsRepository(database)

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getTeam(team: String) {
        coroutineScope.launch {
            try {
                _status.value = SportsApiStatus.LOADING
                updateCurrFav()
                val teams = repository.getTeams(team).teams
                if (teams.isNullOrEmpty()){
                    _status.value = SportsApiStatus.NOTFOUND
                }else{
                    updateFav(teams,true)
                }

                _status.value = SportsApiStatus.DONE
            }catch (e: Exception){
                Log.e("TeamsViewmodel",e.toString())
                _status.value = SportsApiStatus.ERROR
            }
        }
    }

    fun updateCurrFav(){
        coroutineScope.launch {
            val teams = repository.getTeamList()
            teams?.let {
                currFavs.clear()
                for (item in teams){
                    currFavs.add(item.idTeam)
                }
            }
        }
    }

    val favTeams = repository.teams

    fun insertTeam(team: Team){
        currFavs.add(team.idTeam)
        _teams.value?.let {
            updateFav(it,true)
        }
        coroutineScope.launch {
            repository.insert(team)
        }
    }

    fun removeTeam(team: Team){
        currFavs.remove(team.idTeam)
        coroutineScope.launch {
            repository.delete(team)
        }
    }

    fun updateFav(teams: List<Team>,isFav: Boolean){
        teams.let {
            for (item in teams){
                if(item.idTeam in currFavs){
                    item.isFav = isFav
                }
            }
            _teams.postValue(teams)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TeamsViewmodel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TeamsViewmodel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}