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
  <a href="#-getting-started">Getting Started</a> •
  <a href="#-solid-principles-implementation">SOLID Principles</a>
</p>

</div>

---

## 🌟 Key Capabilities

- 📊 **Precision BMI Calculation:** Instant metric (kg/cm) and imperial (lbs/in) BMI calculation with exact input sanitization and classification across 6 standard health categories.
- 📈 **Longitudinal Health Tracking:** Persisted timestamped measurement history, allowing users to trace body metrics progression over time.
- 🔄 **Bidirectional Unit Converter:** Built-in domain converter service handling seamless unit switches without numerical drift.
- 👥 **Multi-Profile Management:** Complete profile identity management supporting creation, profile switching, and customized metric records.
- 🎨 **Modern High-DPI Desktop Interface:** Styled JavaFX presentation layer fine-tuned for high-resolution displays (1920x1080) with a modern dark theme palette.
- 🔒 **Zero Native Coupling Persistence:** Fully abstracted repository persistence utilizing in-memory and local file-backed H2 storage through Hibernate.

---

## 🛠️ Technical Stack & Dependencies

All dependencies are defined in `pom.xml` (`com.black:BMI-Tracker:1.0-SNAPSHOT`):

| Component | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Java (JDK) | `25` | Target language and virtual machine runtime |
| **GUI Framework** | OpenJFX (`controls`, `fxml`) | `26.0.2` | Reactive desktop interface components |
| **ORM / JPA** | Hibernate Core | `8.0.0.Beta3` | Entity persistence and relational mapping |
| **Database** | H2 Database Engine | `2.5.252` | Lightweight embedded local SQL persistence |
| **Boilerplate** | Project Lombok | `1.18.48` | Code generation (Builders, Accessors, Records) |
| **Logging** | SLF4J & Logback Classic | `2.0.18` / `1.6.3` | Structured runtime execution tracing |
| **Testing** | JUnit Jupiter Engine | `6.1.3` | Isolated domain and use-case automated tests |
| **Build Tool** | Apache Maven | `3.8+` | Dependency resolution and lifecycle build |

---

## 🏛️ Architecture & Engineering

The application strictly enforces **Clean Architecture** (Ports and Adapters pattern), isolating core domain logic from any framework or persistence mechanism:
```text
com.black.bmitracker/
├── 🌐 domain/
│   ├── model/entity/               # Pure POJO business entities (UserProfile, MeasurementEntry)
│   ├── model/enums/                # Invariant value objects (UnitSystem, BmiCategory)
│   ├── service/                    # Pure domain logic (BmiCalculatorService, UnitConverterService)
│   └── repository/                 # Domain repository interfaces / Ports (DIP abstraction)
│
├── ⚙️ application/
│   ├── dto/                        # Immutable data transfer records (BmiResultDto)
│   └── service/                    # Application use-case coordinators (HealthTrackerService)
│
├── 🗄️ infrastructure/
│   └── persistence/
│       ├── config/                 # Hibernate SessionFactory bootstrapping (HibernateUtil)
│       └── repository/             # Concrete Hibernate repository adapters
│
└── 🖥️ ui/
├── controller/                 # Presentation controllers
└── view/                       # JavaFX application entrypoint (BmiTrackerApp) & views
