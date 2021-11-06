package com.example.tournamentmaker.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.databinding.ListItemsMyTournamentsBinding

class MyTournamentAdapter() :
    RecyclerView.Adapter<MyTournamentAdapter.MyTournamentViewHolder>() {

    inner class MyTournamentViewHolder(binding: ListItemsMyTournamentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tournamentName: TextView = binding.tvTournamentName
        val tournamentSport: TextView = binding.tvTournamentSport
        val tournamentScheduled: TextView = binding.tvScheduled
        val deleteTournament: ImageView = binding.ivDelete
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyTournamentViewHolder {
        val binding = ListItemsMyTournamentsBinding.inflate(
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

            deleteTournament.setOnClickListener {
                onDeleteClickListener?.let {
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

    private var onDeleteClickListener: ((Tournament) -> Unit)? = null

    fun setOnDeleteClickListener(listener: (Tournament) -> Unit) {
        onDeleteClickListener = listener
    }
}