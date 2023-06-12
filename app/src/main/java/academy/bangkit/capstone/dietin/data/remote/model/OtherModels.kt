package academy.bangkit.capstone.dietin.data.remote.model

import com.google.gson.annotations.SerializedName

data class FoodHistory(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    var userId: Int,
    @SerializedName("recipe_id")
    val recipeId: String,
    @SerializedName("calories")
    val calories: Float,
    @SerializedName("carbs")
    val carbs: Float,
    @SerializedName("fats")
    val fats: Float,
    @SerializedName("proteins")
    val proteins: Float,
    @SerializedName("date")
    val date: String,
    @SerializedName("food_time")
    val time: Int,
)

data class SearchHistory(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    var userId: Int,
    @SerializedName("recipe_id")
    val recipeId: String,
    @SerializedName("searched_at")
    val searchedAt: String,
    @SerializedName("user")
    val user: User?,
    @SerializedName("recipe")
    val recipe: Recipe?,
)

data class FavouriteRecipe(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    var userId: Int,
    @SerializedName("recipe_id")
    val recipeId: String,
    @SerializedName("user")
    val user: User?,
    @SerializedName("recipe")
    val recipe: Recipe?,
)