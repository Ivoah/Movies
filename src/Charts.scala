package net.ivoah.movies

import scalatags.Text.all.*
import scalatags.Text.tags2.title

import java.util.UUID

object Charts {
  // Put here to avoid ambiguity with scalatags.Text.all.*
  import scalatags.Text.svgAttrs.*
  import scalatags.Text.svgTags.*

  def histogram[T](bins: Seq[(T, Int)], linkMapper: T => Option[String] = (_: T) => None): Frag = {
    object margins {
      val top = 20
      val right = 0
      val bottom = 25
      val left = 25
    }
    val barWidth = 10
    val barPadding = 5
    val _height = 200
    val yMax = math.ceil((bins.map(_._2) :+ 7).max/7.0).toInt*7

    def xScale(t: T) = bins.indexWhere(t == _._1)*(barWidth + barPadding)
    def yScale(n: Int) = margins.top + (_height - margins.bottom - margins.top)/yMax.toDouble*(yMax - n)

    val _width = xScale(bins.last._1) + barWidth

    val scrollboxId = UUID.randomUUID().toString

    div(scalatags.Text.styles.display:="flex",
      svg(flexShrink:=0, height:=_height, width:=margins.left,
        g(transform:=s"translate(${margins.left},0)", fontSize:=10, fontFamily:="sans-serif", textAnchor:="end",
            for (y <- 0 to yMax by (yMax/7)) yield g(transform:=s"translate(0,${yScale(y)})",
            text(x:="-9", dy:="0.32em", y),
            line(x2:="-6", stroke:="black")
          )
        )
      ),
      div(overflowX:="scroll", id:=scrollboxId,
        svg(height:=_height, width:=_width, fontSize:=10,
          for (y <- 0 to yMax by (yMax/7)) yield line(x1:=0, y1:=yScale(y), x2:=_width, y2:=yScale(y), stroke:="currentColor", strokeOpacity:=0.2),
          for ((d, m) <- bins) yield g(transform:=s"translate(${xScale(d) + barWidth/2.0}, 0)", textAnchor:="end",
            a(linkMapper(d).map(l => href:=l),
              title(m),
              line(y1:=yScale(0), y2:=yScale(m), stroke:="black", strokeWidth:=barWidth),
              text(dominantBaseline:="middle", transform:=s"translate(0, ${yScale(0) + 3}) rotate(-90)", d.toString)
            )
          )
        )
      ),
      script(raw(s"document.getElementById('$scrollboxId').scrollLeft = document.getElementById('$scrollboxId').scrollWidth"))
    )
  }
}
