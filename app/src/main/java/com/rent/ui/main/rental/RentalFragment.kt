package com.rent.ui.main.rental


import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
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
import com.rent.ui.rental.add.AddRentalActivity
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

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.title = "Locations"
        setHasOptionsMenu(true)

        binding = FragmentRentalBinding.bind(root)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_location, menu)

        // Associate searchable configuration with the SearchView
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView

        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(requireActivity().componentName)
        )
        searchView.isSubmitButtonEnabled = false
        searchView.setOnQueryTextListener(viewModel)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_loc -> {
                navigateToClass(AddRentalActivity::class)
            }

        }
        return super.onOptionsItemSelected(item)
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
            startActivity(it)
        }
    }
}
