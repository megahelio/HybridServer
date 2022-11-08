package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    }

    @Override
    public void run() {
        BufferedReader inputReader;
        System.out.println("ServiceThread Run "+count+" : " + this.socketVar.toString());
        try (Socket socket = this.socketVar) {
            System.out.println("ServiceThread Run 1º try" + count + " : " + socket.toString());
            //System.out.println(socket.getInputStream().toString());
            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                try {
                    // request inicializado presuntamente
                    System.out.println("Input Reader");
                    request = new HTTPRequest(inputReader);// THROWS HTTPParseException
                    System.out.println("Request: "+ request.getMethod());
                    if (request.getMethod() == HTTPRequestMethod.POST) {
                        System.out.println("Case POST");
                        // ******************************************** */
                        // CASO POST

                        dao.addPage(request.getContent());

                    } else if (request.getMethod() == HTTPRequestMethod.GET) {
                        System.out.println("Case GET");
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
                        System.out.println("Case DELETE");
                        // ******************************************** */
                        // CASO DELETE

                    } else {
                        System.out.println("Case DEFAULT");
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
                    System.out.println("Throws Parse Exception");
                    response.setStatus(HTTPResponseStatus.S400);
                }

           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
