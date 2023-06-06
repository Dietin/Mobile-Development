package academy.bangkit.capstone.dietin.ui.main_screen.search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel(private val application: Application) : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text
}