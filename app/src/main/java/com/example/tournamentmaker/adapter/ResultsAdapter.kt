package com.example.tournamentmaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.data.entity.User
import com.example.tournamentmaker.databinding.ListItemsResultBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ResultsAdapter() :
    RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>() {

    private val users = FirebaseFirestore.getInstance().collection("users")
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private lateinit var tournament: Tournament

    inner class ResultsViewHolder(binding: ListItemsResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val person1 = binding.tvPerson1Name
        val person2 = binding.tvPerson2Name
        val btnEdit = binding.ivEdit
        val scorePerson1 = binding.tvScorePerson1
        val scorePerson2 = binding.tvScorePerson2
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ResultsViewHolder {
        val binding = ListItemsResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ResultsViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: ResultsViewHolder,
        position: Int
    ) {
        val result: Map<String, Map<String, Map<String, String>>> = resultList[position]
        holder.apply {

            CoroutineScope(Dispatchers.Main).launch {

                if (result.values.toList()[0].toList()[0].toList()[1].toString() == "{winner=}") {
                    scorePerson1.isVisible = false
                    scorePerson2.isVisible = false
                } else {
                    scorePerson1.isVisible = true
                    scorePerson2.isVisible = true
                    btnEdit.isVisible = false
                }

                val str = result.values.toList()[0].toList()[0].toList()[1].toString()
                val winner = str.substringAfter("{winner=").substringBefore("}")

                val user1 =
                    users.document(result.keys.toList()[0]).get().await()
                        .toObject(User::class.java)!!
                val user2 =
                    users.document(result.values.toList()[0].toList()[0].toList()[0].toString())
                        .get().await().toObject(User::class.java)!!

                when (winner) {
                    user1.uid -> {
                        scorePerson1.text = "2"
                        scorePerson2.text = "0"
                    }
                    user2.uid -> {
                        scorePerson2.text = "2"
                        scorePerson1.text = "0"
                    }
                    "Draw" -> {
                        scorePerson1.text = "1"
                        scorePerson2.text = "1"
                    }
                }

                person1.text = user1.userName
                person2.text = user2.userName


                btnEdit.setOnClickListener {
                    onEditClickListener?.let {
                        it(arrayOf(user1.uid, user2.uid, position.toString()))
                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    private val diffCallBack =
        object : DiffUtil.ItemCallback<Map<String, Map<String, Map<String, String>>>>() {
            override fun areItemsTheSame(
                oldItem: Map<String, Map<String, Map<String, String>>>,
                newItem: Map<String, Map<String, Map<String, String>>>
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(
                oldItem: Map<String, Map<String, Map<String, String>>>,
                newItem: Map<String, Map<String, Map<String, String>>>
            ): Boolean {
                return oldItem == newItem
            }
        }
    private val differ = AsyncListDiffer(this, diffCallBack)

    var resultList: List<Map<String, Map<String, Map<String, String>>>>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private var onEditClickListener: ((Array<String>) -> Unit)? = null

    fun setOnEitClickListener(listener: (Array<String>) -> Unit) {
        onEditClickListener = listener
    }


}