package com.rent.ui.main.calendar


import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import com.rent.R
import com.rent.base.BaseFragment
import com.rent.data.model.flight.Flight
import com.rent.data.model.relations.RentalWithLocataire
import com.rent.databinding.FragmentCalendarBinding
import com.rent.global.helper.Navigation
import com.rent.global.helper.ViewModelFactory
import com.rent.global.utils.ExtraKeys
import com.rent.global.utils.daysOfWeekFromLocale
import com.rent.global.utils.observeOnlyNotNull
import com.rent.global.utils.setTextColorRes
import com.rent.ui.rental.add.AddRentalActivity
import com.rent.ui.rental.detail.RentalDetailActivity
import kotlinx.android.synthetic.main.calendar_day_item.view.*
import kotlinx.android.synthetic.main.calendar_day_legend.view.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class CalendarFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: CalendarViewModel by viewModels { viewModelFactory }

    companion object {
        fun newInstance(): CalendarFragment {
            return CalendarFragment()
        }
    }

    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    @Inject
    lateinit var calendarAdapter: CalendarAdapter

    lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.title = "Calendrier"
        setHasOptionsMenu(true)

        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        binding = FragmentCalendarBinding.bind(view)
        registerBindingAndBaseObservers()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerBaseObserver(viewModel)
        registerCalendarRecycler()
        registerCalendarObservers()
    }

    private fun registerCalendarObservers() {
        viewModel.rentalItems.observeOnlyNotNull(viewLifecycleOwner) {
            prepareView(it)
        }
    }

    private fun registerBindingAndBaseObservers() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun registerCalendarRecycler() {
        calendarAdapter.setListener(viewModel)
        binding.exFiveRv.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.exFiveRv.adapter = calendarAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_loc -> {
                Intent(requireContext(), AddRentalActivity::class.java).also {
                    startActivityForResult(it, REQUEST_CODE)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun prepareView(items: Map<LocalDate, List<Flight>>) {
        val daysOfWeek = daysOfWeekFromLocale()

        val currentMonth = YearMonth.now()
        binding.exFiveCalendar.setup(
            currentMonth.minusMonths(10),
            currentMonth.plusMonths(10),
            daysOfWeek.first()
        )
        binding.exFiveCalendar.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.exFiveDayText
            val layout = view.exFiveDayLayout
            val flightTopView = view.exFiveDayFlightTop
            val flightBottomView = view.exFiveDayFlightBottom
            val flightMiddleView = view.exFiveDayFlightMiddle

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (viewModel.selectDate.value != day.date) {
                            val oldDate = viewModel.selectDate.value
                            viewModel.setSelectedDate(day.date)
                            binding.exFiveCalendar.notifyDateChanged(day.date)
                            oldDate?.let { binding.exFiveCalendar.notifyDateChanged(it) }
                            viewModel.setSelectedDate(day.date)
                        }
                    }
                }
            }
        }
        binding.exFiveCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                val layout = container.layout
                textView.text = day.date.dayOfMonth.toString()

                val flightTopView = container.flightTopView
                val flightBottomView = container.flightBottomView
                val flightMiddleView = container.flightMiddleView

                flightTopView.background = null
                flightBottomView.background = null
                flightMiddleView.background = null

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.setTextColorRes(R.color.example_5_text_grey)
                    layout.setBackgroundResource(if (viewModel.selectDate.value == day.date) R.drawable.calendar_selected_bg else 0)

                    items[day.date]?.let { item ->
                        when {
                            item.count() == 1 -> {
                                flightBottomView.setBackgroundColor(item[0].color)
                                flightTopView.visibility = View.GONE
                            }
                            item.count() == 2 -> {
                                flightTopView.visibility = View.GONE
                                flightMiddleView.setBackgroundColor(item[0].color)
                                flightBottomView.setBackgroundColor(item[1].color)
                            }
                            else -> {
                                flightTopView.setBackgroundColor(item[0].color)
                                flightMiddleView.setBackgroundColor(item[1].color)
                                flightBottomView.setBackgroundColor(item[2].color)
                            }
                        }
                    }
                } else {
                    textView.setTextColorRes(R.color.example_5_text_grey_light)
                    layout.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = view.legendLayout
        }
        binding.exFiveCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = month.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].name.take(3)
                                tv.setTextColorRes(R.color.example_5_text_grey)
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                            }
                        month.yearMonth
                    }
                }
            }

        binding.exFiveCalendar.monthScrollListener = { month ->
            val title = "${monthTitleFormatter.format(month.yearMonth)} ${month.yearMonth.year}"
            binding.exFiveMonthYearText.text = title
            viewModel.setSelectedDate(null)
        }

        binding.exFiveNextMonthImage.setOnClickListener {
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.next)
            }
        }

        binding.exFivePreviousMonthImage.setOnClickListener {
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.previous)
            }
        }
    }

    override fun navigate(navigationTo: Navigation) {
        super.navigate(navigationTo)
        when (navigationTo) {
            is Navigation.RentalDetailActivityNavigation -> navigateToRentalDetail(navigationTo.rentalAndLocataire)
        }
    }

    private fun navigateToRentalDetail(rental: RentalWithLocataire) {
        Intent(requireActivity(), RentalDetailActivity::class.java).also {
            it.putExtra(ExtraKeys.RentalDetailActivity.RENAL_DETAIL_EXTRA_RENTAL, rental)
            startActivityForResult(it, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.loadRentals()
    }
}

const val REQUEST_CODE = 101