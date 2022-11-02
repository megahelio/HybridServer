package es.uvigo.esei.dai.hybridserver;

public interface Dao {

    public String addPage(String content);

    public void updatePage(String content);

    public void deletePage(String id);

    public String listPages();

    public Page get(String id);

    public boolean exist(String id);
}
