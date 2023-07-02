# HybridServer

Autores
Óscar Lestón Casais - 78810582R

Comentarios
General [-1.2]
[-0.25] Los tests no compilan.
->Mismo problema

[-0.25] Los DAO no funcionan debido a que el nombre de la tabla es incorrecto (debería ser HTML y no html).
-> Se cambia el nombre de las tablas

[-0.1] El nombre de los autores no está en la página raíz.
->Se crea welcome.html sustituyendo la implementación con strings para mostrar la página de bienvenida.

[-0.1] Los enlaces en los listados deben dirigir al servidor en el que está alojado cada documento.*1
- Los enlaces en los listados deben aparecer en forma de lista.*1

[-0.25] La página raíz y los listados no son una página HTML.
->Se añaden las etiquetas HTML correspondientes en los métodos list de cada controlador.

[-0.25] Existen flujos no cerrados en XMLUtility.
->En xmlToHtml() cerramos writer con writer.close()
->En  loadAndValidateWithInternalXSD() y loadAndValidateWithExternalXSD() añadimos un try con recursos:
		try (FileInputStream inputStream = new FileInputStream(new File(documentPath))) {
            return builder.parse(inputStream);
        }



[1.75/2] B1. XML
[-0.25] El listado de XML no tiene enlaces, solo los ids de los XMLs.*1

[1.25/2] B2. XSD & XSLT
[-0.5] La transformación configuration.xsl no funciona.
->Sin arreglar.
[-0.25] El listado de XML no tiene enlaces, solo los ids de los XMLs.*1

[4.75/5] B3. P2P
[-0.25] Los servicios web no usan el mismo thread pool que HTTP, por lo que no hay control en el número máximo de peticiones activas.

[1/1] Configuración
OK

*1 -> Se cambia la salida que se muestra por cada página a esta estructura:
 "<li><a href=http://localhost:" + port + "/xml?uuid=" + uuid + ">" + uuid + "</a>" + "</li>"
 La variable port nos permite dirigir al servidor donde se aloja el documento y ahora mostramos un enlace en una lista y no solo el id