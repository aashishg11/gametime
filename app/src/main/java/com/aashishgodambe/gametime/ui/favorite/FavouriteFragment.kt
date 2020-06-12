package com.aashishgodambe.gametime.ui.favorite


import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aashishgodambe.gametime.BaseFragment
import com.aashishgodambe.gametime.R
import com.aashishgodambe.gametime.adapters.TeamsAdapter
import com.aashishgodambe.gametime.databinding.FragmentFavouriteBinding
import com.aashishgodambe.gametime.models.Team
import com.aashishgodambe.gametime.ui.teamSearch.SportsApiStatus
import com.aashishgodambe.gametime.ui.teamSearch.TeamsViewmodel

/**
 * A simple [Fragment] subclass.
 */
class FavouriteFragment : BaseFragment(),TeamsAdapter.ClickListener {

    private lateinit var teamAdapter: TeamsAdapter
    private lateinit var dialogBuilder: AlertDialog.Builder

    private val viewModel: TeamsViewmodel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this,
            TeamsViewmodel.Factory(activity.application)
        )
            .get(TeamsViewmodel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogBuilder = AlertDialog.Builder(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentFavouriteBinding =  DataBindingUtil.inflate(
                inflater,
            R.layout.fragment_favourite,
        container,
        false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.setLifecycleOwner(viewLifecycleOwner)

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        teamAdapter = TeamsAdapter(this,
            favViewIdentifier
        )

        binding.root.findViewById<RecyclerView>(R.id.fav_team_grid).apply {
            layoutManager = GridLayoutManager(context,getCardCountRow())
            adapter = teamAdapter
        }

        viewModel.favTeams.observe(viewLifecycleOwner, Observer {

            Log.d(favViewIdentifier, "Fav list size is ${it.size}")
            teamAdapter.data = it
        })

        val statusImageView = binding.root.findViewById<ImageView>(R.id.status_image)
        viewModel.status.observe(viewLifecycleOwner, Observer {status ->
            when (status) {
                SportsApiStatus.LOADING -> {
                    statusImageView.visibility = View.VISIBLE
                    statusImageView.setImageResource(R.drawable.loading_animation)
                }
                SportsApiStatus.ERROR -> {
                    statusImageView.visibility = View.VISIBLE
                    statusImageView.setImageResource(R.drawable.ic_connection_error)
                }
                SportsApiStatus.DONE -> {
                    statusImageView.visibility = View.GONE
                }
            }
        })

        return binding.root
    }

    override fun onTeamClick(team: Team) {
        this.findNavController().navigate(
            FavouriteFragmentDirections.actionFavouriteFragmentToSchedulesFragment(
                team
            )
        )
    }

    override fun onAddRemoveFavClick(team: Team) {
            dialogBuilder.setMessage("Remove ${team.strTeam} from favourites ?")
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { dialog, id ->
                    viewModel.removeTeam(team)
                    dialog.dismiss()
                })
                .setNegativeButton(getString(R.string.later), DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })

            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.remove_team))
            alert.show()
    }

    companion object{
        val favViewIdentifier: String = FavouriteFragment::class.java.simpleName
    }
}
