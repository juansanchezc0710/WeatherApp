# Changelog

## [Unreleased]

### [Task #24] Configurar API Key y ProGuard
- API Key configurada en BuildConfig con valor hardcodeado
- SigningConfigs configurados para debug y release (usando debug keystore)
- ProGuard configurado para modo release (reglas preparadas, minify deshabilitado)
- Reglas ProGuard configuradas para Retrofit, Gson, OkHttp, Koin
- Reglas para preservar BuildConfig, NetworkModule y modelos de datos
- Reglas optimizadas con -dontwarn para clases internas de librerías
- testOptions configurados para unit tests (isReturnDefaultValues = true)

### [Task #22] Agregar pruebas unitarias
- JUnit 5 configurado (junit-jupiter 5.10.2)
- MockK configurado (1.13.10) para mocking
- kotlinx-coroutines-test agregado para testing de coroutines
- Configuración de JUnit Platform en build.gradle.kts
- Tests organizados con @Nested y @DisplayName para mejor legibilidad
- **Repository Tests:**
  - WeatherRepositoryImplTest con grupos: "When searching locations", "When getting weather forecast"
- **Use Case Tests:**
  - SearchLocationsUseCaseTest con pruebas para queries vacíos, éxito y errores
  - GetWeatherForecastUseCaseTest con pruebas para éxito y errores
- **Mapper Tests:**
  - WeatherMapperTest creado con grupos: "When mapping Location to domain", "When mapping WeatherResponse to domain"
  - Pruebas para mapeo de Location, WeatherResponse, CurrentWeather y ForecastDay
- **ViewModel Tests:**
  - SearchViewModelTest creado con grupos: "When search query changes", "When performing location search", "When refreshing location search"
  - Pruebas para debounce, manejo de errores, timeouts y estados de UI
  - WeatherDetailsViewModelTest creado con grupos: "When loading weather forecast data", "When refreshing weather forecast data"
  - Pruebas para carga de datos, manejo de errores y validaciones

### [Task #21] Crear utilidad de logs
- Logger object creado en `util/Logger.kt`
- Métodos para diferentes niveles de log: d(), i(), w(), e()
- Método e() con soporte para excepciones
- Tag por defecto configurado como "WeatherApp"
- Truncado automático de tags que excedan 23 caracteres (límite de Android)
- Implementación simple basada en android.util.Log
- Logs implementados en ViewModels (SearchViewModel, WeatherDetailsViewModel)
- Logs implementados en Repository (WeatherRepositoryImpl)
- Logs implementados en Use Cases (SearchLocationsUseCase, GetWeatherForecastUseCase)
- Logs implementados en WeatherApp (inicialización de Koin)
- Logs implementados en MainActivity (ciclo de vida)
- Logs estratégicos para operaciones críticas, errores y validaciones

### [Task #25] Crear icono de la app
- Icono adaptativo creado con gradiente azul de fondo
- Fondo diferenciado para modo claro (#3B82F6 a #1E40AF) y modo oscuro (#1E3A8A a #0F172A)
- Configuración en mipmap-anydpi-v26 para iconos adaptativos
- Soporte para icono cuadrado y redondo

### [Task #20] Ajustar tema y colores
- Esquema de colores azules personalizado para la app del clima
- DarkColorScheme con fondos oscuros (DarkBlueOuter, DarkBlueEdge) y textos blancos
- LightColorScheme con fondos claros (LightBlueOuter, LightBlueEdge) y textos negros
- Colores primarios azules (PrimaryBlue, LightBlue) configurados
- Dynamic color deshabilitado para mantener colores personalizados consistentes

### [Task #15] Crear casos de uso
- SearchLocationsUseCase creado para encapsular lógica de búsqueda de ubicaciones
- GetWeatherForecastUseCase creado para encapsular lógica de obtención de pronóstico
- Operador invoke implementado en ambos use cases para uso como funciones
- Validación de queries vacíos en SearchLocationsUseCase
- UseCaseModule creado para inyección de dependencias con Koin
- Use cases registrados como factory en Koin
- Integración de useCaseModule en appModule

### [Task #14] Configurar Koin para el repositorio
- RepositoryModule creado para inyección de dependencias del repositorio
- WeatherRepositoryImpl registrado como singleton en Koin
- Integración de repositoryModule en appModule
- Dependencias inyectadas: WeatherApiService y API Key

### [Task #13] Implementar el repositorio
- WeatherRepositoryImpl creado implementando WeatherRepository
- Método searchLocations() para búsqueda de ubicaciones con mapeo a dominio
- Método getWeatherForecast() para obtener pronóstico con mapeo a dominio
- Manejo de errores con Result<T>
- Uso de WeatherMapper para convertir modelos de data a domain

### [Task #12] Crear interfaz del repositorio
- WeatherRepository interface creada en domain layer
- Método searchLocations() definido retornando Result<List<Location>>
- Método getWeatherForecast() definido retornando Result<Weather>
- Interfaz siguiendo Dependency Inversion Principle

### [Task #17] Crear ViewModel para detalles del clima
- WeatherDetailsViewModel creado para gestionar el estado y lógica de la pantalla de detalles
- WeatherDetailsUiState como data class con weatherData, isLoading y error
- Función loadWeatherForecastData() que obtiene datos reales desde WeatherApiService
- Función refreshWeatherForecastData() para actualizar los datos
- Integración con Koin para inyección de dependencias (WeatherApiService y API Key)
- WeatherDetailScreen actualizado para usar koinViewModel() y observar el estado
- LaunchedEffect para cargar datos automáticamente al navegar a la pantalla
- Manejo de estados: loading, error y datos cargados
- Mapeo de datos desde API (WeatherResponse) a modelos de dominio (Weather)

### [Task #16] Agregar Navigation Compose
- Dependencia Navigation Compose agregada (version 2.8.0)
- NavGraph creado con rutas para Splash, Search y Details
- MainActivity actualizado para usar NavController y NavGraph
- SplashScreen actualizado con callback onNavigateToSearch
- SearchScreen actualizado con callback onNavigateToDetails
- WeatherDetailScreen actualizado con callback onBackClick
- Navegación configurada entre todas las pantallas

### [Task #18] Crear pantalla de detalles del clima
- WeatherDetailScreen creada con UI completa y moderna
- Header con temperatura grande, condición e icono del clima desde la API
- Sección de detalles del clima (humedad, viento, presión, sensación térmica)
- Pronóstico de 3 días con iconos y temperaturas máximas/mínimas
- Diseño responsive con cards Material Design 3
- Soporte modo claro/oscuro con gradientes consistentes
- Botón de retroceso para navegación
- Formato de fechas en español
- Dependencia Coil agregada para cargar imágenes desde URLs
- Modelo de dominio CurrentWeather actualizado para incluir presión
- Mapper actualizado para mapear presión desde el modelo de datos
- Previews para Android Studio

### [Task #9] Integrar Koin con ViewModels
- ViewModelModule creado para inyección de ViewModels
- SearchViewModel actualizado para recibir dependencias por inyección
- SearchScreen actualizado para usar `koinViewModel()` en lugar de `viewModel()`
- Dependencias inyectadas: WeatherApiService y API Key
- Separación de responsabilidades mejorada

### [Task #8] Configurar Koin para inyección de dependencias
- Dependencias Koin agregadas (koin-android, koin-androidx-compose)
- Clase `WeatherApp` creada como Application para inicializar Koin
- NetworkModule creado para inyección de dependencias de red
- WeatherApiService y API Key configurados como singletons
- AndroidManifest actualizado para registrar la clase Application
- Módulo principal `appModule` que combina todos los módulos

### [Task #10] Crear funciones para mapear datos a modelos del dominio
- Funciones de extensión para convertir modelos de data a domain
- Mapper para Location (data → domain)
- Mapper para WeatherResponse (data → Weather domain)
- Mapper para CurrentWeather y ForecastDay
- Separación clara entre capas Data y Domain

### [Task #7] Agregar ViewModel para la búsqueda
- SearchViewModel creado con StateFlow para reactividad
- Estados de UI: Empty, Loading, LocationsLoaded, Error
- Función searchLocations() con debounce de 500ms
- Manejo de errores y estados de carga
- Integración con WeatherApiService para búsqueda de ubicaciones
- SearchScreen conectada con ViewModel usando viewModel() de Compose
- UI actualizada para mostrar estados: Empty, Loading, LocationsLoaded y Error
- Lista de ubicaciones mostrada en cards cuando se encuentran resultados
- Búsqueda automática con debounce al escribir en el campo de búsqueda
- Permiso INTERNET agregado al AndroidManifest para llamadas HTTP

### [Task #6] Crear servicio de la API del clima
- Interfaz WeatherApiService con Retrofit implementada
- Endpoint para buscar ubicaciones (search.json)
- Endpoint para obtener pronóstico del clima (forecast.json)
- Configuración de Retrofit con base URL de WeatherAPI

### [Task #5] Agregar Retrofit para llamadas a la API
- Dependencias Retrofit y OkHttp agregadas en build.gradle
- Módulo de red (NetworkModule) configurado
- Interceptor de logging para debugging
- Timeouts configurados (30 segundos)
- Conversor Gson para serialización JSON

### [Task #4] Crear modelos de datos de la API
- Modelos de datos para la capa Data (Location, WeatherResponse)
- Modelos de dominio para la capa Domain (Location, Weather)
- Dependencia Gson agregada para serialización JSON
- Estructura Clean Architecture implementada

### [Task #3] Crear pantalla principal de búsqueda
- Pantalla de búsqueda con toolbar y campo de búsqueda
- Icono animado del clima en toolbar
- Estado vacío con card y mensaje descriptivo
- Soporte modo claro/oscuro
- Previews para Android Studio
- Refactor 
  - SearchViewModel refactorizado (SearchUiState a data class) 
  - BackHandler con diálogo de confirmación
  - Pull-to-refresh
  - LocationSearchResultCard como componente
  - Manejo mejorado de errores 
  - Previews en claro/oscuro

### [Task #2] Implementar Splash Screen
- Splash Screen con iconos animados (sol, nubes, tormenta)
- Animaciones fade in/out
- Soporte modo claro/oscuro
- Previews para Android Studio

### [Task #1] Crear proyecto Android
- Crear proyecto Android con Jetpack Compose
- Configurar estructura básica del proyecto
- Configurar minSdk 21, targetSdk 36
- Configurar dependencias básicas de Compose

