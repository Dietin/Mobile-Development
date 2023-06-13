package academy.bangkit.capstone.dietin.di

import academy.bangkit.capstone.dietin.data.remote.ApplicationRepository
import android.app.Application

object Injection {
    fun provideRepository(application: Application): ApplicationRepository {
        return ApplicationRepository.getInstance(application)
    }

//    fun provideRepository(context: Context): NewsRepository {
//        val apiService = ApiConfig.getApiService()
//        val database = NewsDatabase.getInstance(context)
//        val dao = database.newsDao()
//        val appExecutors = AppExecutors()
//        return NewsRepository.getInstance(apiService, dao, appExecutors)
//    }
}