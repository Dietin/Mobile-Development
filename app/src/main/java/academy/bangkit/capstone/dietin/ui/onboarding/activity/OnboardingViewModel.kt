package academy.bangkit.capstone.dietin.ui.onboarding.activity

import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
import academy.bangkit.capstone.dietin.data.remote.model.DataUser
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

class OnboardingViewModel(private val application: Application): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    val userData = MutableLiveData<DataUser>()

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    fun uploadUserData() = viewModelScope.launch {
        _isLoading.value = true
        try {
            // Flip gender: 0 -> 1, 1 -> 0
            userData.value!!.gender = if (userData.value!!.gender == 0) 1 else 0
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().onboarding(
                token = "Bearer $token",
                dataUser = userData.value!!
            ).data

            // Save user data
            predictCalories(data)
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

    fun predictCalories(data: DataUser) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val predictedDataUser = ApiConfig.getApiServiceML().predictCalories(
                userId = data.userId,
                body = userData.value!!
            ).data

            // Save to preference
            data.idealCalories = predictedDataUser.predictedCalories
            Utils.setUserData(application, data)
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

    fun getUserData() = viewModelScope.launch {
        try {
            _isLoading.value = true
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().getDataUser(
                token = "Bearer $token"
            ).data
            userData.value = data
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _message.value = Event(errorResponse.message)
        } finally {
            _isLoading.value = false
        }
    }
}