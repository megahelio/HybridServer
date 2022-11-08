package es.uvigo.esei.dai.hybridserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
	
	private Socket socket;
	private Dao dao;

    public ServiceThread(Socket socket, Dao dao) {
    	
    	this.socket = socket;
    	this.dao = dao;
    	
    }
    

    public ServiceThread(Socket socket) {
    	
    	this.socket = socket;
    	
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

	}
    	
    }


