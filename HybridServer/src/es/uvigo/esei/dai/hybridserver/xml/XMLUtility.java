package es.uvigo.esei.dai.hybridserver.xml;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLUtility {

    public static boolean validateSchema(String xml, String xsd) {

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(new StringReader(xsd)));
            Validator validator = schema.newValidator();
            validator.setErrorHandler(new SimpleErrorHandler());
            validator.validate(new StreamSource(new StringReader(xml)));
            return true;

        } catch (SAXException | IOException e) {

            return false;
        }
    }

    public static String xmlToHtml(String xml, String xslt) {

        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new StringReader(xslt)));
            StringWriter writer = new StringWriter();
            transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
            return writer.toString();

        } catch (TransformerException e) {
            return null;
        }

    }

    // Carga y validación con XSD de un documento almacenado en un fichero con
    // DOM
    public static Document loadAndValidateWithInternalXSD(String documentPath)
            throws ParserConfigurationException, SAXException, IOException {
        // Construcción del parser del documento activando validación
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        factory.setAttribute(
                "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // Al construir el parser hay que añadir un manejador de errores
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new SimpleErrorHandler());
        // Parsing y validación del documento
        return builder.parse(new File(documentPath));
    }

    public static Document loadAndValidateWithExternalXSD(
            String documentPath, String schemaPath) throws ParserConfigurationException, SAXException, IOException {
        // Construcción del schema
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new File(schemaPath));
        // Construcción del parser del documento. Se establece el esquema y se
        // activa la validación y comprobación de namespaces
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setSchema(schema);
        // Se añade el manejador de errores
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new SimpleErrorHandler());
        return builder.parse(new File(documentPath));
    }

    public static void parseAndValidateWithExternalXSD(
            File xml, String schemaPath, ContentHandler handler)
            throws ParserConfigurationException, SAXException, IOException {
        String xmlPath = xml.getPath().toString();
        System.out.println(xmlPath + "   " + schemaPath);
        // Construcción del schema
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
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
