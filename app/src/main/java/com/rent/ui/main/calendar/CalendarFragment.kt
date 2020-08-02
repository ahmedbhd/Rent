package com.rent.ui.main.calendar


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
import com.rent.data.model.flight.Flight
import com.rent.databinding.FragmentCalendarBinding
import com.rent.global.helper.ViewModelFactory
import com.rent.global.utils.daysOfWeekFromLocale
import com.rent.global.utils.observeOnlyNotNull
import com.rent.global.utils.setTextColorRes
import com.rent.tools.BaseFragment
import com.rent.ui.rental.add.AddRentalActivity
import kotlinx.android.synthetic.main.calendar_day_legend.view.*
import kotlinx.android.synthetic.main.example_5_calendar_day.view.*
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

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        if (PhoneGrantings.isNetworkAvailable(requireActivity())) // online actions
//            selectLocations()
//        else {
//            Toast.makeText(context, "Internet Non Disponible", Toast.LENGTH_SHORT).show()
//        }
//
//        exFiveRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//        exFiveRv.adapter = flightsAdapter
//
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerBaseObserver(viewModel)
        registerCalendarRecycler()
        registerCalendarObservers()
    }

    private fun registerCalendarObservers() {
        viewModel.flights.observeOnlyNotNull(viewLifecycleOwner) {
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
                navigateToClass(AddRentalActivity::class)
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

                flightTopView.background = null
                flightBottomView.background = null

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.setTextColorRes(R.color.example_5_text_grey)
                    layout.setBackgroundResource(if (viewModel.selectDate.value == day.date) R.drawable.example_5_selected_bg else 0)

                    val item = items[day.date]
                    if (item != null) {
                        if (item.count() == 1) {
                            flightBottomView.setBackgroundColor(item[0].color)
                        } else {
                            flightTopView.setBackgroundColor(item[0].color)
                            flightBottomView.setBackgroundColor(item[1].color)
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
}