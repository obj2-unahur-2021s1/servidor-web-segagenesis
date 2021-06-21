package ar.edu.unahur.obj2.servidorWeb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ServidorWebTest : DescribeSpec({
  describe("Un servidor web") {
    val servidorWeb = ServidorWeb()

    describe("Realiza un pedido") {
      it("Cuando el protocolo no es el correcto devuelve 501(NOT_IMPLEMENTED)") {
        val pedido = Pedido("192.168.1.13","https://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        val respuesta = servidorWeb.realizarPedido(pedido)
        respuesta.codigo.shouldBe(CodigoHttp.NOT_IMPLEMENTED)
      }
      it("Cuando algún modulo si puede trabajar con el pedido devuelve 200(OK)") {
        val pedido = Pedido("192.168.1.13","http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        val modulo = Modulo(listOf("html"),"",10)
        servidorWeb.agregarModulo(modulo)
        val respuesta = servidorWeb.realizarPedido(pedido)
        respuesta.codigo.shouldBe(CodigoHttp.OK)
      }
      it("Cuando algun modulo no puede trabajar con el pedido devuelve 404(NOT_FOUND)") {
        val pedido = Pedido("192.168.1.13","http://pepito.com.ar/documentos/documentos/docl.html", LocalDateTime.now())
        val respuesta = servidorWeb.realizarPedido(pedido)
        respuesta.codigo.shouldBe(CodigoHttp.NOT_FOUND)
      }

    }
    describe("Envia un pedido a un analizador") {
      it("A Detección de demora en respuesta que se le pregunta la cantidad de respuestas demoradas.") {
        val analizadorDeteccionDemoraRespuesta = DetecccionDemoraEnRespuesta(5)
        servidorWeb.agregarAnalizador(analizadorDeteccionDemoraRespuesta)
        val pedidoConDemora = Pedido("192.168.1.13","http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        val pedidoSinDemora = Pedido("192.168.1.13","https://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        servidorWeb.realizarPedido(pedidoConDemora)
        servidorWeb.realizarPedido(pedidoSinDemora)
        analizadorDeteccionDemoraRespuesta.cantRespuestasDemoradas().shouldBe(1)

      }
    }

    describe("IP sospechosa"){
      it("Cantidad de pedidos"){
        val analizadorDeteccionDemoraRespuesta = DetecccionDemoraEnRespuesta(5)
        servidorWeb.agregarAnalizador(analizadorDeteccionDemoraRespuesta)
        val pedido = Pedido("192.168.1.13","http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        val pedido2 = Pedido("192.168.1.13","http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        val pedido3 = Pedido("192.168.1.13","http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
        servidorWeb.realizarPedido(pedido)
        servidorWeb.realizarPedido(pedido2)
        servidorWeb.realizarPedido(pedido3)

        analizadorDeteccionDemoraRespuesta.cantidadPedidosDeIP("192.168.1.13").shouldBe(3)
      }
    }



  }
})
