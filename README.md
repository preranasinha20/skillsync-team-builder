# SkillSync: Skill-Based Intelligent Team Builder

SkillSync is a Java-based application developed at Symbiosis Institute of Technology, Pune. This project was created for the Department of Artificial Intelligence and Machine Learning as part of the **"Programming in Java"** curriculum.

---

##  Project Overview

In academic institutions, manual or random team formation often leads to skill imbalances, inefficient collaboration, and unequal workloads.

**SkillSync** solves this by providing a structured, automated system that forms balanced project teams based on individual skill levels and predefined project requirements.

---

## ✨ Key Features

* **Intelligent Scoring Engine**
  Evaluates teams based on:

  * Skill Coverage
  * Skill Balance
  * Diversity Scores

* **Strategy-Based Formation**
  Implements the Strategy Design Pattern to allow flexible team-building algorithms.

* **Database Integration**
  Utilizes JDBC to connect with a MySQL database for persistent storage of student profiles and team history.

* **Data Portability**
  Supports CSV import for bulk student registration and export for team reporting.

* **Robust Validation**
  Includes custom exception handling to ensure data integrity (e.g., `InvalidSkillLevelException`).

---

## 🏗️ Architecture & OOP Design

The project follows a **modular layered architecture** to ensure clear separation between logic, database, and file handling.

### Core OOP Concepts Applied

* **Encapsulation**
  Used in `Student`, `Skill`, and `Team` classes to protect data and validate inputs via getters/setters.

* **Abstraction**
  The `TeamScoringStrategy` interface hides implementation details from the service layer.

* **Inheritance**
  Custom exceptions extend the base `Exception` class for structured error management.

* **Polymorphism**
  Multiple strategies (e.g., `BalancedTeamStrategy`) implement the same interface, allowing the system to switch logic at runtime.

---

## 🛠️ Tech Stack

* **Language:** Java (JDK 17+)
* **Database:** MySQL
* **Connectivity:** JDBC (Java Database Connectivity)
* **Tools:** IntelliJ IDEA / Eclipse, Git, GitHub

---

## 📂 Project Structure

```
SkillSync/
├── src/
│   ├── com.skillsync.model/       # Entity classes (Student, Skill, Team)
│   ├── com.skillsync.service/     # Scoring logic and Strategy Pattern
│   ├── com.skillsync.dao/         # JDBC DatabaseManager
│   ├── com.skillsync.exception/   # Custom Exception classes
│   └── com.skillsync.util/        # CSV File I/O utilities
├── sql/                           # MySQL database schema scripts
├── data/                          # Sample CSV files for import/testing
└── README.md
```

---

## 👥 Contributors

* **Manas Kumar** (24070126103)
* **Mani Chandana J** (24070126107)
* **Prerana Sinha** (24070126139)

**Mentor:** Dr. Pratima Joshi
