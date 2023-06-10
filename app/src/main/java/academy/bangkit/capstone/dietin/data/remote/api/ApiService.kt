package academy.bangkit.capstone.dietin.data.remote.api

import academy.bangkit.capstone.dietin.data.remote.model.*
import retrofit2.http.*

interface ApiService {
    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
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

    @POST("dataUser")
    suspend fun onboarding(
        @Header("Authorization") token: String,
        @Body dataUser: DataUser
    )

    @GET("recipe")
    suspend fun getRecommendations(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<List<Recipe>>

    @GET("category")
    suspend fun getAllCategories(
        @Header("Authorization") token: String
    ): ApiResponse<List<RecipeCategory>>

    @GET("foodHistoryGroup/{date}")
    suspend fun getFoodHistoryGroupedByTime(
        @Header("Authorization") token: String,
        @Path("date") date: String
    ): ApiResponse<List<FoodHistoryGroup>>

    @POST("foodHistory")
    suspend fun addFoodHistory(
        @Header("Authorization") token: String,
        @Body foodHistory: FoodHistory
    ): ApiResponse<FoodHistory>
}