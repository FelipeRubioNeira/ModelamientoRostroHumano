package main;

class Iterador {

    protected NodoLista current;
    protected NodoLista postcurrent;
    protected NodoLista backcurrent;
    protected NodoLista head;
    
    //Recibe una lista como parametro
    Iterador(Lista lista) {
        current = lista.first; //Primer elemento de la lista
        backcurrent = lista.first;
        postcurrent = lista.first;
        head = lista.first;
    }

    Iterador(NodoLista nodo) {
        current = nodo;
        backcurrent = nodo;
        postcurrent = nodo;
        head = nodo;
    }

    public void Next() {
        backcurrent = current;
        current = current.next;
    }

    public void Back() {
        backcurrent = current;
        current = current.back;
    }

    public ElementoGeometrico Current() {
        if (!IsDone()) {//Si es null
            return current.eg;
        } else {
            return (ElementoGeometrico) null;
        }
    }

    public ElementoGeometrico PostCurrent() {
        postcurrent = postcurrent.next;
        if (postcurrent != null) {
            return postcurrent.eg;
        } else {
            return (ElementoGeometrico) null;
        }
    }

    public ElementoGeometrico BackCurrent() {
        backcurrent = backcurrent.back;
        if (backcurrent != null) {
            return backcurrent.eg;
        } else {
            return (ElementoGeometrico) null;
        }
    }

    public NodoLista ValueCurrent() {
        return current;
    }

    public NodoLista ValueBackCurrent() {
        return backcurrent;
    }

    public NodoLista ValuePostCurrent() {
        return postcurrent;
    }

    public void Set() {
        current = head;
        backcurrent = head;
    }

    public void Set(NodoLista new_head) {
        current = new_head;
        backcurrent = new_head;
        head = new_head;
    }

    public void ResetCurrent() {
        current = backcurrent;
    }

    public void SetPostCurrent() {
        postcurrent = current;
    }

    public boolean IsDone() {
        return current == null;
    }
} //end Iterador class

