package com.aashishgodambe.gametime.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Team(
        @PrimaryKey
        val idTeam: String,
        val strTeam: String?,
        val strTeamShort: String?,
        val strWebsite: String?,
        val strTwitter: String?,
        val strTeamBadge: String?
        ) : Parcelable{
        @Transient
        var isFav: Boolean = false
}
