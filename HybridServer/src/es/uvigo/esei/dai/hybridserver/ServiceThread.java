package es.uvigo.esei.dai.hybridserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.Socket;

public class ServiceThread implements Runnable {
	
	private Socket socket;
	private Dao dao;

public class ServiceThread implements Runnable {
    private Socket socket;
    private Dao dao;
    private HTTPRequest request;
    private HTTPResponse response;

    public ServiceThread(Socket socket, Dao dao) {
    }

    @Override
    public void run() {
        
    	try (Socket socket = this.socket) {
			
    		InputStreamReader input = new InputStreamReader (socket.getInputStream());
    		
    		OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());
			
			HTTPRequest request = new HTTPRequest(input);
			
			HTTPResponse response= new HTTPResponse();
			
			response.setVersion("HTTP1.1");
			
			response.setStatus(HTTPResponseStatus.S200);
			
			response.setContent("Hybrid Server");
			
			

			response.print(output);
			output.flush();
    		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HTTPParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

    }}}

