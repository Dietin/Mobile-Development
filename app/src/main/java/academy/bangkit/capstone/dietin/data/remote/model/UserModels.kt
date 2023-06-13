package academy.bangkit.capstone.dietin.data.remote.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("created_at")
    val createdAt: String,
)

data class DataUser(
    @SerializedName("dataUser_id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("age")
    var age: Int,
    @SerializedName("weight")
    var weight: Float,
    @SerializedName("height")
    var height: Float,
    @SerializedName("bmr")
    val bmr: Float,
    @SerializedName("activity_level")
    var activityLevel: Float,
    @SerializedName("gender")
    var gender: Int,
    @SerializedName("idealCalories")
    val idealCalories: Float,
    @SerializedName("diet_objective")
    var goal: Int, // Sebenarnya tidak ada di API, tapi untuk memudahkan di UI, jadi ditambahkan
    @SerializedName("current_weight")
    var currentWeight: Float,
    @SerializedName("user")
    val user: User?,
)