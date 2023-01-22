package es.uvigo.esei.dai.hybridserver;

import javax.jws.WebMethod;
import javax.jws.WebService;

//Se definen todas la acciones que se pueden hacer en cada servidor individualmente
@WebService
public interface HybridServerService {
    // HTML
    @WebMethod
    public String addPageHTML(String content);

    @WebMethod
    public void deletePageHTML(String id);

    @WebMethod
    public String listPagesHTML();

    @WebMethod
    public String getHTML(String id);

    // XML
    @WebMethod
    public String addPageXML(String content);

    @WebMethod
    public void deletePageXML(String id);

    @WebMethod
    public String listPagesXML();

    @WebMethod
    public String getXML(String id);

    // XSD
    @WebMethod
    public String addPageXSD(String content);

    @WebMethod
    public void deletePageXSD(String id);

    @WebMethod
    public String listPagesXSD();

    @WebMethod
    public String getXSD(String id);

    // XSLT
    @WebMethod
    public String addPageXSLT(String content);

    @WebMethod
    public void deletePageXSLT(String id);

    @WebMethod
    public String listPagesXSLT();

    @WebMethod
    public String getXSLT(String id);

    @WebMethod
    public String getXSDofaXSLT(String id);

}