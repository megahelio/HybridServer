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
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HTTPResponse {

	private HTTPResponseStatus status;

	private String content;

	private String version;

	private LinkedHashMap<String, String> parameters;

	/**
	 * create a new HTTPResponse objet
	 * 
	 * @version default value HTTP/1.1
	 */
	public HTTPResponse() {

		this.parameters = new LinkedHashMap<>();
		this.version = "HTTP/1.1";

	}

	public HTTPResponseStatus getStatus() {
		return this.status;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.status = status;
	}

	public String getVersion() {
		return this.version;
	}

	/**
	 * 
	 * @param version = HTTP/1.1 default value;
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {

		/*
		 * String r = "<body>Hybrid Server<\body>"; return r
		 */
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public String putParameter(String name, String value) {
		this.parameters.put(name, value);
		return value;
	}

	public boolean containsParameter(String name) {
		return getParameters().containsKey(name);
	}

	public String removeParameter(String name) {
		return getParameters().remove(name);
	}

	public void clearParameters() {
		this.parameters.clear();
	}

	public List<String> listParameters() {

		List<String> toRet = new ArrayList<>();

		for (String i : getParameters().values()) {
			toRet.add(i);
		}

		return toRet;

	}

	public void print(Writer writer) throws IOException {
		writer.append(getVersion() + " " + getStatus().getCode() + " " + getStatus().getStatus() + "\r\n");
		if (getParameters() != null) {
			getParameters().forEach((clave, valor) -> {
				try {
					writer.append(clave + ": " + valor + "\r\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

		}

		if (content != null && content.length() > 0) {
			writer.append("Content-Length: " + content.length() + "\r\n");
		}

		writer.append("\r\n");
		if (getContent() != null) {
			writer.append(getContent());
		}

	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return writer.toString();
	}
}
