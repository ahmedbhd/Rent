package com.rent.ui.main.rental


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rent.R
import com.rent.base.BaseFragment
import com.rent.data.model.relations.RentalWithLocataire
import com.rent.databinding.FragmentRentalBinding
import com.rent.global.helper.Navigation
import com.rent.global.helper.ViewModelFactory
import com.rent.global.listener.DialogCustomCallListener
import com.rent.global.utils.ExtraKeys
import com.rent.global.utils.observeOnlyNotNull
import com.rent.ui.main.calendar.REQUEST_CODE
import com.rent.ui.rental.detail.RentalDetailActivity
import com.rent.ui.shared.dialog.CustomCallDialog
import javax.inject.Inject


class RentalFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: RentalViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var rentalListAdapter: RentalListAdapter

    private lateinit var binding: FragmentRentalBinding

    companion object {
        fun newInstance(): RentalFragment {
            return RentalFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_rental, container, false)

        binding = FragmentRentalBinding.bind(root)
        registerBindingAndBaseObservers()

        return root
    }

    private fun registerBindingAndBaseObservers() {
        binding.viewModel = viewModel
        binding.activity = requireActivity()
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerBaseObserver(viewModel)
        registerRentalRecycler()
        registerRentalObservers()
    }

    private fun registerRentalObservers() {
        viewModel.callDialog.observeOnlyNotNull(viewLifecycleOwner) {
            showCallDialog(it.stringTel, it.dialogCustomCallListener, it.dismissActionBlock)
        }

        viewModel.telToBeCalled.observeOnlyNotNull(viewLifecycleOwner) {
            phoneCall(it)
            viewModel.setTelNull()
        }
    }

    private fun showCallDialog(
        stringTel: String,
        dialogCustomCallListener: DialogCustomCallListener,
        dismissActionBlock: (() -> Unit)?
    ) {
        activity?.let {
            CustomCallDialog(
                requireContext(),
                stringTel,
                dialogCustomCallListener,
                dismissActionBlock
            ).show()
        }
    }

    private fun phoneCall(tel: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))
        requireActivity().startActivity(intent)
    }

    private fun registerRentalRecycler() {
        rentalListAdapter.setListenerClick(viewModel)
        rentalListAdapter.setListenerSwipe(viewModel)
        binding.flistfav.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.flistfav.adapter = rentalListAdapter
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
