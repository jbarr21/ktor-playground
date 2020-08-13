import io.ktor.application.ApplicationCall
import io.ktor.features.origin
import io.ktor.request.host
import io.ktor.request.port

fun ApplicationCall.redirectUrl(path: String): String {
  val defaultPort = if (request.origin.scheme == "http") 80 else 443
  val hostPort = request.host() + request.port().let { port ->
    if (port == defaultPort || "herokuapp" in request.origin.host) "" else ":$port"
  }
  val protocol = request.origin.scheme
  return "$protocol://$hostPort$path"
}
