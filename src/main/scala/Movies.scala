package net.ivoah.movies

import java.sql.Connection
import java.nio.file._
import net.ivoah.vial._

class Movies()(implicit val db: Connection) {
//  private def person(name: String): String = {
//    Templates.person(name, MovieWatch.get(person = Some(name)))
//  }
  
  val router: Router = Router {
    case ("GET", "/", request) => Response(Templates.root(
      "Noah's movie list",
      Movie.getAll(),
      request.params.getOrElse("sort_by", "latest_rating"),
      nav_back = false
    ))

    case ("GET", s"/static/$file", _) => Response.forFile(Paths.get(s"static/$file"))

    case ("GET", s"/movies/$title", request) => Response(Templates.movie(
      title,
      title,
      MovieWatch.get(title = Some(title)),
//      request.params.getOrElse("sort_by", "date")
    ))

    case ("GET", s"/people/$name", request) => Response(Templates.movie(
      name,
      s"Movies watched with $name",
      MovieWatch.get(person = Some(name)),
    ))

    case ("GET", s"/locations/$name", request) => Response(Templates.movie(
      name,
      s"Movies watched at $name",
      MovieWatch.get(location = Some(name)),
    ))
  }
}
