package com.rent


import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import com.rent.data.LocationServices
import com.rent.data.Model
import com.rent.tools.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.calendar_day_legend.view.*
import kotlinx.android.synthetic.main.example_5_calendar_day.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import com.rent.adapters.util.ViewDialog




data class Flight(val time: LocalDateTime, val departure: Airport, val destination: Airport,  val color: Int) {
    data class Airport(val city: String, val code: String)
}

class HomeItemsAdapter : RecyclerView.Adapter<HomeItemsAdapter.HomeItemsViewHolder>() {

    val flights = mutableListOf<Flight>()

    private val formatter = DateTimeFormatter.ofPattern("EEE'\n'dd MMM'\n'HH:mm" , Locale.FRANCE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeItemsViewHolder {
        return HomeItemsViewHolder(parent.inflate(R.layout.example_5_event_item_view))
    }

    override fun onBindViewHolder(viewHolder: HomeItemsViewHolder, position: Int) {
        viewHolder.bind(flights[position])
    }

    override fun getItemCount(): Int = flights.size

    inner class HomeItemsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(loc: Flight) {
            val itemflighttext = containerView.findViewById<TextView>(R.id.itemFlightDateText)

            itemflighttext.text = formatter.format(loc.time)
            itemflighttext.setBackgroundColor(loc.color)

            containerView.findViewById<TextView>(R.id.itemDepartureAirportCodeText).text = loc.departure.code
            containerView.findViewById<TextView>(R.id.itemDepartureAirportCityText).text = loc.departure.city

        }
    }
}


class HomeFragment : Fragment() {

    var viewDialog: ViewDialog? = null


    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
    private var disposable: Disposable? = null
    private val locationService by lazy {
        LocationServices.create()
    }
    private var locations: ArrayList<Model.location>? = ArrayList()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.title = "Calendrier"
        setHasOptionsMenu(true)
        viewDialog = ViewDialog(activity!!)

        selectLocations()

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private var selectedDate: LocalDate? = null
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    private val flightsAdapter = HomeItemsAdapter()
    private  var  flights :Map<LocalDate,List<Flight>>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exFiveRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        exFiveRv.adapter = flightsAdapter






    }

    override fun onStart() {
        super.onStart()
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.example_5_toolbar_color)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
    }

    private fun updateAdapterForDate(date: LocalDate?) {
        flightsAdapter.flights.clear()
        flightsAdapter.flights.addAll(flights!![date].orEmpty())
        flightsAdapter.notifyDataSetChanged()
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
                 R.id.add_loc -> {
                 val intent = Intent(context!!, AddLocActivity().javaClass)

                 activity!!.startActivity(intent)
             }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun selectLocations() {
        viewDialog!!.showDialog()

        disposable =
            locationService.selectLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        locations = result as ArrayList<Model.location>?
                        println(locations)
                        flights =  generateFlights(locations!!).groupBy { it.time.toLocalDate() }

                        prepareView()
                        flightsAdapter.notifyDataSetChanged()
                        viewDialog!!.hideDialog()

                    },
                    { error ->
                        Toast.makeText(context,"Opération échouée!",Toast.LENGTH_LONG).show()
                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                        viewDialog!!.hideDialog()
                    }
                )
    }

    private fun prepareView(){
        exFiveRv.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        val daysOfWeek = daysOfWeekFromLocale()

        val currentMonth = YearMonth.now()
        exFiveCalendar.setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
        exFiveCalendar.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.exFiveDayText
            val layout = view.exFiveDayLayout
            val flightTopView = view.exFiveDayFlightTop
            val flightBottomView = view.exFiveDayFlightBottom

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            exFiveCalendar.notifyDateChanged(day.date)
                            oldDate?.let { exFiveCalendar.notifyDateChanged(it) }
                            updateAdapterForDate(day.date)
                        }
                    }
                }
            }
        }
        exFiveCalendar.dayBinder = object : DayBinder<DayViewContainer> {
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
                    layout.setBackgroundResource(if (selectedDate == day.date) R.drawable.example_5_selected_bg else 0)

                    val flights
                            = flights!![day.date]
                    if (flights != null) {
                        if (flights.count() == 1) {
                            flightBottomView.setBackgroundColor(flights[0].color)
                        } else {
                            flightTopView.setBackgroundColor(flights[0].color)
                            flightBottomView.setBackgroundColor(flights[1].color)
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
        exFiveCalendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].name.take(3)
                        tv.setTextColorRes(R.color.example_5_text_grey)
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    }
                    month.yearMonth
                }
            }
        }

        exFiveCalendar.monthScrollListener = { month ->
            val title = "${monthTitleFormatter.format(month.yearMonth)} ${month.yearMonth.year}"
            exFiveMonthYearText.text = title

            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                exFiveCalendar.notifyDateChanged(it)
                updateAdapterForDate(null)
            }
        }

        exFiveNextMonthImage.setOnClickListener {
            exFiveCalendar.findFirstVisibleMonth()?.let {
                exFiveCalendar.smoothScrollToMonth(it.yearMonth.next)
            }
        }

        exFivePreviousMonthImage.setOnClickListener {
            exFiveCalendar.findFirstVisibleMonth()?.let {
                exFiveCalendar.smoothScrollToMonth(it.yearMonth.previous)
            }
        }
    }
}
