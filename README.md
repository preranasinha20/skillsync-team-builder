# 🚀 SkillSync – Skill-Based Intelligent Team Builder

SkillSync is a Java-based backend system that automatically forms optimized student teams based on skills, availability, and project requirements.

It replaces manual team allocation with a **data-driven, structured, and scalable approach** using JDBC and MySQL.

---

## 📌 Key Features

* 🔍 Skill-based team formation
* ⚖ Balanced team distribution
* 📊 Data-driven project allocation
* 🔗 Relational database design (MySQL)
* 🧩 Modular backend using DAO pattern

---

## 🛠 Tech Stack

* **Java (JDK 17+)**
* **JDBC**
* **MySQL (Railway Cloud)**
* **Maven**
* **Git & GitHub**

---

## 🗄 Database Design

The system uses a relational database with the following core tables:

### 👤 Users

* Stores student details

### 🧠 Skills

* Stores available skills

### 👥 Teams (by teammate)

* Handles team structure

### 📁 Projects (⭐ Added by Manas)

* Stores project details

### 🔗 Project Skills (⭐ Added by Manas)

* Maps required skills to projects

---

## 🧑‍💻 My Contribution (Manas Kumar)

* ✅ Designed and implemented `ProjectSchema.java`
* ✅ Created `projects` and `project_skills` tables
* ✅ Integrated foreign key relationships with `users`
* ✅ Configured Maven build system
* ✅ Fixed remote database connection (Railway + JDBC)
* ✅ Tested schema execution via Maven

---

## ⚙️ How to Run the Project

### 1. Clone the repository

```bash
git clone https://github.com/Manarsenic/SkillSync-Team-Builder.git
cd SkillSync-Team-Builder
```

### 2. Compile using Maven

```bash
mvn clean compile
```

### 3. Test database connection

```bash
mvn exec:java -Dexec.mainClass="database.DBConnection"
```

### 4. Create tables

Run schemas in this order:

```bash
mvn exec:java -Dexec.mainClass="database.UserSchema"
mvn exec:java -Dexec.mainClass="database.TeamSchema"
mvn exec:java -Dexec.mainClass="database.ProjectSchema"
```

---

## 🧪 Output

After running schemas, database will contain:

* users
* skills
* teams
* projects
* project_skills

---

## 📌 Future Improvements

* Add ProjectDAO for CRUD operations
* Implement team allocation algorithm
* Build frontend interface
* Add REST APIs

---

## 🤝 Contributors

* Prerana Sinha
* Manas Kumar
* Chandana

---

## 📄 License

This project is licensed under the Apache 2.0 License.

---

⭐ If you like this project, consider starring the repo!
