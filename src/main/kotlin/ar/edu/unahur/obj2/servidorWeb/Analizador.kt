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
  var listaDeIpsSopechosas = mutableListOf<String>()
  var listaDeRespuestasDeIPSospechosas = mutableSetOf<Respuesta>()

  fun establecerListaDeSospechosos(): MutableSet<Respuesta> {
    while (listaDeIpsSopechosas.isNotEmpty()) {
      if (listaDeIpsSopechosas.size > 0) {
        listaDeRespuestasDeIPSospechosas.addAll(buscarPedidos(listaDeIpsSopechosas[0]))
        listaDeIpsSopechosas.removeAt(0)
      }
    }
    return listaDeRespuestasDeIPSospechosas
  }

  fun buscarPedidos(ip: String): MutableList<Respuesta> = respuestas.filter { respuesta: Respuesta -> respuesta.pedido.ip == ip }.toMutableList()

  fun cantidadpedidosDeIP(ip: String) = buscarPedidos(ip).size

  fun cantidadQueRequirieronRuta(url : String): Int {
    val listaux = listaDeRespuestasDeIPSospechosas.filter{respuesta: Respuesta -> respuesta.pedido.url == url}
    return listaux.map{respuesta -> respuesta.pedido}.size
  }

}


class ModuloMasConsultado(): IpsSospechosas(){
  fun elMasConsultado(){


  }
}



class Estadisticas: Analizador(){
  fun tiempoRespuestaPromedio(): Int = respuestas.map{respuesta -> respuesta.tiempo }.sum() / respuestas.map{respuesta -> respuesta.tiempo }.size

  fun cantidadRespuestasConBody(body : String) = respuestas.count{respuesta: Respuesta -> respuesta.body == body }



}

// En Ip Sospechosa modulo mas consultado
// En estadisticas falta cantidad de pedidos con respuesta exitosa, y entre dos momentos.
