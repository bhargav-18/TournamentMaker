package com.example.tournamentmaker.mainactivity.mainfragments.ui.creatematches

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.tournamentmaker.R
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.data.entity.User
import com.example.tournamentmaker.databinding.FragmentCreateMatchesBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class CreateMatchesFragment : Fragment(R.layout.fragment_create_matches) {

    private val args: CreateMatchesFragmentArgs by navArgs()
    private lateinit var binding: FragmentCreateMatchesBinding
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private val users = FirebaseFirestore.getInstance().collection("users")
    private lateinit var tournament: Tournament

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCreateMatchesBinding.bind(view)

        val id = args.id
        CoroutineScope(Dispatchers.Main).launch {
            tournament = tournaments.document(id).get().await().toObject(Tournament::class.java)!!
            val persons = tournament.persons

            val list = arrayListOf<String>()

            for (p in persons) {

                val user = users.document(p).get().await().toObject(User::class.java)!!

                list.add(user.userName)
            }

            listMatches(list, persons)

        }

    }

    @SuppressLint("SetTextI18n")
    private fun listMatches(ListTeam: ArrayList<String>, ListID: ArrayList<String>) {

        showProgress(true)

        val matchList: ArrayList<Map<String, Map<String, Map<String, String>>>> = arrayListOf()

        val numTeams = ListTeam.size
        if (ListTeam.size % 2 != 0) {
            ListTeam.add("Bye")
        }
        var numDays: Int = numTeams - 1
        var halfSize: Int = numTeams / 2
        if (numTeams % 2 != 0) {
            halfSize += 1
            numDays += 1
        }

        val row = TableRow(context)
        val lp: TableRow.LayoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)

        row.apply {
            layoutParams = lp
            gravity = Gravity.CENTER
        }

        val title = TextView(context)
        title.apply {
            text = "MATCHES"
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        row.addView(title)

        binding.tlMatches.addView(row)

        val emptyRow = TableRow(context)

        emptyRow.apply {
            layoutParams = lp
            gravity = Gravity.CENTER
        }
        emptyRow.setPadding(10)

        binding.tlMatches.addView(emptyRow)

        val teams: ArrayList<String> = arrayListOf()
        teams.addAll(ListTeam)
        teams.removeAt(0)

        val teamsId: ArrayList<String> = arrayListOf()
        teamsId.addAll(ListID)
        teamsId.removeAt(0)

        val teamsSize: Int = teams.size

        for (day in 0 until numDays) {

            val rowRound = TableRow(context)
            rowRound.apply {
                layoutParams = lp
                gravity = Gravity.CENTER
            }

            val emptyRow1 = TableRow(context)

            emptyRow1.apply {
                layoutParams = lp
                gravity = Gravity.CENTER
            }
            emptyRow1.setPadding(10)

            binding.tlMatches.addView(emptyRow1)

            val round = TextView(context)
            round.apply {
                text = "Round ${day + 1}"
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }
            round.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)

            rowRound.addView(round)
            binding.tlMatches.addView(rowRound)

            val teamIdx = day % teamsSize

            val rowMatch = TableRow(context)
            rowMatch.apply {
                layoutParams = lp
                gravity = Gravity.CENTER
            }

            val match = TextView(context)
            match.apply {
                text = "${teams[teamIdx]} vs ${ListTeam[0]}"
                gravity = Gravity.CENTER
            }

            val m = mapOf(teamsId[teamIdx] to mapOf(ListID[0] to mapOf("winner" to "")))

            matchList.add(m)

            rowMatch.addView(match)
            binding.tlMatches.addView(rowMatch)


            for (idx in 1 until halfSize) {
                val firstTeam = (day + idx) % teamsSize
                val secondTeam = (day + teamsSize - idx) % teamsSize

                val rowMatches = TableRow(context)
                rowMatches.apply {
                    layoutParams = lp
                    gravity = Gravity.CENTER
                }

                val matches = TextView(context)
                matches.apply {
                    text = "${teams[firstTeam]} vs ${teams[secondTeam]}"
                    gravity = Gravity.CENTER
                }
                val m1 =
                    mapOf(teamsId[firstTeam] to mapOf(teamsId[secondTeam] to mapOf("winner" to "")))

                matchList.add(m1)

                rowMatches.addView(matches)
                binding.tlMatches.addView(rowMatches)
            }
        }

        CoroutineScope(Dispatchers.Main).launch {

            showProgress(true)

            tournaments.document(tournament.id).update("matches", matchList).await()

            for (id in ListID) {

                val map = mutableMapOf(

                    "played" to 0f,
                    "won" to 0f,
                    "lost" to 0f,
                    "draw" to 0f,
                    "tieBreaker" to 0f

                )

                tournaments.document(tournament.id).update("results.${id}", map)

            }


            showProgress(false)
        }

    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressCreateMatches.isVisible = bool
            if (bool) {
                parentLayoutCreateMatches.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutCreateMatches.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

}