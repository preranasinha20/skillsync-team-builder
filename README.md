A desktop application that automates team formation for academic and professional projects using skill-based compatibility scoring. Teachers post projects, students apply, and the system generates optimal team combinations with match percentages.
Features
For Teachers

Post projects with required skill sets, visible to students filtered by department and year
Request auto-generated team combination sheets for an entire batch or branch
Export optimal team assignments as CSV with compatibility scores per team

For Students

Browse available projects matching their department and year
Build or join teams manually through the platform
View profile with skill scores and team history

Core Algorithm

Skill Matcher scores student compatibility based on complementary skill sets
Team Builder generates balanced combinations across a batch, optimizing for equal skill distribution
Match percentage calculated per team to quantify formation quality

Tech Stack
Language: Java
UI Framework: JavaFX
Build Tool: Maven
Architecture: MVC — model, service, UI layers
Data: DAO pattern with CSV export support


Project Structure
SkillSync/
├── src/main/java/
│   ├── model/          # User, Team data models
│   ├── service/        # SkillMatcher, TeamBuilder, CSVExporter
│   └── ui/screens/     # JavaFX screens per role
└── pom.xml


How It Works

Teacher uploads a project with required skills and target department/year
Students view available projects on their dashboard and form teams
For bulk assignment, teacher requests an optimized combination sheet
SkillSync scores all possible student combinations using the Skill Matcher
Balanced teams are generated and exported as a CSV with match percentages

Running Locally
bash# Requires Java 11+ and Maven
mvn clean install
mvn javafx:run
