package academy.bangkit.capstone.dietin.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val TOKEN_KEY = stringPreferencesKey(token_key)
    private val USER_ID_KEY = intPreferencesKey(user_id_key)

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

    fun getUserId(): Flow<Int> {
        return dataStore.data.map { p ->
            p[USER_ID_KEY] ?: 0
        }
    }

    suspend fun setUserId(value: Int) {
        dataStore.edit { p ->
            p[USER_ID_KEY] = value
        }
    }

    companion object {
        const val token_key = "token"
        const val user_id_key = "user_id"
        private var INSTANCE: AppPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): AppPreferences =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferences(dataStore)
            }
    }
}