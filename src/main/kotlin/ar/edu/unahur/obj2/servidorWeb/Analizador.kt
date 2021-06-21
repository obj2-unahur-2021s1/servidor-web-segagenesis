package ar.edu.unahur.obj2.servidorWeb

abstract class Analizador() {

  val respuestas = mutableListOf<Respuesta>()
  val modulos = mutableListOf<Modulo>()

  fun cantidadPedidosDeIP(ip: String): Any = respuestas.count{respuesta: Respuesta -> respuesta.pedido.ip == ip }

}

class DetecccionDemoraEnRespuesta(val minTiempoDemora: Int): Analizador(){

  fun cantRespuestasDemoradas() = this.respuestas.count{esRespuestaDemorada(it)}

  fun esRespuestaDemorada(respuesta: Respuesta) = respuesta.tiempo > this.minTiempoDemora
}
