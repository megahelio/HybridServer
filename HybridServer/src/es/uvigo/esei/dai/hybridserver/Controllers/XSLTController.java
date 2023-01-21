package es.uvigo.esei.dai.hybridserver.controllers;

import java.util.Set;

import es.uvigo.esei.dai.hybridserver.controllers.exceptions.InvalidParameterException;
import es.uvigo.esei.dai.hybridserver.controllers.exceptions.MissedParameterException;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSD;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSLT;
import es.uvigo.esei.dai.hybridserver.dao.UUIDgenerator;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class XSLTController implements GenericController {
    private DaoXSLT daoXSLT;
    private DaoXSD daoXSD;

    /**
     * @param daoXSLT
     */
    public XSLTController(DaoXSLT daoXSLT, DaoXSD daoXSD) {
        this.daoXSLT = daoXSLT;
        this.daoXSD = daoXSD;
    }

    @Override
    public HTTPResponse get(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();

        System.out.println("uuid de la request: " + request.getResourceParameters().get("uuid"));
        if (UUIDgenerator.validate(request.getResourceParameters().get("uuid"))) {
            System.out.println("uuid de la request: valida");

            String content = this.daoXSLT.getContent(request.getResourceParameters().get("uuid"));
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
            if (!keys.contains("xslt")) {
                throw new MissedParameterException("No xslt found");
            }

            String xsd = request.getResourceParameters().get("xsd");// NullPointerException
            String content = request.getResourceParameters().get("xslt");// NullPointerException

            if (!this.daoXSD.exist(xsd)) {
                throw new InvalidParameterException("XSD no exist");
            }
            nuevaPaginaUuid = this.daoXSLT.addPage(content, xsd);
            response.setContent(
                    "<a href=\"xslt?uuid=" + nuevaPaginaUuid + "\">" + nuevaPaginaUuid + "</a>");
            response.setStatus(HTTPResponseStatus.S200);

        } catch (MissedParameterException e) {

            // BadRequest NO XSD or XSLT
            response.setStatus(HTTPResponseStatus.S400);
        } catch (InvalidParameterException e) {
            // NOT FOUND XSD no exite
            response.setStatus(HTTPResponseStatus.S404);
        }
        return response;
    }

    @Override
    public HTTPResponse delete(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        try {
            this.daoXSLT.deletePage(request.getResourceParameters().get("uuid"));
            response.setStatus(HTTPResponseStatus.S200);
        } catch (RuntimeException e) {
            response.setStatus(HTTPResponseStatus.S500);
        }
        return response;
    }

    @Override
    public HTTPResponse list(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        response.setContent(this.daoXSLT.listPages());
        response.setStatus(HTTPResponseStatus.S200);
        return response;
    }

}
