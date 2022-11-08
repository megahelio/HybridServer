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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HTTPRequest {
	String url = "";

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
		System.out.println("HTTPRequest Creator");

		int aux = reader.read();
		System.out.print((char) aux);
		while (reader.ready()) {

			this.url += (char) aux;

			aux = reader.read();
			System.out.print((char) aux);
		}
		this.url = URLDecoder.decode(this.url, "UTF-8");
		System.out.println("HTTPRequest Creator validation");
		validRequest();
		System.out.println("HTTPRequest Creator end");
	}

	public HTTPRequest(String reader) throws UnsupportedEncodingException, HTTPParseException {
		url = URLDecoder.decode(reader, "UTF-8");
		validRequest();
	}

	private void validMethod() throws HTTPParseException {

		if (getMethod() == null) {
			System.out.println("ValidateMethod NO");
			throw new HTTPParseException();
		}
		System.out.println("ValidateMethod YES");
	}

	private void validRequest() throws HTTPParseException {
		validMethod();
		validResource();
		validVersion();
		validateHeaders();
	}

	private void validVersion() throws HTTPParseException {
		if (getHttpVersion() == "") {
			System.out.println("ValidateVersion NO");
			throw new HTTPParseException();
		}
		System.out.println("ValidateVersion YES");
	}

	private void validResource() throws HTTPParseException {
		if (getResourceChain().charAt(0) != '/') {
			System.out.println("ValidateResource NO");
			throw new HTTPParseException();
		}
		System.out.println("ValidateResource YES");

	}

	public HTTPRequestMethod getMethod() {
		System.out.println("GetMethod: ");

		for (HTTPRequestMethod c : HTTPRequestMethod.values()) {
			System.out.println("Cheking: " + c);
			if (url.contains(c.toString())) {
				System.out.println("Method: " + c);
				return c;
			}
		}
		System.out.println("NO Method found");
		return null;

	}

	public String getResourceChain() {

		String list[];

		list = url.split(" ");

		return list[1];
	}

	public String[] getResourcePath() {

		String r = getResourceChain();

		String list[] = r.split("\\?");

		String s = list[0];

		String[] aux = s.split("/");

		if (aux.length != 0) {

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

		String s = getResourceChain();

		String list[] = s.split("\\?");

		return list[0].substring(1, list[0].length());
	}

	public Map<String, String> getResourceParameters() {

		Map<String, String> m = new LinkedHashMap<String, String>();

		switch (getMethod()) {
			case DELETE:
			case GET:
				String chain = getResourceChain();

				if (!chain.contains("?")) {

					return m;

				}

				String list[] = chain.split("\\?");

				chain = list[1];

				list = chain.split("&");

				for (int i = 0; i < list.length; i++) {

					m.put(list[i].split("=")[0], list[i].split("=")[1]);

				}

				break;

			case POST:
				String content = getContent();
				System.out.println("content: " + content);

				String lista[] = content.split("&");

				for (int i = 0; i < lista.length; i++) {

					m.put(lista[i].split("=")[0], lista[i].split("=")[1]);

				}

				break;

			default:
				break;
		}

		return m;

	}

	public String getHttpVersion() {

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

		String[] list = url.split("\r\n");
		for (int i = 1; i < list.length; i++) {

			if (list[i].contains(":")) {
				m.put(list[i].split(": ")[0], list[i].split(": ")[1]);
			}
		}

		return m;

	}

	private void validateHeaders() throws HTTPParseException {
		Map<String, String> m = new LinkedHashMap<String, String>();

		String[] list = url.split("\r\n\r\n")[0].split("\r\n");

		try {
			for (int i = 1; i < list.length-1; i++) {
				System.out.println(list[i] + " Contains : => " + list[i].contains(":"));
				if (list[i].contains(":")) {
					System.out.println(list[i].split(": ")[0] + " ---- " + list[i].split(": ")[1]);
					m.put(list[i].split(": ")[0], list[i].split(": ")[1]);
				} else {
					System.out.println("Validate Headers NO");
					throw new HTTPParseException();
				}
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Validate Headers NO");
			throw new HTTPParseException();
		}
		System.out.println("Validate Headers YES");
	}

	public String getContent() {
		if (getContentLength() == 0) {
			return null;
		}

		return url.split("\\r\\n\\r\\n")[1];

	}

	public int getContentLength() {
		try {
			String contentLenght = getHeaderParameters().get("Content-Length");
			if (contentLenght != null)
				return Integer.parseInt(contentLenght);
			return 0;

		} catch (NullPointerException e) {
			return 0;
		}

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
