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
import java.util.Map;

public class HTTPRequest {

	String url = "";

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		int aux = reader.read();

		while (aux != -1) {

			this.url += (char) aux;

			aux = reader.read();
		}
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
		
		String s = getResourceName();
		
		String list[] = s.split("/");
		
		return list;
	}

	public String getResourceName() {
		// TODO Auto-generated method stub
		
		String s = getResourceChain();
		
		String list[] = s.split("\\?");
		
		return list[0].substring(1, list[0].length());
	}

	public Map<String, String> getResourceParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHttpVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getHeaderParameters() {
		// TODO Auto-generated method stub
		return null;
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
	public String toString(){
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
