package academy.bangkit.capstone.dietin.ui.auth.fragments.login

import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
import academy.bangkit.capstone.dietin.data.remote.model.LoginInnerResponse
import academy.bangkit.capstone.dietin.data.remote.service.ApiConfig
import academy.bangkit.capstone.dietin.utils.Event
import academy.bangkit.capstone.dietin.utils.Utils
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(private val application: Application): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _loginResult = MutableLiveData<LoginInnerResponse>()
    val loginResult: LiveData<LoginInnerResponse> = _loginResult

    private val _loginError = MutableLiveData<ApiErrorResponse>()
    val loginError: LiveData<ApiErrorResponse> = _loginError

    fun clientLogin(email: String, password: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val data = ApiConfig.getApiService().login(
                email = email,
                password = password
            ).data

            // Set preferences
            Utils.setToken(application, data.token)
            Utils.setUser(application, data.user)
            Utils.setUserData(application, data.dataUser)

            _loginResult.value = data
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _loginError.value = errorResponse
            _message.value = Event(errorResponse.message)
        } finally {
            _isLoading.value = false
        }
    }
}