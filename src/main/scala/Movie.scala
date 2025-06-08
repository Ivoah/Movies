package net.ivoah.movies

import java.sql.Connection
import java.util.Date
import scala.util.Using

case class Movie(title: String, rating: BigDecimal, cried: Boolean, last_watched: Date, watch_count: Int, watched_with: Seq[String], locations: Seq[String])

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
        |	IFNULL(GROUP_CONCAT(NULLIF(watched_with, '') SEPARATOR ', '), '') watched_with,
        |	IFNULL(GROUP_CONCAT(NULLIF(location, '') SEPARATOR ', '), '') locations
        |FROM movies
        |GROUP BY title
        |ORDER BY last_watched DESC
        |""".stripMargin)) { stmt =>
      Iterator.unfold(stmt.executeQuery()) { results =>
        if (results.next()) Some(Movie(
          results.getString("outer_title"),
          results.getBigDecimal("rating"),
          results.getBoolean("cried"),
          results.getDate("last_watched"),
          results.getInt("watch_count"),
          results.getString("watched_with").split(", ").filter(_.nonEmpty).distinct,
          results.getString("locations").split(", ").filter(_.nonEmpty).distinct.sorted
        ), results)
        else None
      }.toSeq
    }
  }
}
