package academy.bangkit.capstone.dietin.ui.auth.activity

import academy.bangkit.capstone.dietin.MainActivity
import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.databinding.ActivityAuthenticationBinding
import academy.bangkit.capstone.dietin.ui.auth.fragments.login.LoginFragment
import academy.bangkit.capstone.dietin.ui.auth.fragments.register.RegisterFragment
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.runBlocking

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?): Unit = runBlocking {
        super.onCreate(savedInstanceState)

        // First: check if the user is already logged in
        viewModel = ViewModelProvider(this@AuthenticationActivity, ViewModelFactory.getInstance(application))[AuthenticationViewModel::class.java]
        if (viewModel.isTokenAvailable()) {
            val intent = Intent(this@AuthenticationActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return@runBlocking
        }

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment("login", false)
    }

    fun setFragment(frg: String, addToBackStack: Boolean = true) {
        /**
         * Fragment animation example: https://developer.android.com/guide/fragments/animate
         */
        val transaction = supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in, // enter
                R.anim.fade_out, // exit
                R.anim.fade_in, // popEnter
                R.anim.fade_out // popExit
            )
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        when (frg) {
            "login" -> {
                transaction.replace(binding.root.id, LoginFragment())
                    .commit()
            }
            "register" -> {
                transaction.replace(binding.root.id, RegisterFragment())
                    .commit()
            }
        }
    }
}