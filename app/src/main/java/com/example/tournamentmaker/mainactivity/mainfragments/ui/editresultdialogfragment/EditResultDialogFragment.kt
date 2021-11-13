package com.example.tournamentmaker.mainactivity.mainfragments.ui.editresultdialogfragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tournamentmaker.R
import com.example.tournamentmaker.data.entity.User
import com.example.tournamentmaker.databinding.EditResultDialogBinding
import com.example.tournamentmaker.util.exhaustive
import com.example.tournamentmaker.util.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EditResultDialogFragment : DialogFragment(R.layout.edit_result_dialog) {

    private lateinit var binding: EditResultDialogBinding
    private val args: EditResultDialogFragmentArgs by navArgs()
    private val viewModel: EditResultViewModel by viewModels()
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

            CoroutineScope(Dispatchers.IO).launch {

                val user1 =
                    users.document(args.persons[0]).get().await()
                        .toObject(User::class.java)!!
                val user2 =
                    users.document(args.persons[1])
                        .get().await().toObject(User::class.java)!!

                withContext(Dispatchers.Main) {
                    rbPerson1.text = user1.userName
                    rbPerson2.text = user2.userName
                }

            }

            etPerson1Score.setText(viewModel.scoreP1)
            etPerson2Score.setText(viewModel.scoreP2)


            rgWinner.setOnCheckedChangeListener { _, checkedId ->

                when (checkedId) {
                    R.id.rb_person1 -> {
                        viewModel.winner = args.persons[0]
                    }
                    R.id.rb_person2 -> {
                        viewModel.winner = args.persons[1]
                    }
                    R.id.rb_draw -> {
                        viewModel.winner = "Draw"
                    }
                }

            }


            etPerson1Score.addTextChangedListener {
                viewModel.scoreP1 = it.toString()
            }

            etPerson2Score.addTextChangedListener {
                viewModel.scoreP2 = it.toString()
            }


            btnUpdateResult.setOnClickListener {

                hideKeyboard(activity as Activity)

                showProgress(true)

                viewModel.updateResult(
                    id = args.id,
                    person1 = args.persons[0],
                    person2 = args.persons[1],
                    position = args.persons[2].toInt()
                )

            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.editResultEvent.collect { event ->
                @Suppress("IMPLICIT_CAST_TO_ANY")
                when (event) {
                    is EditResultViewModel.EditResultEvent.NavigateBackWithResult -> {
                        showProgress(false)
                        findNavController().navigate(
                            EditResultDialogFragmentDirections
                                .actionEditResultDialogFragmentToResultsFragment(
                                    id = args.id
                                )
                        )
                    }
                    is EditResultViewModel.EditResultEvent.ShowErrorMessage -> {
                        showProgress(false)
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
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