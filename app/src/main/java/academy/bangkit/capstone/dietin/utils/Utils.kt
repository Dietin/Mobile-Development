package academy.bangkit.capstone.dietin.utils

import academy.bangkit.capstone.dietin.R
import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
object Utils {
    suspend fun getToken(context: Context): String {
        val pref = AppPreferences.getInstance(context.dataStore)
        return pref.getToken().first()
    }

    suspend fun setToken(context: Context, value: String) {
        val pref = AppPreferences.getInstance(context.dataStore)
        pref.setToken(value)
    }

    // TODO: Temporary: user id should not be stored in shared preferences
    suspend fun getUserId(context: Context): Int {
        val pref = AppPreferences.getInstance(context.dataStore)
        return pref.getUserId().first()
    }

    suspend fun setUserId(context: Context, value: Int) {
        val pref = AppPreferences.getInstance(context.dataStore)
        pref.setUserId(value)
    }

    fun generateLoader(context: Context): AlertDialog {
        val view = View.inflate(context, R.layout.layout_loader, null)
        return AlertDialog.Builder(context, R.style.DietinLoadingDialog)
            .setView(view)
            .setCancelable(false)
            .create()
    }

    fun getCurrentDate(pattern: String = "yyyy-MM-dd"): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }

    fun formatDate(time: Date, pattern: String = "yyyy-MM-dd"): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(time)
    }

    fun formatString(format: String, vararg args: Any?) = String.format(Locale.getDefault(), format, args)
}