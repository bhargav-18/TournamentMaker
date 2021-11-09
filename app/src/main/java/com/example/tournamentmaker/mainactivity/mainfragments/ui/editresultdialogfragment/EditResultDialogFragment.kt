package com.example.tournamentmaker.mainactivity.mainfragments.ui.editresultdialogfragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.example.tournamentmaker.R
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.data.entity.User
import com.example.tournamentmaker.databinding.EditResultDialogBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditResultDialogFragment : DialogFragment(R.layout.edit_result_dialog) {

    private lateinit var binding: EditResultDialogBinding
    private val args: EditResultDialogFragmentArgs by navArgs()
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private val users = FirebaseFirestore.getInstance().collection("users")

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = EditResultDialogBinding.bind(view)

        binding.apply {


            CoroutineScope(Dispatchers.Main).launch {

                val user1 =
                    users.document(args.persons[0]).get().await()
                        .toObject(User::class.java)!!
                val user2 =
                    users.document(args.persons[1])
                        .get().await().toObject(User::class.java)!!

                rbPerson1.text = user1.userName
                rbPerson2.text = user2.userName

            }

            val position = args.persons[2]

            var winner = ""

            rgWinner.setOnCheckedChangeListener { _, checkedId ->

                winner = when (checkedId) {
                    R.id.rb_person1 -> {
                        args.persons[0]
                    }
                    R.id.rb_person2 -> {
                        args.persons[1]
                    }
                    else -> {
                        "Draw"
                    }
                }

            }

            btnUpdateResult.setOnClickListener {

                val map: Map<String, Map<String, Map<String, String>>> =
                    mapOf(args.persons[0] to mapOf(args.persons[1] to mapOf("winner" to winner)))

                CoroutineScope(Dispatchers.IO).launch {

                    val tournament =
                        tournaments.document(args.id).get().await()
                            .toObject(Tournament::class.java)!!

                    val matchesArray = tournament.matches

                    matchesArray.removeAt(position.toInt())
                    matchesArray.add(position.toInt(), map)

                    tournaments.document(args.id)
                        .update("matches", matchesArray).await()

                    val resultsArray = tournament.results
                    val newResultsArray: ArrayList<Map<String, Map<String, Float>>> = arrayListOf()

                    var m: Map<String, Map<String, Float>>

                    for (r in resultsArray) {
                        if (r.keys.toList()[0] == args.persons[0] || r.keys.toList()[0] == args.persons[1]) {

                            m = mutableMapOf(
                                r.keys.toList()[0] to mapOf(
                                    "played" to r.values.toList()[0].toList()[0].second + 1f,
                                    "won" to if (winner == r.keys.toList()[0]) r.values.toList()[0].toList()[1].second + 1f else r.values.toList()[0].toList()[1].second,
                                    "lost" to
                                            if (winner != r.keys.toList()[0] && winner != "Draw") r.values.toList()[0].toList()[2].second + 1f else r.values.toList()[0].toList()[2].second,
                                    "draw" to if (winner == "Draw") r.values.toList()[0].toList()[3].second + 1f else r.values.toList()[0].toList()[3].second,
                                    "tieBreaker" to r.values.toList()[0].toList()[4].second + etPerson1Score.text.toString()
                                        .toFloat()
                                )
                            )

                            newResultsArray.add(m)

                        } else {
                            newResultsArray.add(r)
                        }
                    }

                    tournaments.document(args.id)
                        .update("results", resultsArray).await()

                }

                dialog?.dismiss()
            }

        }

    }
}