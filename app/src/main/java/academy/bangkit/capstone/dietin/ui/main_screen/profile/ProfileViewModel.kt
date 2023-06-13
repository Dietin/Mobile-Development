package academy.bangkit.capstone.dietin.ui.main_screen.profile

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
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

class ProfileViewModel(private val application: Application) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> = _logoutSuccess

    suspend fun getUser() = Utils.getUser(application)
    suspend fun getDataUser() = Utils.getUserData(application)

    fun updateUser(name: String, email: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().updateUser(
                token = "Bearer $token",
                name = name,
                email = email
            ).data

            // Set user
            Utils.setUser(application, data)

            _message.value = Event(application.getString(R.string.successfully_updated))
            _isSuccess.value = true
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
            _isSuccess.value = false
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _message.value = Event(errorResponse.message)
            _isSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }

    fun logout() = viewModelScope.launch {
        try {
            _isLoading.value = true
            val token = Utils.getToken(application)
            ApiConfig.getApiService().logout(
                token = "Bearer $token"
            )

            removeData()

            _message.value = Event(application.getString(R.string.successfully_logout))
            _logoutSuccess.value = true
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
            _logoutSuccess.value = false
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            if (e.code() == 401) {
                removeData()
                _message.value = Event(application.getString(R.string.session_expired))
                _logoutSuccess.value = true
            } else {
                val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
                _message.value = Event(errorResponse.message)
                _logoutSuccess.value = false
            }
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun removeData() {
        Utils.setToken(application, "")
        Utils.setUser(application, null)
        Utils.setUserData(application, null)
    }
}