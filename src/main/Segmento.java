package main;

class Segmento extends ElementoGeometrico {

    Punto a;
    Punto b;
    Punto z;
    int num_seg;

    Segmento() {
        a = null;
        b = null;
        z = null;
    }

    Segmento(Punto _prim, Punto _ult) {
        a = _prim;
        b = _ult;
    }

    Punto initial() {
        return a;
    }

    Punto terminal() {
        return b;
    }

    double distance() {
        float dis = (b.CordX() - a.CordX()) * (b.CordX() - a.CordX()) + (b.CordY() - a.CordY()) * (b.CordY() - a.CordY()) + (b.CordZ() - a.CordZ()) * (b.CordZ() - a.CordZ());

        return Math.sqrt((double) dis);
    }

    // biseccion: entrega el punto medio de este lado.
    Punto biseccion() {
        return new Punto((a.CordX() + b.CordX()) / 2, (a.CordY() + b.CordY()) / 2,(a.CordZ() + b.CordZ()) / 2);
    }

} //fin Segmento class

