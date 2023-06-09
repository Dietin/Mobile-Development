package academy.bangkit.capstone.dietin.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ApiResponse<T>(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: T
)

data class ApiErrorResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("errors")
    val errors: HashMap<String, List<String>>? // eg: "email": ["The email has already been taken."]
) {
    override fun toString(): String {
        var errors = ""
        this.errors?.forEach { (key, value) ->
            errors += "$key: ${value[0]}\n"
        }
        return errors
    }
}

data class LoginResponse(
    @SerializedName("user")
    val user: User,
    @SerializedName("access_token")
    val token: String,
)

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("weight")
    val weight: Int,
    @SerializedName("height")
    val height: Int,
)

@Parcelize
data class Recipe(
    @SerializedName("recipe_id")
    val id: String,
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
    @SerializedName("image")
    val image: String,
    @SerializedName("category")
    val category: RecipeCategory,
): Parcelable

@Parcelize
data class RecipeCategory(
    @SerializedName("category_id")
    val id: String,
    @SerializedName("category_name")
    val name: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("colour_array")
    val _colourArray: String,
): Parcelable {
    private fun getColourArray(): List<Int> {
        return _colourArray.split(",").map { it.toInt() }
    }
    fun getColourArrayAsHex(): Int {
        val colourArray = getColourArray()
        val r = colourArray[0]
        val g = colourArray[1]
        val b = colourArray[2]
        return android.graphics.Color.rgb(r, g, b)
    }
}

data class FoodHistoryGroup(
    @SerializedName("food_time")
    val time: Int,
    @SerializedName("total_calories")
    val totalCalories: Float,
)

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