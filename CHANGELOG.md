# Changelog

## [Unreleased]

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

