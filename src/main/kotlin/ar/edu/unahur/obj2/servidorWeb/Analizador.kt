package ar.edu.unahur.obj2.servidorWeb

import java.sql.Time
import java.sql.Timestamp
import java.util.concurrent.TimeUnit

abstract class Analizador() {

  val respuestas = mutableListOf<Respuesta>()
  val modulos = mutableListOf<Modulo>()


}

class DetecccionDemoraEnRespuesta(val minTiempoDemora: Int): Analizador(){

  fun cantRespuestasDemoradas() = this.respuestas.count{esRespuestaDemorada(it)}

  fun esRespuestaDemorada(respuesta: Respuesta) = respuesta.tiempo > this.minTiempoDemora
}


open class IpsSospechosas: Analizador(){
  val listaDeIpsSopechosas = mutableListOf<String>()
  val listaDePedidos = respuestas.map{respuesta -> respuesta.pedido}.toList()

  fun buscarPedido(ip: String): Set<Respuesta> = respuestas.filter { respuesta: Respuesta -> respuesta.pedido.ip == ip }.toSet()

}

class IpDeBusqueda(val ip: String): IpsSospechosas(){
  fun cantidadpedidosDeIP() = listaDePedidos.count{pedido: Pedido -> pedido.ip == ip }
}

class ModuloMasConsultado(): IpsSospechosas(){
  fun moduloMasConsultado() = listaDePedidos.map{pedido: Pedido -> pedido.url }
}

class ConjuntoDeIPQueBuscaron() : IpsSospechosas(){
  fun buscaronLaRuta(ruta : String) = listaDePedidos.filter{pedido: Pedido -> pedido.url == ruta }
}

class Estadisticas: Analizador(){
  fun tiempoRespuestaPromedio(): Int = respuestas.map{respuesta -> respuesta.tiempo }.sum() / respuestas.map{respuesta -> respuesta.tiempo }.size

  fun cantidadRespuestasConBody(body : String) = respuestas.count{respuesta: Respuesta -> respuesta.body == body }


}

// En Ip Sospechosa, registro de pedidos, modulo mas consultado
// En estadisticas falta cantidad de pedidos con respuesta exitosa, y entre dos momentos.
