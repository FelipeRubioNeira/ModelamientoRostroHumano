package main;

import java.lang.*;
import java.util.*;
import java.awt.*;

abstract class Poligono extends ElementoGeometrico {

    protected ListaFifo listPto;
    protected ListaFifo listSeg;
    protected Punto op1, op2, pto_eleg;
    protected Punto p1, p2, p3;
    protected NodoLista nodo_op1, nodo_op2;
    protected ListaFifo segAdy;
    protected Segmento seg;
    protected boolean in;
    protected Iterador it;
    protected int numpoints;
    protected int red, green, blue;

    Poligono() {
        op1 = null;
        op2 = null;
        pto_eleg = null;
        nodo_op1 = null;
        nodo_op2 = null;
        seg = null;
        listSeg = null;
        listPto = null;
        color = new Color(1.0f, 1.0f, 1.0f, 0.0f);
        numpoints = 0;
        red = green = blue = 1;
    }

    Poligono(ListaFifo listaPto) {
        listSeg = null;
        listPto = null;
        color = new Color(1.0f, 1.0f, 1.0f, 0.0f);

        listPto = new ListaFifo((ListaFifo) listaPto);
        construir_lista_segmentos();
        numpoints = 0;
        red = green = blue = 1;
    }

    Poligono(Lista listaSeg) {
        listSeg = null;
        listPto = null;
        color = new Color(1.0f, 1.0f, 1.0f, 0.0f);

        listSeg = new ListaFifo((ListaFifo) listaSeg);
        construir_lista_puntos();
        numpoints = 0;
        red = green = blue = 1;
    }

    Poligono(Punto p1_, Punto p2_, Punto p3_) {
        NodoLista nodo;
        Segmento seg;

        p1 = p1_;
        p2 = p2_;
        p3 = p3_;

        listSeg = null;
        listPto = null;
        color = new Color(1.0f, 1.0f, 1.0f, 0.0f);

        listPto = new ListaFifo();
        listPto.insertar_elemento(new NodoLista((Punto) p1));
        listPto.insertar_elemento(new NodoLista((Punto) p2));
        listPto.insertar_elemento(new NodoLista((Punto) p3));
        construir_lista_segmentos();
        numpoints = 3;
        red = green = blue = 1;
    }

    protected void construir_lista_segmentos() {
        Punto prim, ptoa, ptob;
        Segmento seg;

        listSeg = new ListaFifo();
        it = new Iterador(listPto);
        this.cerrar_lista();
        prim = (Punto) it.Current();
        do {
            ptoa = (Punto) it.Current();
            it.Next();
            ptob = (Punto) it.Current();
            seg = new Segmento(ptoa, ptob);
            listSeg.insertar_elemento(new NodoLista((Segmento) seg));
        } while ((Punto) it.Current() != prim);
        this.abrir_lista();

    }

    protected void construir_lista_puntos() {
        Segmento seg;
        numpoints = 0;

        listPto = new ListaFifo();
        it = new Iterador((ListaFifo) listSeg);
        while (!it.IsDone()) {
            seg = (Segmento) it.Current();
            listPto.insertar_elemento(new NodoLista((Punto) seg.a));
            numpoints++;
            it.Next();
        }
    }

    protected void construir_lista_puntos(Punto p1, Punto p2, Punto p3) {
        Segmento seg;
        numpoints = 0;

        listPto = new ListaFifo();
        listPto.insertar_elemento(new NodoLista(p1));
        listPto.insertar_elemento(new NodoLista(p2));
        listPto.insertar_elemento(new NodoLista(p3));
        numpoints += 3;
    }

    public void reconstruir_segmentos() {
        construir_lista_segmentos();
    }

    protected void setNewColor(Color col) {
        color = col;
    }

    protected void setRedRGB(int r) {
        red = r;
    }

    protected void setGreenRGB(int g) {
        green = g;
    }

    protected void setBlueRGB(int b) {
        blue = b;
    }

    protected int getRedRGB() {
        return red;
    }

    protected int getGreenRGB() {
        return green;
    }

    protected int getBlueRGB() {
        return blue;
    }

    protected Color getColor() {
        return color;
    }

    public ListaFifo getListaPuntos() {
        return listPto;
    }

    protected void Dibujar(Graphics g) {
        Segmento curSg;
        Punto a, b;

        it = new Iterador(listSeg);
        do {
            g.setColor(Color.blue);
            curSg = (Segmento) it.Current();
            a = (Punto) curSg.a;
            b = (Punto) curSg.b;
            g.drawLine((int) a.CordX(), (int) a.CordY(), (int) b.CordX(), (int) b.CordY());
            it.Next();
        } while (!it.IsDone());

        //this.Dibujar(g, color);
    }

    protected void Dibujar(Graphics2D g) {
        Segmento curSg;
        Punto a, b;

        it = new Iterador(listSeg);
        do {
            g.setColor(Color.blue);
            curSg = (Segmento) it.Current();
            a = (Punto) curSg.a;
            b = (Punto) curSg.b;
            g.drawLine((int) a.CordX(), (int) a.CordY(), (int) b.CordX(), (int) b.CordY());
            it.Next();
        } while (!it.IsDone());

        //this.Dibujar(g, color);
    }

    protected void Dibujar(Graphics g, Color c, boolean trimage) {
        Segmento curSg;
        Punto a, b;
        int i = 0;

        it = new Iterador(listSeg);
        //System.out.println("Creó el iterador de segmentos");
        int nump = listSeg.cantidad_elementos;
        //System.out.println("Cantidad de segmentos " + nump);
        int[] x = new int[nump];
        int[] y = new int[nump];
        do {
            curSg = (Segmento) it.Current();
            //System.out.println("Tomó un segmento");
            a = (Punto) curSg.a;
            b = (Punto) curSg.b;
            x[i] = (int) a.CordX();
            y[i] = (int) a.CordY();
            //g.drawLine((int)a.CordX(),(int)a.CordY(),(int)b.CordX(),(int)b.CordY());
            it.Next();
            i++;
        } while (!it.IsDone());
        g.setColor(c);
        g.fillPolygon(x, y, nump);
        //System.out.println("Salió dibujar los segmentos");

        this.Dibujar(g);
    }

    protected void Dibujar(Graphics2D g, Color c, boolean trimage) {
        Segmento curSg;
        Punto a, b;
        int i = 0;

        it = new Iterador(listSeg);
        //System.out.println("Creó el iterador de segmentos");
        int nump = listSeg.cantidad_elementos;
        //System.out.println("Cantidad de segmentos " + nump);
        int[] x = new int[nump];
        int[] y = new int[nump];
        do {
            curSg = (Segmento) it.Current();
            //System.out.println("Tomó un segmento");
            a = (Punto) curSg.a;
            b = (Punto) curSg.b;
            x[i] = (int) a.CordX();
            y[i] = (int) a.CordY();
            //g.drawLine((int)a.CordX(),(int)a.CordY(),(int)b.CordX(),(int)b.CordY());
            it.Next();
            i++;
        } while (!it.IsDone());
        g.setColor(c);
        g.fillPolygon(x, y, nump);
        //System.out.println("Salió dibujar los segmentos");

        this.Dibujar(g);
    }

    protected void Imprime_Elemento() {
        Punto n;

        it = new Iterador(listPto);
        System.out.println("--. Vertices del Poligono: ");

        while (!it.IsDone()) {
            n = (Punto) it.Current();
            System.out.println("--. Id vertice poligono: " + n.getIdv());
            n.Imprime_Elemento();
            System.out.println(" ");
            it.Next();
        }
    }

    public void abrir_lista() {
        listPto.last.next = null;
        if (listSeg != null && listSeg.first != null) {
            listSeg.last.next = null;
        }
    }

    public void cerrar_lista() {
        listPto.last.next = listPto.first;
        if (listSeg != null && listSeg.first != null) {
            listSeg.last.next = listSeg.first;
        }
    }

    protected boolean contiene_punto(Punto q) {
        return in;
    }

    public boolean ElementosIguales(ElementoGeometrico eg) {
        igual = false;
        System.out.println("Poligonos son distintos");

        return igual;
    }
} //fin Poligono class

