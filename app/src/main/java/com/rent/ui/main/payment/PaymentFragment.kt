package com.rent.ui.main.payment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rent.R
import com.rent.base.BaseFragment
import com.rent.databinding.FragmentPaymentBinding
import com.rent.global.helper.ViewModelFactory
import com.rent.global.utils.observeOnlyNotNull
import com.rent.ui.shared.dialog.CustomPaymentDialog
import javax.inject.Inject


class PaymentFragment : BaseFragment() {
    companion object {
        fun newInstance(): PaymentFragment {
            return PaymentFragment()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: PaymentViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var paymentListAdapter: PaymentListAdapter

    lateinit var binding: FragmentPaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_payment, container, false)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.title = "Paiements"

        binding = FragmentPaymentBinding.bind(root)
        registerBindingAndBaseObservers()

        return root
    }

    private fun registerBindingAndBaseObservers() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerBaseObserver(viewModel)
        registerRecycler()
        registerPaymentDialog()
    }

    private fun registerRecycler() {
        paymentListAdapter.setClickListener(viewModel)
        paymentListAdapter.setSwipeListener(viewModel)
        binding.paymentRecycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.paymentRecycler.adapter = paymentListAdapter
    }

    private fun registerPaymentDialog() {
        viewModel.paymentDialog.observeOnlyNotNull(this) { dialog ->
            activity?.let {
                CustomPaymentDialog(
                    requireContext(),
                    dialog.payment,
                    dialog.paymentDialogListener,
                    dialog.dismissActionBlock
                ).show()
            }
        }
    }
}
