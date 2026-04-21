# 🚀 SkillSync – Skill-Based Intelligent Team Builder

SkillSync is a Java-based system that automatically forms **balanced and optimized teams** based on student skills and project requirements.

It replaces manual/random team allocation with a **data-driven, scoring-based approach** to ensure fairness, efficiency, and better collaboration.

---

## 📌 Problem Statement

In academic environments, teams are often formed manually, which leads to:

- Skill imbalance in teams
- Uneven workload distribution
- Bias in team selection
- Inefficient collaboration

SkillSync solves this by intelligently grouping students based on their skills.

---

## 🎯 Objectives

- Build a system to manage student skills
- Define project requirements
- Generate teams automatically
- Ensure balanced skill distribution
- Apply scoring logic to select best teams

---

## 🧠 Core Concept

The system evaluates teams based on:

- **Skill Coverage** → Required skills satisfied
- **Skill Balance** → Even distribution across members
- **Diversity Score** → Variety of skills in team

### Example Scoring Formula

Score = (Coverage × 0.5) + (Balance × 0.3) + (Diversity × 0.2)

The team with the **highest score is selected**.

---

## 🏗️ System Architecture

User (CLI)
↓
Service Layer (TeamBuilderService)
↓
Model Layer (Student, Team, Skill)
↓
Database (MySQL) / File (CSV)

---

## 🧩 Project Structure

SkillSync/
│── model/
│ ├── Student.java
│ ├── Skill.java
│ ├── Team.java
│ └── ProjectRequirement.java

│
│── service/
│ ├── TeamBuilderService.java
│ └── TeamScoringStrategy.java
│

│── strategy/
│ ├── BalancedTeamStrategy.java
│ └── RandomTeamStrategy.java
│

│── database/
│ └── DatabaseManager.java
│
│── utils/
│ └── CSVHandler.java
│


│── exceptions/
│ ├── InvalidSkillLevelException.java
│ └── TeamFormationException.java
│
│── main/
│ └── Main.java



---

## ⚙️ Technologies Used

- Java (JDK 17+)
- MySQL
- JDBC
- File I/O (BufferedReader, FileWriter)
- OOP Principles
- Strategy Design Pattern
- Git & GitHub

---

## 🧪 How It Works

1. Add student data (skills + levels)
2. Define project requirements
3. Generate all possible team combinations
4. Apply scoring algorithm
5. Select the highest-scoring team
6. Display or export results

---

## 💻 How to Run

### Prerequisites

- Java JDK 17+
- MySQL installed
- IDE (IntelliJ / Eclipse)

### Steps

1. Clone the repository

git clone https://github.com/manarsenic/SkillSync.git

2. Navigate to project

cd SkillSync

3. Compile

javac Main.java

4. Run

java Main

---

## 📌 Sample CLI Menu

==== SkillSync System ====

1. Add Student  
2. View Students  
3. Define Project Requirements  
4. Generate Team  
5. Export Team  
6. Exit  

---

## 📊 Example Output

Best Team:
- Rahul (Java:4, SQL:3)
- Priya (ML:5)
- Aman (Java:3, Python:4)

Team Score: 8.7

---

## 🧠 OOP Concepts Used

- Encapsulation → Data hiding using private variables
- Abstraction → Interface-based design
- Polymorphism → Multiple scoring strategies
- Inheritance → Custom exception handling

---

## 🔮 Future Enhancements

- Web-based interface (Spring Boot)
- AI/ML-based team recommendations
- Mobile application
- Advanced analytics dashboard

---

## 👨‍💻 Contributors

- Manas Kumar  
- Mani Chandana J  
- Prerana Sinha  

---

## 🎓 Academic Context

Developed as part of a Programming in Java Project  
Symbiosis Institute of Technology, Pune

---

## ⭐ Final Note

SkillSync demonstrates how real-world problems like team formation can be solved using:

- Java  
- OOP Design  
- Algorithmic Thinking  

---
