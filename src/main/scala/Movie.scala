package net.ivoah.movies

import java.sql.Connection
import java.util.Date
import scala.collection.mutable
import scala.util.Using

case class Movie(title: String, rating: Double, cried: Boolean, last_watched: Date, watch_count: Int, watched_with: Seq[String])

object Movie {
  def getAll()(implicit db: Connection): Seq[Movie] = {
    Using.resource(db.prepareStatement(
      """
        |SELECT
        |	title outer_title,
        |	(SELECT rating FROM movies WHERE title=outer_title ORDER BY started DESC LIMIT 1) rating,
        |	BIT_OR(cried) cried,
        |	max(started) last_watched,
        |	count(*) watch_count,
        |	IFNULL(GROUP_CONCAT(NULLIF(watched_with, '') SEPARATOR ', '), '') watched_with
        |FROM movies
        |GROUP BY title
        |ORDER BY last_watched DESC
        |""".stripMargin)) { stmt =>
      val results = stmt.executeQuery()
      val buffer = mutable.Buffer[Movie]()
      while (results.next()) {
        buffer.append(Movie(
          results.getString("outer_title"),
          results.getDouble("rating"),
          results.getBoolean("cried"),
          results.getDate("last_watched"),
          results.getInt("watch_count"),
          results.getString("watched_with").split(", ").filter(_.nonEmpty).distinct
        ))
      }
      buffer.toSeq

    }
  }
}
