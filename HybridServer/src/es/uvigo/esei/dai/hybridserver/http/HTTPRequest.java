/**
 *  HybridServer
 *  Copyright (C) 2022 Miguel Reboiro-Jato
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Ejemplo:
 * 
 * GET /hello/world.html HTTP/1.1\r\n User-Agent: Mozilla/4.0
 * (compatible;MSIE5.01; Windows NT)\r\n Host: www.tutorialspoint.com\r\n
 * Accept-Language:en-us\r\n Accept-Encoding: gzip, deflate\r\n Connection:
 * Keep-Alive\r\n
 *
 *
 */
public class HTTPRequest {

	String url = "";

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		int aux = reader.read();
		while (aux != -1) {
			this.url += (char) aux;
			aux = reader.read();
		}
	}

	public HTTPRequest(String reader) {
		url = reader;
	}

	/**
	 * Devuelve el método del la petición GET, POST, DELETE...
	 * 
	 * @return HTTPRequestMethod
	 */
	public HTTPRequestMethod getMethod() {

		String list[] = url.split(" ");

		for (HTTPRequestMethod c : HTTPRequestMethod.values()) {
			if (c.name().equals(list[0])) {
				return HTTPRequestMethod.valueOf(list[0]);
			}
		}

		return null;

	}

	/**
	 * Devuelve la ruta "/hello/world.html"
	 * 
	 * @return String
	 */
	public String getResourceChain() {

		String list[] = url.split(" ");
		return list[1];
	}

	/**
	 * Devuelve la ruta "hello world.html" en un array
	 * 
	 * @return String[]
	 */

	public String[] getResourcePath() {

		String r = getResourceChain();
		String list[] = r.split("\\? ");
		String s = list[0];
		String list2[] = s.split("/");
		return list2;
	}

	/**
	 * 
	 * @return String
	 */

	public String getResourceName() {

		String s = getResourceChain();
		String list[] = s.split("\\?");
		return list[0].substring(1, list[0].length());
	}

	public Map<String, String> getResourceParameters() {

		String s = getResourceChain();
		Map<String, String> m = new LinkedHashMap<String, String>();

		if (!s.contains("?")) {
			return m;
		}

		String list[] = s.split("\\?");
		s = list[1];
		list = s.split("&");

		for (int i = 0; i < list.length; i++) {
			m.put(list[i].split("=")[0], list[i].split("=")[1]);
		}
		return m;
	}

	public String getHttpVersion() {

		String s = url.split("\r\n")[0];

		String r = s.split(" ")[2];
		

		if (r.equals(HTTPHeaders.HTTP_1_1.getHeader())) {
			System.out.println("a"+r+"a");
			return r;

		}

		System.out.println(r + "   " + HTTPHeaders.HTTP_1_1.getHeader());
		return "";
	}

	/**
	 * Devuelve un mapa con el par NombreCabecera:ValorDelParámetro
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getHeaderParameters() {
		// traemos toda la url
		String s = this.url;
		// inicializamos el mapa
		Map<String, String> m = new LinkedHashMap<String, String>();
		// separamos la url en lineas
		String list[] = s.split("\r\n");

		/*
		 * para cada linea salvo la primera, es decir, las que incluyen las cabeceras,
		 * insertamos en el mapa
		 */
		for (int i = 1; i < list.length; i++) {
			//linea debug
			//System.out.println(list[i].split(":")[0] +":"+ list[i].split(":")[1]);
			m.put(list[i].split(":")[0], list[i].split(":")[1]);
		}
		return m;
	}

	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getContentLength() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(":").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}
}
