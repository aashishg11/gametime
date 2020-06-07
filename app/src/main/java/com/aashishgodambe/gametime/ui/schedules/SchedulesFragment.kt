package com.aashishgodambe.gametime.ui.schedules


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aashishgodambe.gametime.R
import com.aashishgodambe.gametime.adapters.SchedulesAdapter
import com.aashishgodambe.gametime.databinding.FragmentSchedulesBinding
import com.aashishgodambe.gametime.ui.teamSearch.SportsApiStatus
import com.aashishgodambe.gametime.ui.teamSearch.TeamsViewmodel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_search_team.view.*
import kotlinx.android.synthetic.main.fragment_search_team.view.status_image
import kotlinx.android.synthetic.main.loading_dialog.view.*
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog


/**
 * A simple [Fragment] subclass.
 */
class SchedulesFragment : Fragment(), SchedulesAdapter.ClickListener {



    private lateinit var schedulesAdapter: SchedulesAdapter
    private lateinit var dialogBuilder: AlertDialog.Builder

    private val viewModel: SchedulesViewmodel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, SchedulesViewmodel.Factory(activity.application))
            .get(SchedulesViewmodel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogBuilder = AlertDialog.Builder(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentSchedulesBinding =  DataBindingUtil.inflate(
                inflater,
            R.layout.fragment_schedules,
        container,
        false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.setLifecycleOwner(viewLifecycleOwner)

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        val team= SchedulesFragmentArgs.fromBundle(requireArguments()).team
        viewModel.getTeam(team.idTeam)

        schedulesAdapter = SchedulesAdapter(this)

        binding.root.findViewById<RecyclerView>(R.id.rv_schedule).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = schedulesAdapter
        }

        viewModel.results.observe(viewLifecycleOwner, Observer { results ->
            if ( results.isNotEmpty()) {
                schedulesAdapter.data = results
            }
        })

        val loading = binding.root.findViewById<FrameLayout>(R.id.loading)
        viewModel.status.observe(viewLifecycleOwner, Observer {status ->
            when (status) {
                SportsApiStatus.LOADING -> {
                    Picasso.get().load(team.strTeamBadge)
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                        .into(loading.imageView)
                    loading.tv_info_msg.text = getString(R.string.tv_info_msg)
                    loading.visibility = View.VISIBLE
                }
                SportsApiStatus.ERROR -> {
                    loading.visibility = View.VISIBLE
                    loading.status_image.setImageResource(R.drawable.ic_connection_error)
                    loading.tv_info_msg.text = getString(R.string.connection_error)
                }
                SportsApiStatus.DONE -> {
                    loading.visibility = View.GONE
                }
            }
        })

        return binding.root
    }

    override fun onOpenLink(link: String) {
        val webpage = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun onItemClick() {
        dialogBuilder.setMessage(getString(R.string.match_paid_msg))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.yes)) { dialog, id ->
                dialog.dismiss()
                Toast.makeText(activity,getString(R.string.pay_success_msg),Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.later)) { dialog, id ->
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.match_details))
        alert.show()
    }
}
