package com.example.tournamentmaker.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.data.entity.User
import com.example.tournamentmaker.databinding.ListItemsParticipantsBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ParticipantsAdapter(val id: String) :
    RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder>() {

    private val users = FirebaseFirestore.getInstance().collection("users")
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private lateinit var tournament: Tournament

    inner class ParticipantViewHolder(binding: ListItemsParticipantsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvPersons = binding.tvPersonName
        val removeParticipant = binding.ivRemoveParticipant
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParticipantViewHolder {
        val binding = ListItemsParticipantsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ParticipantViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: ParticipantViewHolder,
        position: Int
    ) {
        val person: String = personList[position]
        holder.apply {

            CoroutineScope(Dispatchers.IO).launch {
                val user = users.document(person).get().await().toObject(User::class.java)

                tournament =
                    tournaments.document(id).get().await().toObject(Tournament::class.java)!!


                withContext(Dispatchers.Main) {

                    tvPersons.text = user?.userName
                    if (tournament.matches.size > 0) {
                        removeParticipant.visibility = View.GONE
                    } else {
                        removeParticipant.visibility = View.VISIBLE
                    }

                }

            }

            removeParticipant.setOnClickListener {
                onRemoveClickListener?.let {
                    it(person)
                }
            }

            itemView.setOnClickListener {
                onPersonClickListener?.let {
                    it(person)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return personList.size
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this, diffCallBack)

    var personList: List<String>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private var onPersonClickListener: ((String) -> Unit)? = null

    fun setOnPersonClickListener(listener: (String) -> Unit) {
        onPersonClickListener = listener
    }

    private var onRemoveClickListener: ((String) -> Unit)? = null

    fun setOnRemoveClickListener(listener: (String) -> Unit) {
        onRemoveClickListener = listener
    }

}