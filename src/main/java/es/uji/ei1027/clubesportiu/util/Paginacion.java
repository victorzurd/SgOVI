package es.uji.ei1027.clubesportiu.util;

public class Paginacion {

    private int page = 1;
    private int size = 10;
    private String buscar = "";

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getBuscar() {
        return buscar;
    }

    public void setBuscar(String buscar) {
        this.buscar = buscar;
    }

    public int getOffset() {
        return (page - 1) * size;
    }
}