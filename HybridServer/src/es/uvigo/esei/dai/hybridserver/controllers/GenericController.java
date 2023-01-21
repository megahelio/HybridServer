package es.uvigo.esei.dai.hybridserver.controllers;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;

public interface GenericController {
public HTTPResponse get(HTTPRequest request);

public HTTPResponse post(HTTPRequest request);

public HTTPResponse delete(HTTPRequest request);

public HTTPResponse list(HTTPRequest request);
}
