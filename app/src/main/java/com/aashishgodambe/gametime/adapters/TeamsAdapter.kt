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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aashishgodambe.gametime.R
import com.aashishgodambe.gametime.models.Team
import com.aashishgodambe.gametime.ui.favorite.FavouriteFragment
import com.aashishgodambe.gametime.ui.teamSearch.SearchTeamFragment
import com.squareup.picasso.Picasso

class TeamsAdapter(val clickListener: ClickListener,viewId: String) :
    RecyclerView.Adapter<TeamsAdapter.TeamsViewHolder>() {

    var data = listOf<Team>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var viewIdentifier = viewId


    override fun onBindViewHolder(holder: TeamsViewHolder, position: Int) {
        val item = data[position]
        var imageUrl = item.strTeamBadge
        imageUrl?.let {
            Picasso.get().load(imageUrl)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .into(holder.teamLogo)
        }
        holder.addFav.setOnClickListener {
            clickListener.onAddRemoveFavClick(item)
        }
        holder.teamName.text = item.strTeam
        holder.teamLogo.setOnClickListener {
            clickListener.onTeamClick(item)
        }
        when(viewIdentifier){
            SearchTeamFragment.searchViewIdentifier -> {
                if(item.isFav){
                    holder.addFav.setImageResource(R.drawable.ic_favorite_pink)
                }else{
                    holder.addFav.setImageResource(R.drawable.ic_favorite_grey)
                }
            }
            FavouriteFragment.favViewIdentifier -> holder.addFav.setImageResource(R.drawable.ic_remove)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class TeamsViewHolder(itemView: View):
            RecyclerView.ViewHolder(itemView) {
        val teamLogo: ImageView = itemView.findViewById(R.id.team_image)
        val addFav: ImageView = itemView.findViewById(R.id.iv_add_fav)
        val teamName: TextView = itemView.findViewById(R.id.tv_team_name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.team_item, parent, false)

        return TeamsViewHolder(view)
    }

    interface ClickListener {
        fun onTeamClick(team: Team)
        fun onAddRemoveFavClick(team: Team)
    }

}
