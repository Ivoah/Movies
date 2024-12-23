package net.ivoah.movies

import java.sql.Connection
import java.util.Date
import scala.collection.mutable
import scala.util.Using

case class MovieWatch(title: String, rating: Double, cried: Boolean, started: Date, finished: Date, watched_with: Seq[String])

object MovieWatch {
  def get(title: Option[String] = None, person: Option[String] = None)(implicit db: Connection): Seq[MovieWatch] = {
    val order_by = "ORDER BY started DESC"
    Using.resource({
      if (title.nonEmpty) db.prepareStatement(s"SELECT * FROM movies WHERE title = ? $order_by")
      else if (person.nonEmpty) db.prepareStatement(s"SELECT * FROM movies WHERE LOCATE(?, watched_with) $order_by")
      else db.prepareStatement(s"SELECT * FROM movies $order_by")
    }) { stmt =>
      title.foreach(stmt.setString(1, _))
      person.foreach(stmt.setString(1, _))
      Iterator.unfold(stmt.executeQuery()) { results =>
        if (results.next()) Some(MovieWatch(
          results.getString("title"),
          results.getDouble("rating"),
          results.getBoolean("cried"),
          results.getDate("started"),
          results.getDate("finished"),
          results.getString("watched_with").split(", ").filter(_.nonEmpty)
        ), results)
        else None
      }.toSeq
    }
  }
}
