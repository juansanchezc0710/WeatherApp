# WeatherApp

Aplicación Android desarrollada como prueba técnica para el proceso de selección en **Bold**.

La aplicación permite consultar el clima actual y pronóstico del tiempo de cualquier ubicación del mundo utilizando la API de WeatherAPI.

## Características

- 🌤️ **Búsqueda de ubicaciones**: Búsqueda en tiempo real de ciudades y ubicaciones
- 📍 **Información detallada**: Visualización del clima actual con temperatura, humedad, viento y presión
- 📅 **Pronóstico extendido**: Pronóstico del clima para los próximos 3 días
- 🌓 **Modo oscuro/claro**: Soporte completo para temas claro y oscuro
- 📱 **Responsive**: Adaptación automática a orientación vertical y horizontal
- 🔄 **Pull to refresh**: Actualización de datos deslizando hacia abajo
- ⚡ **Búsqueda con debounce**: Optimización de búsquedas con delay automático

## Requisitos

- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 11 o superior
- Android SDK mínimo: API 21 (Android 5.0 Lollipop)
- Android SDK objetivo: API 36

## Instalación

1. Clona el repositorio:
```bash
git clone https://github.com/juansanchezc0710/WeatherApp.git
cd WeatherApp
```

2. Abre el proyecto en Android Studio

3. Sincroniza las dependencias de Gradle

4. Configura la API Key:
   - La API Key de WeatherAPI ya está configurada en `BuildConfig`
   - Si necesitas usar tu propia API Key, modifica `app/build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "WEATHER_API_KEY", "\"tu_api_key_aqui\"")
   ```

5. Ejecuta la aplicación:
   - Conecta un dispositivo Android o inicia un emulador
   - Haz clic en "Run" en Android Studio o presiona `Shift + F10`

## Build

### Debug
```bash
./gradlew assembleDebug
```

### Release
```bash
./gradlew assembleRelease
```

El APK se generará en: `app/build/outputs/apk/`

## Arquitectura

La aplicación sigue los principios de **Clean Architecture** con las siguientes capas:

- **Presentation**: UI con Jetpack Compose y ViewModels
- **Domain**: Casos de uso y modelos de dominio
- **Data**: Repositorios, servicios API y mappers

### Patrones de diseño

- **MVVM** (Model-View-ViewModel)
- **Repository Pattern**
- **Dependency Injection** con Koin
- **Use Cases** para lógica de negocio

## Tecnologías

- **Kotlin**: Lenguaje de programación
- **Jetpack Compose**: Framework de UI moderno
- **Material Design 3**: Sistema de diseño
- **Navigation Compose**: Navegación entre pantallas
- **Retrofit**: Cliente HTTP para llamadas a la API
- **OkHttp**: Cliente HTTP con interceptores
- **Gson**: Serialización JSON
- **Koin**: Inyección de dependencias
- **Coroutines**: Programación asíncrona
- **Coil**: Carga de imágenes
- **JUnit 5**: Framework de testing
- **MockK**: Mocking para tests

## Estructura del proyecto

```
app/src/main/java/com/example/weatherapp/
├── data/
│   ├── api/              # Servicios de API y configuración de red
│   ├── mapper/           # Mappers de data a domain
│   ├── model/            # Modelos de datos de la API
│   └── repository/       # Implementación de repositorios
├── domain/
│   ├── model/            # Modelos de dominio
│   ├── repository/       # Interfaces de repositorios
│   └── usecase/          # Casos de uso
├── di/                   # Módulos de inyección de dependencias
├── navigation/           # Configuración de navegación
├── ui/
│   ├── screens/          # Pantallas de la aplicación
│   ├── theme/            # Tema y colores
│   └── viewmodel/        # ViewModels
└── util/                 # Utilidades (Logger, etc.)
```

## API

La aplicación utiliza [WeatherAPI](https://www.weatherapi.com/) para obtener datos del clima.

### Endpoints utilizados

- `GET /v1/search.json`: Búsqueda de ubicaciones
- `GET /v1/forecast.json`: Pronóstico del clima

## Testing

La aplicación incluye tests unitarios para:

- ViewModels
- Use Cases
- Repository
- Mappers

Para ejecutar los tests:

```bash
./gradlew test
```

## Requisitos de la Prueba Técnica

Esta aplicación fue desarrollada como parte del proceso de selección para el puesto de Desarrollador Android en Bold. Los requisitos cumplidos incluyen:

✅ Splash screen que presenta la aplicación  
✅ Pantalla de búsqueda de ubicaciones con búsqueda en tiempo real  
✅ Visualización de nombre y país de cada ubicación  
✅ Pantalla de detalles con información del clima actual  
✅ Pronóstico de 3 días con estado gráfico y textual  
✅ Temperatura promedio en grados centígrados  
✅ Soporte para cambio de orientación (portrait y landscape)  
✅ Uso de componentes de arquitectura de Android (Clean Architecture, MVVM)  
✅ Manejo de errores inesperados  
✅ Tests unitarios implementados  
✅ Experiencia de usuario optimizada  

## Sobre Bold

Bold es una fintech que provee soluciones de pagos a microempresarios para recibir pagos con tarjetas débito y crédito a través de un datáfono móvil (mPOS). Con más de 550.000 clientes registrados y más de USD $120 millones en fondos de inversión, Bold es una de las startups de más rápido crecimiento en LatAm en el sector fintech.

Para más información: [https://bold.co](https://bold.co)

## Licencia

Este proyecto fue desarrollado como prueba técnica y es propiedad de su autor.

## Autor

Juan Camilo Sánchez
