package es.uvigo.esei.dai.hybridserver.controllers;

import java.util.Set;

import es.uvigo.esei.dai.hybridserver.controllers.exceptions.InvalidParameterException;
import es.uvigo.esei.dai.hybridserver.controllers.exceptions.MissedParameterException;
import es.uvigo.esei.dai.hybridserver.dao.DaoXML;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSD;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSLT;
import es.uvigo.esei.dai.hybridserver.dao.UUIDgenerator;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.xml.XMLUtility;

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
            String xml = this.daoXML.get(uuid);// return null if no exist
            System.out.println("Contenido del uuid en la BD: " + xml);

            if (xml == null) {
                // NOT FOUND
                response.setStatus(HTTPResponseStatus.S404);
            } else {
                // Si no existe xslt en la request respondemos el xml
                if (!keys.contains("xslt")) {
                    response.setContent(xml);
                    response.putParameter("Content-Type", "application/xml");
                    response.setStatus(HTTPResponseStatus.S200);
                } else {
                    // Si la request contiene un xslt que no existe en la base de datos o el xsd que
                    // está asociado a este no existe
                    String xslt = this.daoXSLT.getContent(request.getResourceParameters().get("xslt"));
                    String xsd = this.daoXSD.get(this.daoXSLT.getXSD(request.getResourceParameters().get("xslt")));
                    // NOTA: La aplicación no permite crear xslt vinculados a un xsd inexistente
                    if (xslt == null || xsd == null) {

                        // NOT FOUND
                        response.setStatus(HTTPResponseStatus.S404);
                    } else {
                        try {
                            XMLUtility.validateSchema(xml, xsd);
                            response.setContent(XMLUtility.xmlToHtml(xml, xslt));
                            response.putParameter("Content-Type", "text/html");
                            response.setStatus(HTTPResponseStatus.S200);

                        } catch (Exception e) {
                            // Bad Request XSLT inválido (XSD del XSLT no valida XML solicitado)
                            response.setStatus(HTTPResponseStatus.S400);

                        }
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
