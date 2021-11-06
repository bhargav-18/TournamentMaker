package com.example.tournamentmaker.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamentmaker.R
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.data.entity.User
import com.example.tournamentmaker.databinding.ListItemsSearchTournamentsBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchTournamentAdapter() :
    RecyclerView.Adapter<SearchTournamentAdapter.MyTournamentViewHolder>() {

    private val users = FirebaseFirestore.getInstance().collection("users")

    inner class MyTournamentViewHolder(binding: ListItemsSearchTournamentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tournamentName: TextView = binding.tvTournamentName
        val tournamentSport: TextView = binding.tvTournamentSport
        val tournamentScheduled: TextView = binding.tvScheduled
        val tournamentHost: TextView = binding.tvTournamentHosts
        val personsJoined: TextView = binding.tvPersonsJoined
        val btnJoin: AppCompatButton = binding.btnJoin
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyTournamentViewHolder {
        val binding = ListItemsSearchTournamentsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyTournamentViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyTournamentViewHolder,
        position: Int
    ) {
        val tournament: Tournament = tournamentList[position]
        holder.apply {
            tournamentName.text = tournament.tournamentName
            tournamentSport.text = tournament.tournamentSport
            tournamentScheduled.text = if (tournament.scheduled) "Scheduled" else "Starting Soon"
            CoroutineScope(Dispatchers.Main).launch {
                val user = users.whereEqualTo("uid", tournament.host).get().await()
                    .toObjects(User::class.java)
                tournamentHost.text = "Hosted by:\n" + user[0].userName
            }
            personsJoined.text =
                "${tournament.persons.size} / ${tournament.maxPersons}"
            if (tournament.tournamentVisibility == "Private") {
                btnJoin.background.setTint(
                    btnJoin.context.resources.getColor(
                        R.color.red, null
                    )
                )
            } else if (tournament.persons.size >= tournament.maxPersons || !tournament.scheduled) {
                btnJoin.background.setTint(
                    btnJoin.context.resources.getColor(
                        R.color.gray, null
                    )
                )
            } else {
                btnJoin.background.setTint(
                    btnJoin.context.resources.getColor(
                        R.color.green, null
                    )
                )
            }
            btnJoin.setOnClickListener {
                onJoinClickListener?.let {
                    it(tournament)
                }
            }

            itemView.setOnClickListener {
                onTournamentClickListener?.let {
                    it(tournament)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return tournamentList.size
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Tournament>() {
        override fun areItemsTheSame(oldItem: Tournament, newItem: Tournament): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Tournament, newItem: Tournament): Boolean {
            return oldItem.id == newItem.id
        }
    }
    private val differ = AsyncListDiffer(this, diffCallBack)

    var tournamentList: List<Tournament>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private var onTournamentClickListener: ((Tournament) -> Unit)? = null

    fun setOnTournamentClickListener(listener: (Tournament) -> Unit) {
        onTournamentClickListener = listener
    }

    private var onJoinClickListener: ((Tournament) -> Unit)? = null

    fun setOnJoinClickListener(listener: (Tournament) -> Unit) {
        onJoinClickListener = listener
    }
}