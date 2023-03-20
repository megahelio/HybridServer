package es.uvigo.esei.dai.hybridserver.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import es.uvigo.esei.dai.hybridserver.HybridServerService;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.controllers.exceptions.MissedParameterException;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSD;
import es.uvigo.esei.dai.hybridserver.dao.UUIDgenerator;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class XSDController  {
    private DaoXSD daoXSD;
    private List<ServerConfiguration> servers;
    private int port;

    /**
     * @param dao
     * @param i 
     */
    public XSDController(DaoXSD dao, List<ServerConfiguration> servers, int port) {
        this.daoXSD = dao;
        this.servers = servers;
        this.port = port;
    }

    
    public HTTPResponse get(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();

      //  System.out.println("uuid de la request: " + request.getResourceParameters().get("uuid"));
        if (UUIDgenerator.validate(request.getResourceParameters().get("uuid"))) {
          //  System.out.println("uuid de la request: valida");

            String content = this.daoXSD.get(request.getResourceParameters().get("uuid"));
          //  System.out.println("Contenido del uuid en la BD: " + content);
            if (content == null) {
                try {
                    Boolean foundXSD = false;
                    int i = 0;
                    while (!foundXSD && i < servers.size()) {
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
                            content = ws.getXSD(request.getResourceParameters().get("uuid"));
                            if (content != null) {
                                foundXSD = true;
                            }
                        }
                        i++;
                    }
                    if (foundXSD) {
                        response.setContent(content);
                        response.putParameter("Content-Type", "application/xml");
                        response.setStatus(HTTPResponseStatus.S200);
                    } else {
                        // NOT FOUND
                        response.setStatus(HTTPResponseStatus.S404);
                    }
                } catch (MalformedURLException e) {
                  //  System.out.println("URL mal formada:  Saltando al siguiente servidor de la lista.");
                }

            } else {
                response.setContent(content);
                response.putParameter("Content-Type", "application/xml");
                response.setStatus(HTTPResponseStatus.S200);
            }
        } else {
          //  System.out.println("uuid de la request: invalida");
            response.setStatus(HTTPResponseStatus.S400);
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
            nuevaPaginaUuid = this.daoXSD.addPage(request.getResourceParameters().get("xsd"));// NullPointerException
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
            this.daoXSD.deletePage(request.getResourceParameters().get("uuid"));
            response.setStatus(HTTPResponseStatus.S200);
        } catch (RuntimeException e) {
            response.setStatus(HTTPResponseStatus.S500);
        }
        return response;
    }

    
    public HTTPResponse list(HTTPRequest request) {
        HTTPResponse response = new HTTPResponse();
        String fullList = "<h1>XSD List</h1><br>";
        fullList=fullList.concat("<h1>LocalHost</h1><br>");
        fullList=fullList.concat("<ul>");
        for (String uuid : this.daoXSD.listPages()) {
        	fullList = fullList.concat("<li><a href=http://localhost:" + port + "/xsd?uuid=" + uuid + ">"
					+ uuid + "</a>" + "</li>");
		}
        fullList=fullList.concat("</ul>");


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
                    fullList = fullList.concat("<html><body><h1>" + server.getName() + "</h1>\n");
                    fullList=fullList.concat("<ul>");
                    for (String uuid : ws.listPagesXSD()) {
                    	fullList = fullList.concat("<li><a href="+server.getHttpAddress()+ "xsd?uuid=" + uuid + ">"
            					+ uuid + "</a>" + "</li>");
            		}
                    fullList=fullList.concat("</ul>");
                    fullList = fullList.concat("</body></html>");
                } catch (MalformedURLException e) {
                    throw new RuntimeException("URL MALFORMED, server skipped");
                }
            }
        }

        response.setContent(fullList);
        response.putParameter("Content-type", MIME.TEXT_HTML.getMime());
      //  System.out.println("ResponseListBody: " + response.getContent());
        response.setStatus(HTTPResponseStatus.S200);
        return response;
    }

}
