package holamundo;

class NodoLista {

    ElementoGeometrico eg;
    NodoLista next;
    NodoLista back;

    NodoLista(ElementoGeometrico _eg) {
        eg = _eg;
        back = null;
        next = null;
    }
} //fin NodoLista class

