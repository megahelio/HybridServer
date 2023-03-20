package es.uvigo.esei.dai.hybridserver.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import es.uvigo.esei.dai.hybridserver.HybridServerService;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;
import es.uvigo.esei.dai.hybridserver.controllers.exceptions.InvalidParameterException;
import es.uvigo.esei.dai.hybridserver.controllers.exceptions.MissedParameterException;
import es.uvigo.esei.dai.hybridserver.dao.DaoXML;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSD;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSLT;
import es.uvigo.esei.dai.hybridserver.dao.UUIDgenerator;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;
import es.uvigo.esei.dai.hybridserver.XMLUtility;

public class XMLController {

	private DaoXML daoXML;
	private DaoXSLT daoXSLT;
	private DaoXSD daoXSD;
	private List<ServerConfiguration> servers;
	private int port;

	/**
	 * @param daoXML
	 * @param i
	 */

	public XMLController(DaoXML daoXML, DaoXSLT daoXSLT, DaoXSD daoXSD, List<ServerConfiguration> servers, int port) {
		this.daoXML = daoXML;
		this.daoXSLT = daoXSLT;
		this.daoXSD = daoXSD;
		this.servers = servers;
		this.port = port;

	}

	public HTTPResponse get(HTTPRequest request) {
		HTTPResponse response = new HTTPResponse();

		try {

			String uuid = request.getResourceParameters().get("uuid");
			// System.out.println("uuid de la request: " + uuid);

			if (!UUIDgenerator.validate(uuid)) {

				throw new InvalidParameterException("Invalid UUID");

			}

			String xml = this.daoXML.get(uuid);// return null if no exist

			try {
				Boolean foundXML = xml != null;
				int i = 0;
				while (!foundXML && i < servers.size()) {
					if (!servers.get(i).getName().equals("Down Server")) {

						URL url;
						// NOTA: Si ServerConfiguration.getName() Devolviese una URL no Saltaría la
						// excepcion y se controlaría la formacion de la url en la propia clase de
						// ServerConfiguration
						url = new URL(servers.get(i).getWsdl());// Throws
						// MalformedURLException

						QName name = new QName(servers.get(i).getNamespace(),
								servers.get(i).getService() + "ImplService");
						Service service = Service.create(url, name);
						HybridServerService ws = service.getPort(HybridServerService.class);
						xml = ws.getXML(request.getResourceParameters().get("uuid"));
						if (xml != null) {
							foundXML = true;
						}
					}
					i++;
				}
				if (foundXML) {

					if (!request.getResourceParameters().containsKey("xslt")) {

						// No se pide transformación --> Devuelvo el XML
						response.setContent(xml);
						response.putParameter("Content-Type", "application/xml");
						response.setStatus(HTTPResponseStatus.S200);
						return response;

					}

					// Me piden tranformacion --> Necesito encotrar el XSLT y el XSD asociado que
					// permita verificar la compatibilidad entre el XSLT y el XSD

					// Buscamos en local
					String xslt = this.daoXSLT.getContent(request.getResourceParameters().get("xslt"));
					String xsdUUID = this.daoXSLT.getXSD(request.getResourceParameters().get("xslt"));

					String xsd = this.daoXSD.get(xsdUUID);

					// Si no encuentro alguna de los 2 los buscaremos en remoto
					// Como para crear un XSLT es necesario incluir la columna con
					// el xsdUUID se asume que encontrado el XSLT -> encontrado el
					// uuid del XSD, de hecho es la relación entre estos
					// elementos. Asumimos que no puedes dar de alta ficheros XSLT
					// sin XSD asociado.
					try {
						// Inicializamos una variable booleana por cada elemento a buscar (2)
						// a true si lo habiamos encontrado en local
						Boolean foundXSLT = xslt != null;
						Boolean foundXSD = xsd != null;

						i = 0;
						// Búsqueda lineal
						while (!foundXSLT && i < servers.size()) {

							// Skip de servidores con nombre DOWN SERVER
							if (!servers.get(i).getName().equals("Down Server")) {

								URL url;
								// NOTA: Si ServerConfiguration.getName() Devolviese una URL no Saltaría la
								// excepcion y se controlaría la formacion de la url en la propia clase de
								// ServerConfiguration
								url = new URL(servers.get(i).getWsdl());// Throws MalformedURLException

								QName name = new QName(servers.get(i).getNamespace(),
										servers.get(i).getService() + "ImplService");
								Service service = Service.create(url, name);
								HybridServerService ws = service.getPort(HybridServerService.class);

								xslt = ws.getXSLT(request.getResourceParameters().get("xslt"));
								xsdUUID = ws.getXSDofaXSLT(request.getResourceParameters().get("xslt"));
								if (xslt != null) {
									foundXSLT = true;
								}

							}
							i++;
						}
						i = 0;
						// Búsqueda lineal
						while (!foundXSD && i < servers.size()) {

							// Skip de servidores con nombre DOWN SERVER
							if (!servers.get(i).getName().equals("Down Server")) {

								URL url;
								// NOTA: Si ServerConfiguration.getName() Devolviese una URL no Saltaría la
								// excepcion y se controlaría la formacion de la url en la propia clase de
								// ServerConfiguration
								url = new URL(servers.get(i).getWsdl());// Throws MalformedURLException

								QName name = new QName(servers.get(i).getNamespace(),
										servers.get(i).getService() + "ImplService");
								Service service = Service.create(url, name);
								HybridServerService ws = service.getPort(HybridServerService.class);

								xsd = ws.getXSD(xsdUUID);

								if (xsd != null) {
									foundXSD = true;
								}

							}
							i++;
						}

						if (foundXSLT && foundXSD) {
							if (!XMLUtility.validateSchema(xml, xsd)) {
								// Bad Request XSLT inválido (XSD del XSLT no valida XML solicitado)
								response.setStatus(HTTPResponseStatus.S400);
							} else {
								String html = XMLUtility.xmlToHtml(xml, xslt);
								if (html != null) {
									response.setContent(html);
									response.putParameter("Content-Type", "text/html");
									response.setStatus(HTTPResponseStatus.S200);
									return response;
								} else {

									response.setStatus(HTTPResponseStatus.S400);
									return response;
								}
							}
						} else {
							// NOT FOUND
							response.setStatus(HTTPResponseStatus.S404);
							return response;
						}
					} catch (MalformedURLException e) {
						// System.out.println("URL mal formada: Saltando al siguiente servidor de la
						// lista.");
					}

				} else {
					// NOT FOUND
					response.setStatus(HTTPResponseStatus.S404);
					return response;
				}
			} catch (MalformedURLException e) {
				// System.out.println("URL mal formada: Saltando al siguiente servidor de la
				// lista.");
			}

		} catch (InvalidParameterException e) {
			// BAD REQUEST
			response.setStatus(HTTPResponseStatus.S400);
		}
		return response;
	}

	/**
	 * Resuelve petiones post
	 * 
	 * @param request
	 * @return
	 */

	public HTTPResponse post(HTTPRequest request) {
		String nuevaPaginaUuid;
		HTTPResponse response = new HTTPResponse();
		Set<String> keys = request.getResourceParameters().keySet();
		try {
			// verificamos que el contenido contenga un elemento XML (pq el mapa puede
			// contener nulos no puedo usar nullpointerexception)
			if (!keys.contains("xml")) {
				throw new MissedParameterException("No xml found");
			}
			// si contiene xml creamos la entrada en la BD
			nuevaPaginaUuid = this.daoXML.addPage(request.getResourceParameters().get("xml"));// NullPointerException
			response.setContent("<a href=\"xml?uuid=" + nuevaPaginaUuid + "\">" + nuevaPaginaUuid + "</a>");
			response.setStatus(HTTPResponseStatus.S200);
		} catch (MissedParameterException e) {
			// BAD REQUEST pq la peticion de post xml no contiene xml
			response.setStatus(HTTPResponseStatus.S400);
		}
		return response;
	}

	/**
	 * Resuelve Peticiones DELETE
	 * 
	 * @param request
	 * @return
	 */

	public HTTPResponse delete(HTTPRequest request) {
		HTTPResponse response = new HTTPResponse();
		try {
			this.daoXML.deletePage(request.getResourceParameters().get("uuid"));
			response.setStatus(HTTPResponseStatus.S200);
		} catch (RuntimeException e) {
			response.setStatus(HTTPResponseStatus.S500);
		}
		return response;
	}

	public HTTPResponse list(HTTPRequest request) {

		HTTPResponse response = new HTTPResponse();
		String fullList = "<h1>XML List</h1><br>";
		fullList = fullList.concat("<h1>LocalHost</h1><br>");
		fullList = fullList.concat("<ul>");
		for (String uuid : this.daoXML.listPages()) {
			fullList = fullList.concat(
					"<li><a href=http://localhost:" + port + "/xml?uuid=" + uuid + ">" + uuid + "</a>" + "</li>");
		}
		fullList = fullList.concat("</ul>");

		for (ServerConfiguration server : this.servers) {

			if (!server.getName().equals("Down Server")) {

				URL url;
				// NOTA: Si ServerConfiguration.getName() Devolviese una URL no Saltaría la
				// excepcion y se controlaría la formacion de la url en la propia clase de
				// ServerConfiguration
				try {
					url = new URL(server.getWsdl());

					QName name = new QName(server.getNamespace(), server.getService() + "ImplService");

					Service service = Service.create(url, name);

					HybridServerService ws = service.getPort(HybridServerService.class);
					fullList = fullList.concat("<html><body><h1>" + server.getName() + "</h1>\n");
					fullList = fullList.concat("<ul>");
					for (String uuid : ws.listPagesXML()) {
						fullList = fullList.concat("<li><a href=" + server.getHttpAddress() + "xml?uuid=" + uuid + ">"
								+ uuid + "</a>" + "</li>");
					}
					fullList = fullList.concat("</ul>");
					fullList = fullList.concat("</body></html>");
				} catch (MalformedURLException e) {
					throw new RuntimeException("URL MALFORMED, server skipped");
				}
			}
		}

		response.setContent(fullList);
		response.putParameter("Content-type", MIME.TEXT_HTML.getMime());
		// System.out.println("ResponseListBody: " + response.getContent());
		response.setStatus(HTTPResponseStatus.S200);
		return response;
	}

}
