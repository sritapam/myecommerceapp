# Informe Técnico: Tecnologías y Arquitectura de MyEcommerceApp

Este documento describe las principales tecnologías y decisiones arquitectónicas adoptadas en el desarrollo de MyEcommerceApp, explicando el porqué de cada elección para construir una aplicación Android robusta, mantenible y escalable.

---

## 1. **Lenguaje Principal: Kotlin**

* **Justificación:** Kotlin es el lenguaje recomendado por Google para el desarrollo de Android. Ofrece seguridad ante nulos, concisión, corrutinas para programación asíncrona y es totalmente interoperable con Java.

## 2. **Versión Mínima de SDK y Versión de Java**

* **Versión Mínima de SDK:** SDK 24 (Android 7.0 Nougat)
* **Versión de Java:** Java 17
* **Justificación:** SDK 24 ofrece un buen equilibrio entre alcance de dispositivos y acceso a APIs modernas. **Java 17** proporciona las últimas características del lenguaje, mejoras de rendimiento y es la versión LTS (Long-Term Support) más reciente soportada por Android Studio y el plugin de Android Gradle, asegurando compatibilidad y mejoras a futuro.

## 3. **Arquitectura Adoptada: MVVM (Model-View-ViewModel)**

* **Justificación:** MVVM facilita la separación de responsabilidades, haciendo el código más testeable y mantenible. El ViewModel gestiona el estado de la UI y actúa como intermediario entre la View y el Model.

## 4. **Frameworks/Librerías Clave**

* **Hilt (Dagger Hilt):**
    * **Justificación:** Para la inyección de dependencias, lo que mejora la modularidad, la testabilidad y reduce el boilerplate.
* **Navigation Component:**
    * **Justificación:** Para la gestión de la navegación entre fragments, proporcionando un grafo de navegación centralizado y seguro.
* **SharedPreferences:**
    * **Justificación:** Para el almacenamiento ligero de datos clave-valor, como el estado de la sesión del usuario.

## 5. **Decisiones de Diseño Adicionales**

* **Gestión de Hilos:**
    * Se utilizan Kotlin Coroutines para manejar operaciones asíncronas, como llamadas a la red y operaciones de base de datos, de forma eficiente y segura.

## 6. **Futuras Implementaciones/Consideraciones**

* **Firebase Authentication:** Se contempla la integración para un sistema de autenticación robusto y escalable.
* **Retrofit:** Se utilizará para la comunicación con APIs RESTful.
* **Room:** Se utilizará para la persistencia de datos local.
* **Jetpack Compose:** Se planea la migración a Compose para la construcción de la UI, lo que simplificará el desarrollo y mejorará el rendimiento.