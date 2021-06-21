package ar.edu.unahur.obj2.servidorWeb

abstract class Analizador() {

  val respuestas = mutableListOf<Respuesta>()
  val modulos = mutableListOf<Modulo>()


}

class DetecccionDemoraEnRespuesta(val minTiempoDemora: Int): Analizador(){

  fun cantRespuestasDemoradas() = this.respuestas.count{esRespuestaDemorada(it)}

  fun esRespuestaDemorada(respuesta: Respuesta) = respuesta.tiempo > this.minTiempoDemora
}

class PedidosDeIP(val ip: String): Analizador(){
  fun cantidadpedidosDeIP() = respuestas.count{respuesta: Respuesta -> respuesta.pedido.ip == ip }

}