package es.uvigo.esei.dai.hybridserver;

public interface DaoInterface {

    public String addPage(String content);

    public void deletePage(String id);

    public String listPages();

    public String get(String id);

    public boolean exist(String id);
}
