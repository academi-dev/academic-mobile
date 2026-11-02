# Academic Mobile 📱🎓

[![CI/CD](https://img.shields.io/github/actions/workflow/status/academic-dev/academic-mobile/ci.yml?branch=main&style=for-the-badge&logo=github-actions&logoColor=white&label=CI%2FCD)](https://github.com/academi-dev/academic-mobile)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android SDK](https://img.shields.io/badge/Android_SDK-API_34-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Arquitectura](https://img.shields.io/badge/Architecture-MVVM-blueviolet?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![Material Design 3](https://img.shields.io/badge/Material_Design-3-757575?style=for-the-badge&logo=material-design&logoColor=white)](https://m3.material.io/)
[![Licencia MIT](https://img.shields.io/badge/Licencia-MIT-green.svg?style=for-the-badge)](LICENSE)

**Academic Mobile** es la aplicación nativa para Android del ecosistema **Academic**, desarrollada en **Kotlin** bajo la arquitectura **MVVM** con **ViewBinding** y componentes **Material Design 3**. Se integra directamente con `academic-api` (`/api/v1`) para la autenticación JWT, la gestión de materias académicas y el seguimiento de tareas con fechas de entrega en formato ISO 8601 UTC.

---

## 📋 Tabla de Contenidos

- [Características Principales](#-características-principales)
- [Pantallas de la Aplicación](#-pantallas-de-la-aplicación)
- [Arquitectura de la Solución](#-arquitectura-de-la-solución)
- [Stack Tecnológico](#-stack-tecnológico)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Requisitos Previos](#-requisitos-previos)
- [Compilación y Ejecución](#-compilación-y-ejecución)
- [Equipo de Desarrollo](#-equipo-de-desarrollo)
- [Licencia](#-licencia)

---

## ✨ Características Principales

- **Arquitectura MVVM con ServiceLocator**: Separación clara entre vistas (`Activities`/`Fragments` con `ViewBinding`), presentación (`ViewModels` con `StateFlow` y `UiState`) y datos (`AuthRepository`, `MateriasRepository`, `TareasRepository`) inyectados mediante `ServiceLocator`.
- **Seguridad JWT con Refresco Automático**: Almacenamiento cifrado de credenciales mediante `EncryptedSharedPreferences` (`TokenManager`), inyección automática de cabecera `Authorization: Bearer <token>` (`AuthInterceptor`) y renovación transparente ante respuestas `401 Unauthorized` (`TokenAuthenticator`).
- **Interfaz Moderna Material Design 3**: Soporte para tema claro y oscuro (`DayNight`), filtros interactivos con `ChipGroup`, listas optimizadas con `ListAdapter` + `DiffUtil` y selectores `MaterialDatePicker` + `MaterialTimePicker`.
- **Manejo Estandarizado de Errores**: Deserialización del contrato de errores de `academic-api` (`{"error": {"codigo": "...", "mensaje": "..."}}`) mediante `ApiErrorParser`.

---

## 📱 Pantallas de la Aplicación

| Pantalla | Descripción | Características Clave |
| :--- | :--- | :--- |
| **Inicio de Sesión (`LoginActivity`)** | Acceso con correo y contraseña | Validación de campos, redirección automática si existe sesión activa y persistencia segura con `EncryptedSharedPreferences` |
| **Registro (`RegisterActivity`)** | Creación de cuenta en `academic-api` | Validación de nombre, correo y contraseña ($\ge 6$ caracteres) e inicio de sesión automático |
| **Materias (`MateriasFragment`)** | Catálogo de materias del estudiante | Tarjetas con código, semestre, nombre y descripción; diálogo modal para crear materia y clic para filtrar tareas |
| **Tareas (`TareasFragment`)** | Tablero de asignaciones académicas | Filtros por estado (`Todas`, `Pendientes`, `En progreso`, `Completadas`) y por materia, con indicadores de prioridad |
| **Crear Tarea (`CrearTareaActivity`)** | Formulario de nueva tarea | Selector de materia y prioridad (`baja`, `media`, `alta`), y selección de fecha/hora UTC con `MaterialDatePicker` y `MaterialTimePicker` |
| **Detalle de Tarea (`DetalleTareaActivity`)** | Vista completa de una tarea | Visualización de descripción, prioridad, estado y fecha límite; acciones rápidas para marcar `en_progreso`, `completada` o eliminar |

---

## 🏛 Arquitectura de la Solución

```text
  ┌────────────────────────────────────────────────────────┐
  │                 UI Layer (View)                        │
  │   Activities / Fragments / ViewBinding (Material 3)    │
  └───────────────────────────▲────────────────────────────┘
                              │ StateFlow<UiState<T>>
  ┌───────────────────────────┴────────────────────────────┐
  │                Presentation Layer                      │
  │      AuthViewModel · MateriasViewModel · TareasVM      │
  └───────────────────────────▲────────────────────────────┘
                              │ Coroutines (Result<T>)
  ┌───────────────────────────┴────────────────────────────┐
  │                   Data Layer                           │
  │   AuthRepository · MateriasRepository · TareasRepo     │
  └─────────────┬────────────────────────────┬─────────────┘
                │                            │
                ▼ (Retrofit 2 / OkHttp 4)    ▼ (EncryptedSharedPreferences)
     [ AcademicApiService ]            [ TokenManager ]
```

---

## 🛠 Stack Tecnológico

| Tecnología | Versión | Propósito / Descripción |
| :--- | :--- | :--- |
| **Kotlin** | 1.9.24 | Lenguaje oficial para el desarrollo nativo en Android |
| **Android SDK** | API 34 (Min 24) | Compatibilidad desde Android 7.0 con *core library desugaring* para `java.time` |
| **Material Design 3** | 1.12.0 | Componentes visuales modernos, temas claro/oscuro, `ChipGroup` y selectores de fecha/hora |
| **Retrofit 2 + Gson** | 2.11.0 | Cliente HTTP tipado y serialización JSON para `academic-api` |
| **OkHttp 4** | 4.12.0 | Interceptores de autenticación Bearer, logging HTTP y `TokenAuthenticator` |
| **Security Crypto** | 1.1.0-alpha06 | `EncryptedSharedPreferences` para almacenamiento cifrado de tokens JWT |
| **Coroutines & StateFlow** | 1.8.1 | Concurrencia estructurada y flujo reactivo de estados en la capa UI |
| **JUnit 4** | 4.13.2 | Pruebas unitarias locales en la JVM para validadores, fechas y parser de errores |

---

## 📂 Estructura del Proyecto

```text
academic-mobile/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/academic/mobile/
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/              # AcademicApiService, RetrofitClient, AuthInterceptor, TokenAuthenticator, ApiErrorParser
│   │   │   │   │   ├── local/            # TokenManager (EncryptedSharedPreferences)
│   │   │   │   │   ├── models/           # AuthModels, MateriaModels, TareaModels, ApiErrorModels
│   │   │   │   │   └── repository/       # Result, AuthRepository, MateriasRepository, TareasRepository
│   │   │   │   ├── ui/
│   │   │   │   │   ├── auth/             # AuthViewModel, LoginActivity, RegisterActivity
│   │   │   │   │   ├── common/           # UiState, ViewModelFactory
│   │   │   │   │   ├── main/             # MainActivity
│   │   │   │   │   ├── materias/         # MateriasViewModel, MateriasAdapter, MateriasFragment
│   │   │   │   │   └── tareas/           # TareasViewModel, TareasAdapter, TareasFragment, CrearTareaActivity, DetalleTareaActivity
│   │   │   │   ├── utils/                # DateUtils, Validators
│   │   │   │   └── AcademicApp.kt        # Application y ServiceLocator
│   │   │   ├── res/                      # Layouts XML, drawables vectoriales, menús, temas Material 3 y network_security_config
│   │   │   └── AndroidManifest.xml       # Declaración de permisos y actividades
│   │   └── test/java/com/academic/mobile/# ValidatorsTest, DateUtilsTest, ApiErrorParserTest
│   ├── build.gradle.kts                  # Configuración del módulo :app y BuildConfig.BASE_URL
│   └── proguard-rules.pro                # Reglas para Retrofit y modelos Gson
├── gradle/
│   ├── libs.versions.toml                # Catálogo centralizado de versiones
│   └── wrapper/                          # Configuración de Gradle Wrapper
├── build.gradle.kts                      # Configuración raíz de Gradle
├── settings.gradle.kts                   # Definición del proyecto e inclusión de :app
└── gradle.properties                     # Propiedades de compilación de AndroidX y Kotlin
```

---

## ⚙️ Requisitos Previos

- **Android Studio Jellyfish / Koala** o superior.
- **JDK 17** configurado en Android Studio o en `JAVA_HOME`.
- Instancia de `academic-api` en ejecución (por defecto accesible desde el emulador en `http://10.0.2.2:8000/`).

---

## 🚀 Compilación y Ejecución

### Configuración de la URL de `academic-api`

Por defecto, `app/build.gradle.kts` define `BuildConfig.BASE_URL = "http://10.0.2.2:8000/"`, que apunta al `localhost:8000` de la máquina anfitriona desde el emulador oficial de Android.

Si deseas apuntar a otra dirección (por ejemplo, en un dispositivo físico en la misma red local), puedes añadir la propiedad en `local.properties` (en la raíz del proyecto):

```properties
academic.baseUrl=http://192.168.1.100:8000/
```

o pasarla por línea de comandos con `-Pacademic.baseUrl=http://192.168.1.100:8000/`.

### Ejecución de Pruebas Unitarias y Compilación APK

```bash
# Ejecutar pruebas unitarias JVM:
./gradlew testDebugUnitTest

# Compilar el APK de depuración:
./gradlew assembleDebug
```

El APK compilado se ubicará en `app/build/outputs/apk/debug/app-debug.apk`.

---

## 👥 Equipo de Desarrollo

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/hilton45">
        <img src="https://github.com/hilton45.png" width="100px;" alt="Jean Mena" style="border-radius:50%;"/><br />
        <sub><b>Jean Mena</b></sub>
      </a><br />
      <a href="https://github.com/hilton45">@hilton45</a>
    </td>
    <td align="center">
      <a href="https://github.com/eyfer3174-commits">
        <img src="https://github.com/eyfer3174-commits.png" width="100px;" alt="Eyfer Mena" style="border-radius:50%;"/><br />
        <sub><b>Eyfer Mena</b></sub>
      </a><br />
      <a href="https://github.com/eyfer3174-commits">@eyfer3174-commits</a>
    </td>
  </tr>
</table>

---

## 📄 Licencia

Este proyecto está distribuido bajo la Licencia **MIT**. Consulte el archivo [LICENSE](LICENSE) para obtener más información.
