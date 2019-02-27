package holamundo;

import java.awt.*;
import java.lang.Object.*;

class ListaOrdenada extends Lista{
   ListaOrdenada() {  //Constructor
       super();
    }

   ListaOrdenada(Lista lista) { //Constructor de copia
       super(lista);
    }

   public void insertar_elemento(NodoLista nodo){
    NodoLista n1, n2;
    Punto     p,q;

    n1 = n2 = this.first;
    if (n1 == null) {
	  this.first = nodo;

	  cantidad_elementos++;
      }
     else {
      p = (Punto)n2.eg;
	q = (Punto)nodo.eg;
      if (p.CordX() > q.CordX()) {
	    nodo.next = this.first;
          this.first = nodo;
	    cantidad_elementos++;
        }
       else {
        if ( !(p.ElementosIguales((Punto)q)) ) {
            n2 = n2.next;
	      while (n2 != null) {
	          p = (Punto)n2.eg;
                if (p.CordX() < q.CordX()) {
                   n1 = n2;
                   n2 = n2.next;
                 }
                else
		        break;
		  }
		 if ( !(p.ElementosIguales((Punto)q)) ) {
			nodo.next = n1.next;
              	n1.next = nodo;
	        	cantidad_elementos++;
	         }
		  else System.out.println("Punto Repetido");
	    }
	   else System.out.println("Punto Repetido");
       }
     }
   }

 } //Fin ListaOrdenada class


