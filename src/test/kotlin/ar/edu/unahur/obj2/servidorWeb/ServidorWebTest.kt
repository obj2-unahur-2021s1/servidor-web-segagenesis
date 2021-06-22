package ar.edu.unahur.obj2.servidorWeb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ServidorWebTest : DescribeSpec({
  describe("Un servidor web") {
    val servidorWeb = ServidorWeb()

    describe("Realiza un pedido") {
      it("Cuando el protocolo no es el correcto devuelve 501(NOT_IMPLEMENTED)") {
        val pedido = Pedido("192.168.1.13", "https://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        val respuesta = servidorWeb.realizarPedido(pedido)
        respuesta.codigo.shouldBe(CodigoHttp.NOT_IMPLEMENTED)
      }
      it("Cuando algún modulo si puede trabajar con el pedido devuelve 200(OK)") {
        val pedido = Pedido("192.168.1.13", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        val modulo = Modulo(listOf("html"), "", 10)
        servidorWeb.agregarModulo(modulo)
        val respuesta = servidorWeb.realizarPedido(pedido)
        respuesta.codigo.shouldBe(CodigoHttp.OK)
      }
      it("Cuando algun modulo no puede trabajar con el pedido devuelve 404(NOT_FOUND)") {
        val pedido = Pedido("192.168.1.13", "http://pepito.com.ar/documentos/documentos/docl.html", LocalDateTime.now())
        val respuesta = servidorWeb.realizarPedido(pedido)
        respuesta.codigo.shouldBe(CodigoHttp.NOT_FOUND)
      }

    }
    describe("Envia un pedido a un analizador") {
      it("A Detección de demora en respuesta que se le pregunta la cantidad de respuestas demoradas.") {
        val analizadorDeteccionDemoraRespuesta = DetecccionDemoraEnRespuesta(5)
        val pedidoConDemora = Pedido("192.168.1.13", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        val pedidoSinDemora = Pedido("192.168.1.13", "https://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        servidorWeb.agregarAnalizador(analizadorDeteccionDemoraRespuesta)
        servidorWeb.realizarPedido(pedidoConDemora)
        servidorWeb.realizarPedido(pedidoSinDemora)
        analizadorDeteccionDemoraRespuesta.cantRespuestasDemoradas().shouldBe(1)

      }
      describe("A Estadisticas") {
        val analizadorEstadistica = Estadistica()
        val moduloImagen = Modulo(listOf("jpg", "png", "gif"), "Que linda foto", 10)
        val moduloVideo = Modulo(listOf("avi", "mp4"), "Que lindo video", 10)
        val pedido1 = Pedido("192.168.1.13", "http://pepito.com.ar/documentos/doc1.jpg",
          LocalDateTime.of(2021, 6, 21, 1, 0, 0))
        val pedido2 = Pedido("192.168.1.13", "http://pepito.com.ar/documentos/doc1.mp4",
          LocalDateTime.of(2021, 6, 21, 1, 0, 0))
        servidorWeb.agregarModulo(moduloImagen)
        servidorWeb.agregarModulo(moduloVideo)
        servidorWeb.agregarAnalizador(analizadorEstadistica)
        servidorWeb.realizarPedido(pedido1)
        servidorWeb.realizarPedido(pedido2)

        it("Tiempo de respuesta promedio") {
          analizadorEstadistica.tiempoRespuestaPromedio().shouldBe(10)
        }

        it("Cantidad de pedidos entre dos momentos (fecha/hora) que fueron atendidos,") {
          analizadorEstadistica.pedidosEntre(
            LocalDateTime.of(2021, 6, 19, 1, 0, 0),
            LocalDateTime.of(2021, 6, 22, 1, 0, 0)
          ).shouldBe(2)
        }

        it("cantidad de respuestas cuyo body incluye un determinado String") {
          analizadorEstadistica.cantidadRespuestasQueIncluyen("Que").shouldBe(2)
        }

        it("porcentaje de pedidos con respuesta exitosa.") {
          analizadorEstadistica.porcentajePedidosExitosos().shouldBe(100)
        }
      }

      describe(" A IP sospechosa") {
        val pedido = Pedido("192.168.1.13", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        val pedido2 = Pedido("192.168.1.14", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        val pedido3 = Pedido("192.168.1.13", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        val pedido4 = Pedido("192.168.1.15", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())

        it("Cantidad de pedidos") {
          val analizadorCantidadDePedidosIP = IpsSospechosas()

          servidorWeb.agregarAnalizador(analizadorCantidadDePedidosIP)
          servidorWeb.realizarPedido(pedido)
          servidorWeb.realizarPedido(pedido2)
          servidorWeb.realizarPedido(pedido3)

          servidorWeb.realizarPedido(pedido4)
          analizadorCantidadDePedidosIP.listaDeIpsSopechosas.add("192.168.1.13")
          analizadorCantidadDePedidosIP.listaDeIpsSopechosas.add("192.168.1.14")

          analizadorCantidadDePedidosIP.cantidadpedidosDeIP("192.168.1.13").shouldBe(2)

        }

        it("Modulo mas consultado por todas las ips sospechosas") {
          val analizadorModuloMasconsultado = IpsSospechosas()

          servidorWeb.agregarAnalizador(analizadorModuloMasconsultado)
          servidorWeb.realizarPedido(pedido)
          servidorWeb.realizarPedido(pedido2)
          servidorWeb.realizarPedido(pedido3)

          analizadorModuloMasconsultado.moduloMasConsultado().shouldBe("asd")

        }

        it("Conjunto de IPs sospechosas que requirieron una cierta ruta") {
          val analizadorCantidadDePedidosIP = IpsSospechosas()

          servidorWeb.agregarAnalizador(analizadorCantidadDePedidosIP)
          servidorWeb.realizarPedido(pedido)
          servidorWeb.realizarPedido(pedido2)
          servidorWeb.realizarPedido(pedido3)
          servidorWeb.realizarPedido(pedido4)

          analizadorCantidadDePedidosIP.listaDeIpsSopechosas.add("192.168.1.13")
          analizadorCantidadDePedidosIP.listaDeIpsSopechosas.add("192.168.1.14")

          analizadorCantidadDePedidosIP.establecerListaDeSospechosos()

          val ipsBuscadas = mutableSetOf<String>()

          ipsBuscadas.add("192.168.1.13")
          ipsBuscadas.add("192.168.1.14")


          analizadorCantidadDePedidosIP.cantidadQueRequirieronRuta("http://pepito.com.ar/documentos/doc1.html").shouldBe(ipsBuscadas)
        }
      }

    }
  }
})
