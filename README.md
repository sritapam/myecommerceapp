# 🚀 E-commerce App: A Modern Android Shopping Experience

Welcome to **E-commerce App**! This is a comprehensive Android application designed to offer a fluid, secure, and intuitive online shopping experience. Developed with the latest Android and Kotlin technologies, this app simulates a modern e-commerce platform, letting users explore a product catalog, manage user authentication, build a shopping cart, view their order history, and much more.

## ✨ Core Features

E-commerce App is built with a robust set of essential functionalities for a complete e-commerce experience:

### 🔐 Authentication & User Profile

- **User Registration**: A secure signup process with full email and password validations
- **User Login**: Smooth authentication with persistent credentials to keep sessions active
- **Profile Management**: Users can view and edit their personal information
- **Real-time Validations**: Instant feedback on email and password forms for an improved UX
- **Test User**: `test@demo.com` / `12345678` (using the API from the docs)
- **Cloudinary Integration**: Profile images uploaded (from gallery or camera) are securely stored in Cloudinary
- **Theme Control**: Options for light, dark, or system theme, with preference persistence
- **Logout Functionality**: Complete user logout

### 🛒 Advanced Product Catalog

- **Flexible Display**: Product catalog presented in an interactive grid with high-quality images
- **Real-time Search**: Instant search functionality by product name and description
- **Category Filtering**: Products can be filtered using an interactive dropdown menu
- **Detailed Product View**: Dedicated screens showing comprehensive information for each product
- **Special Indicators**: Clear identification for products that include a beverage
- **API Integration**: All products are fetched and updated from a REST API
- **Smart Synchronization**: Utilizes WorkManager for automatic background product synchronization, ensuring up-to-date data and offline persistence

### 🛍️ Comprehensive Shopping Cart

- **Intuitive Management**: Easily add, edit quantities, and remove products from the cart
- **Automatic Total Calculation**: Real-time, automatic updates of the cart's total cost
- **Quantity Controls**: Dedicated buttons to increase or decrease the quantity of each item
- **Clearing Options**: Ability to remove individual products or clear the entire cart
- **Visual Summary**: Clear view with images and descriptions of items in the cart
- **Local Persistence**: The cart is saved using Room, ensuring items persist even when the app is closed

### 📦 Order System & History

- **Purchase Confirmation**: Checkout process with a detailed order summary
- **Complete History**: Access to all orders placed by the user, complete with timestamps
- **Unique Identification**: Each order has a unique ID generated with UUID
- **Chronological View**: History presented in a relative time format for easy understanding
- **Persistence & Synchronization**: Orders are saved with Room and synchronized with the API, ensuring their persistence and availability

### 🧭 Intuitive Navigation & Design

- **Bottom Navigation**: Quick and easy access to the main sections of the application
- **Contextual Navigation**: Optimized navigation flow that preserves user context
- **Jetpack Compose**: The entire user interface is built declaratively with Jetpack Compose, guaranteeing a modern and performant UI
- **Material Design 3**: Implementation following Material Design 3 principles for a consistent and accessible visual experience
- **Multi-theme Support**: The app correctly supports light and dark modes
- **Centralized Texts**: All UI texts are managed through strings.xml per module, simplifying localization and maintenance

## 🏗️ Architecture & Tech Stack

The project rigorously adheres to **Clean Architecture** and a **Multi-Module design**, promoting separation of concerns, testability, and scalability.

### 📋 Module Structure (Layer by Layer)

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Feature       │────│     Domain      │────│      Data       │────│      Core       │
│   UI + VM       │    │   Use Cases     │    │  Repositories   │    │  Models + Utils │
│   (Compose)     │    │  (Interfaces)   │    │ (Implementation)│    │   (Shared)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
```

**Key Dependency Rules** (strictly "downwards"):

- **Feature** depends on Domain, Data, and Core
- **Data** implements Domain, but DOES NOT depend on Feature
- **Domain** defines interfaces and business rules, and depends only on Core
- **Core** contains shared models and utilities, with NO dependencies on other modules

### 🧩 Module Breakdown

#### 📱 Presentation Layer (`:feature`)
Contains UI logic and ViewModels, implemented with Jetpack Compose.

- **`:app`**: Main module, orchestrates global navigation and DI configuration
- **`:feature:auth`**: Login screen with validations and Registration screen
- **`:feature:product-list`**: Product catalog with search, filters, and detail screen
- **`:feature:cart`**: Shopping cart and quantity management
- **`:feature:profile`**: User profile, order history, and settings

#### 🎯 Domain Layer (`:domain`)
*This module acts as a logical placeholder for domain-specific interfaces and use cases. In this project's structure, the concrete definitions for these (such as `AuthRepository` interface, `GetProductsUseCase`, and domain models like `Order`, `Product`) are located within the `:core:model` module for shared access across layers.*

#### 💾 Data Layer (`:data`)
This module is the core implementation of the data layer, encapsulating all data sources and their interactions. It contains the following top-level packages:

- **`local/`**: Manages local data persistence, including Room DAOs and database entities
- **`mappers/`**: Provides data transformation logic between DTOs, entities, and domain models
- **`remote/`**: Handles all remote data operations, including API service definitions and network DTOs
- **`repository/`**: Contains the concrete implementations of domain repository interfaces, coordinating data flow from local and remote sources
- **`service.imageupload/`**: Specific service for image upload functionality (e.g., Cloudinary integration details)
- **`workers/`**: Houses background processing units, such as WorkManager workers for data synchronization

#### 🔧 Core Modules (`:core`)
Contains shared and utility elements that are independent of other layers.

- **`:core:model`**: This module is the heart of the domain layer, containing all **domain models** (e.g., `CartItem`, `Order`, `Product`, `User`), the **interfaces for all domain repositories** (e.g., `CartRepository`, `ProductRepository`, `UserRepository`), and the **domain use cases** (e.g., `GetProductsUseCase`). It serves as the central hub for business logic definitions and data contracts across the application.
- **`:core:ui`**: Reusable UI components, theme system, and design system
- **`:core:navigation`**: Shared navigation components
- **`:core:cloudinary`**: Centralized service for Cloudinary image uploads

## 🛠️ Tech Stack

The project leverages a modern and robust tech stack for Android development:

### 📱 Frontend & UI
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Declarative UI toolkit for Android
- **Material 3**: Google's latest design system for Android
- **Navigation Compose**: Jetpack component for safe and structured navigation
- **Coil**: Lightweight and fast asynchronous image loading library
- **Cloudinary**: Platform for cloud-based image management and optimization

### ⚙️ Backend & Data Management
- **Retrofit**: Type-safe HTTP client for REST API communication
- **Moshi (with KSP)**: Efficient JSON serialization/deserialization framework for Kotlin
- **OkHttp**: Retrofit's underlying HTTP client, with interceptors for logging and debugging
- **Room**: Local persistence layer for offline data storage
- **Hilt**: Google's recommended Dependency Injection solution, for scalable and testable dependency management
- **StateFlow**: Reactive data streams for efficient and type-safe UI state management
- **Repository Pattern**: Implemented in the data layer to abstract data origin
- **WorkManager**: For executing background tasks, such as data synchronization

### 🧪 Quality & Testing
- **JUnit 4**: Standard unit testing framework for Java/Kotlin
- **MockK**: Mocking framework for Kotlin, allowing creation of test doubles for dependencies
- **Turbine**: Utility for easily testing Kotlin Flows reactively
- **Kover**: Gradle tool for generating detailed code coverage reports
- **Kotlin Coroutines Test**: Full support for testing asynchronous code with coroutines

## 🚀 Development Setup

To get this project up and running locally, follow these steps:

### 1. Clone the repository:

```bash
git clone https://github.com/tu-usuario/MyEcommerceApp.git
cd MyEcommerceApp
```

### 2. Open in Android Studio:
Open the project with Android Studio (Hedgehog 2023.1.1 or later recommended).

### 3. Sync the project:
Let Gradle synchronize all dependencies.

### 4. Configure Environment Variables:
Create a `local.properties` file in your project's root directory with the following variables:

```properties
RENDER_BASE_URL=https://tu-api.render.com
CLOUDINARY_CLOUD_NAME=YOUR_CLOUDINARY_CLOUD_NAME
CLOUDINARY_API_KEY=YOUR_CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET=YOUR_CLOUDINARY_API_SECRET
```

### 5. Run the app:
You can run the application on an emulator or a physical device.

## 🧪 Testing & Code Quality

Code quality and test coverage are fundamental pillars of this project.

### 🎯 Testing Strategy

- **MVVM Pattern**: Implemented across all screens, facilitating the separation of business logic from the UI
- **Organization**: Project structured into logical packages and modules (UI, ViewModel, data, domain) for enhanced clarity and maintenance
- **Reactive State Management**: Extensive use of ViewModels with StateFlow for efficient and asynchronous state management
- **Unit Tests**: Robust coverage of ViewModels and Repositories using mocks to isolate logic and ensure correct functionality
- **Code Coverage**: Kover is used to generate detailed reports on test coverage

### 📊 Testing & Quality Commands

```bash
# Run all tests in the project
./gradlew test

# Run tests for a specific module (e.g., :feature:cart)
./gradlew :feature:cart:test

# Generate HTML coverage report for all modules
./gradlew koverHtmlReportDebug

# Open the general HTML coverage report (after running the above command)
open app/build/reports/kover/debug/html/index.html
```

## 📚 Additional Documentation

For a deeper dive into implementation details and design decisions:

- 🧩 **Multi-Module Architecture** – A comprehensive guide to the project's modular structure
- 📖 **Technologies Used** – Justifications and roles of each tool in the tech stack
- 🔗 **Hilt Configuration** – Details on how Dependency Injection is managed
- 🎨 **Design & Themes** – Information on the design system and visual customization
- 🧪 **Testing Guide** – General configuration, commands, and testing strategies
- 📋 **ProductList Testing** – Detailed guide for testing specific ViewModels
- 🚀 **API Configuration** – Details on API setup and integration
- 🔐 **Authentication** – Handling of login, registration, and session
- ☁️ **Cloudinary** – How image uploading and loading are managed
- 📦 **WorkManager** – Implementation of background tasks

---

*This project demonstrates modern Android development practices with Clean Architecture, Jetpack Compose, and comprehensive testing strategies.*
🤝 Contributions
Your collaboration is welcome! If you find a bug, have a suggestion for improvement, or want to add a new feature, feel free to open an issue or submit a pull request.

📄 License
This project is licensed under the MIT License.
