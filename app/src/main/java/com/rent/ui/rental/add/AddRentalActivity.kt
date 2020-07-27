package com.rent.ui.rental.add

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.OrientationHelper
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.applikeysolutions.cosmocalendar.view.CalendarView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.rent.R
import com.rent.base.BaseActivity
import com.rent.ui.shared.adapter.util.ViewDialog
import com.rent.data.LocataireServices
import com.rent.data.LocationServices
import com.rent.data.model.locataire.Locataire
import com.rent.data.model.rental.Rental
import com.rent.databinding.ActivityAddRentalBinding
import com.rent.global.helper.ViewModelFactory
import com.rent.global.utils.getColorCompat
import com.rent.global.utils.observeOnlyNotNull
import com.rent.tools.PhoneGrantings
import com.rent.ui.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_rental.*
import kotlinx.android.synthetic.main.add_cal_bottomsheet.*
import kotlinx.android.synthetic.main.example_5_event_item_view.*
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class AddRentalActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: AddRentalViewModel by viewModels { viewModelFactory }

//    private lateinit var viewDialog: ViewDialog
    private var calendarView: CalendarView? = null
    lateinit var myDialog: Dialog

//    private var resultloc: ArrayList<Locataire>? = ArrayList()
    private var mDefaultColor: Int = 0
    private lateinit var newStringColor: String
//    private var selectedLocatair: String = "-"
    private var stringTel: String = ""
//    private var locataires: ArrayList<String> = ArrayList()
//    private var newLocation: Rental = Rental()

    private var disposable: Disposable? = null
    private val locationService by lazy {
        LocationServices.create()
    }
    private val locataireService by lazy {
        LocataireServices.create()
    }
    private lateinit var binding: ActivityAddRentalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_rental)
        val actionBar = (this as AppCompatActivity).supportActionBar
        actionBar!!.title = "Nouveau Location"
        myDialog = Dialog(this)
//        viewDialog = ViewDialog(this)
        mDefaultColor = this.getColorCompat(R.color.greencircle)
        viewModel.newRental.color = "#9FE554"

        initCalendar()
//        loadLocataires()

        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheetCal)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        imageDateAdd.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        }
        cancel_add.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        save_add.setOnClickListener {
            println(calendarView!!.selectedDays[0])
            println(calendarView!!.selectedDays[calendarView!!.selectedDays.size - 1])

            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = format.format(calendarView!!.selectedDays[0].calendar.time)
            val end =
                format.format(calendarView!!.selectedDays[calendarView!!.selectedDays.size - 1].calendar.time)

            add_date_start.text = start
            add_date_end.text = end

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }

        imageTimeAdd.setOnClickListener {

            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int
                    ->
                    add_time_add.text = "$hourOfDay:$minute"


                },
                0, 0, true
            )
            timePickerDialog.show()
        }


        mark.setOnClickListener {
            openColorPicker()
        }


        add_tel.setOnClickListener {
            ShowPopupTel()
        }
//        viewModel.locataires.value!!.add("-")

        addLocation.setOnClickListener {
//            if (PhoneGrantings.isNetworkAvailable(this)) {
                viewModel.newRental.dateDebut =
                    add_date_start.text.toString() + " " + add_time_add.text + ":00"
                viewModel.newRental.dateFin =
                    add_date_end.text.toString() + " " + add_time_add.text + ":00"
                checkInputs()
//            } else
//                Toast.makeText(this, "Internet Non Disponible", Toast.LENGTH_SHORT).show()
        }


        registerBindingAndBaseObservers()
    }

    private fun registerBindingAndBaseObservers() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        registerBaseObservers(viewModel)
        registerAddRentalObservers()
    }

    private fun registerAddRentalObservers() {
        viewModel.locataires.observeOnlyNotNull(this) {
            prepareLocataireSpinner(it)
        }
    }

    private fun checkInputs() {
        if ((viewModel.selectedLocatair != "-") && add_date_start.text != "00-00-00") {
            addLocation()

            return
        }

        if (!add_cin.text.isNullOrEmpty() && !add_name.text.isNullOrEmpty() && add_date_start.text != "00-00-00") {
            println("here2")

            addLocataire()
            return
        }
        Toast.makeText(this, "Information Incorrectes", Toast.LENGTH_LONG).show()

    }


    private fun prepareLocataireSpinner(locataires: ArrayList<String>) {
//        val adapter = ArrayAdapter(
//            this,
//            R.layout.drop_down_list_tel, locataires
//        )
//        spinnerusers.adapter = adapter
//        spinnerusers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
//                // An item was selected. You can retrieve the selected item using
//                selectedLocatair = parent.getItemAtPosition(pos) as String
//                if (pos > 0)
//                    viewModel.newRental.locataire = viewModel.resultloc!![pos - 1]
//                else
//                    viewModel.newRental.locataire = viewModel.resultloc!![pos]
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Another interface callback
//            }
//        }
    }


    private fun openColorPicker() {
        val onAmbilWarnaListener = object : OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog) {

            }

            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                mDefaultColor = color
                val background = mark.background
                (background as GradientDrawable).setColor(mDefaultColor)
                newStringColor = String.format("#%06X", 0xFFFFFF and mDefaultColor)
                viewModel.newRental.color = newStringColor
                initCalendar()
            }
        }
        val colorPicker = AmbilWarnaDialog(this, mDefaultColor, onAmbilWarnaListener)
        colorPicker.show()
    }

    private fun initCalendar() {
        calendarView = findViewById<CalendarView>(R.id.calendar_view)
        calendarView!!.calendarOrientation = OrientationHelper.HORIZONTAL
        calendarView!!.selectionType = SelectionType.RANGE
        calendarView!!.selectedDayBackgroundColor = mDefaultColor
    }

    fun ShowPopupTel() {
        myDialog.setCanceledOnTouchOutside(false)

        myDialog.show()

        myDialog.setContentView(R.layout.custompopup)

//        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txt: TextView = myDialog.findViewById(R.id.txtcloseTel) as TextView
        val imgAdd: ImageView = myDialog.findViewById(R.id.addTelimg) as ImageView
        val addTel1: EditText = myDialog.findViewById(R.id.addtel) as EditText

        val btn: Button = myDialog.findViewById(R.id.saveTel) as Button
        val myLayout: LinearLayout = myDialog.findViewById(R.id.listnumbers) as LinearLayout


        val telArray = ArrayList<EditText>()

        if (stringTel != "") {
            val tab = stringTel.split(",")
            addTel1.setText(tab[0])
            for (i in 1 until tab.size) {
                val myEditText = EditText(this) // Pass it an Activity or Context
                myEditText.hint = "Telephone"
                myEditText.inputType = InputType.TYPE_CLASS_PHONE
                myEditText.setText(tab[i])
                myEditText.layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ) // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                myLayout.addView(myEditText)
                telArray.add(myEditText)
            }

        }

        imgAdd.setOnClickListener {
            val myEditText = EditText(this) // Pass it an Activity or Context
            myEditText.hint = "Telephone"
            myEditText.inputType = InputType.TYPE_CLASS_PHONE
            myEditText.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ) // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
            myLayout.addView(myEditText)
            telArray.add(myEditText)
        }



        txt.setOnClickListener { myDialog.dismiss() }
        btn.setOnClickListener {
            stringTel = addTel1.text.toString()
            telArray.forEach {
                stringTel = stringTel + "," + it.text.toString()
            }
            println(stringTel)
            myDialog.dismiss()
        }

    }


//    private fun loadLocataires() {
////        viewDialog.showDialog()
//        disposable =
//            locataireService.selectLocataires()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { result ->
//                        resultloc = result as ArrayList<Locataire>?
//                        println(resultloc)
////                        resultloc!!.forEach {
////                            locataires.add(it.fullName + " - " + it.cin)
////                        }
//                        prepareLocataireSpinner()
//                        viewDialog.hideDialog()
//
//                    },
//                    { error ->
//                        Toast.makeText(this, "Opération échouée!", Toast.LENGTH_LONG).show()
//                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
//                        viewDialog.hideDialog()
//                    }
//
//                )
//    }

    private fun addLocataire() {
//        viewDialog.showDialog()

        viewModel.newLocataire =
            Locataire(0, add_cin.text.toString(), add_name.text.toString(), stringTel)
        viewModel.newRental.locataire = viewModel.newLocataire!!
        viewModel.addLocataire()
//        disposable =
//            locataireService.addLocataire(newLocataire)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    {
//                        viewDialog.hideDialog()
//                        newLocation.locataire = it as Locataire
//                        addLocation()
//                    },
//                    { error ->
//                        Toast.makeText(this, "Opération échouée!", Toast.LENGTH_LONG).show()
//                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
//                        viewDialog.hideDialog()
//                    }
//                )

    }

    private fun addLocation() {
//        viewDialog.showDialog()

//        disposable =
//            locationService.ajouterLocation(newLocation)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    {
//                        viewDialog.hideDialog()
//
//                        Toast.makeText(this, "Ajout avec succée", Toast.LENGTH_LONG).show()
//                        val intent = Intent(this, MainActivity().javaClass)
//
//                        startActivity(intent)
//                    },
//                    { error ->
//                        Toast.makeText(this, "Opération échouée!", Toast.LENGTH_LONG).show()
//                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
//                        viewDialog.hideDialog()
//                    }
//                )
        viewModel.addRental()
    }
}
