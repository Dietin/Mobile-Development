package academy.bangkit.capstone.dietin.utils

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.remote.model.DataUser
import academy.bangkit.capstone.dietin.data.remote.model.User
import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.accompanist.themeadapter.material3.Mdc3Theme
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

    suspend fun getUser(context: Context): User? {
        val pref = AppPreferences.getInstance(context.dataStore)
        return pref.getUser().first()
    }

    suspend fun setUser(context: Context, value: User?) {
        val pref = AppPreferences.getInstance(context.dataStore)
        pref.setUser(value)
    }

    suspend fun getUserData(context: Context): DataUser? {
        val pref = AppPreferences.getInstance(context.dataStore)
        return pref.getUserData().first()
    }

    suspend fun setUserData(context: Context, value: DataUser?) {
        val pref = AppPreferences.getInstance(context.dataStore)
        pref.setUserData(value)
    }

    suspend fun getIsUserFirstTime(context: Context): Int {
        val pref = AppPreferences.getInstance(context.dataStore)
        return pref.getIsUserFirstTime().first()
    }

    suspend fun setIsUserFirstTime(context: Context, value: Int) {
        val pref = AppPreferences.getInstance(context.dataStore)
        pref.setIsUserFirstTime(value)
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

    fun setComposableFunction(view: ComposeView, composable: @Composable () -> Unit) {
        view.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Mdc3Theme {
                    composable()
                }
            }
        }
    }

    fun setShimmerVisibility(view: ShimmerFrameLayout, state: Boolean) {
        if (state) {
            view.visibility = View.VISIBLE
            view.startShimmer()
        } else {
            view.visibility = View.GONE
            view.stopShimmer()
        }
    }
}