package com.rent.base

import android.app.Application
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.rent.R
import com.rent.global.helper.Navigation
import com.rent.global.helper.SingleLiveEvent
import com.rent.global.helper.dialog.ChooseDialog
import com.rent.global.helper.dialog.SimpleDialog
import com.rent.global.listener.SchedulerProvider
import com.rent.global.utils.isNetworkAvailable
import java.io.IOException


abstract class BaseAndroidViewModel(
    application: Application,
    protected val schedulerProvider: SchedulerProvider
) :
    AndroidViewModel(application) {

    //for navigation events
    val navigation: SingleLiveEvent<Navigation> = SingleLiveEvent()

    //application context for resource access only
    protected val applicationContext = application.applicationContext!!

    //for blocking progress bar
    val progressBar: MutableLiveData<Boolean> = MutableLiveData()

    //for displaying simple dialog
    val simpleDialog: MutableLiveData<SimpleDialog> = MutableLiveData()

    //for displaying chose dialog
    val choseDialog: MutableLiveData<ChooseDialog> = MutableLiveData()

    //for displaying global snack bar
    val snackBarMessage: SingleLiveEvent<String> = SingleLiveEvent()

    protected fun showSnackBarMessage(message: String) {
        snackBarMessage.value = message
    }

    protected fun showSnackBarMessage(@StringRes messageResourceId: Int) {
        showSnackBarMessage(applicationContext.getString(messageResourceId))
    }

    private fun setShowBlockingProgress(show: Boolean) {
        progressBar.value = show
    }

    protected fun showBlockingProgressBar() {
        setShowBlockingProgress(true)
    }

    protected fun hideBlockingProgressBar() {
        setShowBlockingProgress(false)
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
        simpleDialog.value =
            SimpleDialog.build(
                applicationContext,
                titleId,
                messageId,
                okId,
                okActionBlock,
                dismissSimpleBuild(dismissActionBlock)
            )
    }

    /**
     * show simple ok dialog
     * @param title  message string optional
     * @param message  message string
     * @param okActionBlock action to do on click
     * @param dismissActionBlock action to do on dismiss optional
     *
     */
    fun showSimpleDialog(
        title: String? = null,
        message: String,
        ok: String = applicationContext.getString(R.string.global_ok),
        okActionBlock: (() -> Unit)? = null,
        dismissActionBlock: (() -> Unit)? = null
    ) {
        simpleDialog.value =
            SimpleDialog.build(
                title,
                message,
                ok,
                okActionBlock,
                dismissSimpleBuild(dismissActionBlock)
            )
    }

    private fun dismissSimpleBuild(dismissActionBlock: (() -> Unit)? = null): () -> Unit {
        return {
            dismissActionBlock?.invoke()
            simpleDialog.value = null
        }
    }

    /**
     * show simple ok dialog
     *
     * @param titleId      dialog title resources Id
     * @param messageId       message resources Id
     * @param okId        action button resources Id
     * @param cancelId     cancel button resources Id
     * @param okActionBlock    action to do on click ok
     * @param cancelActionBlock action to do on click cancel
     * @param dismissActionBlock action to do on dismiss optional
     *
     */
    fun showChoseDialog(
        @StringRes titleId: Int? = null, @StringRes messageId: Int, @StringRes okId: Int,
        @StringRes cancelId: Int, okActionBlock: (() -> Unit)? = null,
        cancelActionBlock: (() -> Unit)? = null,
        dismissActionBlock: (() -> Unit)? = null
    ) {

        choseDialog.value =
            ChooseDialog.build(
                applicationContext,
                titleId,
                messageId,
                okId,
                cancelId,
                okActionBlock,
                cancelActionBlock,
                dismissChoseBuild(dismissActionBlock)
            )
    }

    /**
     * show simple ok dialog
     *
     * @param title      dialog title
     * @param message       message
     * @param ok        action button
     * @param cancel     cancel button
     * @param okActionBlock     action to do on click ok
     * @param cancelActionBlock action to do on click cancel
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
        choseDialog.value =
            ChooseDialog.build(
                title,
                message,
                ok,
                cancel,
                okActionBlock,
                cancelActionBlock,
                dismissChoseBuild(dismissActionBlock)
            )
    }

    private fun dismissChoseBuild(dismissActionBlock: (() -> Unit)? = null): () -> Unit {
        return {
            dismissActionBlock?.invoke()
            choseDialog.value = null
        }
    }

    /**
     * show simple error server ok dialog
     *
     */
    protected fun showServerErrorSimpleDialog() {
        showSimpleDialog(
            titleId = R.string.global_error,
            messageId = R.string.global_error_server,
            okId = R.string.global_ok
        )
    }

    fun showToast(message: String) {
        Toast.makeText(applicationContext, message , Toast.LENGTH_LONG).show()
    }

    /**
     * show simple error ok dialog
     * @param throwable error
     *
     */
    protected fun handleThrowable(throwable: Throwable) {
        when (throwable) {
            is IOException -> {
                if (!applicationContext.isNetworkAvailable()) {
                    showSimpleDialog(
                        titleId = R.string.global_error,
                        messageId = R.string.global_error_unavailable_network,
                        okId = R.string.global_ok
                    )
                } else {
                    showServerErrorSimpleDialog()
                }
            }
            else -> showServerErrorSimpleDialog()
        } }


    /**
     * used for navigation events
     * @param navigationTo  destination
     *
     */
    fun navigate(navigationTo: Navigation) {
        navigation.value = navigationTo
    }
}