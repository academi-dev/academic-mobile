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
import com.academic.mobile.databinding.ActivityRegisterBinding
import com.academic.mobile.ui.common.UiState
import com.academic.mobile.ui.common.ViewModelFactory
import com.academic.mobile.ui.main.MainActivity
import com.academic.mobile.utils.Validators
import kotlinx.coroutines.launch

/**
 * Pantalla de registro de nuevos usuarios en el sistema Academic.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory((application as AcademicApp).serviceLocator)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarEventos()
        observarEstado()
    }

    private fun configurarEventos() {
        binding.btnRegister.setOnClickListener {
            intentarRegistro()
        }

        binding.btnGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun intentarRegistro() {
        val nombre = binding.etNombre.text?.toString().orEmpty()
        val email = binding.etEmail.text?.toString().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()

        val nombreError = Validators.validateNombre(nombre)
        val emailError = Validators.validateEmail(email)
        val passwordError = Validators.validatePassword(password)

        binding.tilNombre.error = nombreError
        binding.tilEmail.error = emailError
        binding.tilPassword.error = passwordError

        if (nombreError != null || emailError != null || passwordError != null) {
            return
        }

        binding.tvError.visibility = View.GONE
        viewModel.registrarUsuario(nombre, email, password)
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
        binding.btnRegister.isEnabled = !cargando
        binding.btnGoToLogin.isEnabled = !cargando
    }

    private fun navegarAMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
