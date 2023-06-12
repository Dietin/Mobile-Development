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
    ): ApiResponse<LoginInnerResponse>

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

    @GET("recipe/search")
    suspend fun searchGlobal(
        @Header("Authorization") token: String,
        @Query("q") query: String,
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

    @GET("searchHistory")
    suspend fun getSearchHistory(
        @Header("Authorization") token: String
    ): ApiResponse<List<SearchHistory>>

    @POST("searchHistory")
    suspend fun addSearchHistory(
        @Header("Authorization") token: String,
        @Body searchHistory: SearchHistory
    ): ApiResponse<SearchHistory>

    @GET("favorite")
    suspend fun getFavouriteRecipes(
        @Header("Authorization") token: String
    ): ApiResponse<List<FavouriteRecipe>>

    @POST("favorite")
    suspend fun addFavouriteRecipe(
        @Header("Authorization") token: String,
        @Body favourite: FavouriteRecipe
    ): ApiResponse<FavouriteRecipe>

    @DELETE("favorite/{id}")
    suspend fun deleteFavouriteRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): ApiResponse<Nothing>
}