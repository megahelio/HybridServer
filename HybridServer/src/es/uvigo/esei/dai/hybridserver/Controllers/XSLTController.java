package es.uvigo.esei.dai.hybridserver.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import es.uvigo.esei.dai.hybridserver.HybridServerService;
import es.uvigo.esei.dai.hybridserver.configuration.ServerConfiguration;
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
    private List<ServerConfiguration> servers;

    /**
     * @param daoXSLT
     */
    public XSLTController(DaoXSLT daoXSLT, DaoXSD daoXSD, List<ServerConfiguration> servers) {
        this.daoXSLT = daoXSLT;
        this.daoXSD = daoXSD;
        this.servers = servers;
    }

    @Override
    public HTTPResponse get(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();

        // System.out.println("uuid de la request: " +
        // request.getResourceParameters().get("uuid"));
        if (UUIDgenerator.validate(request.getResourceParameters().get("uuid"))) {
            // System.out.println("uuid de la request: valida");

            String content = this.daoXSLT.getContent(request.getResourceParameters().get("uuid"));
            // System.out.println("Contenido del uuid en la BD: " + content);
            if (content == null) {

                try {
                    Boolean foundXSLT = false;
                    int i = 0;
                    while (!foundXSLT && i < servers.size()) {
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
                            content = ws.getXSLT(request.getResourceParameters().get("uuid"));
                            if (content != null) {
                                foundXSLT = true;
                            }
                        }
                        i++;
                    }
                    if (foundXSLT) {
                        response.setContent(content);
                        response.putParameter("Content-Type", "application/xml");
                        response.setStatus(HTTPResponseStatus.S200);
                    } else {
                        // NOT FOUND
                        response.setStatus(HTTPResponseStatus.S404);
                    }
                } catch (MalformedURLException e) {
                    // System.out.println("URL mal formada: Saltando al siguiente servidor de la
                    // lista.");
                }
            } else {
                response.setContent(content);
                response.putParameter("Content-Type", "application/xml");
                response.setStatus(HTTPResponseStatus.S200);
            }
        } else {
            // System.out.println("uuid de la request: invalida");
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
        String fullList = "<h1>LocalHost</h1>\n";
        fullList = fullList.concat(this.daoXSLT.listPages());

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
                    fullList = fullList.concat(ws.listPagesXSLT());
                } catch (MalformedURLException e) {
                    throw new RuntimeException("URL MALFORMED, server skipped");
                }
            }
        }

        response.setContent(fullList);
        // System.out.println("ResponseListBody: " + response.getContent());
        response.setStatus(HTTPResponseStatus.S200);
        return response;
    }

}
