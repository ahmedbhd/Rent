package com.rent.ui.main.payment


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rent.ui.shared.adapter.CustomListAdapter
import com.rent.data.PaymentServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rent.R
import com.rent.ui.shared.adapter.util.ViewDialog
import com.rent.data.model.payment.Payment
import com.rent.global.helper.ViewModelFactory
import com.rent.tools.PhoneGrantings
import com.rent.ui.splash.SplashViewModel
import javax.inject.Inject


class PaymentFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: PaymentViewModel by viewModels{ viewModelFactory }

    companion object {
        fun newInstance(): PaymentFragment {
            return PaymentFragment()
        }
    }
    //private var customListAdapter: CustomListAdapter? = null

    private lateinit var viewDialog: ViewDialog
    private val paymentService by lazy {
        PaymentServices.create()
    }
    private var disposable: Disposable? = null


    var list: RecyclerView? = null

    private lateinit var customListAdapter: RecyclerView.Adapter<*>

    //private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable
    private var payments: MutableList<Payment>? = ArrayList()
    private lateinit var swipe: SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_payment, container, false)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.title = "Paiements"
        list = root.findViewById(R.id.det_listview22) as RecyclerView
        swipe = root.findViewById(R.id.swipeContainer) as SwipeRefreshLayout

        swipe.setOnRefreshListener {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            selectLocPayments()
            swipe.isRefreshing = false

        }
        viewDialog = ViewDialog(requireActivity())

        if (PhoneGrantings.isNetworkAvailable(requireActivity())) // online actions
            selectLocPayments()
        else
            Toast.makeText(context, "Internet Non Disponible", Toast.LENGTH_SHORT).show()

        return root
    }

    private fun selectLocPayments() {
        viewDialog.showDialog()

        disposable =
            paymentService.selectPayments()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        payments = result as MutableList<Payment>?
                        println("hhhhhhhhhhhh $payments")
                        //dataset.add(payments!![0].type)

                        prepareRecyclerView()
                        customListAdapter.notifyDataSetChanged()
                        viewDialog.hideDialog()

                    },
                    { error ->
                        Toast.makeText(context, "Opération échouée!", Toast.LENGTH_LONG).show()
                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                        viewDialog.hideDialog()
                    })
    }

    private fun prepareRecyclerView() {

        customListAdapter = CustomListAdapter(payments!!, requireContext(), requireActivity())

        viewManager = LinearLayoutManager(requireContext())

        colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
        deleteIcon = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_delete_white_24dp
        )!!

        list!!.apply {
            setHasFixedSize(true)
            adapter = customListAdapter
            layoutManager = viewManager
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                viewHolder2: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                (customListAdapter as CustomListAdapter).removeItem(
                    viewHolder.adapterPosition,
                    viewHolder
                )
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
                val iconMarginVertical =
                    (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    colorDrawableBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.left + iconMarginVertical,
                        itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMarginVertical
                    )
                } else {
                    colorDrawableBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                        itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical,
                        itemView.bottom - iconMarginVertical
                    )
                    deleteIcon.level = 0
                }

                colorDrawableBackground.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )

                deleteIcon.draw(c)

                c.restore()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(list)
    }

}
