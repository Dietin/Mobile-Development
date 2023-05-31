package academy.bangkit.capstone.dietin.data.remote

import android.app.Application

class UserRepository private constructor(private val application: Application) {
    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(application: Application): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(application)
            }
    }
}