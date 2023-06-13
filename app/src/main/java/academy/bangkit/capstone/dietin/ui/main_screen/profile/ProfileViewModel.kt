package academy.bangkit.capstone.dietin.ui.main_screen.profile

import academy.bangkit.capstone.dietin.utils.Utils
import android.app.Application
import androidx.lifecycle.ViewModel

class ProfileViewModel(private val application: Application) : ViewModel() {

    suspend fun getUser() = Utils.getUser(application)
    suspend fun getDataUser() = Utils.getUserData(application)
}