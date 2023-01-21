package es.uvigo.esei.dai.hybridserver.controllers;

import java.util.Set;

import es.uvigo.esei.dai.hybridserver.controllers.exceptions.MissedParameterException;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSD;
import es.uvigo.esei.dai.hybridserver.dao.UUIDgenerator;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class XSDController implements GenericController {
    private DaoXSD dao;

    /**
     * @param dao
     */
    public XSDController(DaoXSD dao) {
        this.dao = dao;
    }

    @Override
    public HTTPResponse get(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();

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

        return response;
    }

    @Override
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

    @Override
    public HTTPResponse delete(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        try {
            this.dao.deletePage(request.getResourceParameters().get("uuid"));
            response.setStatus(HTTPResponseStatus.S200);
        } catch (RuntimeException e) {
            response.setStatus(HTTPResponseStatus.S500);
        }
        return response;
    }

    @Override
    public HTTPResponse list(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        response.setContent(this.dao.listPages());
        response.setStatus(HTTPResponseStatus.S200);
        return response;
    }

}
