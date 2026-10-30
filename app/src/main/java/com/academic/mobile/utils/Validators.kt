package com.academic.mobile.utils

/**
 * Funciones puras de validación de formularios para autenticación, materias y tareas.
 */
object Validators {

    private val EMAIL_REGEX = Regex(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )

    /**
     * Valida que el correo electrónico tenga un formato estándar válido.
     * Devuelve el mensaje de error en español o `null` si es válido.
     */
    fun validateEmail(email: String?): String? {
        val clean = email?.trim().orEmpty()
        if (clean.isEmpty()) {
            return "El correo electrónico es obligatorio."
        }
        if (!EMAIL_REGEX.matches(clean)) {
            return "Ingresa un correo electrónico válido."
        }
        return null
    }

    /**
     * Valida que la contraseña tenga al menos 6 caracteres según el contrato de `academic-api`.
     */
    fun validatePassword(password: String?): String? {
        val value = password.orEmpty()
        if (value.isEmpty()) {
            return "La contraseña es obligatoria."
        }
        if (value.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres."
        }
        return null
    }

    /**
     * Valida el nombre completo del usuario o el nombre de una materia (mínimo 2 caracteres).
     */
    fun validateNombre(nombre: String?): String? {
        val clean = nombre?.trim().orEmpty()
        if (clean.isEmpty()) {
            return "El nombre es obligatorio."
        }
        if (clean.length < 2) {
            return "El nombre debe tener al menos 2 caracteres."
        }
        return null
    }

    /**
     * Valida el título de una tarea académica (mínimo 2 caracteres).
     */
    fun validateTituloTarea(titulo: String?): String? {
        val clean = titulo?.trim().orEmpty()
        if (clean.isEmpty()) {
            return "El título de la tarea es obligatorio."
        }
        if (clean.length < 2) {
            return "El título debe tener al menos 2 caracteres."
        }
        return null
    }

    /**
     * Valida el código de una materia académica (mínimo 2 caracteres).
     */
    fun validateCodigoMateria(codigo: String?): String? {
        val clean = codigo?.trim().orEmpty()
        if (clean.isEmpty()) {
            return "El código de la materia es obligatorio."
        }
        if (clean.length < 2) {
            return "El código debe tener al menos 2 caracteres."
        }
        return null
    }

    /**
     * Valida el semestre de una materia académica.
     */
    fun validateSemestre(semestre: String?): String? {
        val clean = semestre?.trim().orEmpty()
        if (clean.isEmpty()) {
            return "El semestre es obligatorio."
        }
        return null
    }
}
