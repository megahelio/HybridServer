package es.uvigo.esei.dai.hybridserver;

import javax.jws.WebService;

import es.uvigo.esei.dai.hybridserver.dao.DaoHTML;
import es.uvigo.esei.dai.hybridserver.dao.DaoXML;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSD;
import es.uvigo.esei.dai.hybridserver.dao.DaoXSLT;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.HybridServerService")
public class HybridServerServiceImpl implements HybridServerService {

    DaoHTML daoHTML;
    DaoXML daoXML;
    DaoXSD daoXSD;
    DaoXSLT daoXSLT;

    public HybridServerServiceImpl(DaoHTML daoHTML, DaoXML daoXML, DaoXSD daoXSD, DaoXSLT daoXSLT) {
        this.daoHTML = daoHTML;
        this.daoXML = daoXML;
        this.daoXSD = daoXSD;
        this.daoXSLT = daoXSLT;
    }

    // HTML
    @Override
    public String addPageHTML(String content) {

        return daoHTML.addPage(content);
    }

    @Override
    public void deletePageHTML(String id) {
        daoHTML.deletePage(id);
    }

    @Override
    public String listPagesHTML() {

        return daoHTML.listPages();
    }

    @Override
    public String getHTML(String id) {
        return daoHTML.get(id);
    }

    // XML
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
        return daoXML.listPages();
    }

    @Override
    public String getXML(String id) {
        return daoXML.get(id);
    }

    // XSD
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
        return daoXSD.listPages();
    }

    @Override
    public String getXSD(String id) {
        return daoXSD.get(id);
    }

    // XSLT
    @Override
    public String addPageXSLT(String content) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deletePageXSLT(String id) {
        daoXSLT.deletePage(id);
    }

    @Override
    public String listPagesXSLT() {

        return daoXSLT.listPages();
    }

    @Override
    public String getXSLT(String id) {
        return daoXSLT.getContent(id);
    }

    @Override
    public String getXSDofaXSLT(String id) {
        return daoXSLT.getXSD(id);
    }

    public void close() {

    }

}
