package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
    private Socket socketVar;
    private DaoInterface dao;
    private HTTPRequest request;
    private HTTPResponse response;

    public ServiceThread(Socket socketparam, DaoInterface daoparam) {
        System.out.println("Creando un ServiceThread: "+ socketparam.toString());
        this.socketVar = socketparam;
        this.dao = daoparam;
        response = new HTTPResponse();
    }

    @Override
    public void run() {
        System.out.println("ServiceThread Run: "+ this.socketVar.toString());
        try (Socket socket = this.socketVar) {
            try (Reader inputReader = new InputStreamReader(socketVar.getInputStream())) {
                try {
                    // request inicializado presuntamente
                    request = new HTTPRequest(inputReader);//THROWS HTTPParseException

                    if (request.getMethod() == HTTPRequestMethod.POST) {
                        System.out.println("POST");
                        // ******************************************** */
                        // CASO POST

                        dao.addPage(request.getContent());

                    } else if (request.getMethod() == HTTPRequestMethod.GET) {
                        System.out.println("GET");
                        // ******************************************** */
                        // CASO GET

                        try (OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream())) {
                            try {
                                writer.write(dao.get(request.getHeaderParameters().get("uuid")));
                            } catch (NullPointerException e) {
                                writer.write(dao.listPages());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (request.getMethod() == HTTPRequestMethod.DELETE) {
                        System.out.println("DELETE");
                        // ******************************************** */
                        // CASO DELETE

                    } else {
                        System.out.println("DEFAULT");
                        // CASO UNIMPLEMENTED METHOD

                        try (OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream())) {
                            try {
                                writer.write("Hybrid Server");
                            } catch (NullPointerException e) {
                                writer.write(dao.listPages());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                } catch (HTTPParseException e) {
                    response.setStatus(HTTPResponseStatus.S400);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
