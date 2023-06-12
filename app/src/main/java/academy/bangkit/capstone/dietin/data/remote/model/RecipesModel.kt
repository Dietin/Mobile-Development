package academy.bangkit.capstone.dietin.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
data class Recipe(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("calories")
    val calories: Float,
    @SerializedName("carbs")
    val carbs: Float,
    @SerializedName("fats")
    val fats: Float,
    @SerializedName("proteins")
    val proteins: Float,
    @SerializedName("image")
    val image: String?,
    @SerializedName("category")
    val category: RecipeCategory,
    @SerializedName("recipe_steps") @IgnoredOnParcel
    val steps: List<RecipeSteps>? = null,
    @SerializedName("recipe_ingredients") @IgnoredOnParcel
    val ingredients: List<RecipeIngredient>? = null,
): Parcelable

data class RecipeSteps(
    @SerializedName("id")
    val id: Int,
    @SerializedName("recipe_id")
    val recipeId: Int,
    @SerializedName("step_no")
    val stepNo: Int,
    @SerializedName("text")
    val text: String
)

data class RecipeIngredient(
    @SerializedName("id")
    val id: Int,
    @SerializedName("recipe_id")
    val recipeId: Int,
    @SerializedName("amount")
    val amount: Float
)

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