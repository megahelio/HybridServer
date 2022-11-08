package es.uvigo.esei.dai.hybridserver.DaoImplementations;

import java.util.LinkedHashMap;
import java.util.Map;

import es.uvigo.esei.dai.hybridserver.DaoInterface;
import es.uvigo.esei.dai.hybridserver.UUIDgenerator;

public class DaoMapper implements DaoInterface{

    private static Map<String, String> mapa;

    public DaoMapper(Map<String, String> pages) {
        System.out.println("Creando DaoMapper");
        mapa = new LinkedHashMap<>(pages);
    }

    public DaoMapper() {
        System.out.println("Creando DaoMapper");
        mapa = new LinkedHashMap<>();
    }

    @Override
    public String addPage(String content) {
        System.out.println("DaoMapper Addpage Start");
        String toret = UUIDgenerator.generate();
        mapa.put(UUIDgenerator.generate(), content);
        System.out.println("DaoMapper Addpage End");
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