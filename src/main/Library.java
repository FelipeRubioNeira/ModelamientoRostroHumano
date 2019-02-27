package main;

import java.lang.Object.*;
import java.util.*;

class Library {

    //----- Test del circulo entre dos triangulos -----
    public boolean incircle(Triangulo t, Punto vd) {
        double adx, bdx, cdx, ady, bdy, cdy;
        double bdxcdy, cdxbdy, cdxady, adxcdy, adxbdy, bdxady;
        double alift, blift, clift;
        double det;

        //Point<3,double>	pa, pb, pc, pd;
        double[] pa = new double[2];
        double[] pb = new double[2];
        double[] pc = new double[2];
        double[] pd = new double[2];

        Punto va = t.vertex(0);
        Punto vb = t.vertex(1);
        Punto vc = t.vertex(2);

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

    float Orient2D(Punto v0, Punto v1, Punto v2) {
        return (v0.CordX() - v2.CordX()) * (v1.CordY() - v2.CordY()) - (v0.CordY() - v2.CordY()) * (v1.CordX() - v2.CordX());
    }

    //este metodo no es ocupado
    // Return the index of edge into a 'this' triangle where q is collinear
    int coLlinear(Triangulo t, Punto q) {
        if (Orient2D(t.edges[0].a, t.edges[0].b, q) == 0.0) {
            return 0;
        }
        if (Orient2D(t.edges[1].a, t.edges[1].b, q) == 0.0) {
            return 1;
        }
        if (Orient2D(t.edges[2].a, t.edges[2].b, q) == 0.0) {
            return 2;
        }

        return -1;
    }

    static public Triangulo bisect(Triangulo t, Punto newVertex) {

        int index = t.longestEdgeIndex();
        Textura nuevaTextura = null;

        Punto v1 = t.vertex(index); //vértice opuesto a arista
        Punto v2 = newVertex; //El nuevo vértice
        Punto v3 = t.vertex((index + 2) % 3);
             
        int verticeArista = (index + 2) % 3;

        //Creacion de la nueva textura en base a los vertices de la arista mas larga
        System.out.println("INDEX:" + index);
        switch (index) {
            case 0:
                nuevaTextura = t.crearTextura(verticeArista, 1);
                break;
            case 1:
                nuevaTextura = t.crearTextura(verticeArista, 2);
                break;
            case 2:
                nuevaTextura = t.crearTextura(verticeArista, 0);
                break;
        }
    
        
        //Aqui se crean los nuevo triangulos, tener ¡Cuidado! con las cordenads texturas
        Triangulo newTriangle = new Triangulo(v1, t.coords[0], v2, nuevaTextura.idText, v3, t.coords[2]);
        newTriangle.coords[1] = nuevaTextura.idText;
        
        newTriangle.texturas[0] = t.texturas[0];
        newTriangle.texturas[1] = nuevaTextura;
        newTriangle.texturas[2] = t.texturas[2];
        
        
        
        
        t.setVertex((index + 2) % 3, v2); //Modifica triángulo para que se transforme en nuevo, cambia su vertice        
        t.setTexturas(nuevaTextura, (index + 2) % 3);
        t.coords[1] = nuevaTextura.idText;
        t.texturas[1] = nuevaTextura;
        
        
        //REtorna vertices 0, pues aun no se le asigna su id
        System.out.println("(library)Triangulo Seleccionado: " + t.idt + " :v: " + t.vertices[0].idv + " " + t.vertices[1].idv + " " + t.vertices[2].idv);

        
        
        Triangulo neighbor = t.neighbor((index + 1) % 3);
        if (neighbor != null) { //¿Tiene vecino?
            newTriangle.addNeighbor(neighbor); //cuidado!, que el vecino neighbor podría estar en manos
            neighbor.addNeighbor(newTriangle); //de otro thread!!.
        }

        newTriangle.addNeighbor(t);
        t.addNeighbor(newTriangle);

        return newTriangle;
    }

}
