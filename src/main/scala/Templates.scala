package net.ivoah.movies

import java.text.SimpleDateFormat
import scala.math.Ordering.Implicits.seqOrdering
import scalatags.Text.all._
import scalatags.Text.tags2.title

import Extensions._

object Templates {
  private val doctype = "<!DOCTYPE html>\n"
  private val dateFormatter = new SimpleDateFormat("yyyy-MM-dd")

  private def pluralize(count: Int, singular: String, plural: String): String = s"$count ${if (count == 1) singular else plural}"

  def sortable_table[T <: Product](rows: Seq[T], sort_by: String): Frag = {
    table(
      table(
        thead(
          tr(rows.head.productElementNames.toSeq.map { header =>
            th(a(href:=s"?sort_by=${header.toLowerCase.replace(" ", "_").replace("?", "")}", header))
          })
        ),
//        for (Movie(title, rating, cried, last_watch, watch_count, watched_with) <- sort_by match {
//          case "title" => movies.sortBy(_.title)
//          case "latest_rating" => movies.sortBy(_.rating).reverse
//          case "cried" => movies.sortBy(!_.cried)
//          case "last_watch" => movies.sortBy(_.last_watched).reverse
//          case "watch_count" => movies.sortBy(_.watch_count).reverse
//          case "watched_with" => movies.sortBy(_.watched_with.length).reverse
//          case _ => movies.sortBy(_.last_watched).reverse
//        }) yield {
        for (row <- rows) yield {
          tr(
            row.productIterator.toSeq.map(c => td(c.toString))
          )
        }
      )
    )
  }

  def root(_title: String, movies: Seq[Movie], sort_by: String, nav_back: Boolean): String = doctype + html(
    head(
      title(_title),
      link(rel:="icon", href:="/static/favicon.png"),
      link(rel:="stylesheet", href:="/static/style.css")
    ),
    body(
      if (nav_back) a(`class`:="lnav", href:="/", "< all movies") else frag(),
      h1(_title),
      p(pluralize(movies.length, "result", "results")),
      table(
        thead(
          tr(Seq("Title", "Latest rating", "Cried?", "Last watch", "Watch count", "Watched with", "Location(s)").map { header =>
            val sort_by = header.toLowerCase.replace(" ", "_").replace("?", "").replace("(", "").replace(")", "")
            th(a(href:=s"?sort_by=$sort_by", header))
          })
        ),
        for (movie <- sort_by match {
          case "title" => movies.sortBy(_.title)
          case "latest_rating" => movies.sortBy(_.rating).reverse
          case "cried" => movies.sortBy(!_.cried)
          case "last_watch" => movies.sortBy(_.last_watched).reverse
          case "watch_count" => movies.sortBy(_.watch_count).reverse
          case "watched_with" => movies.sortBy(_.watched_with.length).reverse
          case "locations" => movies.sortBy(_.locations.sorted)
          case _ => movies.sortBy(_.last_watched).reverse
        }) yield {
          tr(
            td(a(href:=s"/movies/${movie.title.urlEncoded}", movie.title)),
            td(movie.rating),
            td(if (movie.cried) "✓" else "✗"),
            td(dateFormatter.format(movie.last_watched)),
            td(movie.watch_count),
            td(movie.watched_with.map(name => a(href:=s"/people/${name.urlEncoded}", name)).mkSeq(frag(", "))),
            td(movie.locations.map(name => a(href:=s"/locations/${name.urlEncoded}", name)).mkSeq(frag(", "))),
          )
        }
      )
    )
  )

  def movie(title: String, header: String, watches: Seq[MovieWatch]): String = doctype + html(
    head(
      tag("title")(title),
      link(rel:="icon", href:="/static/favicon.png"),
      link(rel:="stylesheet", href:="/static/style.css")
    ),
    body(
      a(`class`:="lnav", href:="/", "< all movies"),
      h1(header),
      p(pluralize(watches.length, "result", "results")),
      table(
        thead(
          tr(Seq("Title", "Date", "Rating", "Cried?", "Watched with", "Location").map(h => th(h)))
        ),
        for (watch <- watches) yield {
          tr(
            td(a(watch.title, href:=s"/movies/${watch.title.urlEncoded}")),
            td(dateFormatter.format(watch.started)),
            td(watch.rating),
            td(if (watch.cried) "✓" else "✗"),
            td(watch.watched_with.map(name => a(href:=s"/people/${name.urlEncoded}", name)).mkSeq(frag(", "))),
            td(a(href:=s"/locations/${watch.location.urlEncoded}", watch.location))
          )
        }
      )

    )
  )
}
