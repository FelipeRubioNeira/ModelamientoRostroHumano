package holamundo;

import java.util.ArrayList;

class TriMesh {

    ListaFifo lt, laux, selectedTriangles;
    ListaFifo lp;
    //ListaTriangulos[] listOfTriangles;
    int countsel;
    int _np;
    float xmin, xmax, ymin, ymax, zmin, zmax;
    ArrayList<Punto> listaPuntos3D = new ArrayList<>();

    TriMesh() {
        lt = new ListaFifo();
        lp = new ListaFifo();
        selectedTriangles = new ListaFifo();
        xmin = xmax = ymin = ymax = zmin = zmax = -1;
    }

    public void addTriangle(Triangulo triangle) {
        assert triangle != null;

        //lt.append(triangle);
        NodoLista nodo = new NodoLista((ElementoGeometrico) triangle);
        triangle.setNodo(nodo);
        lt.insertar_elemento(nodo);
    }

    public void addVertex(Punto p) {
        assert p != null;

        //lp.append(p);
        NodoLista nodo = new NodoLista(p);
        p.setNodo(nodo);
        lp.insertar_elemento(nodo);
    }

    //Metodo para crear punto 3D
    public void addVertex3D(Punto p) {
        assert p != null;

        //lp.append(p);
        NodoLista nodo = new NodoLista(p);
        p.setNodo(nodo);
        lp.insertar_elemento(nodo);
        this.listaPuntos3D.add(p);
    }

    public void addVertex(NodoLista nodo) {
        assert nodo != null;

        Punto eg = (Punto) nodo.eg;
        eg.setNodo(nodo);
        lp.insertar_elemento(nodo);
    }

    public ListaFifo triangles() {
        return lt;
    }

    public ListaFifo striangles() {
        //retorna los triangulos seleccionados
        return selectedTriangles;
    }

    public ListaFifo vertices() {
        return lp;
    }

    public void clearMesh() {
        lt.clear();
        lp.clear();
        selectedTriangles.clear();
    }

    public void printMinMax() {
        System.out.println("( X : " + this.retXmin() + ", " + this.retXmax() + ")");
        System.out.println("( Y : " + this.retYmin() + ", " + this.retYmax() + ")");
        System.out.println("( Z : " + this.retZmin() + ", " + this.retZmax() + ")");
    }

    void setMinMax(float xmi, float xma, float ymi, float yma, float zmi, float zma) {
        xmin = xmi;
        xmax = xma;
        ymin = ymi;
        ymax = yma;
        zmin = zmi;
        zmax = zma;

    }

    float retXmin() {
        return xmin;
    }

    float retXmax() {
        return xmax;
    }

    float retYmin() {
        return ymin;
    }

    float retYmax() {
        return ymax;
    }

    float retZmin() {
        return zmin;
    }

    float retZmax() {
        return zmax;
    }

    int countSelected() {
        return countsel;
    }

}
