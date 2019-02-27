package main;

import java.awt.*;

class Triangulo extends PoligonoConvexo {

    private Punto _centroid;
    private Circulo c;      //Circulo que forman los vertices del triangulo
    private Triangulo next, back;
    Triangulo[] vecinos;
    Textura[] texturas;
    public Punto[] vertices;
    private int[] neighbors;
    Segmento[] edges;
    int[] coords;
    protected static int lastidt;
    protected int idt;
    private boolean ficticio, selected;

    public void imprimirVecinos() {
        for (int i = 0; i < vecinos.length; i++) {
            if (vecinos[i] != null) {
                System.out.println(vecinos[i].idt - 1);
            }
        }
    }

    Triangulo(Punto p1, int coord1, Punto p2, int coord2, Punto p3, int coord3) {

        super(p1, p2, p3);
        vecinos = new Triangulo[3];
        neighbors = new int[3];
        vertices = new Punto[6];
        coords = new int[3];
        texturas = new Textura[3];

        vertices[0] = p1;
        vertices[1] = p2;
        vertices[2] = p3;

        coords[0] = coord1;
        coords[1] = coord2;
        coords[2] = coord3;

        texturas[0] = null;
        texturas[1] = null;
        texturas[2] = null;

        vecinos[0] = null;
        vecinos[1] = null;
        vecinos[2] = null;

        resetNeighbors();//Nani??? por que se reinician los vecinos
        edges = new Segmento[3];
        //setSegmentos();
        fixEdges();
        idt = 0;
        next = back = null;
        ficticio = false;
        selected = false;
    }

    public void setTexturas(Textura textura, int id) {

        this.texturas[id] = textura;

    }

    public void setTexturasArray(Textura[] textura) {

        this.texturas = textura;

    }

    public Textura crearTextura(int a, int b) {

        System.out.println("vertices Arista: " + a + "/" + b);
        Textura text;
        int sizeCoordenada = Ventana.archivoMallaRostro.listaTexCoords.size() + 1;

        //null pointer
        
        System.out.println("posible null T:" + this.idt );
        
        float texturaX = this.texturas[a].textX;
        float texturaY = this.texturas[a].textY;

        float texturaX2 = texturas[b].textX;
        float texturaY2 = texturas[b].textY;

        float promX, promY;

        promX = (texturaX + texturaX2) / 2;
        promY = (texturaY + texturaY2) / 2;

        text = new Textura(promX, promY);
        text.idText = sizeCoordenada;

        Ventana.archivoMallaRostro.listaTexCoords.add(text);

        return text;
    }

    Triangulo(Punto p1, Punto p2, Punto p3) {
        super(p1, p2, p3);
        vecinos = new Triangulo[3];
        neighbors = new int[3];
        vertices = new Punto[3];

        vertices[0] = p1;
        vertices[1] = p2;
        vertices[2] = p3;

        vecinos[0] = null;
        vecinos[1] = null;
        vecinos[2] = null;

        resetNeighbors();//Nani??? por que se reinician los vecinos
        edges = new Segmento[3];
        //setSegmentos();
        fixEdges();
        idt = 0;
        next = back = null;
        ficticio = false;
        selected = false;
    }

    public void setFicitio(boolean fict) {
        ficticio = fict;
    }

    public boolean isVirtual() {
        return ficticio;
    }

    public void setSelected(boolean sel) {
        selected = sel;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setCentroid() {
        Punto p;
        float x, y;

        x = (vertices[0].CordX() + vertices[1].CordX() + vertices[2].CordX()) / 3.0f;
        y = (vertices[0].CordY() + vertices[1].CordY() + vertices[2].CordY()) / 3.0f;
        _centroid = new Punto(x, y);
    }

    public Punto centroid() {
        return _centroid;
    }

    public static void setLastIdt(int lidt) {
        lastidt = lidt;
    }

    public static int getLastIdt() {
        return lastidt;
    }

    public void setIdt(int id) {
        idt = id;
    }

    public int getIdt() {
        return idt;
    }

    public Punto vertex(int i) {
        return vertices[i];
    }

    protected void Dibujar(Graphics g, Color c, boolean trimage) {
        construir_lista_puntos(vertices[0], vertices[0], vertices[0]);
        construir_lista_segmentos();
        setSegmentos();

        int[] x = new int[3];
        int[] y = new int[3];
        x[0] = (int) vertex(0).CordX();
        y[0] = (int) vertex(0).CordY();

        x[1] = (int) vertex(1).CordX();
        y[1] = (int) vertex(1).CordY();

        x[2] = (int) vertex(2).CordX();
        y[2] = (int) vertex(2).CordY();

        if (!trimage) {
            g.drawLine((int) vertex(0).CordX(), (int) vertex(0).CordY(), (int) vertex(1).CordX(), (int) vertex(1).CordY());
            g.drawLine((int) vertex(1).CordX(), (int) vertex(1).CordY(), (int) vertex(2).CordX(), (int) vertex(2).CordY());
            g.drawLine((int) vertex(2).CordX(), (int) vertex(2).CordY(), (int) vertex(0).CordX(), (int) vertex(0).CordY());
        }

        g.setColor(c);
        g.fillPolygon(x, y, 3);
        //System.out.println("Pintando triángulos");

        if (!trimage) {
            this.Dibujar(g);
        }
    }

    //--------- buscar_vertices_opuestos-----------------------
    //Retorna los dos vertices de este triangulo (this) que son distintos
    //al punto dado
    //--------------------------------------------------
    public Punto buscar_vertices_opuestos(Punto punto_elegido) {
        Punto vertice_opuesto = null;

        if ((this.vertices[0].x == punto_elegido.x)
                && (this.vertices[0].y == punto_elegido.y)) {

            vertice_opuesto = this.vertices[1];
            vertice_opuesto.next = this.vertices[2];

        } else if ((this.vertices[1].x == punto_elegido.x)
                && (this.vertices[1].y == punto_elegido.y)) {
            vertice_opuesto = this.vertices[0];
            vertice_opuesto.next = this.vertices[2];

        } else if ((this.vertices[2].x == punto_elegido.x)
                && (this.vertices[2].y == punto_elegido.y)) {
            vertice_opuesto = this.vertices[0];
            vertice_opuesto.next = this.vertices[1];

        }

        return vertice_opuesto;
    }

    void setSegmentos() {
        int i = 0;
        Iterador it = new Iterador(listSeg);
        while (!it.IsDone()) {
            Segmento seg = (Segmento) it.Current();
            edges[i] = seg;
            i++;
            it.Next();
        }
    }

//----- Calcula test del circulo entre dos triangulos -----
    public boolean Test_del_Circulo(Punto punto) {
        float distancia;

        distancia = c.Calcula_Radio(punto);

        if (distancia <= c.radio) {
            return false;
        } else {
            return true;
        }
    }

    public boolean incircle(Punto vd) {
        double adx, bdx, cdx, ady, bdy, cdy;
        double bdxcdy, cdxbdy, cdxady, adxcdy, adxbdy, bdxady;
        double alift, blift, clift;
        double det;

        //Point<3,double>	pa, pb, pc, pd;
        double[] pa = new double[2];
        double[] pb = new double[2];
        double[] pc = new double[2];
        double[] pd = new double[2];

        Punto va = this.vertex(0);
        Punto vb = this.vertex(1);
        Punto vc = this.vertex(2);

        pa[0] = va.CordX();
        pa[1] = va.CordY();

        pb[0] = vb.CordX();
        pb[1] = vb.CordY();

        pc[0] = vc.CordX();
        pc[1] = vc.CordY();

        pd[0] = vd.CordX();
        pd[1] = vd.CordY();

        adx = pa[0] - pd[0];
        bdx = pb[0] - pd[0];
        cdx = pc[0] - pd[0];
        ady = pa[1] - pd[1];
        bdy = pb[1] - pd[1];
        cdy = pc[1] - pd[1];

        bdxcdy = bdx * cdy;
        cdxbdy = cdx * bdy;
        alift = adx * adx + ady * ady;

        cdxady = cdx * ady;
        adxcdy = adx * cdy;
        blift = bdx * bdx + bdy * bdy;

        adxbdy = adx * bdy;
        bdxady = bdx * ady;
        clift = cdx * cdx + cdy * cdy;

        det = alift * (bdxcdy - cdxbdy)
                + blift * (cdxady - adxcdy)
                + clift * (adxbdy - bdxady);

        //if ( det  <= 0.0  ) return true;
        if (det <= 0.0) {
            return true; // Si cumple el test de Delaunay
        }
        return false; // Si no cumple el test
    }

    /*--------- vertices_iguales-----------------------
	Retorna TRUE si las coordenadas x e y del vertice v1 son
	iguales a las coordenadas x e y del punto p
	--------------------------------------------------*/
    public boolean vertices_iguales(Punto v1, Punto v2) {

        if ((v1.x == v2.x) && (v1.y == v2.y)) {
            return true;
        }
        return false;
    }

    Segmento edge(int i) {
        return edges[i];
    }

    void fixEdges() {
        for (int i = 0; i < 3; i++) {
            edges[i] = new Segmento(vertices[(i + 1) % 3], vertices[(i + 2) % 3]);
        }
    }

    int GetNeighborIndex(Triangulo triangle) {
        assert triangle != null;

        int index = -1;
        for (int i = 0; i < 3; i++) //Caras
        {
            for (int j = 0; j < 3; j++) {
                //Aquí comparar las caras de los dos triángulos.
                //Now, we compare the faces of triangles.
                //Compare face i of 'this' triangle and face j of 'Triangle'

                if (faceToface(i, j, triangle)) {
                    index = i;

                    return index;
                }
            }
        }

        return -1;
    }

    /*int getNeighborIndex(Triangulo triangle) {
        return neighborIndex(triangle);
    }*/
    //This method determines if 'triangle' is a 'this' auxIdT's neighbor triangle
    int neighborIndex(Triangulo triangle) {
        assert triangle != null;

        int index = -1;
        for (int i = 0; i < 3; i++) {
            boolean shared = false;
            for (int j = 0; j < 3; j++) {
                //if (_vertices[i] == triangle->_vertices[j])
                if ((vertices[i].CordX() == triangle.vertices[j].CordX())
                        && (vertices[i].CordY() == triangle.vertices[j].CordY())) {
                    shared = true;
                    break;
                }
            }

//            assert !shared && (index == -1) || shared;
            if (!shared) {
                index = i;
            }
        }

        assert (index != -1) && (0 <= index) && (index < 3);

        return index;
    }

    boolean addNeighbor(Triangulo neighbor) {
        //int index = getNeighborIndex(neighbor);
        int index = neighborIndex(neighbor);
        if (index != -1) {
            vecinos[index] = neighbor;
            //neighbors[index] = neighbor.getIdt();
            return true;
        }

        return false;
    }

    //retorna el triangulo vecino de la arista mas larga
    public Triangulo neighbor(int i) {
        return vecinos[i];
    }

    int vecinoidx(int idx) {
        return neighbors[idx];
    }

    void setVertex(int i, Punto vertex) {
        vertices[i] = vertex;
        fixEdges();
    }

    float Orient2D(Punto v0, Punto v1, Punto v2) {
        return (v0.CordX() - v2.CordX()) * (v1.CordY() - v2.CordY()) - (v0.CordY() - v2.CordY()) * (v1.CordX() - v2.CordX());
    }

    void resetNeighbors() {
        //crea un arreglo de 3 vacio 
        for (int i = 0; i < 3; i++) {
            vecinos[i] = null;
            neighbors[i] = 0;
        }
    }

    int longestEdgeIndex() {
        double max = -1.0;
        int index = -1;
        System.out.println("ID Actual: " + this.idt);
        for (int i = 0; i < 3; i++) {//de 0 a 2
            Segmento e = this.edge(i);
            double dist = e.distance();
            if (max < dist) {
                max = dist;
                index = i;
            }
        }
        assert -1 != index;
        return index;//que indice es el que retorna???
    }

    public Segmento longestEdge() {
        return edges[longestEdgeIndex()];
    }

    public Triangulo longestEdgeNeighbor() {
        int index = this.longestEdgeIndex();
        System.out.println("Vecino Arista mas larga:" + index);
        return this.neighbor(index);//Busca el triangulo vecino con la arista mas larga
        //se requieren los vecinos!!!            
    }

    public boolean isTerminal() {
        Triangulo neighbor = this.longestEdgeNeighbor();
        if (neighbor == null) {
            return true;
        }

        neighbor = neighbor.longestEdgeNeighbor();

        return (this == neighbor);
    }

    public boolean isTerminal(Triangulo neighbor) {
        if (neighbor == null) {
            return true;
        }

        Triangulo newNeighbor = neighbor.longestEdgeNeighbor();

        return (this == newNeighbor);
    }

    boolean TestVertexInTriangle(Punto q) {
        if (Orient2D(vertices[0], vertices[1], q) < 0) {
            return false;
        }
        if (Orient2D(vertices[1], vertices[2], q) < 0) {
            return false;
        }
        if (Orient2D(vertices[2], vertices[0], q) < 0) {
            return false;
        }

        return true;
    }

    // Return the index of edge into a 'this' triangle where q is collinear
    int coLlinear(Punto q) {
        if (Orient2D(edges[0].a, edges[0].b, q) == 0.0) {
            return 0;
        }
        if (Orient2D(edges[1].a, edges[1].b, q) == 0.0) {
            return 1;
        }
        if (Orient2D(edges[2].a, edges[2].b, q) == 0.0) {
            return 2;
        }

        return -1;
    }

    Triangulo bisect(Punto newVertex) {
        int index = this.longestEdgeIndex();

        Punto v1 = this.vertex(index); //vértice opuesto a arista
        Punto v2 = newVertex; //El nuevo vértice
        Punto v3 = this.vertex((index + 2) % 3);

        Triangulo newTriangle = new Triangulo(v1, v2, v3);

        this.setVertex((index + 2) % 3, newVertex); //Modifica triángulo para que se transforme en nuevo
        Triangulo neighbor = this.neighbor((index + 1) % 3);

        if (neighbor != null) { //¿Tiene vecino?
            newTriangle.addNeighbor(neighbor); //cuidado!, que el vecino neighbor podría estar en manos
            neighbor.addNeighbor(newTriangle); //de otro thread!!.
        }

        newTriangle.addNeighbor(this);
        this.addNeighbor(newTriangle);

        return newTriangle;
    }

    Triangulo bisect(Punto newVertex, int index) {
        Punto v1 = this.vertex(index); //vértice opuesto a arista
        Punto v2 = newVertex; //El nuevo vértice
        Punto v3 = this.vertex((index + 2) % 3);

        Triangulo newTriangle = new Triangulo(v1, v2, v3);

        this.setVertex((index + 2) % 3, newVertex); //Modifica triángulo para que se transforme en nuevo
        Triangulo neighbor = this.neighbor((index + 1) % 3);

        if (neighbor != null) { //¿Tiene vecino?
            newTriangle.addNeighbor(neighbor); //cuidado!, que el vecino neighbor podría estar en manos
            neighbor.addNeighbor(newTriangle); //de otro thread!!.
        }

        newTriangle.addNeighbor(this);
        this.addNeighbor(newTriangle);

        return newTriangle;
    }

    // Los siguientes métodos deben ir en la clase Triangle (o Triangulo)
    /**
     * ************ SETEAMOS LOS VECINOS ******************
     */
    boolean setNeighbor(Triangulo neighbor) {

        //System.out.println("Dentro de setNeighbors");
        assert (null != neighbor);

        int index = getNeighborIndex(neighbor);
        if (index != -1) {
            //System.out.println("index:" + index);
            vecinos[index] = neighbor;
            //System.out.println("Vecino Encontrado!!");
            return true;
        }
        return false;
    }

    int getNeighborIndex(Triangulo triangle) {
        assert (triangle.idt != 0);

        int index = -1;
        for (int i = 0; i < 3; i++) //Caras o aristas
        {
            for (int j = 0; j < 3; j++) {
                if (faceToface(i, j, triangle)) {
                    index = i;

                    return index;
                }
            }
        }

        return -1;
    }

    boolean faceToface(int edgei, int edgej, Triangulo t) {
        if (((edges[edgei].initial().ElementosIguales(t.edge(edgej).initial())
                && (edges[edgei].terminal().ElementosIguales(t.edge(edgej).terminal()))))
                || ((edges[edgei].initial().ElementosIguales(t.edge(edgej).terminal()))
                && (edges[edgei].terminal().ElementosIguales(t.edge(edgej).initial())))) {
            //System.out.println("True vecino");
            return true;
        }
        //System.out.println("false vecino");
        return false;
    }

    // El arreglo _neighbors[index] es un arreglo de punteros y almacena las direcciones de memoria 
// de los triángulos vecinos.
}
