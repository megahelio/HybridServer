/**
 *  Temario DAI
 *  Copyright (C) 2014 Miguel Reboiro-Jato
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
package es.uvigo.esei.dai.hybridserver.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;



public class SAXParsing {
	public static void parseFile(String xmlPath, ContentHandler handler)
	throws SAXException, IOException, ParserConfigurationException {
		// Construcción del parser SAX
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		
		// Se añade el handler al parser SAX
		SAXParser parser = parserFactory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		reader.setContentHandler(handler);
		
		// Parsing
		try (FileReader fileReader = new FileReader(new File(xmlPath))) {
			reader.parse(new InputSource(fileReader));
		}
	}
	
	public static void parseAndValidatedWithInternalDTD(String xmlPath, ContentHandler handler)
	throws ParserConfigurationException, SAXException, IOException {
		// Construcción del parser SAX activando la validación
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		
		// Al construir el parser hay que añadir un manejador de errores
		SAXParser parser = parserFactory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(handler);
		xmlReader.setErrorHandler(new SimpleErrorHandler());

		// Parsing
		try (FileReader fileReader = new FileReader(new File(xmlPath))) {
			xmlReader.parse(new InputSource(fileReader));
		}
	}
	
	public static void parseAndValidateWithInternalXSD(
		String xmlPath, ContentHandler handler
	) throws ParserConfigurationException, SAXException, IOException {
		// Construcción del parser del documento. Se activa
		// la validación y comprobación de namespaces
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		parserFactory.setNamespaceAware(true);

		// Se añade el manejador de errores y se activa la validación por schema
		SAXParser parser = parserFactory.newSAXParser();
		parser.setProperty(
			"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
			XMLConstants.W3C_XML_SCHEMA_NS_URI
		);
		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(handler);
		xmlReader.setErrorHandler(new SimpleErrorHandler());

		// Parsing
		try (FileReader fileReader = new FileReader(new File(xmlPath))) {
			xmlReader.parse(new InputSource(fileReader));
		}
	}
	
	public static void parseAndValidateWithExternalXSD(
		String xmlPath, String schemaPath, ContentHandler handler
	) throws ParserConfigurationException, SAXException, IOException {
		// Construcción del schema
		SchemaFactory schemaFactory = 
			SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new File(schemaPath));
		
		// Construcción del parser del documento. Se establece el esquema y se activa
		// la validación y comprobación de namespaces
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(false);
		parserFactory.setNamespaceAware(true);
		parserFactory.setSchema(schema);

		// Se añade el manejador de errores
		SAXParser parser = parserFactory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(handler);
		xmlReader.setErrorHandler(new SimpleErrorHandler());

		// Parsing
		try (FileReader fileReader = new FileReader(new File(xmlPath))) {
			xmlReader.parse(new InputSource(fileReader));
		}
	}
}
