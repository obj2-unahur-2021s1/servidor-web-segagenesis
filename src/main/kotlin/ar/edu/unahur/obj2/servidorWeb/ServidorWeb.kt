package ar.edu.unahur.obj2.servidorWeb

import java.time.LocalDateTime

// Para no tener los códigos "tirados por ahí", usamos un enum que le da el nombre que corresponde a cada código
// La idea de las clases enumeradas es usar directamente sus objetos: CodigoHTTP.OK, CodigoHTTP.NOT_IMPLEMENTED, etc
enum class CodigoHttp(val codigo: Int) {
  OK(200),
  NOT_IMPLEMENTED(501),
  NOT_FOUND(404),
}

class Pedido(val ip: String, val url: String, val fechaHora: LocalDateTime)

class Respuesta(val codigo: CodigoHttp, val body: String, val tiempo: Int, val pedido: Pedido) {
  fun esExitoso() = this.codigo == CodigoHttp.OK
}


class ServidorWeb {
  var modulos = mutableListOf<Modulo>()
  var analizadores = mutableListOf<Analizador>()

  fun agregarModulo(modulo: Modulo) = this.modulos.add(modulo)

  fun agregarAnalizador(analizador: Analizador) = this.analizadores.add(analizador)

  fun realizarPedido(pedido: Pedido): Respuesta {
    if (!pedido.url.startsWith("http:")) {
      val respuesta = Respuesta(CodigoHttp.NOT_IMPLEMENTED,"",1,pedido)
      this.enviarRespuesta(respuesta)
      return respuesta
    }
    if (this.algunModuloSoporta(pedido.url)) {
      val modulo = this.modulos.find { it.puedeSoportar(pedido.url) }!!
      val respuesta = Respuesta(CodigoHttp.OK,modulo.body,modulo.tiempo,pedido)
      this.enviarRespuesta(respuesta)
      this.enviarModulo(modulo)
      return respuesta
    }
    else {
      val respuesta = Respuesta(CodigoHttp.NOT_FOUND,"",10,pedido)
      this.enviarRespuesta(respuesta)
      return respuesta
    }
  }

  fun algunModuloSoporta(url: String) = this.modulos.any{it.puedeSoportar(url)}

  fun enviarRespuesta(respuesta: Respuesta) = this.analizadores.all { it.respuestas.add(respuesta) }

  fun enviarModulo(modulo: Modulo) = this.analizadores.all { it.modulos.add(modulo) }
}

