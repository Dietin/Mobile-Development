package academy.bangkit.capstone.dietin.ui.auth.fragments.register

import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
import academy.bangkit.capstone.dietin.data.remote.model.User
import academy.bangkit.capstone.dietin.data.remote.service.ApiConfig
import academy.bangkit.capstone.dietin.utils.Event
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterViewModel(private val application: Application): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _registeredUser = MutableLiveData<User>()
    val registeredUser: LiveData<User> = _registeredUser

    private val _registerError = MutableLiveData<ApiErrorResponse>()
    val registerError: LiveData<ApiErrorResponse> = _registerError

    fun clientRegister(
        name: String,
        email: String,
        password: String,
        gender: Int,
        weight: Int,
        height: Int
    ) = viewModelScope.launch {
        try {
            _isLoading.value = true
            _registeredUser.value = ApiConfig.getApiService().register(
                name = name,
                email = email,
                password = password,
//                gender = gender,
//                weight = weight,
//                height = height
            ).data!!
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _registerError.value = errorResponse
            _message.value = Event(errorResponse.message)
        } finally {
            _isLoading.value = false
        }
    }
}