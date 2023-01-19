package es.uvigo.esei.dai.hybridserver.Controllers;

import java.util.Set;

import es.uvigo.esei.dai.hybridserver.UUIDgenerator;
import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoXSD;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class XSDController {
    private DaoXSD dao;

    /**
     * @param dao
     */
    public XSDController(DaoXSD dao) {
        this.dao = dao;
    }

    public HTTPResponse get(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        try {
            System.out.println("try");
            if (request.getResourceParameters().containsKey("uuid")) {
                System.out.println("uuid de la request: " + request.getResourceParameters().get("uuid"));
                if (UUIDgenerator.validate(request.getResourceParameters().get("uuid"))) {
                    System.out.println("uuid de la request: valida");

                    String content = this.dao.get(request.getResourceParameters().get("uuid"));
                    System.out.println("Contenido del uuid en la BD: " + content);
                    if (content == null) {
                        response.setStatus(HTTPResponseStatus.S404);
                    } else {
                        response.setContent(content);
                        response.putParameter("Content-Type", "application/xml");
                        response.setStatus(HTTPResponseStatus.S200);
                    }
                } else {
                    System.out.println("uuid de la request: invalida");
                    response.setStatus(HTTPResponseStatus.S400);
                }
            } else {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            System.out.println("catch");

            response.setContent(this.dao.listPages());
            response.setStatus(HTTPResponseStatus.S200);
        }
        return response;
    }

    public HTTPResponse post(HTTPRequest request) {
        String nuevaPaginaUuid;
        HTTPResponse response = new HTTPResponse();
        Set<String> keys = request.getResourceParameters().keySet();
        try {
            if (!keys.contains("xsd")) {
                throw new MissedParameterException("No xsd found");
            }
            nuevaPaginaUuid = this.dao.addPage(request.getResourceParameters().get("xsd"));// NullPointerException
            response.setContent(
                    "<a href=\"xsd?uuid=" + nuevaPaginaUuid + "\">" + nuevaPaginaUuid + "</a>");
            response.setStatus(HTTPResponseStatus.S200);
        } catch (MissedParameterException e) {
            response.setStatus(HTTPResponseStatus.S400);
        }
        return response;
    }

    public HTTPResponse delete(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        try {
            this.dao.deletePage(request.getResourceParameters().get("uuid"));
            response.setStatus(HTTPResponseStatus.S200);
        } catch (NullPointerException e) {
            // En el caso de que la página que se busca no exista

            // Preparamos como contenido la lista de páginas disponibles
            response.setContent(this.dao.listPages());
            response.setStatus(HTTPResponseStatus.S404);
        }
        return response;
    }

}
