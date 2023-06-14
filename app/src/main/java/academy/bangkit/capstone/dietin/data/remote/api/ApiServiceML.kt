package academy.bangkit.capstone.dietin.data.remote.api

import academy.bangkit.capstone.dietin.data.remote.model.ApiResponse
import academy.bangkit.capstone.dietin.data.remote.model.DataUser
import academy.bangkit.capstone.dietin.data.remote.model.DataUserML
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiServiceML {
    @POST("predict/{user_id}")
    // JSON
    suspend fun predictCalories(
        @Path("user_id") userId: Int,
        @Body body: DataUser
    ): ApiResponse<DataUserML>
}