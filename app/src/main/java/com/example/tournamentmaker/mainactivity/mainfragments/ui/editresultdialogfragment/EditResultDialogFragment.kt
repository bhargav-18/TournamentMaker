package com.example.tournamentmaker.mainactivity.mainfragments.ui.editresultdialogfragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tournamentmaker.R
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.data.entity.User
import com.example.tournamentmaker.databinding.EditResultDialogBinding
import com.example.tournamentmaker.util.hideKeyboard
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

                hideKeyboard(activity as Activity)

                showProgress(true)

                if (rgWinner.checkedRadioButtonId == -1) {
                    Toast.makeText(context, "Please select the winner", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val map: Map<String, Map<String, Map<String, String>>> =
                    mapOf(args.persons[0] to mapOf(args.persons[1] to mapOf("winner" to winner)))

                CoroutineScope(Dispatchers.Main).launch {

                    val tournament =
                        tournaments.document(args.id).get().await()
                            .toObject(Tournament::class.java)!!

                    val matchesArray = tournament.matches

                    matchesArray.removeAt(position.toInt())
                    matchesArray.add(position.toInt(), map)

                    tournaments.document(args.id)
                        .update("matches", matchesArray).await()

                    tournaments.document(args.id)
                        .update("results.${args.persons[0]}.played", FieldValue.increment(1))

                    when (winner) {
                        args.persons[0] -> {
                            tournaments.document(args.id)
                                .update("results.${args.persons[0]}.won", FieldValue.increment(1))
                                .await()

                            tournaments.document(args.id)
                                .update("results.${args.persons[1]}.lost", FieldValue.increment(1))
                                .await()
                        }
                        "Draw" -> {
                            tournaments.document(args.id)
                                .update("results.${args.persons[0]}.draw", FieldValue.increment(1))
                                .await()

                            tournaments.document(args.id)
                                .update("results.${args.persons[1]}.draw", FieldValue.increment(1))
                                .await()
                        }
                        else -> {
                            tournaments.document(args.id)
                                .update("results.${args.persons[0]}.lost", FieldValue.increment(1))
                                .await()

                            tournaments.document(args.id)
                                .update("results.${args.persons[1]}.won", FieldValue.increment(1))
                                .await()
                        }
                    }

                    tournaments.document(args.id)
                        .update(
                            "results.${args.persons[0]}.tieBreaker",
                            FieldValue.increment(etPerson1Score.text.toString().toLong())
                        ).await()

                    tournaments.document(args.id)
                        .update(
                            "results.${args.persons[1]}.tieBreaker",
                            FieldValue.increment(etPerson2Score.text.toString().toLong())
                        ).await()

                    showProgress(false)

                    findNavController().navigate(
                        EditResultDialogFragmentDirections
                            .actionEditResultDialogFragmentToResultsFragment(
                                id = tournament.id
                            )
                    )

                }
            }

        }

    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressEditResult.isVisible = bool
            if (bool) {
                parentLayoutEditResult.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutEditResult.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

}