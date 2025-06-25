# My E-Commerce App

¡Bienvenidos a MyEcommerceApp! Esta aplicación Android en desarrollo te permitirá explorar un catálogo de productos, gestionar tu autenticación de usuario (login y registro) y disfrutar de una experiencia de compra fluida.

## Descripción del Proyecto

**My E-Commerce App** es una aplicación de Android diseñada para ofrecer una experiencia de compra en línea. Los usuarios pueden navegar por una lista de productos, ver detalles de cada producto, agregar artículos a su carrito de compras y (presumiblemente) completar un proceso de compra. La aplicación también incluye funcionalidades de autenticación de usuarios (inicio de sesión y registro) y permite a los usuarios ver su historial de pedidos y gestionar su perfil.

La aplicación está construida utilizando tecnologías modernas de Android, incluyendo Kotlin y componentes de Jetpack (wip), con un enfoque en la navegación modular a través de Fragments y la gestión de datos mediante ViewModels.

## Estructura del Proyecto

El proyecto sigue una arquitectura que separa las preocupaciones, influenciado por patrones como MVVM (Model-View-ViewModel). A continuación, se describe una estructura de directorios típica basada en las funcionalidades identificadas:

com.example.myecommerceapp/  ├── data/ # (Suposición) Modelos de datos, fuentes de datos (remotas/locales), repositorios │ ├── model/ # Clases de datos (ej: Product, User, Order) │ ├── remote/ # Lógica para interactuar con APIs (ej: Retrofit services) │ ├── local/ # Lógica para interactuar con base de datos local (ej: Room DAOs) │ └── repository/ # Repositorios que abstraen el origen de los datos │ ├── di/ # (Suposición si se usa Hilt/Koin) Módulos de Inyección de Dependencias │ ├── domain/ # (Suposición) Casos de uso o interactors que contienen la lógica de negocio │ └── usecase/ │ ├── presentation/ # Capa de UI (Vistas y ViewModels) │ ├── main/ # Relacionado con la MainActivity y la navegación principal │ │ ├── MainActivity.kt │ │ └── MainViewModel.kt │ │ │ ├── productlist/ # UI para mostrar la lista de productos │ │ ├── ProductListFragment. kt │ │ └── ProductListViewModel. kt │ │ │ ├── productdetail/ # UI para mostrar los detalles de un producto │ │ ├── ProductDetailActivity. kt (o Fragment) │ │ └── ProductDetailViewModel. kt │ │ │ ├── cart/ # UI para el carrito de compras │ │ ├── CartFragment.kt │ │ └── CartViewModel.kt │ │ │ ├── auth/ # UI para autenticación (login/registro) │ │ ├── LoginFragment.kt │ │ ├── RegisterBottomSheetFragment. kt │ │ └── AuthViewModel.kt │ │ │ ├── profile/ # UI para el perfil del usuario │ │ ├── ProfileActivity.kt (o Fragment) │ │ └── ProfileViewModel.kt │ │ │ ├── orderhistory/ # UI para el historial de pedidos │ │ ├── OrderHistoryActivity. kt (o Fragment) │ │ └── OrderHistoryViewModel. kt │ │ │ └── adapters/ # (Suposición) RecyclerView Adapters si se usan vistas XML tradicionales │ ├── ui/ # (Suposición si se usa Compose) Componentes Composable reutilizables, temas │ ├── components/ │ └── theme/ │ └── util/ # Clases de utilidad, helpers, extensiones



### Componentes Clave:

*   **Activities:**
    *   `MainActivity`: Actividad principal que alberga el `NavHostFragment` para la navegación entre los principales flujos de la aplicación.
    *   `ProductDetailActivity`: Muestra información detallada de un producto específico.
    *   `OrderHistoryActivity`: Muestra el historial de pedidos del usuario.
    *   `ProfileActivity`: Permite al usuario ver y editar su información de perfil.
*   **Fragments:**
    *   `ProductListFragment`: Muestra la lista de productos disponibles.
    *   `CartFragment`: Muestra los artículos que el usuario ha agregado a su carrito.
    *   `LoginFragment`: Interfaz para que los usuarios inicien sesión.
    *   `RegisterBottomSheetFragment`: Un BottomSheet para el registro de nuevos usuarios.
*   **ViewModels:**
    *   Cada pantalla (Activity/Fragment) relevante tiene un `ViewModel` asociado (ej: `ProductListViewModel`, `AuthViewModel`) para gestionar los datos y la lógica de la UI, separándolos de la vista.
*   **Navegación (`nav_graph.xml`):**
    *   Define los flujos de navegación entre los diferentes Fragments y Activities de la aplicación utilizando el Navigation Component de Jetpack.
*   **Dependencias Notables (del `build.gradle`):**
    *   **Navigation Component:** Para la navegación estructurada dentro de la app.
    *   **ViewModel & LiveData/StateFlow:** Para implementar el patrón MVVM y gestionar el estado de la UI de forma reactiva.
    *   **Material Components for Android:** Para los elementos de la interfaz de usuario con estilo Material Design.
    *   **(wip) Retrofit/OkHttp:** Para realizar llamadas de red a una API de backend.
    *   **(wip) Room:** Para persistencia de datos local (ej: caché de productos, carrito).
    *   **(wip) Hilt/Koin:** Para la inyección de dependencias.
    *   **Jetpack Compose:** Para construir la interfaz de usuario de forma declarativa.

---
## 🚀 **Tecnologías Utilizadas y Arquitectura**

Para entender en profundidad las decisiones técnicas y las tecnologías que dan vida a esta aplicación, hemos preparado un informe técnico detallado. Este documento explica por qué elegimos cada herramienta y cómo encaja en nuestra arquitectura Clean (Data, Domain, Presentation).

[**Ver Informe Técnico: Tecnologías y Arquitectura**](./docs/tencnologias.md)

---

## ✨ **¿Cómo se verá la App? (Diseño UI/UX)**

Hemos diseñado la interfaz de usuario con **Figma**, siguiendo los principios de **Material Design 3** para asegurar una experiencia moderna, intuitiva y accesible.

Puedes explorar el prototipo y los flujos de la aplicación en detalle a través de este enlace de la comunidad de Figma:

[**Ver el Diseño UI/UX en Figma**](https://www.figma.com/community/file/1509353274665521781)

--- 

## Próximos Pasos / Mejoras

*   Implementar la funcionalidad de checkout.
*   Añadir pruebas unitarias y de UI.
*   Integrar un sistema de pago.
*   Mejorar la gestión de errores y los estados de carga.

---

## ⚙️ **Configuración del Proyecto**

Para correr este proyecto localmente, sigue estos pasos:

1.  **Clona el repositorio:**
    ```bash
    git clone [https://github.com/tu-usuario/MyEcommerceApp.git](https://github.com/tu-usuario/MyEcommerceApp.git)
    cd MyEcommerceApp
    ```
2.  **Abre en Android Studio:**
    Abre el proyecto con Android Studio (versión Hedgehog 2023.1.1 o superior recomendada).
3.  **Sincroniza el proyecto:**
    Deja que Gradle sincronice todas las dependencias.
4.  **Ejecuta la app:**
    Puedes ejecutar la aplicación en un emulador o un dispositivo físico.

---

## 🤝 **Contribuciones**

¡Las contribuciones son bienvenidas! Si encuentras un error o tienes una sugerencia de mejora, no dudes en abrir un *issue* o enviar un *pull request*.

---

## 📄 **Licencia**

Este proyecto está licenciado bajo la [Licencia MIT](LICENSE).
