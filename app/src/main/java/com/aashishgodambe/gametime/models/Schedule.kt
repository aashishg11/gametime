package com.aashishgodambe.gametime.models

import com.squareup.moshi.Json

data class Schedule(
        val idEvent: String?,
        val strLeague: String?,
        val strHomeTeam: String?,
        val strAwayTeam: String?,
        val intHomeScore: String?,
        val intAwayScore: String?,
        @Json(name = "dateEvent")
        val strDate: String?,
        val strVideo: String?,
        val intRound: String?,
        val idHomeTeam: String?,
        val idAwayTeam: String?,
        var homeTeamLogo: String? = "",
        var awayTeamLogo: String? = ""
        )
