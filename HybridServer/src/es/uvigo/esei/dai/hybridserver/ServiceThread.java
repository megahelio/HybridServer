package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
    private static int count = 0;
    private Socket socketVar;
    private DaoInterface dao;
    private HTTPRequest request;
    private HTTPResponse response;

    public ServiceThread(Socket socketparam, DaoInterface daoparam) {
        System.out.println("Creando un ServiceThread " + (++count) + ": " + socketparam.toString());
        this.socketVar = socketparam;
        this.dao = daoparam;
        this.response = new HTTPResponse();
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
                System.out.println("Request: " + request.getMethod());

                if (request.getMethod() == HTTPRequestMethod.POST) {
                    System.out.println("Case POST");
                    // ******************************************** */
                    // CASO POST

                    // añadimos la pagina al dao
                    dao.addPage(request.getContent());
                    // Preparamos un 200 OK para la respuesta
                    response.setStatus(HTTPResponseStatus.S200);

                } else if (request.getMethod() == HTTPRequestMethod.GET) {
                    System.out.println("Case GET");
                    // ******************************************** */
                    // CASO GET

                    // Buscamos mediante el dao la pagina que solicita el GET
                    try {
                        System.out.println("try");
                        if (request.getHeaderParameters().containsKey("uuid")) {
                            response.setContent(dao.get(request.getHeaderParameters().get("uuid")));
                            response.setStatus(HTTPResponseStatus.S200);
                        } else {
                            throw new NullPointerException();
                        }
                    } catch (NullPointerException e) {
                        // En el caso de que la página que se busca no exista

                        // Preparamos como contenido la lista de páginas disponibles
                        System.out.println("catch");
                        response.setContent(dao.listPages());
                        response.setStatus(HTTPResponseStatus.S404);
                    }

                } else if (request.getMethod() == HTTPRequestMethod.DELETE) {
                    System.out.println("Case DELETE");
                    // ******************************************** */
                    // CASO DELETE
                    // Buscamos mediante el dao la pagina que solicita el GET para borrarla
                    try {
                        dao.deletePage(request.getHeaderParameters().get("uuid"));
                        response.setStatus(HTTPResponseStatus.S200);
                    } catch (NullPointerException e) {
                        // En el caso de que la página que se busca no exista

                        // Preparamos como contenido la lista de páginas disponibles
                        response.setContent(dao.listPages());
                        response.setStatus(HTTPResponseStatus.S404);
                    }
                } else {
                    System.out.println("Case DEFAULT");
                    // CASO UNIMPLEMENTED METHOD
                    response.setStatus(HTTPResponseStatus.S501);
                }

            } catch (HTTPParseException e) {
                System.out.println("Throws Parse Exception");

                response.setStatus(HTTPResponseStatus.S400);
            } finally {
                socket.getOutputStream().write(response.toString().getBytes(), 0,
                        response.toString().getBytes().length);
            }

        } catch (

        IOException e) {
            e.printStackTrace();
        }
    }
}
