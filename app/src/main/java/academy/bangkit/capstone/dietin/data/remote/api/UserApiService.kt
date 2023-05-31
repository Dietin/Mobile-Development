package academy.bangkit.capstone.dietin.data.remote.api

import academy.bangkit.capstone.dietin.data.remote.model.ApiResponse
import academy.bangkit.capstone.dietin.data.remote.model.LoginResponse
import academy.bangkit.capstone.dietin.data.remote.model.User
import retrofit2.http.*

interface UserApiService {
    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("gender") gender: Int,
        @Field("weight") weight: Int,
        @Field("height") height: Int
    ): ApiResponse<User>

    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): ApiResponse<LoginResponse>

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): ApiResponse<Nothing>
}