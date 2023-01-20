package es.uvigo.esei.dai.hybridserver.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.controllers.HTMLController;
import es.uvigo.esei.dai.hybridserver.controllers.XMLController;
import es.uvigo.esei.dai.hybridserver.controllers.XSDController;
import es.uvigo.esei.dai.hybridserver.controllers.XSLTController;
import es.uvigo.esei.dai.hybridserver.dao.DaoHTML;
import es.uvigo.esei.dai.hybridserver.dao.DaoXML;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSD;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSLT;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.exceptions.HTTPParseException;

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

            } catch (HTTPParseException e) {
                System.out.println("Throws Parse Exception");

                response.setStatus(HTTPResponseStatus.S400);
            } finally {
                System.out.println("RESPONSE TOSTRING:");
                System.out.println(response.toString());
                socket.getOutputStream().write(response.toString().getBytes(), 0,
                        response.toString().getBytes().length);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
