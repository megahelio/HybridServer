package es.uvigo.esei.dai.hybridserver.Controllers;

import java.util.Set;

import es.uvigo.esei.dai.hybridserver.UUIDgenerator;
import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoXML;
import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoXSD;
import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoXSLT;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class XMLController {

    private DaoXML daoXML;
    private DaoXSLT daoXSLT;
    private DaoXSD daoXSD;

    /**
     * @param daoXML
     */
    public XMLController(DaoXML daoXML, DaoXSLT daoXSLT, DaoXSD daoXSD) {
        this.daoXML = daoXML;
        this.daoXSLT = daoXSLT;
        this.daoXSD = daoXSD;

    }

    public HTTPResponse get(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        try {
            System.out.println("try");
            Set<String> keys = request.getResourceParameters().keySet();

            if (!keys.contains("uuid")) {
                throw new MissedParameterException("Missed UUID");
            }

            String uuid = request.getResourceParameters().get("uuid");
            System.out.println("uuid de la request: " + uuid);

            if (!UUIDgenerator.validate(uuid)) {

                throw new InvalidParameterException("Invalid UUID");

            }

            System.out.println("uuid de la request: valida");
            String content = this.daoXML.get(uuid);// return null if no exist
            System.out.println("Contenido del uuid en la BD: " + content);

            if (content == null) {
                // NOT FOUND
                response.setStatus(HTTPResponseStatus.S404);
            } else {
                // Si no existe xslt en la request respondemos el xml
                if (!keys.contains("xslt")) {
                    response.setContent(content);
                    response.putParameter("Content-Type", "application/xml");
                    response.setStatus(HTTPResponseStatus.S200);
                } else {
                    // Si la request contiene un xslt que no existe en la base de datos o el xsd que
                    // está asociado a este no existe

                    // NOTA: La aplicación no permite crear xslt vinculados a un xsd inexistente
                    if ((!this.daoXSLT.exist(request.getResourceParameters().get("xslt")) ||
                            (!this.daoXSD.exist(this.daoXSLT.getXSD(request.getResourceParameters().get("xslt")))))) {

                        // NOT FOUND
                        response.setStatus(HTTPResponseStatus.S404);
                    } else {

                        response.putParameter("Content-Type", "text/html");
                        response.setStatus(HTTPResponseStatus.S200);
                    }
                }
            }

        } catch (MissedParameterException e) {// UUID MISSED
            System.out.println(e.getMessage());

            response.setContent(this.daoXML.listPages());
            response.setStatus(HTTPResponseStatus.S200);

        } catch (InvalidParameterException e) {
            System.out.println(e.getMessage());

            response.setStatus(HTTPResponseStatus.S400);
        }
        return response;
    }

    public HTTPResponse post(HTTPRequest request) {
        String nuevaPaginaUuid;
        HTTPResponse response = new HTTPResponse();
        Set<String> keys = request.getResourceParameters().keySet();
        try {
            if (!keys.contains("xml")) {
                throw new MissedParameterException("No xml found");
            }
            nuevaPaginaUuid = this.daoXML.addPage(request.getResourceParameters().get("xml"));// NullPointerException
            response.setContent(
                    "<a href=\"xml?uuid=" + nuevaPaginaUuid + "\">" + nuevaPaginaUuid + "</a>");
            response.setStatus(HTTPResponseStatus.S200);
        } catch (MissedParameterException e) {
            response.setStatus(HTTPResponseStatus.S400);
        }
        return response;
    }

    public HTTPResponse delete(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        try {
            this.daoXML.deletePage(request.getResourceParameters().get("uuid"));
            response.setStatus(HTTPResponseStatus.S200);
        } catch (NullPointerException e) {
            // En el caso de que la página que se busca no exista

            // Preparamos como contenido la lista de páginas disponibles
            response.setContent(this.daoXML.listPages());
            response.setStatus(HTTPResponseStatus.S404);
        }
        return response;
    }

}
