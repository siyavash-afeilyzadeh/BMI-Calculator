<div align="center">

# 🏋️‍♂️ BMI Tracker

**Enterprise-grade desktop health analytics and body mass index tracking application.**  
*Engineered with Modern Java, JavaFX, Hibernate ORM, and Clean Architecture principles.*

---

[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-26.0.2-FF6F00?style=for-the-badge&logo=oracle&logoColor=white)](https://openjfx.io/)
[![Hibernate](https://img.shields.io/badge/Hibernate_ORM-8.0.0.Beta3-59666C?style=for-the-badge&logo=hibernate&logoColor=white)](https://hibernate.org/)
[![H2 Database](https://img.shields.io/badge/Database-H2_2.5.252-044F88?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.h2database.com/)
[![Lombok](https://img.shields.io/badge/Lombok-1.18.48-BC1823?style=for-the-badge)](https://projectlombok.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

<p align="center">
  <a href="#-key-capabilities">Key Features</a> •
  <a href="#-technical-stack--dependencies">Tech Stack</a> •
  <a href="#-architecture--engineering">Architecture</a> •
  <a href="#-getting-started--execution">Getting Started</a> •
  <a href="#-solid-principles-implementation">SOLID Principles</a> •
  <a href="#-license">License</a>
</p>

</div>

---

## 🌟 Key Capabilities

- 📊 **Precision BMI Calculation:** Instant metric (kg/cm) and imperial (lbs/in) BMI calculation with exact input sanitization and diagnostic classification across standard health categories.
- 📈 **Longitudinal Health Tracking:** Persisted timestamped measurement history, allowing users to trace body metrics progression over time with trend analytics.
- 🔄 **Bidirectional Unit Converter:** Built-in domain converter service handling seamless unit switches without numerical drift.
- 👥 **Multi-Profile Management:** Complete profile identity management supporting dynamic creation, profile switching, and isolated historical records.
- 🎨 **Modern High-DPI Desktop Interface:** Styled JavaFX presentation layer fine-tuned for high-resolution displays with a sleek, dark-slate palette (`#0b1120`).
- 🔒 **Zero Native Coupling Persistence:** Fully abstracted repository persistence utilizing local file-backed H2 embedded storage through Hibernate ORM.
- 📦 **Dual Distribution Channels:** Available both as a standalone **Fat JAR** and a fully bundled, zero-dependency **Windows Native Executable (`.exe`)**.

---

## 🛠️ Technical Stack & Dependencies

All dependencies are defined and orchestrated in `pom.xml` (`com.black:BMI-Tracker:1.0.0`):

| Component | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Java (JDK) | `25` | Target language and virtual machine runtime |
| **GUI Framework** | OpenJFX (`controls`, `fxml`) | `26.0.2` | Reactive desktop interface components |
| **ORM / JPA** | Hibernate Core | `8.0.0.Beta3` | Entity persistence and relational mapping |
| **Database** | H2 Database Engine | `2.5.252` | Lightweight embedded local SQL persistence |
| **Boilerplate** | Project Lombok | `1.18.48` | Code generation (Builders, Accessors, Slf4j) |
| **Logging** | SLF4J & Logback Classic | `2.0.18` / `1.6.3` | Structured runtime execution tracing |
| **Testing** | JUnit Jupiter Engine | `6.1.3` | Isolated domain and use-case automated unit tests |
| **Packaging** | Apache Maven & Shade Plugin | `3.8+` / `3.6.0` | Fat JAR creation and dependency shading |
| **Bundling** | JDK `jpackage` | `25` | Native self-contained Windows executable bundling |

---

## 🏛️ Architecture & Engineering

The application strictly enforces **Clean Architecture** (Ports and Adapters pattern), isolating core domain rules from UI frameworks and persistence drivers:
```text
com.black.bmitracker/
├── ⚙️ application/
│   ├── dto/
│   │   └── BmiResultDto.java                     # Immutable DTO record for BMI computation outcomes
│   └── service/
│       └── HealthTrackerService.java             # Orchestrates use-cases & cross-cutting logic
│
├── 🌐 domain/
│   ├── exception/
│   │   └── InvalidMeasurementException.java     # Domain-level invariant violations & bounds check
│   ├── model/
│   │   ├── entity/
│   │   │   ├── MeasurementEntry.java             # Historical metric snapshot entity
│   │   │   └── UserProfile.java                  # User demographic aggregate entity
│   │   └── enums/
│   │       ├── BmiCategory.java                  # Health classification thresholds (WHO/CDC)
│   │       ├── Gender.java                       # Demographic biological profile constants
│   │       └── UnitSystem.java                   # Metric / Imperial measurement unit descriptors
│   ├── repository/                               # Domain Repository Ports (DIP Interfaces)
│   │   ├── MeasurementEntryRepositoryPort.java
│   │   └── UserProfileRepositoryPort.java
│   └── service/                                  # Pure Domain Business Logic
│       ├── BmiCalculatorService.java             # Exact mathematical formula calculation engine
│       ├── BmiCategoryClassifier.java            # Diagnostic categorization rules
│       └── UnitConverterService.java             # Precision bidirectional unit transformation
│
├── 🗄️ infrastructure.persistence/
│   ├── config/
│   │   └── HibernateUtil.java                    # SessionFactory lifecycle & ORM configuration
│   └── repository/                               # Concrete Persistence Adapters
│       ├── BaseRepository.java                   # Generic CRUD repository abstraction
│       ├── AbstractHibernateRepository.java      # Template method base implementation
│       ├── MeasurementEntryRepository.java       # Hibernate implementation of Measurement Port
│       └── UserProfileRepository.java            # Hibernate implementation of UserProfile Port
│
└── 🖥️ ui/
├── view/                                     # Presentation Components & Views
│   ├── BmiGaugeView.java                     # Custom visual speedometer gauge
│   ├── MainDashboardView.java                # Analytics dashboard, tables & trend charts
│   └── ProfileSelectionView.java             # User onboarding & profile management screen
├── BmiTrackerApp.java                        # JavaFX Application lifecycle manager
└── Launcher.java                             # Modular-safe Fat JAR / Native EXE entrypoint
