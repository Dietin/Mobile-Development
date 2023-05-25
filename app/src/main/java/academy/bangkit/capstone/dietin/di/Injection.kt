package academy.bangkit.capstone.dietin.di

import android.content.Context

object Injection {
    /**
     * TODO: Provide repository
     * @see com.dicoding.newsapp.di.Injection
     */
    fun provideRepository(context: Context): Any {
        return Any()
    }

//    fun provideRepository(context: Context): NewsRepository {
//        val apiService = ApiConfig.getApiService()
//        val database = NewsDatabase.getInstance(context)
//        val dao = database.newsDao()
//        val appExecutors = AppExecutors()
//        return NewsRepository.getInstance(apiService, dao, appExecutors)
//    }
}