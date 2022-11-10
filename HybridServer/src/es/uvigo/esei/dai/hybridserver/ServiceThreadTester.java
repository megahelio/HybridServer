package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThreadTester implements Runnable {
    private static int count = 0;
    private Socket socketVar;
    // private DaoInterface dao;
    // private HTTPRequest request;
    private HTTPResponse response;

    public ServiceThreadTester(Socket socketparam, DaoInterface daoparam) {
        System.out.println("Creando un ServiceThread " + (++count) + ": " + socketparam.toString());
        this.socketVar = socketparam;
        // this.dao = daoparam;
        this.response = new HTTPResponse();
    }

    @Override
    public void run() {
        System.out.println("ServiceThread Run " + count + " : " + this.socketVar.toString());
        try (Socket socket = this.socketVar) {

            response.setContent("holaaaaaaaaaaaaaaaaaaaa");
            response.setVersion("HTTP/1.1");
            response.setStatus(HTTPResponseStatus.S200);
            socket.getOutputStream().write(response.toString().getBytes(), 0,
                    response.toString().getBytes().length);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
