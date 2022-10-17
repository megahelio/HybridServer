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
package es.uvigo.esei.dai.hybridserver;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;

public class Launcher {
	public static void main(String[] args) {
		
		String requestText = "GET /hello/world.html?country=Spain&province=Ourense&city=Ourense HTTP/1.1\r\n" +
				"Host: localhost\r\n" +
				"Accept: text/html\r\n" +
				"Accept-Encoding: gzip,deflate\r\n";

		HTTPRequest a = new HTTPRequest(requestText);
		
		a.getResourcePath();
		
	}
}
