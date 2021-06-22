package ar.edu.unahur.obj2.servidorWeb

import java.time.LocalDateTime

abstract class Analizador {

  val respuestas = mutableListOf<Respuesta>()
  val modulos = mutableListOf<Modulo>()

  fun pedidos() = this.respuestas.map { it.pedido }
  fun cantidadPedidos() = this.respuestas.size

}

class DetecccionDemoraEnRespuesta(val minTiempoDemora: Int): Analizador(){

  fun cantRespuestasDemoradas() = this.respuestas.count{esRespuestaDemorada(it)}

  fun esRespuestaDemorada(respuesta: Respuesta) = respuesta.tiempo > this.minTiempoDemora
}

open class IpsSospechosas: Analizador(){
  // En Ip Sospechosa modulo mas consultado
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

  fun cantidadQueRequirieronRuta(url: String): Set<String> {
    val aux = listaDeRespuestasDeIPSospechosas.filter { respuesta: Respuesta -> respuesta.pedido.url == url }
    return aux.map{respuesta -> respuesta.pedido.ip}.toSet()
  }

  fun moduloMasConsultado(){

  }
}



class Estadistica: Analizador() {

  fun tiempoRespuestaPromedio() = respuestas.sumBy { it.tiempo } / respuestas.size

  fun pedidosEntre(fecha1: LocalDateTime, fecha2: LocalDateTime) = this.pedidos().filter { it.fechaHora.isBefore(fecha2) and it.fechaHora.isAfter(fecha1) }.size

  fun cantidadRespuestasQueIncluyen(body :String) = respuestas.count{ it.body.contains(body) }

  fun porcentajePedidosExitosos() = (this.pedidosExitosos() * 100) / this.cantidadPedidos()

  fun pedidosExitosos() = this.respuestas.filter { it.esExitoso() }.map { it.pedido }.size
}


