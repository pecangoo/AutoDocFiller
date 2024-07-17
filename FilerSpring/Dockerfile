FROM openjdk:20
RUN apt-get update
RUN apt-get install -y mongodb
COPY /Users/svetislavdobromirov/working/AutoDocFiller/FilerSpring/target/FilerSpring-0.0.1-SNAPSHOT.jar /app/app.jar
CMD service mongodb start && java -jar /app/app.jar
