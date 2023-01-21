package es.uvigo.esei.dai.hybridserver.controllers;

import java.util.Set;

import es.uvigo.esei.dai.hybridserver.controllers.exceptions.MissedParameterException;
import es.uvigo.esei.dai.hybridserver.dao.DaoHTML;
import es.uvigo.esei.dai.hybridserver.dao.UUIDgenerator;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class HTMLController implements GenericController {

    private DaoHTML dao;

    /**
     * @param dao
     */
    public HTMLController(DaoHTML dao) {
        this.dao = dao;
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
        System.out.println("UUID de la request: " + request.getResourceParameters().get("uuid"));
        if (UUIDgenerator.validate(request.getResourceParameters().get("uuid"))) {

            // En caso de UUID bien formada, comprobamos la existencia de la uuid en la base
            // de datos
            System.out.println("UUID de la request: valida");
            String content = this.dao.get(request.getResourceParameters().get("uuid"));
            System.out.println("Contenido del uuid en la BD: " + content);
            if (content == null) {// Si no existe (NOT FOUND)
                response.setStatus(HTTPResponseStatus.S404);
            } else {// Si existe la UUID en la base de datos el contenido de la respuesta será el
                    // content correspondiente en la base de datos
                response.setContent(content);
                response.putParameter("Content-Type", "text/html");
                response.setStatus(HTTPResponseStatus.S200);
            }
        } else {
            // En caso de UUID mal formada (BAD REQUEST)
            System.out.println("uuid de la request: invalida");
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
        response.setContent(this.dao.listPages());
        System.out.println("ResponseListBody: " + response.getContent());
        response.setStatus(HTTPResponseStatus.S200);
        return response;
    }

}
