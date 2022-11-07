package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
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
                dao.addPage(request.getContent());
                break;
            case GET:

                try (OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream())) {
                    try {
                        writer.write(dao.get(request.getHeaderParameters().get("uuid")));
                    } catch (NullPointerException e) {
                        writer.write(dao.listPages());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case DELETE:

                break;
            default:

                break;
        }

    }

}
