package es.uvigo.esei.dai.hybridserver.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import es.uvigo.esei.dai.hybridserver.HybridServerService;
import es.uvigo.esei.dai.hybridserver.configuration.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.controllers.exceptions.MissedParameterException;
import es.uvigo.esei.dai.hybridserver.dao.DaoHTML;
import es.uvigo.esei.dai.hybridserver.dao.UUIDgenerator;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class HTMLController implements GenericController {

    private DaoHTML dao;
    private List<ServerConfiguration> servers;

    /**
     * @param dao
     */
    public HTMLController(DaoHTML dao, List<ServerConfiguration> servers) {
        this.dao = dao;
        this.servers = servers;
    }

    /**
     * Resuelve Peticiones GET que contengan UUID en ResourceParameters
     * 
     * @param request debe contener un UUID en getResourceParameters()
     * @return HTTPResponse
     */
    @Override
    public HTTPResponse get(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();

        // Validamos si la uuid de la request es válida
      //  System.out.println("UUID de la request: " + request.getResourceParameters().get("uuid"));
        if (UUIDgenerator.validate(request.getResourceParameters().get("uuid"))) {

            // En caso de UUID bien formada, comprobamos la existencia de la uuid en la base
            // de datos
          //  System.out.println("UUID de la request: valida");
            String content = this.dao.get(request.getResourceParameters().get("uuid"));
          //  System.out.println("Contenido del uuid en la BD: " + content);
            if (content == null) {// Si no existe (NOT FOUND)
                try {
                    Boolean foundHTML = false;
                    int i = 0;
                    while (!foundHTML && i < servers.size()) {

                        if (!servers.get(i).getName().equals("Down Server")) {
                            URL url;
                            // NOTA: Si ServerConfiguration.getName() Devolviese una URL no Saltaría la
                            // excepcion y se controlaría la formacion de la url en la propia clase de
                            // ServerConfiguration
                            url = new URL(servers.get(i).getWsdl());// Throws
                            // MalformedURLException

                            QName name = new QName(servers.get(i).getNamespace(),
                                    servers.get(i).getService() + "ImplService");

                            Service service = Service.create(url, name);

                            HybridServerService ws = service.getPort(HybridServerService.class);
                            content = ws.getHTML(request.getResourceParameters().get("uuid"));
                            if (content != null) {
                                foundHTML = true;
                            }

                        }
                        i++;
                    }
                    if (foundHTML) {
                        response.setContent(content);
                        response.putParameter("Content-Type", "text/html");
                        response.setStatus(HTTPResponseStatus.S200);
                    } else {
                        // NOT FOUND
                        response.setStatus(HTTPResponseStatus.S404);
                    }
                } catch (MalformedURLException e) {
                  //  System.out.println("URL mal formada:  Saltando al siguiente servidor de la lista.");
                }

            } else {// Si existe la UUID en la base de datos el contenido de la respuesta será el
                    // content correspondiente en la base de datos
                response.setContent(content);
                response.putParameter("Content-Type", "text/html");
                response.setStatus(HTTPResponseStatus.S200);
            }
        } else {
            // En caso de UUID mal formada (BAD REQUEST)
          //  System.out.println("uuid de la request: invalida");
            response.setStatus(HTTPResponseStatus.S400);
        }
        return response;
    }

    /**
     * Resuelve Peticiones POST
     * 
     * @param request
     * @return
     */
    @Override
    public HTTPResponse post(HTTPRequest request) {
        String nuevaPaginaUuid;// guardamos la UUID creada para devolver el enlace al recurso generado en la
                               // respuesta
        HTTPResponse response = new HTTPResponse();
        Set<String> keys = request.getResourceParameters().keySet();
        try {
            // verificamos que el contenido contenga un elemento HTML (pq el mapa puede
            // contener nulos no puedo usar nullpointerexception)
            if (!keys.contains("html")) {
                throw new MissedParameterException("No html found");
            }
            // si contiene html creamos la entrada en la BD
            nuevaPaginaUuid = this.dao.addPage(request.getResourceParameters().get("html"));
            // generamos respuesta con el enlace
            response.setContent(
                    "<a href=\"html?uuid=" + nuevaPaginaUuid + "\">" + nuevaPaginaUuid + "</a>");
            response.setStatus(HTTPResponseStatus.S200);

        } catch (MissedParameterException e) {
            // BAD REQUEST pq la peticion de post html no contiene html
            response.setStatus(HTTPResponseStatus.S400);
        }
        return response;
    }

    /**
     * Resuelve Peticiones DELETE
     * 
     * @param request
     * @return
     */
    @Override
    public HTTPResponse delete(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        try {
            this.dao.deletePage(request.getResourceParameters().get("uuid"));// THROWS NullPointerException si no existe
                                                                             // uuid
            response.setStatus(HTTPResponseStatus.S200);
        } catch (RuntimeException e) {
            response.setStatus(HTTPResponseStatus.S500);
        }
        return response;
    }

    /**
     * Resuelve Peticiones GET que NO contengan UUID en ResourceParameters
     * 
     * @param request debe NO contener un UUID en getResourceParameters()
     * @return HTTPResponse
     */
    @Override
    public HTTPResponse list(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        String fullList = "<h1>LocalHost</h1>\n";
        fullList = fullList.concat(this.dao.listPages());

        for (ServerConfiguration server : this.servers) {

            if (!server.getName().equals("Down Server")) {

                URL url;
                // NOTA: Si ServerConfiguration.getName() Devolviese una URL no Saltaría la
                // excepcion y se controlaría la formacion de la url en la propia clase de
                // ServerConfiguration
                try {
                    url = new URL(server.getWsdl());

                    QName name = new QName(server.getNamespace(),
                            server.getService() + "ImplService");

                    Service service = Service.create(url, name);

                    HybridServerService ws = service.getPort(HybridServerService.class);
                    fullList = fullList.concat("<h1>" + server.getName() + "</h1>\n");
                    fullList = fullList.concat(ws.listPagesHTML());
                } catch (MalformedURLException e) {
                    throw new RuntimeException("URL MALFORMED, server skipped");
                }
            }
        }

        response.setContent(fullList);
      //  System.out.println("ResponseListBody: " + response.getContent());
        response.setStatus(HTTPResponseStatus.S200);
        return response;
    }

}
