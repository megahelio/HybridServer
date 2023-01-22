package es.uvigo.esei.dai.hybridserver.configuration;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import es.uvigo.esei.dai.hybridserver.Configuration;

public class ConfigurationContentHandler extends DefaultHandler {

    Configuration configuration;
    List<ServerConfiguration> servers;
    boolean configurationTag;
    boolean connectionsTag;
    boolean httpTag;
    boolean webServiceTag;
    boolean numClientsTag;
    boolean databaseTag;
    boolean userTag;
    boolean passwordTag;
    boolean urlTag;
    boolean serversTag;
    boolean serverTag;

    public ConfigurationContentHandler() {
        this.configuration = new Configuration();
        this.servers = new ArrayList<>();
        this.configurationTag = false;
        this.connectionsTag = false;
        this.httpTag = false;
        this.webServiceTag = false;
        this.numClientsTag = false;
        this.databaseTag = false;
        this.userTag = false;
        this.passwordTag = false;
        this.urlTag = false;
        this.serversTag = false;
        this.serverTag = false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // System.out.println(
        // "\nStartElement: \n" + "uri: " + uri + "\n" + "localName: " + localName +
        // "\n" + "qName: " + qName
        // + "\nAtributes: " + attributes.getLength());
        if (qName.equals("configuration")) {
            configurationTag = true;
        }
        if (qName.equals("connections")) {
            connectionsTag = true;
        }
        if (qName.equals("http")) {
            httpTag = true;
        }
        if (qName.equals("webservice")) {
            webServiceTag = true;
        }
        if (qName.equals("numClients")) {
            numClientsTag = true;
        }
        if (qName.equals("database")) {
            databaseTag = true;
        }
        if (qName.equals("user")) {
            userTag = true;
        }
        if (qName.equals("password")) {
            passwordTag = true;
        }
        if (qName.equals("url")) {
            urlTag = true;
        }
        if (qName.equals("servers")) {
            serversTag = true;
        }
        if (qName.equals("server")) {
            serverTag = true;

            ServerConfiguration server = new ServerConfiguration();
            server.setName(attributes.getValue("name"));
            server.setWsdl(attributes.getValue("wsdl"));
            server.setNamespace(attributes.getValue("namespace"));
            server.setService(attributes.getValue("service"));
            server.setHttpAddress(attributes.getValue("httpAddress"));
            servers.add(server);
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // System.out.println("\nEndElement: \n" + "uri: " + uri + "\n" + "localName: "
        // + localName + "\n" + "qName: "
        // + qName);
        if (qName.equals("configuration")) {
            configurationTag = false;
        }
        if (qName.equals("connections")) {
            connectionsTag = false;
        }
        if (qName.equals("http")) {
            httpTag = false;
        }
        if (qName.equals("webservice")) {
            webServiceTag = false;
        }
        if (qName.equals("numClients")) {
            numClientsTag = false;
        }
        if (qName.equals("database")) {
            databaseTag = false;
        }
        if (qName.equals("user")) {
            userTag = false;
        }
        if (qName.equals("password")) {
            passwordTag = false;
        }
        if (qName.equals("url")) {
            urlTag = false;
        }
        if (qName.equals("servers")) {
            serversTag = false;
            configuration.setServers(servers);
        }
        if (qName.equals("server")) {
            serverTag = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // System.out.println("\nCharacter: ");

        // for (int i = start; i < start + length; i++) {
        // System.out.print(ch[i]);
        // }
        if (configurationTag && connectionsTag && httpTag) {
            configuration.setHttpPort(Integer.parseInt(new String(ch, start, length)));// redundante parsear a String en
                                                                                       // vez de parsear directamente a
                                                                                       // int
        }
        if (configurationTag && connectionsTag && webServiceTag) {
            configuration.setWebServiceURL(new String(ch, start, length));
        }
        if (configurationTag && connectionsTag && numClientsTag) {
            configuration.setNumClients(Integer.parseInt(new String(ch, start, length)));// redundante parsear a String
                                                                                         // en
                                                                                         // vez de parsear directamente
                                                                                         // a
                                                                                         // int
        }
        if (configurationTag && databaseTag && userTag) {
            configuration.setDbUser(new String(ch, start, length));
        }

        if (configurationTag && databaseTag && passwordTag) {
            configuration.setDbPassword(new String(ch, start, length));
        }

        if (configurationTag && databaseTag && urlTag) {
            configuration.setDbURL(new String(ch, start, length));
        }

        // System.out.println("\nstart: " + start + "\n" + "length: " + length);

    }

    @Override
    public void startDocument() throws SAXException {
        // System.out.println("\nStartDocument: ");
    }

    @Override
    public void endDocument() throws SAXException {
        // System.out.println("\nStopDocument: ");
    }

    public Configuration getConfiguration() {
        return configuration;
    }

}
