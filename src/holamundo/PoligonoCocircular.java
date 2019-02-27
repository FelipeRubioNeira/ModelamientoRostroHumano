package holamundo;

import java.lang.Object.*;
import java.util.*;

class  PoligonoCocircular extends PoligonoConvexo {
   protected Circulo  c;
   private   int sumaTotal;
   protected Punto puntoa, puntob, puntoc;

   PoligonoCocircular(ListaFifo listaPto) {
	super(listaPto);
	iniciar_puntos();
	c = new Circulo(puntoa,puntob,puntoc);
	calcularRadioInicial();
    }

   PoligonoCocircular(Punto p1, Punto p2, Punto p3) {
	super(p1,p2,p3);
	iniciar_puntos();
	puntoa = p1;
	puntob = p2;
	puntoc = p3;
    	c = new Circulo(puntoa, puntob, puntoc);
	calcularRadioInicial();
    }

   PoligonoCocircular (Lista listaSeg) {
	super(listaSeg);
	iniciar_puntos();
	c = new Circulo(puntoa,puntob,puntoc);
	calcularRadioInicial();
    }

   private void iniciar_puntos() {
	it = new Iterador(listPto);
	puntoa = (Punto)it.Current(); 
	it.Next();
	puntob = (Punto)it.Current();
	it.Next();
	puntoc = (Punto)it.Current();
	it.Set();
    }

  private void calcularRadioInicial() {
	sumaTotal += c.Calcula_Radio((Punto)puntoa);
	sumaTotal += c.Calcula_Radio((Punto)puntob);
	sumaTotal += c.Calcula_Radio((Punto)puntoc);
	c.radio = sumaTotal/3.0f;
    }

  protected ElementoGeometrico Clone() {
	return new PoligonoCocircular((ListaFifo)this.listPto);
  }

} //fin PoligonoCocircular class

