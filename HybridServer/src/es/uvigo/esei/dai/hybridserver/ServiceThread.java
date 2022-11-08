package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
    private Socket socket;
    private Dao dao;
    private HTTPRequest request;
    private HTTPResponse response;

    /**
     * @param socket
     * @param dao
     */
    public ServiceThread(Socket socket, Dao dao) {
        this.socket = socket;
        this.dao = dao;
        response = new HTTPResponse();

        try (Reader inputReader = new InputStreamReader(socket.getInputStream())) {
            try {
                request = new HTTPRequest(inputReader);
            } catch (HTTPParseException e) {
                response.setStatus(HTTPResponseStatus.S400);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        switch (request.getMethod()) {
            case POST:

                break;
            case GET:

                break;
            case DELETE:

                break;
            default:

                break;
        }

    }

}
