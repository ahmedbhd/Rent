package com.rent.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import com.bumptech.glide.RequestManager
import com.rent.R
import com.rent.global.helper.Navigation
import com.rent.global.utils.DebugLog
import com.rent.global.utils.TAG
import com.rent.global.utils.observeOnlyNotNull
import com.rent.ui.shared.dialog.CustomChooseDialog
import com.rent.ui.shared.dialog.CustomProgressDialog
import com.rent.ui.shared.dialog.CustomSimpleDialog
import com.rent.ui.shared.view.CustomSnackBar
import dagger.Lazy
import dagger.android.AndroidInjection
import javax.inject.Inject
import kotlin.reflect.KClass


abstract class BaseActivity : AppCompatActivity() {

    @Inject
    protected lateinit var glideLazy: Lazy<RequestManager>

    private var customSnackBar: CustomSnackBar? = null
    private var progressDialog: CustomProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        super.onCreate(savedInstanceState)
    }

    /**
     * observe snackBarMessage and show snack bar
     * observe loader and show hide progress bar
     * observe dialog and show dialog
     *
     * @param viewModel BaseAndroidViewModel
     */
    protected fun registerBaseObservers(viewModel: ViewModel) {
        if (viewModel is BaseAndroidViewModel) {
            registerSnackBar(viewModel)
            registerSimpleDialog(viewModel)
            registerChoseDialog(viewModel)
            registerProgressBar(viewModel)
            registerNavigation(viewModel)
        }
    }

    private fun registerNavigation(viewModel: BaseAndroidViewModel) {
        viewModel.navigation.observe(this) {
            navigate(it)
        }
    }

    private fun registerSnackBar(viewModel: BaseAndroidViewModel) {
        viewModel.snackBarMessage.observeOnlyNotNull(this) { showSnackBar(it) }
    }

    private fun registerSimpleDialog(viewModel: BaseAndroidViewModel) {
        viewModel.simpleDialog.observeOnlyNotNull(
            this
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
            this
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
        viewModel.progressBar.observeOnlyNotNull(this) {
            when {
                it -> showProgressBar()
                else -> hideProgressBar()
            }
        }
    }

    protected fun getGlide(): RequestManager {
        return glideLazy.get()
    }

    /**
     * This method show simple Snackbar
     *
     * @param messageId message dialog text id
     */
    fun showSnackBar(@StringRes messageId: Int) {
        showSnackBar(getString(messageId))
    }

    /**
     * hide snackBar if it's on screen
     */
    fun hideSnackBar() {
        if (!isFinishing) {
            customSnackBar?.dismiss()
            customSnackBar = null
        }
    }

    /**
     * This method show simple SnackBar
     *
     * @param message message dialog text
     */
    fun showSnackBar(message: String) {
        if (!isFinishing) {
            val root = window.decorView.findViewById<ViewGroup>(android.R.id.content)
            customSnackBar = CustomSnackBar.make(root, CustomSnackBar.DURATION).apply {
                setText(message)
                show()
            }
        }
    }

    /*
     * show blocking progressBar on the root of the activity
     */
    fun showProgressBar() {
        if (!isFinishing) {
            if (progressDialog == null) {
                progressDialog = CustomProgressDialog(
                    this,
                    R.style.ProgressDialogStyle
                )
                    .apply {
                        setCancelable(false)
                    }

            }
            if (progressDialog?.isShowing != true) {
                progressDialog!!.show()
            }
        }
    }

    /**
     * hide blocking progressBar
     */
    fun hideProgressBar() {
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    /**
     * show simple ok dialog
     *
     * @param titleId  resources id optional
     * @param messageId  resources id
     * @param okActionBlock action to do on click
     * @param dismissActionBlock action to do on dismiss optional
     *
     */
    fun showSimpleDialog(
        @StringRes titleId: Int? = null,
        @StringRes messageId: Int,
        @StringRes okId: Int = R.string.global_ok,
        okActionBlock: (() -> Unit)? = null,
        dismissActionBlock: (() -> Unit)? = null
    ) {
        if (!isFinishing) {
            val title = titleId?.let { getString(it) }
            showSimpleDialog(
                title,
                getString(messageId),
                getString(okId),
                okActionBlock,
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
     */
    fun showSimpleDialog(
        title: String? = null,
        message: String,
        ok: String = getString(R.string.global_ok),
        okActionBlock: (() -> Unit)? = null,
        dismissActionBlock: (() -> Unit)? = null
    ) {
        if (!isFinishing) {
            CustomSimpleDialog(this, title, message, ok, okActionBlock, dismissActionBlock).show()
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
    fun showChoseDialog(
        @StringRes titleId: Int? = null,
        @StringRes messageId: Int,
        @StringRes okId: Int,
        @StringRes cancelId: Int,
        okActionBlock: (() -> Unit)? = null,
        cancelActionBlock: (() -> Unit)? = null,
        dismissActionBlock: (() -> Unit)? = null
    ) {
        if (!isFinishing) {
            val title = titleId?.let { getString(it) }
            showChoseDialog(
                title,
                getString(messageId),
                getString(okId),
                getString(cancelId),
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
    fun showChoseDialog(
        title: String? = null,
        message: String,
        ok: String,
        cancel: String,
        okActionBlock: (() -> Unit)? = null,
        cancelActionBlock: (() -> Unit)? = null,
        dismissActionBlock: (() -> Unit)? = null
    ) {
        if (!isFinishing) {
            CustomChooseDialog(
                this,
                title,
                message,
                ok,
                cancel,
                okActionBlock,
                cancelActionBlock,
                dismissActionBlock
            ).show()
        }
    }

    /**
     * try to hide Keyboard from the focused screen
     */
    fun hideKeyboard() {
        try {
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        } catch (e: Exception) {
            DebugLog.d(TAG, "Could not hide keyboard, window unreachable. $e")
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        var ret = true
        try {
            ret = ev?.let { super.dispatchTouchEvent(it) } ?: true
            val view = currentFocus
            if (view is EditText) {
                val screenCoords = intArrayOf(0, 0)
                view.getLocationOnScreen(screenCoords)
                val x = ev?.rawX?.plus(view.left)?.minus(screenCoords[0])
                val y = ev?.rawY?.plus(view.top)?.minus(screenCoords[1])
                if (ev?.action == MotionEvent.ACTION_UP && (x!! < view.left || x >= view.right || y!! < view.top || y > view.bottom)) {
                    hideKeyboard()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            return ret
        }
    }

    /**
     * startActivity to class
     * @param kClass activity to navigate to
     * @param shouldFinish should finish current activity
     */
    fun navigateToActivity(kClass: KClass<out Activity>, shouldFinish: Boolean = false) {
        startActivity(Intent(this, kClass.java))
        if (shouldFinish) finish()
    }

    /**
     * handling navigation actions
     * @param navigationTo activity to navigate to
     */
    @CallSuper
    open fun navigate(navigationTo: Navigation) {
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun finish() {
        super.finish()
    }

    override fun onDestroy() {
        hideKeyboard()
        hideProgressBar()
        super.onDestroy()
    }
}
