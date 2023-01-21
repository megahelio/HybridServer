package es.uvigo.esei.dai.hybridserver.webservice;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.dao.DaoHTML;
import es.uvigo.esei.dai.hybridserver.dao.DaoXML;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSD;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSLT;

@WebService(endpointInterface = "com.example.dai.TimeService")
public class WebServiceImplementation
        implements WebServiceInterface {

    DaoHTML daoHTML;
    DaoXML daoXML;
    DaoXSD daoXSD;
    DaoXSLT daoXSLT;

    public WebServiceImplementation(DaoHTML daoHTML, DaoXML daoXML, DaoXSD daoXSD, DaoXSLT daoXSLT) {
        this.daoHTML = daoHTML;
        this.daoXML = daoXML;
        this.daoXSD = daoXSD;
        this.daoXSLT = daoXSLT;
    }

    @Override
    public String addPageHTML(String content) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deletePageHTML(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public String listPagesHTML() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getHTML(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String addPageXML(String content) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deletePageXML(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public String listPagesXML() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getXML(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String addPageXSD(String content) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deletePageXSD(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public String listPagesXSD() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getXSD(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String addPageXSLT(String content) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deletePageXSLT(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public String listPagesXSLT() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getXSLT(String id) {
        // TODO Auto-generated method stub
        return null;
    }

}
