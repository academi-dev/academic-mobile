package com.academic.mobile.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.academic.mobile.AcademicApp
import com.academic.mobile.databinding.ActivityLoginBinding
import com.academic.mobile.ui.common.UiState
import com.academic.mobile.ui.common.ViewModelFactory
import com.academic.mobile.ui.main.MainActivity
import com.academic.mobile.utils.Validators
import kotlinx.coroutines.launch

/**
 * Pantalla de inicio de sesión y actividad principal de arranque (`LAUNCHER`).
 * Redirige automáticamente a [MainActivity] si existe un token JWT activo.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory((application as AcademicApp).serviceLocator)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.tieneSesionActiva()) {
            navegarAMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarEventos()
        observarEstado()
    }

    private fun configurarEventos() {
        binding.btnLogin.setOnClickListener {
            intentarLogin()
        }

        binding.btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun intentarLogin() {
        val email = binding.etEmail.text?.toString().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()

        val emailError = Validators.validateEmail(email)
        val passwordError = Validators.validatePassword(password)

        binding.tilEmail.error = emailError
        binding.tilPassword.error = passwordError

        if (emailError != null || passwordError != null) {
            return
        }

        binding.tvError.visibility = View.GONE
        viewModel.iniciarSesion(email, password)
    }

    private fun observarEstado() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { state ->
                    when (state) {
                        is UiState.Idle -> mostrarCargando(false)
                        is UiState.Loading -> mostrarCargando(true)
                        is UiState.Success -> {
                            mostrarCargando(false)
                            navegarAMain()
                        }
                        is UiState.Error -> {
                            mostrarCargando(false)
                            binding.tvError.text = state.message
                            binding.tvError.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun mostrarCargando(cargando: Boolean) {
        binding.progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !cargando
        binding.btnGoToRegister.isEnabled = !cargando
    }

    private fun navegarAMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
