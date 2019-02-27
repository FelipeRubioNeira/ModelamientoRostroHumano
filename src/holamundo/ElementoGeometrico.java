package holamundo;

import java.lang.*;
import java.awt.*;

class ElementoGeometrico {
        final int   ENNUBE   	= 0;
     	final int   NOENNUBE   	= 1;
     	final int   COCIRCULAR 	= 2;
     	final int   OUTCIRCULO 	= 3;
     	final int   INCIRCULO  	= 4;
     	final int   CONCAVO 	= 5;
     	final int   NOCONCAVO 	= 6;
     	final float ERROR		= (float)1.0; 
	boolean	igual;
	protected   ElementoGeometrico egclone;
	protected   Color	color;
	protected	NodoLista	nodo = null;

   	ElementoGeometrico() {}
	protected void Dibujar(Graphics g) {}
   	protected void Imprime_Elemento() {}
	protected void SetColor(Color _color) {color = _color;}
	protected boolean ElementosIguales(ElementoGeometrico eg) {return igual;}
	protected ElementoGeometrico Clone() {return egclone;}
	protected void setNodo(NodoLista n) {nodo = n;}
	protected NodoLista getNodo() {return nodo;}

 }//fin ElementoGeometrico class

