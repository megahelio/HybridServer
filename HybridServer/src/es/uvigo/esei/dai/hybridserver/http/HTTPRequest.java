package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {
	private String url = "";

	private HTTPRequestMethod method;
	private Map<String, String> headers;
	private String resourceChain;
	private String[] resourcePath;
	private String resourceName;
	private Map<String, String> resourceParameters;
	private String version;
	private String content;
	private int contentLenght;

	/**
	 * @param readerParam
	 * @throws IOException
	 * @throws HTTPParseException
	 */
	public HTTPRequest(Reader readerParam) throws IOException, HTTPParseException {
		System.out.println("HTTPRequest Creator");
		// this.url = URLDecoder.decode(this.url, "UTF-8");
		BufferedReader readerBuff = new BufferedReader(readerParam);

		// leemos primera linea
		String[] firstLine = URLDecoder.decode(readerBuff.readLine(), StandardCharsets.UTF_8).split(" ");
		System.out.print("firstLine: ");
		for (String seccion : firstLine) {
			System.out.print(seccion + " ");
		}
		System.out.println();

		// La primera linea debe tener 3 elementos, Método, ResourcePath y versión
		if (firstLine.length != 3) {
			throw new HTTPParseException("Primera linea Inválida");
		}

		// Buscamos método
		String method = firstLine[0];

		this.method = null;
		for (HTTPRequestMethod c : HTTPRequestMethod.values()) {
			if (method.equals(c.toString())) {
				this.method = c;
				System.out.println("Method: " + getMethod().toString());
			}
		}
		// Si no encaja con ningun metodo lanzamos excepcion
		if (this.method == null) {
			throw new HTTPParseException("Metodo Inválido");
		}

		// Buscamos "resourcePath"
		if (firstLine[1].charAt(0) != '/') {
			throw new HTTPParseException("La ruta no es válida");
		}
		this.resourceChain = firstLine[1];
		String dummyResourcePath[] = new String[1];
		if (firstLine[1] != "/" && firstLine[1].length() != 1) {
			// BUG: Añade la verdion a resourcePath
			String[] resourceChainSplited = this.resourceChain.split("/");
			dummyResourcePath = new String[resourceChainSplited.length - 1];
			for (int i = 1; i < resourceChainSplited.length - 1; i++) {
				dummyResourcePath[i - 1] = resourceChainSplited[i];
			}
			dummyResourcePath[dummyResourcePath.length - 1] = resourceChainSplited[resourceChainSplited.length - 1]
					.split("\\?")[0];
			this.resourcePath = dummyResourcePath;

		} else {
			// El test me pide un array vacio cuando me pasan /
			String foo[] = {};
			this.resourcePath = foo;
		}

		System.out.println("ResouceChain: " + getResourceChain());
		System.out.print("ResourcePath: ");
		for (String seccion : getResourcePath()) {
			System.out.print(seccion + " ");
		}
		System.out.println();
		// ResourceName
		if (this.resourceChain.contains("?")) {
			this.resourceName = this.resourceChain.split("\\?")[0].substring(1,
					this.resourceChain.split("\\?")[0].length());
		} else {
			this.resourceName = this.resourceChain.substring(1, this.resourceChain.length());
		}

		// ResourceParameters
		this.resourceParameters = new LinkedHashMap<>();
		if ((getMethod() == HTTPRequestMethod.GET && this.resourceChain.contains("?") )|| getMethod() == HTTPRequestMethod.DELETE
				&& this.resourceChain.contains("?")) {
			for (String parametro : this.resourceChain.split("\\?")[1].split("&")) {
				this.resourceParameters.put(parametro.split("=")[0], parametro.split("=")[1]);
			}
		}
		// Buscamos la versión
		if (firstLine[2].equals("HTTP/1.1") || firstLine[2].equals("HTTP/0.9") || firstLine[2].equals("HTTP/1.0")) {
			this.version = firstLine[2];
			System.out.println("Version: " + getHttpVersion());
		} else {
			throw new HTTPParseException("Versión inválida");
		}
		// Seccion de Cabeceras
		String aHeader;
		String[] aSplitedHeader = new String[2];
		this.headers = new LinkedHashMap<>();
		aHeader = URLDecoder.decode(readerBuff.readLine(), StandardCharsets.UTF_8);
		System.out.println(aHeader);
		while (aHeader.length() != 0) {
			System.out.println("aHeader: " + aHeader.length());
			if (aHeader.contains(": ")) {
				System.out.println(aHeader.split(": ")[0] + " " + aHeader.split(": ")[1]);
				aSplitedHeader = aHeader.split(": ");
				System.out.println(aSplitedHeader[0] + " " + aSplitedHeader[1]);
				if (aSplitedHeader.length != 2) {
					throw new HTTPParseException("Cabecera Inválida (Alguna cabecera tiene más de un : )");
				}
				this.headers.put(aSplitedHeader[0], aSplitedHeader[1]);
				System.out.println("Header Params: " + getHeaderParameters().toString());

			} else {
				throw new HTTPParseException("Cabecera Inválida (Alguna cabecera no contiene :)");
			}
			aHeader = URLDecoder.decode(readerBuff.readLine(), StandardCharsets.UTF_8);
		}

		// Sección Contenido
		if (this.headers.containsKey("Content-Length")) {
			this.contentLenght = Integer.parseInt(this.headers.get("Content-Length"));
			char[] contentChars = new char[Integer.parseInt(this.headers.get("Content-Length"))];
			System.out.println(this.contentLenght);
			readerBuff.read(contentChars, 0, Integer.parseInt(this.headers.get("Content-Length")));
			this.content = "";
			for (char character : contentChars) {
				this.content = this.content.concat(Character.toString(character));
				// System.out.print(Character.toString(character));
			}
			this.content = URLDecoder.decode(this.content, StandardCharsets.UTF_8);
			
			System.out.println("Content: \n" + this.content.toString());
		}

		// ResourceParameters POST
		if (getMethod() == HTTPRequestMethod.POST && !this.content.equals("")) {
			for (String parametro : content.split("&")) {
				this.resourceParameters.put(parametro.split("=")[0], parametro.split("=")[1]);
			}

		}

		System.out.println("Final contructor");

	}

	// public HTTPRequest(String reader) throws UnsupportedEncodingException,
	// HTTPParseException {
	// url = URLDecoder.decode(reader, "UTF-8");

	// }

	public String getUrl() {
		return url;
	}

	public HTTPRequestMethod getMethod() {
		return this.method;
	}

	public String getResourceChain() {
		return this.resourceChain;
	}

	public String[] getResourcePath() {
		return this.resourcePath;
	}

	public String getResourceName() {
		return this.resourceName;
	}

	public Map<String, String> getResourceParameters() {
		return this.resourceParameters;
	}

	public String getHttpVersion() {
		return this.version;
	}

	public Map<String, String> getHeaderParameters() {
		return this.headers;
	}

	public String getContent() {
		return this.content;
	}

	public int getContentLength() {
		return this.contentLenght;
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
