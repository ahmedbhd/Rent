package com.rent

import android.os.Bundle
import androidx.annotation.IdRes
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.OrientationHelper
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioGroup
import android.widget.Toast
import com.applikeysolutions.cosmocalendar.selection.MultipleSelectionManager
import com.applikeysolutions.cosmocalendar.selection.criteria.BaseCriteria
import com.applikeysolutions.cosmocalendar.selection.criteria.WeekDayCriteria
import com.applikeysolutions.cosmocalendar.selection.criteria.month.CurrentMonthCriteria
import com.applikeysolutions.cosmocalendar.selection.criteria.month.NextMonthCriteria
import com.applikeysolutions.cosmocalendar.selection.criteria.month.PreviousMonthCriteria
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.applikeysolutions.cosmocalendar.view.CalendarView

import java.util.*

class Main2Activity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {

    private var calendarView: CalendarView? = null

    private var threeMonthsCriteriaList: MutableList<BaseCriteria>? = null
    private var fridayCriteria: WeekDayCriteria? = null

    private var fridayCriteriaEnabled: Boolean = false
    private var threeMonthsCriteriaEnabled: Boolean = false

    private var menuFridays: MenuItem? = null
    private var menuThreeMonth: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

        initViews()
        createCriterias()
    }

    private fun initViews() {
        calendarView = findViewById<CalendarView>(R.id.calendar_view)
        (findViewById<RadioGroup>(R.id.rg_orientation)).setOnCheckedChangeListener(this)
        (findViewById<RadioGroup>(R.id.rg_selection_type)).setOnCheckedChangeListener(this)
    }

    private fun createCriterias() {
        fridayCriteria = WeekDayCriteria(Calendar.FRIDAY)

        threeMonthsCriteriaList = ArrayList()
        threeMonthsCriteriaList!!.add(CurrentMonthCriteria())
        threeMonthsCriteriaList!!.add(NextMonthCriteria())
        threeMonthsCriteriaList!!.add(PreviousMonthCriteria())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_default_calendar_activity, menu)
        menuFridays = menu.findItem(R.id.select_all_fridays)
        menuThreeMonth = menu.findItem(R.id.select_three_months)
        return true
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
        Toast.makeText(this, "Selected " + calendarView!!.selectedDays.size, Toast.LENGTH_SHORT).show()
    }

    override fun onCheckedChanged(group: RadioGroup, @IdRes checkedId: Int) {
        clearSelectionsMenuClick()
        when (checkedId) {
            R.id.rb_horizontal -> calendarView!!.calendarOrientation = OrientationHelper.HORIZONTAL

            R.id.rb_vertical -> calendarView!!.calendarOrientation = OrientationHelper.VERTICAL

            R.id.rb_single -> {
                calendarView!!.selectionType = SelectionType.SINGLE
                menuFridays!!.isVisible = false
                menuThreeMonth!!.isVisible = false
            }

            R.id.rb_multiple -> {
                calendarView!!.selectionType = SelectionType.MULTIPLE
                menuFridays!!.isVisible = true
                menuThreeMonth!!.isVisible = true
            }

            R.id.rb_range -> {
                calendarView!!.selectionType = SelectionType.RANGE
                menuFridays!!.isVisible = false
                menuThreeMonth!!.isVisible = false
            }

            R.id.rb_none -> {
                calendarView!!.selectionType = SelectionType.NONE
                menuFridays!!.isVisible = false
                menuThreeMonth!!.isVisible = false
            }
        }
    }
}
