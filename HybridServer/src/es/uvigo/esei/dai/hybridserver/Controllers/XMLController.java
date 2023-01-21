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

public class XMLController implements GenericController {

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

    @Override
    public HTTPResponse get(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();

        try {

            String uuid = request.getResourceParameters().get("uuid");
            System.out.println("uuid de la request: " + uuid);

            if (!UUIDgenerator.validate(uuid)) {

                throw new InvalidParameterException("Invalid UUID");

            }

            System.out.println("uuid de la request: valida");
            String xml = this.daoXML.get(uuid);// return null if no exist
            System.out.println("Contenido del uuid en la BD: " + xml);

            // Si el content xml de la bd es nulo
            if (xml == null) {
                // NOT FOUND
                response.setStatus(HTTPResponseStatus.S404);

            } else {

                // Si no existe xslt en la request respondemos el xml
                if (!request.getResourceParameters().containsKey("xslt")) {
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
                        if (!XMLUtility.validateSchema(xml, xsd)) {
                            // Bad Request XSLT inválido (XSD del XSLT no valida XML solicitado)
                            response.setStatus(HTTPResponseStatus.S400);
                        } else {
                            String html = XMLUtility.xmlToHtml(xml, xslt);
                            if (html != null) {
                                response.setContent(html);
                                response.putParameter("Content-Type", "text/html");
                                response.setStatus(HTTPResponseStatus.S200);
                            } else {

                                response.setStatus(HTTPResponseStatus.S400);
                            }
                        }
                    }
                }
            }

        } catch (InvalidParameterException e) {
            System.out.println(e.getMessage());

            // BAD REQUEST
            response.setStatus(HTTPResponseStatus.S400);
        }
        return response;
    }

    /**
     * Resuelve petiones post
     * 
     * @param request
     * @return
     */
    @Override
    public HTTPResponse post(HTTPRequest request) {
        String nuevaPaginaUuid;
        HTTPResponse response = new HTTPResponse();
        Set<String> keys = request.getResourceParameters().keySet();
        try {
            // verificamos que el contenido contenga un elemento XML (pq el mapa puede
            // contener nulos no puedo usar nullpointerexception)
            if (!keys.contains("xml")) {
                throw new MissedParameterException("No xml found");
            }
            // si contiene xml creamos la entrada en la BD
            nuevaPaginaUuid = this.daoXML.addPage(request.getResourceParameters().get("xml"));// NullPointerException
            response.setContent(
                    "<a href=\"xml?uuid=" + nuevaPaginaUuid + "\">" + nuevaPaginaUuid + "</a>");
            response.setStatus(HTTPResponseStatus.S200);
        } catch (MissedParameterException e) {
            // BAD REQUEST pq la peticion de post xml no contiene xml
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
            this.daoXML.deletePage(request.getResourceParameters().get("uuid"));
            response.setStatus(HTTPResponseStatus.S200);
        } catch (RuntimeException e) {
            response.setStatus(HTTPResponseStatus.S500);
        }
        return response;
    }
    
    @Override
    public HTTPResponse list(HTTPRequest request) {

        HTTPResponse response = new HTTPResponse();

        response.setContent(this.daoXML.listPages());
        response.setStatus(HTTPResponseStatus.S200);

        return response;
    }

}
