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