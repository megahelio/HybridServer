package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.exceptions.HTTPParseException;

import java.io.InputStreamReader;

public class ServiceThreadTester implements Runnable {
    private static int count = 0;
    private Socket socketVar;
    //private DaoInterface dao;
    private HTTPRequest request;
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

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            request = new HTTPRequest(inputReader);
            System.out.println(request.getMethod());

            response.setContent("holaaaaaaaaaaaaaaaaaaaa");
            response.setVersion("HTTP/1.1");
            response.setStatus(HTTPResponseStatus.S200);
            socket.getOutputStream().write(response.toString().getBytes(), 0,
                    response.toString().getBytes().length);

        } catch (HTTPParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
