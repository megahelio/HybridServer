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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

	public HTTPRequestMethod getMethod() {

		String list[] = url.split(" ");

		for (HTTPRequestMethod c : HTTPRequestMethod.values()) {
			if (c.name().equals(list[0])) {
				return HTTPRequestMethod.valueOf(list[0]);
			}
		}

		return null;

	}

	public String getResourceChain() {
		// TODO Auto-generated method stub

		String list[] = url.split(" ");

		return list[1];
	}

	public String[] getResourcePath() {
		// TODO Auto-generated method stub

		String r = getResourceChain();

		String list[] = r.split("\\?");

		String s = list[0];

		String[] aux = s.split("/");
		
		if(aux.length!=0) {

		String[] toRet = new String[aux.length - 1];

		for (int i = 1; i < aux.length; i++) {
			toRet[i - 1] = aux[i];
		}
		
		

		return toRet;
		
		} else {
			
			String[] toRet = {};
			
			return toRet;
			
		}
	}

	public String getResourceName() {
		// TODO Auto-generated method stub

		String s = getResourceChain();

		String list[] = s.split("\\?");

		return list[0].substring(1, list[0].length());
	}

	public Map<String, String> getResourceParameters() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

		String s = url.split(" ")[2];

		s = s.split("\r\n")[0];

		String separador = Pattern.quote("\\");

		String r = s.split(separador)[0];

		if (r.compareTo(HTTPHeaders.HTTP_1_1.getHeader()) == 0) {

			return r;

		}

		return "";
	}

	public Map<String, String> getHeaderParameters() {

		Map<String, String> m = new LinkedHashMap<String, String>();

		String requestText = "POST / HTTP/1.1\r\n" + "Host: localhost\r\n"
				+ "Content-Type: application/x-www-form-urlencoded\r\n" + "Content-Length: 116\r\n\r\n"
				+ "message=Hello world!!&mensaje=¡¡Hola mundo!!&mensaxe=Ola mundo!!&mensagem=Olá mundo!!";

		String[] list = url.split("\r\n");
		;

		for (int i = 1; i < list.length; i++) {

			if (list[i].contains(":")) {
				m.put(list[i].split(": ")[0], list[i].split(": ")[1]);
			}
		}

		return m;
	}

	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}
}
