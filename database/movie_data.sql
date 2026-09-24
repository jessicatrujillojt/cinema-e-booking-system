-- MySQL dump 10.13  Distrib 26.7.0, for macos15 (arm64)
--
-- Host: localhost    Database: cinema_db
-- ------------------------------------------------------
-- Server version	26.7.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'ac9188be-b5e5-11f1-8b76-571049416b54:1-25';

--
-- Dumping data for table `movie`
--

LOCK TABLES `movie` WRITE;
/*!40000 ALTER TABLE `movie` DISABLE KEYS */;
INSERT INTO `movie` VALUES (1,'A new chapter in the Resident Evil franchise following a new story of survival against a terrifying outbreak.','Action','https://image.tmdb.org/t/p/original/i7UyjfPio0VFHB9rBUZSFyhOoM8.jpg','R','Currently Running','Resident Evil','https://youtu.be/mNd1gb19A-c?si=dv1_klZ5WVcPm0yr'),(2,'An action adventure crime story starring Ethan Hawke and Russell Crowe.','Action','https://image.tmdb.org/t/p/original/8i5iZV50CoEtmDCFM7RSxCkpE8h.jpg','R','Currently Running','The Weight','https://youtu.be/7YpuMymmiJ8?si=fXUcJCx0Hz620Wd3'),(3,'A mysterious and dangerous story combining action, drama, and horror.','Action','https://image.tmdb.org/t/p/original/1n37BJchWHLiSYQkuFxe5KjB951.jpg','R','Currently Running','Hope','https://youtu.be/0Q4v7IKWO_E?si=ugSPhMtpVi7meCV3'),(4,'The next chapter of the magical story featuring Sandra Bullock and Nicole Kidman.','Fantasy','https://image.tmdb.org/t/p/original/ogwQOLbCfncjvBhFb5l0OmQH8KC.jpg','PG-13','Currently Running','Practical Magic 2','https://youtu.be/Ho10_4IX1jE?si=mUmNHbPLb8Zno2Xp'),(5,'An action comedy thriller starring Alan Ritchson and Owen Wilson.','Action','https://image.tmdb.org/t/p/original/yBKMAIj7clP42UkFejhGDBBoTpb.jpg','PG-13','Currently Running','Runner','https://youtu.be/m8JUmBgHejI?si=n9DESy2abhXXnfbu'),(6,'An action adventure thriller starring Brad Pitt, J.K. Simmons, and Anna Lambe.','Action','https://image.tmdb.org/t/p/original/1ApfSA8JTqeha3GTFEY8syV4auq.jpg','PG-13','Coming Soon','Heart of the Beast','https://youtu.be/JFQcDFhNh4o?si=-aNMTcZUc6V6zIRl'),(7,'A new action drama based on the Street Fighter franchise, starring Jason Momoa and Noah Centineo.','Action','https://image.tmdb.org/t/p/original/utKzCSCz2HtXwcDGfWLj2IHhSob.jpg','PG-13','Coming Soon','Street Fighter','https://youtu.be/Xt4X4FvXk2A?si=P4UCjr71g34h7Qvq'),(8,'A new adaptation of the classic romantic drama starring Daisy Edgar-Jones and Esmé Creed-Miles.','Drama','https://image.tmdb.org/t/p/original/czsyPHosFNbh4PPqXfqWu1aBXnA.jpg','PG-13','Coming Soon','Sense and Sensibility','https://youtu.be/mcrwcvad84U?si=cygnCCQmZFbw7JZw'),(9,'A comedy drama starring Tom Cruise, Riz Ahmed, and John Goodman.','Drama','https://image.tmdb.org/t/p/original/1ATXKrIPJyKNwnJ6lcG088Sa6zi.jpg','PG-13','Coming Soon','Digger','https://youtu.be/job8V254NAE?si=MSWQ7rqlH1MVLErm'),(10,'The next Avengers adventure featuring Robert Downey Jr., Pedro Pascal, Chris Hemsworth, and Vanessa Kirby.','Action','https://image.tmdb.org/t/p/original/jzPwsojjFStf5lR5Nm07w2hH56G.jpg','PG-13','Coming Soon','Avengers: Doomsday','https://youtu.be/irVNGjRFZGk?si=s_z-n0BVyw7M6VEh');
/*!40000 ALTER TABLE `movie` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-23 11:38:52
