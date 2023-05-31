package academy.bangkit.capstone.dietin.ui.auth.activity

import academy.bangkit.capstone.dietin.utils.Utils
import android.app.Application
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first

class AuthenticationViewModel(private val application: Application): ViewModel() {
    suspend fun isTokenAvailable(): Boolean {
        val token = Utils.getToken(application)
        return token.first().isNotEmpty()
    }
}