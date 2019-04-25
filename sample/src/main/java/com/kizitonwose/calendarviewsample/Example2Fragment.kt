package com.kizitonwose.calendarviewsample


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import com.google.android.material.snackbar.Snackbar
import com.kizitonwose.calendarview.model.DayOwner
import kotlinx.android.synthetic.main.calendar_day_legend.*
import kotlinx.android.synthetic.main.example_2_calendar_day.view.*
import kotlinx.android.synthetic.main.example_2_calendar_header.view.*
import kotlinx.android.synthetic.main.exmaple_2_fragment.*
import kotlinx.android.synthetic.main.home_activity.*
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter


class Example2Fragment : BaseFragment(), HasToolbar, HasBackButton {

    override val toolbar: Toolbar?
        get() = exTwoToolbar

    override val titleRes: Int = R.string.example_2_title

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.exmaple_2_fragment, container, false)
    }

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val daysOfWeek = daysOfWeekFromLocale()
        legendLayout.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = daysOfWeek[index].name.first().toString()
                setTextColorRes(R.color.example_2_white)
            }
        }

        exTwoCalendar.setup(YearMonth.now(), YearMonth.now().plusMonths(5), daysOfWeek.first())

        exTwoCalendar.dateViewBinder = { view, day ->
            val textView = view.exTwoDayText
            textView.text = day.date.dayOfMonth.toString()

            when (day.owner) {
                DayOwner.THIS_MONTH -> {
                    textView.makeVisible()
                    textView.setTextColorRes(R.color.example_2_black)
                }
                else -> {
                    textView.makeInVisible()
                }
            }

            when (day.date) {
                selectedDate -> {
                    textView.setTextColorRes(R.color.example_2_white)
                    textView.setBackgroundResource(R.drawable.example_2_selected_bg)
                }
                today -> {
                    textView.setTextColorRes(R.color.example_2_red)
                    textView.background = null
                }
                else -> textView.background = null
            }
        }

        exTwoCalendar.dateClickListener = dateClick@{
            if (it.owner == DayOwner.THIS_MONTH) {
                if (selectedDate == it.date) {
                    selectedDate = null
                    exTwoCalendar.reloadDay(it)
                } else {
                    val oldDate = selectedDate
                    selectedDate = it.date
                    exTwoCalendar.reloadDate(it.date)
                    oldDate?.let { exTwoCalendar.reloadDate(oldDate) }
                }
                menuItem.isVisible = selectedDate != null
            }
        }

        exTwoCalendar.monthHeaderBinder = { view, calMonth ->
            @SuppressLint("SetTextI18n") // Fix concatenation warning for `setText` call.
            view.exTwoHeaderText.text = "${calMonth.yearMonth.month.name.toLowerCase().capitalize()} ${calMonth.year}"
        }
    }

    private lateinit var menuItem: MenuItem
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.example_2_menu, menu)
        menuItem = menu.getItem(0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuItemDone) {
            val date = selectedDate ?: return false
            val text = "Selected: ${DateTimeFormatter.ofPattern("d MMMM yyyy").format(date)}"
            Snackbar.make(requireActivity().homeRootLayout, text, Snackbar.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
