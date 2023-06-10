package academy.bangkit.capstone.dietin.utils

import academy.bangkit.capstone.dietin.ui.auth.activity.AuthenticationViewModel
import academy.bangkit.capstone.dietin.ui.auth.fragments.login.LoginViewModel
import academy.bangkit.capstone.dietin.ui.auth.fragments.register.RegisterViewModel
import academy.bangkit.capstone.dietin.ui.food_history.AddFoodHistoryViewModel
import academy.bangkit.capstone.dietin.ui.main_screen.home.HomeViewModel
import academy.bangkit.capstone.dietin.ui.main_screen.profile.ProfileViewModel
import academy.bangkit.capstone.dietin.ui.main_screen.search.SearchViewModel
import academy.bangkit.capstone.dietin.ui.onboarding.activity.OnboardingViewModel
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory private constructor(private val application: Application): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthenticationViewModel::class.java) -> AuthenticationViewModel(application) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(application) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(application) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(application) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(application) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(application) as T
            modelClass.isAssignableFrom(AddFoodHistoryViewModel::class.java) -> AddFoodHistoryViewModel(application) as T
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> OnboardingViewModel(application) as T
//            modelClass.isAssignableFrom(Onboarding1)
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(application)
            }
    }
}