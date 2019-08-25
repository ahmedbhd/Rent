package com.rent


import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.util.Attributes
import com.rent.adapters.LocListViewAdapter
import com.rent.data.LocationServices
import com.rent.data.Model
import com.rent.tools.PhoneGrantings
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.rent.adapters.CustomListAdapter
import com.rent.adapters.CustomListLocAdapter
import com.rent.adapters.util.ViewDialog
import java.util.*


class LocationFragment : Fragment(), SearchView.OnQueryTextListener {


    private lateinit var viewDialog: ViewDialog
    private var disposable: Disposable? = null
    private val locationService by lazy {
        LocationServices.create()
    }
    private var locations: MutableList<Model.location>? = ArrayList()
    private var backUpLocations: MutableList<Model.location>? = ArrayList()

    var list: RecyclerView? = null


    private lateinit var customListAdapter: CustomListLocAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable
    private lateinit var swipe: SwipeRefreshLayout

    companion object {
        fun newInstance(): LocationFragment {
            return LocationFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_location, container, false)

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.title = "Locations"
        setHasOptionsMenu(true)

        viewDialog = ViewDialog(activity!!)

        list = root.findViewById(R.id.flistfav) as RecyclerView

        if (PhoneGrantings.isNetworkAvailable(activity!!)) // online actions
            selectLocations()
        else
            Toast.makeText(context, "Internet Non Disponible", Toast.LENGTH_SHORT).show()


        list = root.findViewById(R.id.flistfav) as RecyclerView
        swipe =  root.findViewById(R.id.swipe_containerLoc) as SwipeRefreshLayout
        swipe.setOnRefreshListener {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            selectLocations()
            swipe.isRefreshing = false

        }


        selectLocations()
        return root
    }


    private fun selectLocations() {
//        viewDialog.showDialog()

        disposable =
            locationService.selectLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
//                        mAdapter = LocListViewAdapter(context!!,activity!!, result as ArrayList<Model.location>, activity)
//                        mAdapter!!.mode = Attributes.Mode.Single
//                        list!!.adapter = mAdapter
                        locations = result as MutableList<Model.location>?
//                        viewDialog.hideDialog()
                        backUpLocations = locations
                        prepareRecyclerView()
                        customListAdapter.notifyDataSetChanged()

                    },
                    { error ->
                        Toast.makeText(context,"Opération échouée!",Toast.LENGTH_LONG).show()
                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                        viewDialog.hideDialog()
                    }
                )
    }

    private fun prepareRecyclerView(){

        customListAdapter = CustomListLocAdapter(locations!!,context!!,activity!!)

        viewManager = LinearLayoutManager(context!!)

        colorDrawableBackground = ColorDrawable(Color.parseColor("#0097A7"))
        deleteIcon = ContextCompat.getDrawable(context!!, R.drawable.phonecall)!!

        list!!.apply {
            setHasFixedSize(true)
            adapter = customListAdapter
            layoutManager = viewManager
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                customListAdapter.ShowPopupTel(viewHolder.adapterPosition, viewHolder)
                customListAdapter.notifyItemChanged(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                } else {
                    colorDrawableBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                    deleteIcon.level = 0
                }

                colorDrawableBackground.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                deleteIcon.draw(c)

                c.restore()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(list)
    }


    override fun onCreateOptionsMenu(menu: Menu? , inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_location, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu!!.findItem(R.id.search)
        val searchView =  searchMenuItem.actionView as SearchView

        searchView.setSearchableInfo(searchManager.
                                getSearchableInfo(activity!!.componentName))
        searchView.isSubmitButtonEnabled = false
        searchView.setOnQueryTextListener(this)
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

    override fun onQueryTextChange(p0: String): Boolean {
        if (p0.isNotEmpty()){
            println(p0)
            locations = backUpLocations !!.filter { row ->
                row.locataire.full_name.toLowerCase(Locale.getDefault()).contains(p0.toLowerCase(Locale.getDefault())) || row.locataire.num_tel.contains(p0) }
                    as MutableList<Model.location>
            println(locations)
            prepareRecyclerView()
            customListAdapter.notifyDataSetChanged()
        }else {
            locations = backUpLocations
            prepareRecyclerView()
            customListAdapter.notifyDataSetChanged()
        }
        return false
    }

    override fun onQueryTextSubmit(p0: String): Boolean {

        return false
     }
}
