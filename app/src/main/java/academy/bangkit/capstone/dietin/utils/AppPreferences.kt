package academy.bangkit.capstone.dietin.utils

import academy.bangkit.capstone.dietin.data.remote.model.User
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val TOKEN_KEY = stringPreferencesKey(token_key)
    private val USER_KEY = stringPreferencesKey(user_key)
    private val IS_USER_FIRST_TIME_KEY = intPreferencesKey(iuft_key)

    /**
     * Info:
     * To save a parcelable object, use Gson to convert it to a string and save it as a string.
     * There is currently no way to save a parcelable object directly.
     */

    fun getToken(): Flow<String> {
        return dataStore.data.map { p ->
            p[TOKEN_KEY] ?: ""
        }
    }

    suspend fun setToken(value: String) {
        dataStore.edit { p ->
            p[TOKEN_KEY] = value
        }
    }

    fun getUser(): Flow<User?> {
        return dataStore.data.map { p ->
            Gson().fromJson(p[USER_KEY] ?: "null", User::class.java)
        }
    }

    suspend fun setUser(value: User?) {
        dataStore.edit { p ->
            p[USER_KEY] = Gson().toJson(value)
        }
    }

    fun getIsUserFirstTime(): Flow<Int> {
        return dataStore.data.map { p ->
            p[IS_USER_FIRST_TIME_KEY] ?: 0
        }
    }

    suspend fun setIsUserFirstTime(value: Int) {
        dataStore.edit { p ->
            p[IS_USER_FIRST_TIME_KEY] = value
        }
    }

    companion object {
        const val token_key = "token"
        const val user_key = "user"
        const val iuft_key = "is_user_first_time"
        private var INSTANCE: AppPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): AppPreferences =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferences(dataStore)
            }
    }
}