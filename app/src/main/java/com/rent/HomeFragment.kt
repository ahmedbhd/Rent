package com.rent


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.recyclerview.widget.OrientationHelper
import com.applikeysolutions.cosmocalendar.selection.MultipleSelectionManager
import com.applikeysolutions.cosmocalendar.selection.criteria.BaseCriteria
import com.applikeysolutions.cosmocalendar.selection.criteria.WeekDayCriteria
import com.applikeysolutions.cosmocalendar.selection.criteria.month.CurrentMonthCriteria
import com.applikeysolutions.cosmocalendar.selection.criteria.month.NextMonthCriteria
import com.applikeysolutions.cosmocalendar.selection.criteria.month.PreviousMonthCriteria
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.applikeysolutions.cosmocalendar.view.CalendarView
import com.rent.data.LocationServices
import com.rent.data.Model
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Consumer
import android.provider.SyncStateContract.Helpers.update
import android.text.format.DateFormat
import android.widget.FrameLayout
import androidx.annotation.IntegerRes
import com.applikeysolutions.cosmocalendar.model.Day
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(){

    private var disposable: Disposable? = null
    private val locationService by lazy {
        LocationServices.create()
    }

    private var locations: ArrayList<Model.location>? = ArrayList()

    private var calendarView: CalendarView? = null

    private var threeMonthsCriteriaList: MutableList<BaseCriteria>? = null
    private var fridayCriteria: WeekDayCriteria? = null

    private var fridayCriteriaEnabled: Boolean = false
    private var threeMonthsCriteriaEnabled: Boolean = false

    private var menuFridays: MenuItem? = null
    private var menuThreeMonth: MenuItem? = null

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(root)
        createCriterias()
        val cLayout = root.findViewById<FrameLayout>(R.id.calendarLayout)

        return root
    }


    private fun initViews(v: View) {
        calendarView = v.findViewById<CalendarView>(R.id.calendar_view)
        calendarView!!.calendarOrientation = OrientationHelper.HORIZONTAL
        calendarView!!.selectionType = SelectionType.RANGE
        if (calendarView!!.selectionManager is RangeSelectionManager) {
            val rangeSelectionManager = calendarView!!.selectionManager as RangeSelectionManager
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 3)
            rangeSelectionManager.toggleDay( Day(Calendar.getInstance()))
            rangeSelectionManager.toggleDay( Day(calendar))
            calendarView!!.update()
        }
        calendarView!!.isClickable = false
    }

    private fun createCriterias() {
        fridayCriteria = WeekDayCriteria(Calendar.FRIDAY)

        threeMonthsCriteriaList = ArrayList()
        threeMonthsCriteriaList!!.add(CurrentMonthCriteria())
        threeMonthsCriteriaList!!.add(NextMonthCriteria())
        threeMonthsCriteriaList!!.add(PreviousMonthCriteria())
    }






    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.select_all_fridays -> {
                fridayMenuClick()
                return true
            }

            R.id.select_three_months -> {
                threeMonthsMenuClick()
                return true
            }

            R.id.clear_selections -> {
                clearSelectionsMenuClick()
                return true
            }

            R.id.log_selected_days -> {
                logSelectedDaysMenuClick()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun fridayMenuClick() {
        if (fridayCriteriaEnabled) {
            menuFridays!!.title = getString(R.string.select_all_fridays)
            unselectAllFridays()
        } else {
            menuFridays!!.title = getString(R.string.unselect_all_fridays)
            selectAllFridays()
        }
        fridayCriteriaEnabled = !fridayCriteriaEnabled
    }

    private fun threeMonthsMenuClick() {
        if (threeMonthsCriteriaEnabled) {
            menuThreeMonth!!.title = getString(R.string.select_three_months)
            unselectThreeMonths()
        } else {
            menuThreeMonth!!.title = getString(R.string.unselect_three_months)
            selectThreeMonths()
        }
        threeMonthsCriteriaEnabled = !threeMonthsCriteriaEnabled
    }

    private fun selectAllFridays() {
        if (calendarView!!.selectionManager is MultipleSelectionManager) {
            (calendarView!!.selectionManager as MultipleSelectionManager).addCriteria(fridayCriteria)
        }
        calendarView!!.update()
    }

    private fun unselectAllFridays() {
        if (calendarView!!.selectionManager is MultipleSelectionManager) {
            (calendarView!!.selectionManager as MultipleSelectionManager).removeCriteria(fridayCriteria)
        }
        calendarView!!.update()
    }

    private fun selectThreeMonths() {
        if (calendarView!!.selectionManager is MultipleSelectionManager) {
            (calendarView!!.selectionManager as MultipleSelectionManager).addCriteriaList(threeMonthsCriteriaList)
        }
        calendarView!!.update()
    }

    private fun unselectThreeMonths() {
        if (calendarView!!.selectionManager is MultipleSelectionManager) {
            (calendarView!!.selectionManager as MultipleSelectionManager).removeCriteriaList(threeMonthsCriteriaList)
        }
        calendarView!!.update()
    }

    private fun clearSelectionsMenuClick() {
        calendarView!!.clearSelections()

        fridayCriteriaEnabled = false
        threeMonthsCriteriaEnabled = false
        menuFridays!!.title = getString(R.string.select_all_fridays)
        menuThreeMonth!!.title = getString(R.string.select_three_months)
    }


    private fun logSelectedDaysMenuClick() {
        Toast.makeText(context!!, "Selected " + calendarView!!.selectedDays.size, Toast.LENGTH_SHORT).show()
    }




    private fun selectLocations() {
        val format =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        disposable =
            locationService.selectLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        locations = result as ArrayList<Model.location>?
                        println("hhhhhhhhhhhh $locations")
                        locations!!.forEach( Consumer {
                            val dateStart = format.parse(it.start)
                            val dateEnd = format.parse(it.end)
//                            calendarView.setMinDate(calendar.getTimeInMillis());
//                            calendar.add(Calendar.DAY_OF_MONTH, 90);
//                            calendarView.setMaxDate(calendar.getTimeInMillis());
                            val mDayStart          =  DateFormat.format("dd",   dateStart)
                            val mDayEnd          =  DateFormat.format("dd",   dateEnd)

                            if (calendarView!!.selectionManager is RangeSelectionManager) {
                                val rangeSelectionManager = calendarView!!.selectionManager as RangeSelectionManager

                                val calStart = Calendar.getInstance()
                                val sDay          =  DateFormat.format("dd",   calStart)
                                val sMonth  = DateFormat.format("MM",   calStart)
                                val sYear         = DateFormat.format("yyyy", calStart)
                                calStart.set(Calendar.DAY_OF_MONTH,sDay.toString().toInt())
                                calStart.set(Calendar.MONTH,sMonth.toString().toInt()) // 0-11 so 1 less
                                calStart.set(Calendar.YEAR, sYear.toString().toInt())

                                val calEnd = Calendar.getInstance()
                                val eDay          =  DateFormat.format("dd",   calEnd)
                                val eMonth  = DateFormat.format("MM",   calEnd)
                                val eYear         = DateFormat.format("yyyy", calEnd)
                                calEnd.set(Calendar.DAY_OF_MONTH,eDay.toString().toInt())
                                calEnd.set(Calendar.MONTH,eMonth.toString().toInt()) // 0-11 so 1 less
                                calEnd.set(Calendar.YEAR, eYear.toString().toInt())

                                rangeSelectionManager.toggleDay(Day(calStart))
                                rangeSelectionManager.toggleDay(Day(calEnd))
                                calendarView!!.update()
                            }

                        })

                    },
                    { error -> println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa") }
                )
    }
}
