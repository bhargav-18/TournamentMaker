package com.example.tournamentmaker.mainactivity.mainfragments.ui.standings

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.tournamentmaker.R
import com.example.tournamentmaker.data.entity.Tournament
import com.example.tournamentmaker.data.entity.User
import com.example.tournamentmaker.databinding.FragmentStandingsBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StandingsFragment : Fragment(R.layout.fragment_standings) {

    private lateinit var binding: FragmentStandingsBinding
    private val args: StandingsFragmentArgs by navArgs()
    private val tournaments = FirebaseFirestore.getInstance().collection("tournaments")
    private val users = FirebaseFirestore.getInstance().collection("users")
    private lateinit var tournament: Tournament

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStandingsBinding.bind(view)

        CoroutineScope(Dispatchers.Main).launch {

            tournament = tournaments.document(args.id).get().await()
                .toObject(Tournament::class.java)!!

            val map = tournament.results

            val persons = tournament.persons

            val row = TableRow(context)
            val lp: TableRow.LayoutParams =
                TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
            lp.setMargins(20, 0, 20, 0)

            row.apply {
                layoutParams = lp
                gravity = Gravity.CENTER
            }

            val title = TextView(context)
            title.apply {
                text = "Standings"
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            row.addView(title)

            binding.tlStandings.addView(row)

            val emptyRow = TableRow(context)

            emptyRow.apply {
                layoutParams = lp
                gravity = Gravity.CENTER
            }
            emptyRow.setPadding(10)

            binding.tlStandings.addView(emptyRow)


            val rowTitle = TableRow(context)
            rowTitle.apply {
                layoutParams = lp
            }

            val headings = TextView(context)
            headings.apply {
                text = "Teams"
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = lp
            }
            headings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

            val headingPlayed = TextView(context)
            headingPlayed.apply {
                text = "P"
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = lp
            }
            headingPlayed.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

            val headingWon = TextView(context)
            headingWon.apply {
                text = "W"
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = lp
            }
            headingWon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

            val headingDraw = TextView(context)
            headingDraw.apply {
                text = "D"
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = lp
            }
            headingDraw.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

            val headingLost = TextView(context)
            headingLost.apply {
                text = "L"
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = lp
            }
            headingLost.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

            val headingTbPoints = TextView(context)
            headingTbPoints.apply {
                text = "NRR"
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = lp
            }
            headingTbPoints.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

            rowTitle.addView(headings)
            rowTitle.addView(headingPlayed)
            rowTitle.addView(headingWon)
            rowTitle.addView(headingDraw)
            rowTitle.addView(headingLost)
            rowTitle.addView(headingTbPoints)

            binding.tlStandings.addView(rowTitle)

            val emptyRow1 = TableRow(context)

            emptyRow1.apply {
                layoutParams = lp
                gravity = Gravity.CENTER
            }
            emptyRow1.setPadding(10)

            binding.tlStandings.addView(emptyRow1)

            val list: ArrayList<String> = persons

            var temp: String

            for (p in list.indices) {
                for (a in p + 1 until list.size) {
                    if (map[list[p]]?.get("won")!! < map[list[a]]?.get("won")!!) {
                        temp = list[p]
                        list[p] = list[a]
                        list[a] = temp
                    } else if (map[list[p]]?.get("won")!! == map[list[a]]?.get("won")!!) {
                        if (map[list[p]]?.get("tieBreaker")!! < map[list[a]]?.get("tieBreaker")!!) {
                            temp = list[p]
                            list[p] = list[a]
                            list[a] = temp
                        }
                    }
                }
            }


            for (p in list) {

                val rowTable = TableRow(context)
                rowTable.apply {
                    layoutParams = lp
                }

                val user = users.document(p).get().await().toObject(User::class.java)!!

                val name = TextView(context)
                name.apply {
                    text = user.userName
                    gravity = Gravity.CENTER
                    layoutParams = lp
                }

                val played = TextView(context)
                played.apply {
                    text = "${map[p]?.get("played")?.toInt()}"
                    gravity = Gravity.CENTER
                    layoutParams = lp

                }

                val won = TextView(context)
                won.apply {
                    text = "${map[p]?.get("won")?.toInt()}"
                    gravity = Gravity.CENTER
                    layoutParams = lp
                }

                val draw = TextView(context)
                draw.apply {
                    text = "${map[p]?.get("draw")?.toInt()}"
                    gravity = Gravity.CENTER
                    layoutParams = lp
                }

                val lost = TextView(context)
                lost.apply {
                    text = "${map[p]?.get("lost")?.toInt()}"
                    gravity = Gravity.CENTER
                    layoutParams = lp
                }

                val tbPoints = TextView(context)
                tbPoints.apply {
                    text = "${map[p]?.get("tieBreaker")!! / map[p]?.get("played")!!}"
                    gravity = Gravity.CENTER
                    layoutParams = lp
                }

                rowTable.addView(name)
                rowTable.addView(played)
                rowTable.addView(won)
                rowTable.addView(draw)
                rowTable.addView(lost)
                rowTable.addView(tbPoints)

                binding.tlStandings.addView(rowTable)

            }
        }
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressStandings.isVisible = bool
            if (bool) {
                parentLayoutStandings.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutStandings.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }
}