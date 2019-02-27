package holamundo;


class PoligonoConvexo extends Poligono {

    private float sumaTotal = 0;

    PoligonoConvexo(ListaFifo listaPto) {
        super(listaPto);
    }

    PoligonoConvexo(Punto p1, Punto p2, Punto p3) {
        super(p1, p2, p3);
    }

    PoligonoConvexo(Lista listaSeg) {
        super(listaSeg);
    }

    protected boolean contiene_punto(Punto q) {
        Punto p1, p2, prim;
        Geometricas geom = new Geometricas();

        it = new Iterador(listPto);
        this.cerrar_lista();
        prim = (Punto) it.Current();
        p1 = (Punto) it.Current();
        it.Next();
        p2 = (Punto) it.Current();
        do {
            if (!geom.lefton(p1, p2, q)) {
                this.abrir_lista();
                return false;
            }
            p1 = p2;
            it.Next();
            p2 = (Punto) it.Current();
        } while (p1 != prim);
        this.abrir_lista();

        return true;
    }

    protected ElementoGeometrico Clone() {
        return new PoligonoConvexo((ListaFifo) this.listPto);
    }

} //fin PoligonoConvexo class

