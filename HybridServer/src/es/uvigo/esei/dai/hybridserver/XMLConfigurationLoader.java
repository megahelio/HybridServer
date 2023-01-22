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

import java.io.File;

import es.uvigo.esei.dai.hybridserver.configuration.ConfigurationContentHandler;
import es.uvigo.esei.dai.hybridserver.xml.XMLUtility;

public class XMLConfigurationLoader {
	public static Configuration load(File xmlFile) throws Exception {
		ConfigurationContentHandler handler = new ConfigurationContentHandler();
		XMLUtility.parseAndValidateWithExternalXSD(xmlFile, "configuration.xsd",
				handler);
		// Configuration configuration = handler.getConfiguration();
		// System.out.println("configuration.getHttpPort(): " +
		// configuration.getHttpPort());
		// System.out.println("configuration.getWebServiceURL(): " +
		// configuration.getWebServiceURL());
		// System.out.println("configuration.getNumClients(): " +
		// configuration.getNumClients());
		// System.out.println("configuration.getDbUser(): " +
		// configuration.getDbUser());
		// System.out.println("configuration.getDbPassword(): " +
		// configuration.getDbPassword());
		// System.out.println("configuration.getDbURL(): " +
		// configuration.getDbURL()+"\n");
		// for (ServerConfiguration server : configuration.getServers()) {

		// System.out.println("server.getName(): " + server.getName());
		// System.out.println("server.getWsdl(): " + server.getWsdl());
		// System.out.println("server.getNamespace(): " + server.getNamespace());
		// System.out.println("server.getService(): " + server.getService());
		// System.out.println("server.getHttpAddress(): " +
		// server.getHttpAddress()+"\n");

		// }
		return handler.getConfiguration();
	}

}
