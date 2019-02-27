package main;

class ListaFifo extends Lista {

    ListaFifo() {
        super();
    }

    ListaFifo(Lista lista) {
        super(lista);
    }

    @Override
    public void insertar_elemento(NodoLista nodo) {
        if (first == null) {
            first = nodo;
            last = nodo;
        } else {
            last.next = nodo;
            nodo.back = last;
            last = nodo;
        }
        this.cantidad_elementos++;
    }
} //end Lista class

