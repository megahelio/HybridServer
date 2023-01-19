package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.Controllers.HTMLController;
import es.uvigo.esei.dai.hybridserver.Controllers.XMLController;
import es.uvigo.esei.dai.hybridserver.Controllers.XSDController;
import es.uvigo.esei.dai.hybridserver.Controllers.XSLTController;
import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoHTML;
import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoXML;
import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoXSD;
import es.uvigo.esei.dai.hybridserver.DaoImplementations.DaoXSLT;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
    private static int count = 0;
    private Socket socketVar;
    private HTTPRequest request;
    private HTTPResponse response;

    private HTMLController htmlController;
    private XMLController xmlController;
    private XSDController xsdController;
    private XSLTController xsltController;

    public ServiceThread(Socket socketparam, DaoHTML daoHTML, DaoXML daoXML, DaoXSD daoXSD, DaoXSLT daoXSLT) {
        System.out.println("Creando un ServiceThread " + (++count) + ": " + socketparam.toString());
        this.socketVar = socketparam;
        this.response = new HTTPResponse();
        this.htmlController = new HTMLController(daoHTML);
        this.xmlController = new XMLController(daoXML, daoXSLT, daoXSD);
        this.xsdController = new XSDController(daoXSD);
        this.xsltController = new XSLTController(daoXSLT, daoXSD);
    }

    @Override
    public void run() {
        BufferedReader inputReader;
        System.out.println("ServiceThread Run " + count + " : " + this.socketVar.toString());
        try (Socket socket = this.socketVar) {

            System.out.println("ServiceThread Run" + count + " : " +
                    socket.toString());
            // System.out.println(socket.getInputStream().toString());
            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // request inicializado presuntamente
            System.out.println("Input Reader");

            try {
                request = new HTTPRequest(inputReader);// THROWS HTTPParseException
                System.out.println("Request ToString: " + request.toString());
                System.out.println("Parameters :" + request.getResourceParameters());
                // System.out.println("ResourceName: " + request.getResourceName());
                // System.out.println("Method: " + request.getMethod());

                switch (request.getResourceName()) {

                    case "html":
                        switch (request.getMethod()) {
                            case DELETE:
                                response = this.htmlController.delete(request);
                                break;
                            case GET:
                                response = this.htmlController.get(request);
                                break;
                            case POST:
                                response = this.htmlController.post(request);
                                break;
                            default:
                                System.out.println("Case DEFAULT");
                                // CASO UNIMPLEMENTED METHOD
                                response.setStatus(HTTPResponseStatus.S501);
                                break;
                        }
                        break;
                    case "xml":
                        switch (request.getMethod()) {
                            case DELETE:
                                response = this.xmlController.delete(request);
                                break;
                            case GET:
                                response = this.xmlController.get(request);
                                break;
                            case POST:
                                response = this.xmlController.post(request);
                                break;
                            default:
                                System.out.println("Case DEFAULT");
                                // CASO UNIMPLEMENTED METHOD
                                response.setStatus(HTTPResponseStatus.S501);
                                break;
                        }
                        break;
                    case "xsd":
                        switch (request.getMethod()) {
                            case DELETE:
                                response = this.xsdController.delete(request);
                                break;
                            case GET:
                                response = this.xsdController.get(request);
                                break;
                            case POST:
                                response = this.xsdController.post(request);
                                break;
                            default:
                                System.out.println("Case DEFAULT");
                                // CASO UNIMPLEMENTED METHOD
                                response.setStatus(HTTPResponseStatus.S501);
                                break;
                        }
                        break;
                    case "xslt":
                        switch (request.getMethod()) {
                            case DELETE:
                                response = this.xsltController.delete(request);
                                break;
                            case GET:
                                response = this.xsltController.get(request);
                                break;
                            case POST:
                                response = this.xsltController.post(request);
                                break;
                            default:
                                System.out.println("Case DEFAULT");
                                // CASO UNIMPLEMENTED METHOD
                                response.setStatus(HTTPResponseStatus.S501);
                                break;
                        }
                        break;
                    case "":
                        if (request.getMethod() == HTTPRequestMethod.GET
                                && !request.getHeaderParameters().containsKey("uuid")) {
                            System.out.println("Welcome Detected");
                            response.setContent("Hybrid Server");
                            response.setStatus(HTTPResponseStatus.S200);
                            break;
                        }

                    default:
                        System.out.println("Case DEFAULT");
                        // CASO UNIMPLEMENTED METHOD
                        response.setStatus(HTTPResponseStatus.S400);
                        break;
                }

                // if (request.getMethod() == HTTPRequestMethod.POST) {
                // System.out.println("Case POST");
                // // ******************************************** */
                // // CASO POST
                // // añadimos la pagina al dao
                // System.out.println("request Content:" + request.getContent());
                // String requestContent = request.getContent();
                // String nuevaPaginaUuid;
                // if (requestContent.contains("html=") && requestContent.substring(0,
                // 5).equals("html=")) {
                // requestContent = requestContent.substring(5);
                // nuevaPaginaUuid = dao.addPage(requestContent);
                // response.setContent(
                // "<a href=\"html?uuid=" + nuevaPaginaUuid + "\">" + nuevaPaginaUuid + "</a>");
                // response.setStatus(HTTPResponseStatus.S200);
                // } else {
                // response.setStatus(HTTPResponseStatus.S400);
                // }

                // } else if (request.getMethod() == HTTPRequestMethod.GET) {
                // System.out.println("Case GET");
                // // CASO GET

                // // Buscamos mediante el dao la pagina que solicita el GET

                // try {
                // System.out.println("try");
                // if (request.getResourceParameters().containsKey("uuid")) {
                // System.out.println("uuid de la request: " +
                // request.getResourceParameters().get("uuid"));
                // if (UUIDgenerator.validate(request.getResourceParameters().get("uuid"))) {
                // System.out.println("uuid de la request: valida");

                // String content = dao.get(request.getResourceParameters().get("uuid"));
                // System.out.println("Contenido del uuid en la BD: " + content);
                // if (content == null) {
                // response.setStatus(HTTPResponseStatus.S404);
                // } else {
                // response.setContent(content);
                // response.putParameter("Content-Type", "text/html");
                // response.setStatus(HTTPResponseStatus.S200);
                // }
                // } else {
                // System.out.println("uuid de la request: invalida");
                // response.setStatus(HTTPResponseStatus.S400);
                // }
                // } else {
                // throw new NullPointerException();
                // }
                // } catch (NullPointerException e) {

                // System.out.println("catch");

                // if (request.getResourceChain().equals("/")
                // && !request.getHeaderParameters().containsKey("uuid")) {
                // System.out.println("Welcome Detected");
                // response.setContent("Hybrid Server");
                // response.setStatus(HTTPResponseStatus.S200);
                // } else {
                // response.setContent(dao.listPages());
                // response.setStatus(HTTPResponseStatus.S200);
                // }

                // }

                // } else if (request.getMethod() == HTTPRequestMethod.DELETE) {
                // System.out.println("Case DELETE");
                // // ******************************************** */
                // // CASO DELETE
                // // Buscamos mediante el dao la pagina que solicita el GET para borrarla

                // try {
                // dao.deletePage(request.getResourceParameters().get("uuid"));
                // response.setStatus(HTTPResponseStatus.S200);
                // } catch (NullPointerException e) {
                // // En el caso de que la página que se busca no exista

                // // Preparamos como contenido la lista de páginas disponibles
                // response.setContent(dao.listPages());
                // response.setStatus(HTTPResponseStatus.S404);
                // }
                // } else {
                // System.out.println("Case DEFAULT");
                // // CASO UNIMPLEMENTED METHOD
                // response.setStatus(HTTPResponseStatus.S501);
                // }

            } catch (HTTPParseException e) {
                System.out.println("Throws Parse Exception");

                response.setStatus(HTTPResponseStatus.S400);
            } finally {
                System.out.println("RESPONSE TOSTRING:");
                System.out.println(response.toString());
                socket.getOutputStream().write(response.toString().getBytes(), 0,
                        response.toString().getBytes().length);
            }

        } catch (

        IOException e) {
            e.printStackTrace();
        }
    }
}
