package holamundo;

//************ Circulo.java **************
import java.lang.*;
import java.awt.*;

class Circulo extends ElementoGeometrico {

   Punto centro;
   float radio;

   Circulo(Punto _p1, Punto _p2, Punto _p3) {
 	Calcular_Centro_Circulo(_p1, _p2, _p3);	
	radio = Calcula_Radio(_p1);
	color = Color.blue;
   }
   
   Circulo(Punto c, float r) {
 	centro = c;
 	radio  = r;
	color = Color.red;
   }  

   protected void Dibujar(Graphics g) {
	g.setColor(color);
      g.drawArc((int)(centro.CordX()-this.radio), (int)(centro.CordY()-this.radio), (int)(2*this.radio),(int)(2*this.radio),0,360);
   }

   public void Calcular_Centro_Circulo(Punto p1, Punto p2, Punto p3) {
      float a,a1,a2,x,y;

	a = (p2.CordY()-p3.CordY())*(p2.CordX()-p1.CordX()) - (p2.CordY()-p1.CordY())*(p2.CordX()-p3.CordX());
	a1= (p1.CordX()+p2.CordX())*(p2.CordX()-p1.CordX()) + (p2.CordY()-p1.CordY())*(p1.CordY()+p2.CordY());
	a2= (p2.CordX()+p3.CordX())*(p2.CordX()-p3.CordX()) + (p2.CordY()-p3.CordY())*(p2.CordY()+p3.CordY());

	x = (a1*(p2.CordY()-p3.CordY()) - a2*(p2.CordY()-p1.CordY()))/a/2;
	y = (a2*(p2.CordX()-p1.CordX()) - a1*(p2.CordX()-p3.CordX()))/a/2;
	centro = new Punto((float)x,(float)y);
    }


    public float Calcula_Radio(Punto p) {
       return (float)Math.sqrt((double)((centro.CordX() - p.CordX())*(centro.CordX() -p.CordX())+(centro.CordY() - p.CordY())*(centro.CordY() - p.CordY())));
     }

 } //Fin Circulo class




