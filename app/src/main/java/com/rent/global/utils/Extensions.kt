package com.rent.global.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.rent.RentApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*
import java.util.concurrent.CancellationException
import java.util.regex.Pattern
import kotlin.math.abs


/**
 * observe non null live data update
 *
 * @param owner
 * @param observer
 *
 */
fun <T> LiveData<T>.observeOnlyNotNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner) { it?.let(observer) }
}


/**
 * This method converts device specific pixels to density independent pixels.
 *
 * @value px      A value in px (pixels) unit. Which we need to convert into db
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent dp equivalent to px value
 */
fun Float.convertPixelsToDp(context: Context?): Float {
    if (context == null) return 0f
    val resources = context.resources
    val metrics = resources.displayMetrics
    return this / (metrics.densityDpi / 160f)
}


/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * @value dp      A value in dp (density independent pixels) unit. Which we need to convert into
 * pixels
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent px equivalent to dp depending on device density
 */
fun Float.convertDpToPixel(context: Context?): Float {
    if (context == null) return 0f
    val resources = context.resources
    val metrics = resources.displayMetrics
    return this * (metrics.densityDpi / 160f)
}


/**
 * This method converts sp unit to equivalent pixels, depending on device density.
 *
 * @value sp      A value in sp unit. Which we need to convert into
 * pixels
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent px equivalent to dp depending on device density
 */
fun Float.convertSpToPixel(context: Context?): Float {
    if (context == null) return 0f
    val resources = context.resources
    val metrics = resources.displayMetrics
    return this * metrics.scaledDensity
}

fun Context?.isNetworkAvailable(): Boolean {
    if (this == null) return false
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        cm.activeNetworkInfo?.let {
            return it.isConnected && (it.type == ConnectivityManager.TYPE_WIFI || it.type == ConnectivityManager.TYPE_MOBILE)
        }
    } else {
        cm.activeNetwork?.let {
            val nc = cm.getNetworkCapabilities(it)

            return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )
        }
    }

    return false
}


/**
 * BitShift the entire string to obfuscate it further
 * and make it harder to guess the password.
 */
fun String?.bitShiftEntireString(): String {
    return if (TextUtils.isEmpty(this)) {
        ""
    } else {
        val msg = StringBuilder(this.toString())
        for (i in msg.indices) {
            msg.setCharAt(i, msg[i] + SERIAL_LENGTH)
        }
        msg.toString()
    }
}


/**
 * Gets the hardware serial number of this device.
 *
 * @return serial number or Settings.Secure.ANDROID_ID if not available.
 * Credit: SecurePreferences for Android
 */
@SuppressLint("HardwareIds")
fun Context?.getDeviceSerialNumber(): String {
    return if (this == null) {
        ""
    } else try {
        val deviceSerial = Build::class.java.getField(SERIAL).get(null) as String
        return if (TextUtils.isEmpty(deviceSerial)) {
            Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            )
        } else {
            deviceSerial
        }
    } catch (ignored: Exception) {
        return Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}

/**
 * property TAG extension for Loging
 *
 */
val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

/**
 * used for suspend tryCatch
 * @param tryBlock  try block to execute
 * @param catchBlock  catch block to execute
 * @param handleCancellationExceptionManually cancellation exception will manually handled
 *
 */
suspend fun tryCatch(
    tryBlock: suspend () -> Unit,
    catchBlock: suspend (Throwable) -> Unit,
    handleCancellationExceptionManually: Boolean = false
) {
    try {
        tryBlock()
    } catch (e: Throwable) {
        if (e !is CancellationException ||
            handleCancellationExceptionManually
        ) {
            catchBlock(e)
        } else {
            throw e
        }
    }
}


object LastClickTimeSingleton {
    var lastClickTime: Long = 0
}

fun View.setClickWithDebounce(action: () -> Unit) {
    setOnClickListener(object : View.OnClickListener {

        override fun onClick(v: View) {
            if (abs(SystemClock.elapsedRealtime() - LastClickTimeSingleton.lastClickTime) < 300L) return
            else action()
            LastClickTimeSingleton.lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

/**
 * check if the target is a sequence of white spaces
 *
 * @return A boolean value
 */
fun CharSequence?.isWhiteSpaces(): Boolean {
    return if (TextUtils.isEmpty(this)) {
        true
    } else {
        this?.matches(WHITE_SPACE_REGEX.toRegex()) ?: true
    }
}

/**
 * check if the target is a valid mail
 *
 * @return A boolean value
 */
fun CharSequence?.isValidEmail(): Boolean {
    return if (isNullOrBlank()) {
        false
    } else {
        val emailRegex = "^[A-Za-z](.*)([@])(.+)(\\.)(.+)"
        emailRegex.toRegex().matches(this!!)
    }
}

/**
 * Checks if the current String is a valid phone number
 *
 * @return Boolean value
 */
fun String.isValidPhoneNumber(): Boolean {
    val pattern = Pattern.compile("[+]?[0-9. ]{8,20}+")
    if (this.isEmpty()) {
        return false
    }
    if (this.trim() == "+") {
        return false
    }

    val match = pattern.matcher(this)
    return match.matches()
}

fun Array<String>.allPermissionsGranted() = this.all {
    ContextCompat.checkSelfPermission(
        RentApplication.getInstance(),
        it
    ) == PackageManager.PERMISSION_GRANTED
}

fun File.toMultipartBody(): MultipartBody.Part {
    val mFile: RequestBody = this.asRequestBody("".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("ic_avatar", this.name, mFile)
}

fun String.clearTempFiles() {
    File(this).listFiles()?.forEach {
        it.delete()
    }
}

fun Int.toFormattedNumber() = when (this.toString().length) {
    1 -> "00$this"
    2 -> "0$this"
    else -> "$this"
}


infix fun <T> Boolean.then(param: T): T? = if (this) param else null


/**
 * try to hide Keyboard from the focused screen
 */
fun View.hideKeyboard() {
    try {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (e: Exception) {
        DebugLog.d(TAG, "Could not hide keyboard, window unreachable. $e")
    }
}

//////////////////////////////////////////


fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun dpToPx(dp: Int, context: Context): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
        context.resources.displayMetrics
    ).toInt()

internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

internal inline fun Boolean?.orFalse(): Boolean = this ?: false

internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) =
    ContextCompat.getDrawable(this, drawable)

internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))

fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.FRENCH).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}

fun GradientDrawable.setCornerRadius(
    topLeft: Float = 0F,
    topRight: Float = 0F,
    bottomRight: Float = 0F,
    bottomLeft: Float = 0F
) {
    cornerRadii = arrayOf(
        topLeft, topLeft,
        topRight, topRight,
        bottomRight, bottomRight,
        bottomLeft, bottomLeft
    ).toFloatArray()
}
