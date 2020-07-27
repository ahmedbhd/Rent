package com.rent.base

import android.app.Activity
import android.content.Context
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bumptech.glide.RequestManager
import com.rent.global.helper.Navigation
import com.rent.global.utils.observeOnlyNotNull
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import kotlin.reflect.KClass

abstract class BaseFragment : Fragment() {

    @Inject
    protected lateinit var glideLazy: Lazy<RequestManager>

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    protected fun getGlide(): RequestManager {
        return glideLazy.get()
    }


    /**
     * observe snackBarMessage and show snack bar
     * observe loader and show hide progress bar
     * observe dialog and chose dialog
     *
     * @param viewModel BaseAndroidViewModel
     */
    protected fun registerBaseObserver(viewModel: ViewModel) {
        if (viewModel is BaseAndroidViewModel) {
            registerSnackBar(viewModel)
            registerSimpleDialog(viewModel)
            registerChoseDialog(viewModel)
            registerProgressBar(viewModel)
            registerNavigation(viewModel)
        }
    }

    private fun registerNavigation(viewModel: BaseAndroidViewModel) {
        viewModel.navigation.observe(viewLifecycleOwner, Observer { navigate(it) })
    }

    private fun registerSnackBar(viewModel: BaseAndroidViewModel) {
        viewModel.snackBarMessage.observeOnlyNotNull(viewLifecycleOwner) { showSnackBar(it) }
    }

    private fun registerSimpleDialog(viewModel: BaseAndroidViewModel) {
        viewModel.simpleDialog.observeOnlyNotNull(
            viewLifecycleOwner
        ) { simpleDialog ->
            showSimpleDialog(
                simpleDialog.title,
                simpleDialog.message,
                simpleDialog.ok,
                simpleDialog.okActionBlock,
                simpleDialog.dismissActionBlock
            )
        }
    }

    private fun registerChoseDialog(viewModel: BaseAndroidViewModel) {
        viewModel.choseDialog.observeOnlyNotNull(
            viewLifecycleOwner
        ) { choseDialog ->
            showChoseDialog(
                choseDialog.title,
                choseDialog.message,
                choseDialog.ok,
                choseDialog.cancel,
                choseDialog.okActionBlock,
                choseDialog.cancelActionBlock,
                choseDialog.dismissActionBlock
            )
        }
    }

    private fun registerProgressBar(viewModel: BaseAndroidViewModel) {
        viewModel.progressBar.observeOnlyNotNull(viewLifecycleOwner) {
            when {
                it -> showProgressBar()
                else -> hideProgressBar()
            }
        }
    }

    /**
     * show simple ok dialog
     *
     * @param titleId  resources id optional
     * @param messageId  resources id
     * @param actionBlock action to do on click
     * @param dismissActionBlock action to do on dismiss optional
     *
     */
    protected fun showSimpleDialog(
        @StringRes titleId: Int? = null,
        @StringRes messageId: Int,
        @StringRes okId: Int,
        actionBlock: (() -> Unit)? = null,
        dismissActionBlock: (() -> Unit)? = null
    ) {
        activity?.let {
            (it as BaseActivity).showSimpleDialog(
                titleId,
                messageId,
                okId,
                actionBlock,
                dismissActionBlock
            )
        }
    }


    /**
     * show simple ok dialog
     * @param title  message string optional
     * @param message  message string
     * @param okActionBlock action to do on click
     * @param dismissActionBlock action to do on dismiss optional
     *
     */
    protected fun showSimpleDialog(
        title: String? = null,
        message: String,
        ok: String,
        okActionBlock: (() -> Unit)? = null,
        dismissActionBlock: (() -> Unit)? = null
    ) {
        activity?.let {
            (it as BaseActivity).showSimpleDialog(
                title,
                message,
                ok,
                okActionBlock,
                dismissActionBlock
            )
        }
    }


    /**
     * show simple ok dialog
     *
     * @param titleId      dialog title resources Id optional
     * @param messageId       message resources Id
     * @param okId        action button resources Id
     * @param cancelId     cancel button resources Id
     * @param okActionBlock    action to do on click ok optional
     * @param cancelActionBlock action to do on click cancel optional
     * @param dismissActionBlock action to do on dismiss optional
     */
    protected fun showChoseDialog(
        @StringRes titleId: Int? = null, @StringRes messageId: Int, @StringRes okId: Int,
        @StringRes cancelId: Int, okActionBlock: (() -> Unit)? = null,
        cancelActionBlock: (() -> Unit)? = null,
        dismissActionBlock: (() -> Unit)? = null
    ) {

        activity?.let {
            (it as BaseActivity).showChoseDialog(
                titleId,
                messageId,
                okId,
                cancelId,
                okActionBlock,
                cancelActionBlock,
                dismissActionBlock
            )
        }
    }

    /**
     * show simple ok dialog
     *
     * @param title      dialog title optional
     * @param message       message
     * @param ok        action button
     * @param cancel     cancel button
     * @param okActionBlock     action to do on click ok optional
     * @param cancelActionBlock action to do on click cancel optional
     * @param dismissActionBlock action to do on dismiss optional
     *
     */
    protected fun showChoseDialog(
        title: String? = null,
        message: String,
        ok: String,
        cancel: String,
        okActionBlock: (() -> Unit)? = null,
        cancelActionBlock: (() -> Unit)? = null,
        dismissActionBlock: (() -> Unit)? = null
    ) {
        activity?.let {
            (it as BaseActivity).showChoseDialog(
                title,
                message,
                ok,
                cancel,
                okActionBlock,
                cancelActionBlock,
                dismissActionBlock
            )
        }
    }


    /**
     * try to hide Keyboard from the focused screen
     */
    protected fun hideKeyboard() {
        activity?.let { (it as BaseActivity).hideKeyboard() }
    }


    /**
     * This method show simple Snackbar
     *
     * @param message message dialog text
     */
    protected fun showSnackBar(message: String) {
        activity?.let { (it as BaseActivity).showSnackBar(message) }
    }

    /**
     * This method show simple Snackbar
     *
     * @param messageId message dialog text id
     */
    fun showSnackBar(@StringRes messageId: Int) {

        activity?.let { (it as BaseActivity).showSnackBar(messageId) }
    }

    /**
     * hide snackBar if it's on screen
     */
    fun hideSnackBar() {
        activity?.let { (it as BaseActivity).hideSnackBar() }
    }

    /**
     * show blocking progressBar on the root of the activity
     */
    fun showProgressBar() {
        activity?.let { (it as BaseActivity).showProgressBar() }
    }

    /**
     * hide blocking progressBar
     */
    fun hideProgressBar() {
        activity?.let { (it as BaseActivity).hideProgressBar() }
    }

    /**
     * startActivity to class
     */
    fun navigateToClass(kClass: KClass<out Activity>, shouldFinish: Boolean = false) {
        activity?.let {
            if (it is BaseActivity)
                it.navigateToActivity(kClass, shouldFinish)
        }
    }

    /**
     * handling navigation actions
     * @param navigationTo activity to navigate to
     */
    @CallSuper
    open fun navigate(navigationTo: Navigation) {

    }
}