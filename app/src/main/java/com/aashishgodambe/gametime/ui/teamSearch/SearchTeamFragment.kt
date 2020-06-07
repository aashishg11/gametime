package com.aashishgodambe.gametime.ui.teamSearch

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aashishgodambe.gametime.R
import com.aashishgodambe.gametime.adapters.TeamsAdapter
import com.aashishgodambe.gametime.databinding.FragmentSearchTeamBinding
import com.aashishgodambe.gametime.models.Team

/**
 * A simple [Fragment] subclass.
 */
class SearchTeamFragment : Fragment(),TeamsAdapter.ClickListener {

    private lateinit var teamAdapter: TeamsAdapter
    private lateinit var dialogBuilder: AlertDialog.Builder
    private var searchQuery = ""
    private var count = 0

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateCurrFav()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentSearchTeamBinding =  DataBindingUtil.inflate(
                inflater,
            R.layout.fragment_search_team,
        container,
        false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.setLifecycleOwner(viewLifecycleOwner)

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        teamAdapter = TeamsAdapter(this, searchViewIdentifier)

        val rv = binding.root.findViewById<RecyclerView>(R.id.teams_grid)
        rv.apply {
            layoutManager = GridLayoutManager(context,2)
            adapter = teamAdapter
        }

        val searchView = binding.root.findViewById<SearchView>(R.id.searchView)
        searchView.apply {
            queryHint = context.getString(R.string.query_hint)
            isActivated = true
            isIconified = false
            clearFocus()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchQuery = query
                viewModel.getTeam(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        viewModel.teams.observe(viewLifecycleOwner, Observer {teams ->
            if ( teams.isNotEmpty()) {
                teamAdapter.data = teams
            }
        })

        viewModel.favTeams.observe(viewLifecycleOwner, Observer {
            if ( it.isNotEmpty()) {
                Log.d(searchViewIdentifier, "Fav list size is ${it.size}")
                this.count = it.size
            }
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
                    rv.visibility = View.GONE
                }
                SportsApiStatus.DONE -> {
                    rv.visibility = View.VISIBLE
                    statusImageView.visibility = View.GONE
                }
                SportsApiStatus.NOTFOUND -> {
                    Toast.makeText(activity,getString(R.string.team_not_found),Toast.LENGTH_LONG).show()
                }
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (searchQuery.isNotEmpty()){
            viewModel.getTeam(searchQuery)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController())
    }

    override fun onTeamClick(team: Team) {
        this.findNavController().navigate(
            SearchTeamFragmentDirections.actionSearchTeamFragmentToSchedulesFragment(team)
        )
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onAddRemoveFavClick(team: Team) {
        if (!viewModel.isFavourite(team.idTeam)){
            if(count > 3){
                showPaidMessage()
            }else{
                dialogBuilder.setMessage("Add ${team.strTeam} to favourites ?")
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { dialog, id ->
                        viewModel.insertTeam(team)
                        dialog.dismiss()
                    })
                    .setNegativeButton(getString(R.string.later), DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })

                val alert = dialogBuilder.create()
                alert.setTitle(getString(R.string.add_fav))
                alert.show()
            }
        }else{
            Toast.makeText(activity,team.strTeam +" "+ getString(R.string.team_exists),Toast.LENGTH_LONG).show()
        }
    }

    fun showPaidMessage() {
        dialogBuilder.setMessage(getString(R.string.paid_msg))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.yes)) { dialog, id ->
                dialog.dismiss()
                Toast.makeText(activity,getString(R.string.pay_success_msg),Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.later)) { dialog, id ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.add_fav))
        alert.show()
    }

    companion object{
        val searchViewIdentifier: String = SearchTeamFragment::class.java.simpleName
    }

}
