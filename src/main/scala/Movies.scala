package net.ivoah.movies

import java.sql.Connection
import java.nio.file._
import net.ivoah.vial._

class Movies()(implicit val db: Connection) {
  private def person(name: String): String = {
    Templates.person(name, MovieWatch.get(person = Some(name)))
  }
  
  private val rootRouter = Router {
    case ("GET", "/", request) => Response(Templates.root(
      "Noah's movie list",
      MovieWatch.get(),
      request.params.getOrElse("sort_by", "rating"),
      nav_back = false
    ))
  }

  private val staticRouter = Router {
    case ("GET", s"/static/$file", _) => Response.forFile(Paths.get(s"static/$file"))
  }
  
  private val moviesRouter = Router {
    case ("GET", s"/movies/$title", request) => Response(Templates.root(
      title,
      MovieWatch.get(title = Some(title)),
      request.params.getOrElse("sort_by", "date"),
      nav_back = true
    ))
  }
  
  private val peopleRouter = Router {
    case ("GET", s"/people/$name", request) => Response(Templates.root(
      s"Movies watched with $name",
      MovieWatch.get(person = Some(name)),
      request.params.getOrElse("sort_by", "date"),
      nav_back = true
    ))
  }
  
  val router: Router = rootRouter ++ staticRouter ++ moviesRouter ++ peopleRouter
}
