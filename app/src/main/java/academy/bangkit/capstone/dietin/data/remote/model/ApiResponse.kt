package academy.bangkit.capstone.dietin.data.remote.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: T
)

data class User(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String
)

data class Recipe(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("ingredients")
    val ingredients: String,
    @SerializedName("steps")
    val steps: String,
    @SerializedName("calories")
    val calories: Float,
    @SerializedName("carbs")
    val carbs: Float,
    @SerializedName("fats")
    val fats: Float,
    @SerializedName("proteins")
    val proteins: Float,
)