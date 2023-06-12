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

data class LoginInnerResponse(
    @SerializedName("user")
    val user: User,
    @SerializedName("access_token")
    val token: String,
)


data class FoodHistoryGroup(
    @SerializedName("food_time")
    val time: Int,
    @SerializedName("total_calories")
    val totalCalories: Float,
)