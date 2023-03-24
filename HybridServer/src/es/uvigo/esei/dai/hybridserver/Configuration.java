package es.uvigo.esei.dai.hybridserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Configuration {
	private final int DEFAULT_httpPort = 8888;
	private final int DEFAULT_numClients = 50;
	private final String DEFAULT_webServiceURL = null;
	private final String DEFAULT_dbUser = "hsdb";
	private final String DEFAULT_dbPassword = "hsdbpass";
	private final String DEFAULT_dbURL = "jdbc:mysql:// localhost:3306/hstestdb";

	private int httpPort;
	private int numClients;
	private String webServiceURL;

	private String dbUser;
	private String dbPassword;
	private String dbURL;

	private List<ServerConfiguration> servers;

	/**
	 * Carga un configuration con atributos por defecto
	 */
	public Configuration() {

		this.httpPort = DEFAULT_httpPort;
		this.numClients = DEFAULT_numClients;
		this.webServiceURL = DEFAULT_webServiceURL;
		this.dbUser = DEFAULT_dbUser;
		this.dbPassword = DEFAULT_dbPassword;
		this.dbURL = DEFAULT_dbURL;
		this.servers = new ArrayList<ServerConfiguration>();
	}

	/**
	 * Construye un objeto Configuration con los atributos que se pasan como
	 * argumento
	 * 
	 * @param httpPort
	 * @param numClients
	 * @param webServiceURL
	 * @param dbUser
	 * @param dbPassword
	 * @param dbURL
	 * @param servers
	 */
	public Configuration(
			int httpPort,
			int numClients,
			String webServiceURL,
			String dbUser,
			String dbPassword,
			String dbURL,
			List<ServerConfiguration> servers) {
		this.httpPort = httpPort;
		this.numClients = numClients;
		this.webServiceURL = webServiceURL;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
		this.dbURL = dbURL;
		this.servers = servers;
	}

	/**
	 * Crea Conficuration a partir de las claves propertis usando claves hardcode,
	 * en caso de no incluir una clave inserta la configuracion por defecto para ese
	 * atributo
	 * 
	 * @param properties
	 */
	public Configuration(Properties properties) {
		// for (Object key : properties.keySet()) {
		// System.out.println(key.toString());
		// }
		if (properties.containsKey("port")) {
			this.httpPort = Integer.parseInt(properties.getProperty("port"));
		} else {
			this.httpPort = DEFAULT_httpPort;
		}

		if (properties.containsKey("numClients")) {
			this.numClients = Integer.parseInt(properties.getProperty("numClients"));
		} else {
			this.numClients = DEFAULT_numClients;
		}
		if (properties.containsKey("wsURL")) {
			this.webServiceURL = properties.getProperty("wsURL");
		} else {

			this.webServiceURL = DEFAULT_webServiceURL;
		}

		if (properties.containsKey("db.user")) {
			this.dbUser = properties.getProperty("db.user");
		} else {
			this.dbUser = DEFAULT_dbUser;
		}
		if (properties.containsKey("db.password")) {
			this.dbPassword = properties.getProperty("db.password");
		} else {
			this.dbPassword = DEFAULT_dbPassword;
		}
		if (properties.containsKey("db.url")) {
			this.dbURL = properties.getProperty("db.url");
		} else {
			this.dbURL = DEFAULT_dbURL;
		}

		this.servers = new ArrayList<>();
	}

	public int getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}

	public int getNumClients() {
		return numClients;
	}

	public void setNumClients(int numClients) {
		this.numClients = numClients;
	}

	public String getWebServiceURL() {
		return webServiceURL;
	}

	public void setWebServiceURL(String webServiceURL) {
		this.webServiceURL = webServiceURL;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getDbURL() {
		return dbURL;
	}

	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}

	public List<ServerConfiguration> getServers() {
		return servers;
	}

	public void setServers(List<ServerConfiguration> servers) {
		this.servers = servers;
	}
	 @Override
	    public String toString() {
	        String toret= "Configuration{" +
	                "httpPort=" + httpPort +
	                ", numClients=" + numClients +
	                ", webServiceURL='" + webServiceURL + '\'' +
	                ", dbUser='" + dbUser + '\'' +
	                ", dbPassword='" + dbPassword + '\'' +
	                ", dbURL='" + dbURL + '\'' +
	                ", servers=\n";
	        for (ServerConfiguration serverConfiguration : servers) {
	        	toret.concat(serverConfiguration.toString()+"\n");
			}
	        toret.concat("\n}");
	        
	        return toret;
	    }
	
}
