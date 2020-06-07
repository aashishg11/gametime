/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.aashishgodambe.gametime.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aashishgodambe.gametime.R
import com.aashishgodambe.gametime.models.Schedule
import com.squareup.picasso.Picasso

class SchedulesAdapter(val clickListener: ClickListener) :
    RecyclerView.Adapter<SchedulesAdapter.SchedulesViewHolder>() {

    var data = listOf<Schedule>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: SchedulesViewHolder, position: Int) {
        val item = data[position]
        Log.d("SchedulesAdapter",item.toString())
        var homeLogo = item.homeTeamLogo
        homeLogo?.let {
            Picasso.get().load(homeLogo)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .into(holder.homeTeamLogo)
        }
        var awayLogo = item.awayTeamLogo
        awayLogo?.let {
            Picasso.get().load(awayLogo)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .into(holder.awayTeamLogo)
        }
        if(!item.strVideo.isNullOrBlank()){
            holder.tvLink.setOnClickListener {
                clickListener.onOpenLink(item.strVideo)
            }
        }else{
            holder.tvLink.visibility = View.GONE
        }
        holder.layout.setOnClickListener {
            clickListener.onItemClick()
        }
        holder.tvAwayScore.text = item.intAwayScore
        holder.tvHomeScore.text = item.intHomeScore
        holder.tvHomeName.text = item.strHomeTeam
        holder.tvAwayName.text = item.strAwayTeam
        holder.tvDate.text = item.strDate
        holder.tvLeagueName.text = item.strLeague
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class SchedulesViewHolder(itemView: View):
            RecyclerView.ViewHolder(itemView) {
        val homeTeamLogo: ImageView = itemView.findViewById(R.id.iv_home_team)
        val awayTeamLogo: ImageView = itemView.findViewById(R.id.iv_away_team)
        val tvAwayScore: TextView = itemView.findViewById(R.id.tv_away_score)
        val tvHomeScore: TextView = itemView.findViewById(R.id.tv_home_score)
        val tvHomeName: TextView = itemView.findViewById(R.id.tv_home_team)
        val tvAwayName: TextView = itemView.findViewById(R.id.tv_away_team)
        val tvLeagueName: TextView = itemView.findViewById(R.id.tv_league_name)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvLink: TextView = itemView.findViewById(R.id.tv_watch_highlights)
        val layout: ConstraintLayout = itemView.findViewById(R.id.constraint_layout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchedulesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.schedule_item, parent, false)

        return SchedulesViewHolder(view)
    }

    interface ClickListener {
        fun onOpenLink(link: String)
        fun onItemClick()
    }
}
