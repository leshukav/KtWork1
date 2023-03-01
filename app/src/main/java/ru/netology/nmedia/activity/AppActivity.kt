package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.ImageFragment.Companion.textArg
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.service.FirebaseModule
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var firebase: FirebaseMessaging

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability

    lateinit var appBarConfiquration: AppBarConfiguration

    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(binding.root, R.string.error_empty_content, LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
                return@let
            }

        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.conteiner) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiquration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiquration)


        var previousMenuProvider: MenuProvider? = null
        authViewModel.data.observe(this) {
            previousMenuProvider?.let(::removeMenuProvider)

            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_auth, menu)
                    menu.setGroupVisible(R.id.unauthorization, !authViewModel.authorized)
                    menu.setGroupVisible(R.id.authorization, authViewModel.authorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.login -> {
                            findNavController(R.id.conteiner).navigate(R.id.action_feedFragment2_to_dialogFragment,
                                Bundle().apply
                                { textArg = "loginGroup" })
                            true
                        }
                        R.id.register -> {
                            findNavController(R.id.conteiner).navigate(R.id.action_feedFragment2_to_dialogFragment,
                                Bundle().apply
                                { textArg = "registrGroup" })
                            true
                        }
                        R.id.logout -> {
                            findNavController(R.id.conteiner).navigate(R.id.loginFragment,
                                Bundle().apply
                                { textArg = "questionGroup" })
                            true
                        }
                        else -> false
                    }
            }.also {
                previousMenuProvider = it
            })
        }


        checkGoogleApiAvailability(firebase, googleApiAvailability)
    }

    private fun checkGoogleApiAvailability(
        firebase: FirebaseMessaging,
        googleApiAvailability: GoogleApiAvailability,
    ) {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(
                this@AppActivity,
                R.string.google_play_unavailable,
                Toast.LENGTH_LONG
            )
                .show()
        }
        firebase.token.addOnSuccessListener {
            println(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.conteiner)
        return navController.navigateUp(appBarConfiquration)
        super.onSupportNavigateUp()
    }
}