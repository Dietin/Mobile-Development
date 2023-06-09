package academy.bangkit.capstone.dietin.ui.auth.activity

import academy.bangkit.capstone.dietin.MainScreenActivity
import academy.bangkit.capstone.dietin.databinding.ActivityAuthenticationBinding
import academy.bangkit.capstone.dietin.ui.auth.fragments.login.LoginFragment
import academy.bangkit.capstone.dietin.ui.auth.fragments.register.RegisterFragment
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.runBlocking
import java.util.Locale

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var viewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?): Unit = runBlocking {
        super.onCreate(savedInstanceState)

        // Set default Locale
        if (Build.VERSION.SDK_INT >= 33) {
            // https://stackoverflow.com/questions/4985805/set-locale-programmatically
            val locale = LocaleListCompat.forLanguageTags("in-ID")
            AppCompatDelegate.setApplicationLocales(locale)
        } else {
            val locale = Locale("in", "ID")
            Locale.setDefault(locale)
        }

        // First: check if the user is already logged in
        viewModel = ViewModelProvider(this@AuthenticationActivity, ViewModelFactory.getInstance(application))[AuthenticationViewModel::class.java]
        if (viewModel.isTokenAvailable()) {

            //surat cinta untuk jolly.. :))
            //disini saya ubah routenya dari MainActivity ke MainScreenActivity
            val intent = Intent(this@AuthenticationActivity, MainScreenActivity::class.java)
            startActivity(intent)
            finish()
            return@runBlocking
        }

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment("login", false)
    }

    fun setFragment(
        frg: String,
        addToBackStack: Boolean = true,
        listElements : ArrayList<Pair<View, String>>? = null,
    ) {

        val transaction = supportFragmentManager
            .beginTransaction()

            .apply {
                listElements?.forEach { (view, transitionName) ->
                    addSharedElement(view, transitionName)
                }
            }



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