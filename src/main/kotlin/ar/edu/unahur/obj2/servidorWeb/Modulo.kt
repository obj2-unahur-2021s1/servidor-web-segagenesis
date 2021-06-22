package ar.edu.unahur.obj2.servidorWeb

class Modulo(val extensiones: List<String>, val body: String, val tiempo: Int) {
  fun puedeSoportar(url:String) = this.extensiones.any { url.endsWith(it) }
}