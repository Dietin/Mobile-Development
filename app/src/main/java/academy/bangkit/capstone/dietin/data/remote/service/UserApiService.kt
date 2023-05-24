package academy.bangkit.capstone.dietin.data.remote.service

import academy.bangkit.capstone.dietin.data.remote.model.ApiResponse
import academy.bangkit.capstone.dietin.data.remote.model.User
import retrofit2.http.*

interface UserApiService {
    @POST("/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): ApiResponse<User>

    @POST("/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): ApiResponse<User>
}