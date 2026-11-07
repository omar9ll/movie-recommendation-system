# 🎬 Movie Recommendation System

A Java console application that recommends movies based on a user's own ratings. It learns which **genres** and **directors** you rate highly and scores the rest of the catalog to suggest what to watch next, with a popularity-based fallback for new users (cold start).

> Built as a team project for a software design course at McMaster University (Fall 2025), following an Agile process with sprints, backlogs and formal requirements/design documentation.

## ✨ Features
- Browse a catalog of movies (title, director, year, genres, average rating)
- Rate movies; ratings are saved to disk and persist between sessions
- Personalized top-5 recommendations based on preferred genres and directors from movies you rated 4+
- Cold-start handling: recommends popular, highly rated movies when you have no ratings yet
- Robust file parsing that validates and discards malformed records
- Unit tests for file-handling logic (JUnit)

## 🧠 How the recommendations work
1. Load the user's ratings and find movies rated **≥ 4**
2. Build a preference profile: counts of favourite **genres** and **directors**
3. Score every unrated movie in the catalog against that profile, factoring in its overall rating
4. Return the top 5; if there is not enough signal, fall back to popular unrated movies

## 🛠️ Tech
**Java 21** · **Maven** · **JUnit** · Object-oriented design (separate classes for UI, file I/O, rating management and the recommendation engine)

## 📁 Project structure
```
src/main/java/.../
  MovieRecommendationSystem.java  # entry point
  ConsoleUI.java                  # menu-driven console interface
  RecommendationEngine.java       # scoring and recommendation logic
  RatingManager.java              # adding and updating user ratings
  FileHandler.java                # reading/writing and validating data files
  Movie.java                      # movie model and genre enum
src/test/java/.../FileHandlerTest.java
data/movies.txt                   # movie catalog
data/my_ratings.txt               # user ratings (auto-created if missing)
docs/                             # SRS, SDD, sprint execution and testing log
```

## ▶️ Run it
Requires **JDK 21+** and **Maven**.
```bash
mvn clean package
java -jar target/MovieRecommendationSystem-1.0-SNAPSHOT.jar
```
The app expects `data/movies.txt` to exist. `data/my_ratings.txt` is created automatically.

**Data formats**
- `movies.txt`: `movie_id,title,director,year,genres,avg_rating,num_ratings`
- `my_ratings.txt`: `movie_id,rating,timestamp`

## 📄 Documentation
- [Software Requirements Specification](docs/SRS.pdf)
- [Software Design Document](docs/SDD.pdf)
- [Sprint Execution](docs/Sprint_Execution.pdf)
- [Testing Log](docs/Testing_Log.pdf)

## 👥 Team
- Omar Alshantout ([@omar9ll](https://github.com/omar9ll))
- [@ibrahimawan327](https://github.com/ibrahimawan327)
- [@Jackyzh8ng](https://github.com/Jackyzh8ng)
