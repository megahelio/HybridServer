package es.uvigo.esei.dai.hybridserver;

import java.util.LinkedHashMap;
import java.util.Map;

public class DaoMap implements Dao {

    private Map<String, String> mapa;

    public DaoMap(Map<String, String> pages) {
        mapa = new LinkedHashMap<>(pages);
    }

    public DaoMap() {
        mapa = new LinkedHashMap<>();
    }

    @Override
    public String addPage(String content) {
        String toret = UUIDgenerator.generate();
        mapa.put(UUIDgenerator.generate(), content);
        return toret;
    }

    @Override
    public void deletePage(String id) {
        mapa.remove(id);
    }

    @Override
    public String listPages() {
        return mapa.keySet().toString();
    }

    @Override
    public String get(String id) {
        return mapa.get(id);
    }

    @Override
    public boolean exist(String id) {
        return mapa.containsKey(id);
    }

}
