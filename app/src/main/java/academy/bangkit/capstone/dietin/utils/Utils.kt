package academy.bangkit.capstone.dietin.utils

import academy.bangkit.capstone.dietin.R
import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
object Utils {
    fun getToken(context: Context): Flow<String> {
        val pref = AppPreferences.getInstance(context.dataStore)
        return pref.getToken()
    }

    suspend fun setToken(context: Context, value: String) {
        val pref = AppPreferences.getInstance(context.dataStore)
        pref.setToken(value)
    }

    fun generateLoader(context: Context): AlertDialog {
        val view = View.inflate(context, R.layout.layout_loader, null)
        return AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(false)
            .create()
    }
}