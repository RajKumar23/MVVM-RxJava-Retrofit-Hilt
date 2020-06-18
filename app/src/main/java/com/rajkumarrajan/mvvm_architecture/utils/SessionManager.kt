package com.rajkumarrajan.mvvm_architecture.utils

import android.app.Dialog
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.webkit.URLUtil
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rajkumarrajan.mvvm_architecture.R
import com.rajkumarrajan.mvvm_architecture.data.model.UserObject
import dagger.hilt.android.qualifiers.ActivityContext
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject


class SessionManager @Inject constructor(@ActivityContext val context: Context) {

    lateinit var dialog: Dialog
    var stringSharedPreferenceName = "AppPrf"
    var stringSharedPreferenceUserProfile = "UserProfilePrf"
    var stringSharedPreferenceDevice = "DevicePrf"

    var mSharedPreferenceMode = 0

    private var mSharedPreferenceApp: SharedPreferences =
        context.getSharedPreferences(stringSharedPreferenceName, mSharedPreferenceMode)
    private var mSharedPreferenceUserProfile: SharedPreferences =
        context.getSharedPreferences(stringSharedPreferenceUserProfile, mSharedPreferenceMode)
    private var mSharedPreferenceDevice: SharedPreferences =
        context.getSharedPreferences(stringSharedPreferenceDevice, mSharedPreferenceMode)

    private var appEditor = mSharedPreferenceApp.edit()
    private var userProfileEditor = mSharedPreferenceUserProfile.edit()
    private var deviceEditor = mSharedPreferenceDevice.edit()

    private val displayMode = "DisplayMode"
    private val language = "Language"
    private val fcmKey = "FCMKey"
    private val deviceToken = "DeviceToken"
    private val accessToken = "AccessToken"
    private val firstName = "FirstName"
    private val lastName = "LastName"
    private val mobileNumber = "MobileNumber"
    private val email = "EMail"

    fun setDisplayMode(displayMode: String) {
        deviceEditor.putString(this.displayMode, displayMode)
        deviceEditor.apply()
    }

    fun getDisplayMode(): String {
        return mSharedPreferenceDevice.getString(displayMode, "light")!!
    }

    fun setLanguage(language: String) {
        deviceEditor.putString(this.language, language)
        deviceEditor.apply()
    }

    fun getLanguage(): String {
        return mSharedPreferenceDevice.getString(language, "en")!!
    }

    fun getApiHeader(): HashMap<String, String> {
        val header = HashMap<String, String>()
        header["UserName"] = "Test"
        header["Password"] = "Test"
        header["Content-Type"] = "application/x-www-form-urlencoded"
        return header
    }

    fun setFCMKey(fcmkeys: String) {
        deviceEditor.putString(this.fcmKey, fcmkeys)
        deviceEditor.apply()
    }

    fun getCMKey(): String {
        return mSharedPreferenceDevice.getString(fcmKey, "1010")!!
    }

    fun setDeviceToken(DeviceToken: String) {
        deviceEditor.putString(this.deviceToken, DeviceToken)
        deviceEditor.apply()
    }

    suspend fun storeUserObject(userObject: UserObject): Boolean {
        return GlobalScope.async(Dispatchers.IO) {
            if (userObject.accessToken != null && userObject.accessToken.isNotEmpty())
                userProfileEditor.putString(accessToken, userObject.accessToken)
            userProfileEditor.putString(firstName, userObject.firstName)
            userProfileEditor.putString(lastName, userObject.lastName)
            userProfileEditor.putString(mobileNumber, userObject.mobileNumber)
            userProfileEditor.putString(email, userObject.email)
            return@async userProfileEditor.commit()
        }.await()
    }

    suspend fun getUserObjectInThread(): UserObject {
        return GlobalScope.async(Dispatchers.IO) {
            return@async getUserObject()
        }.await()
    }

    fun getUserObject(): UserObject {
        return UserObject(
            getCMKey(),
            mSharedPreferenceUserProfile.getString(firstName, ""),
            mSharedPreferenceUserProfile.getString(lastName, ""),
            mSharedPreferenceUserProfile.getString(mobileNumber, ""),
            mSharedPreferenceUserProfile.getString(email, "")
        )
    }

    fun getAccessToken(): String {
        return mSharedPreferenceUserProfile.getString(accessToken, "")!!
    }

    suspend fun fileCompressor(
        imageFile: File
    ): File {
        return GlobalScope.async(Dispatchers.IO) {
            return@async Compressor.compress(context, imageFile) {
                val extension: String =
                    imageFile.absolutePath.substring(imageFile.absolutePath.lastIndexOf("."))
                resolution(640, 480)
                quality(75)
                format(Bitmap.CompressFormat.JPEG)
                size(2_097_152) // 2 MB
                destination(
                    File(
                        context.getExternalFilesDir("")
                            .toString() + "/Compressed/" + System.currentTimeMillis() + extension
                    )
                )
            }
        }.await()
    }

    suspend fun convertBitmapToBase64(bitmap: Bitmap): String {
        return GlobalScope.async(Dispatchers.IO) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            return@async Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        }.await()
    }

    fun displayLoader(LoadingMessage: String) {
        val builder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.loader_progress, null)
        dialogView.findViewById<TextView>(R.id.textViewLoaderMessage).text = LoadingMessage
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    fun dismissLoader() {
        dialog.dismiss()
    }

    fun shortToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBarSuccess(message: String, view: View) {
        val snackBar = Snackbar
            .make(view, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
        val textView =
            snackBarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(context, R.color.full_white))
        snackBar.show()
    }

    fun showSnackBarInternetError(message: String, view: View) {
        val snackBar = Snackbar
            .make(view, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
        val textView =
            snackBarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(context, R.color.full_white))
        snackBar.show()
    }

    fun showSnackBarError(message: String, view: View) {
        val snackBar = Snackbar
            .make(view, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        val textView =
            snackBarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(context, R.color.full_white))
        snackBar.show()
    }

    fun vibrate() {
        val duration = 500 // you can change this according to your need
        if (Build.VERSION.SDK_INT >= 26) {
            (context.getSystemService(VIBRATOR_SERVICE) as Vibrator?)!!.vibrate(
                VibrationEffect.createOneShot(duration.toLong(), VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            (context.getSystemService(VIBRATOR_SERVICE) as Vibrator?)!!.vibrate(
                duration.toLong()
            )
        }
    }

    fun shakeError(): TranslateAnimation? {
        val shake = TranslateAnimation(0f, 10f, 0f, 0f)
        shake.duration = 500
        shake.interpolator = CycleInterpolator(7f)
        return shake
    }

    fun convertEditTextToString(editText: EditText): String {
        return editText.text.toString().trim()
    }

    fun checkEmptyEditText(editText: EditText): Boolean {
        return editText.text.toString().trim().isNotEmpty()
    }

    fun validEMailEditText(editText: EditText): Boolean {
        val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return editText.text.toString().trim().matches(pattern.toRegex())
    }

    fun checkMobileNumberEditText(editText: EditText): Boolean {
        return editText.text.toString().trim().length == 10
    }

    fun checkEditTextStringLength(editText: EditText, size: Int): Boolean {
        return editText.text.toString().trim().length > size
    }

    fun compareEditText(editText1: EditText, editText2: EditText): Boolean {
        return editText1.text.trim().toString() == editText2.text.trim().toString()
    }

    fun isNetworkConnection(): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    fun isValidURL(url: String): Boolean {
        return URLUtil.isValidUrl(url)
    }

    fun getLayoutManagerVertical(): LinearLayoutManager {
        return LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    fun getLayoutManagerHorizontal(): LinearLayoutManager {
        return LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    fun isLogin(): Boolean {
        return getAccessToken().isNotEmpty()
    }
}