package holamundo;

import java.lang.*;

class Geometricas {
   private int   	COCIRCULAR     	= 2;
   private int   	OUTCIRCULO     	= 3;
   private int   	INCIRCULO      	= 4;
   private final float 	ERROR          	= (float)1.0;
   private float  	  	x_min=0.0f, x_max=0.0f, y_min=0.0f, y_max=0.0f;
   private Iterador	  	it;

   Geometricas () {}

    public ListaFifo ConvexHull(Lista lista) {
        float 	  angulo, angulo_menor, distancia, distMenor;
	  ListaFifo   list_hull;
        Punto       ptoa, ptob, HullPto=null, punto_final;
	  Vect        v1, v2, last_vector=null;

	  it 		   = new Iterador(lista);
	  list_hull    = new ListaFifo();
        angulo_menor = (float)(2.0f*3.1416f);
        punto_final  = (Punto)it.Current(); 
        ptoa         = (Punto)it.Current();
	  ptob = new Punto((float)ptoa.CordX(),(float)(ptoa.CordY() + 1.0f));
        v1 = new Vect((float)(ptoa.CordX()-ptob.CordX()),(float)(ptoa.CordY()-ptob.CordY()));
	  list_hull.insertar_elemento(new NodoLista((Punto)ptoa.Clone()));
	  ptob  	= (Punto)it.PostCurrent();
        distancia	= distMenor = Distancia(ptoa,ptob);
        do{
           while( ptob != null ) {
		   v2 = new Vect((float)(ptob.CordX()-ptoa.CordX()), (float)(ptob.CordY()-ptoa.CordY()));
               distancia  = Distancia(ptoa,ptob);
		   angulo     = (float)Math.acos((double)coseno(v1,v2));
               if ( angulo < angulo_menor ) {
                    angulo_menor = angulo;
			  HullPto      = ptob;
                	  last_vector  = v2;
		    }
		   else  
		     if (( angulo == angulo_menor ) && (distancia < distMenor)) {
                      	distancia = distMenor;
				HullPto      = ptob;
                		last_vector  = v2;
		      }
		   it.Next();
		   ptob = (Punto)it.Current();
	       }
	     lista.eliminar_elemento(HullPto);
	     if ( HullPto != punto_final ) {
	       list_hull.insertar_elemento(new NodoLista((Punto)HullPto.Clone()));
		}

           	v1 	= last_vector;
           	ptoa 	= HullPto;
	     	it 	= new Iterador(lista);
	     	ptob 	= (Punto)it.Current();
		angulo_menor = (float)(2.0f*3.1416f);
	}while ( HullPto != punto_final );

      return list_hull;
   }


  private float coseno(Vect v1, Vect v2) {
 	float      prod_punto, normaV1, normaV2;

	prod_punto = ProductoPunto(v1,v2);
      normaV1    = v1.Modulo();
      normaV2    = v2.Modulo();
      
	return (prod_punto/(normaV1*normaV2));
   }

 
  public void buscar_maximos_y_minimos(ListaOrdenada lista){
    Punto p1;

    it = new Iterador(lista);
    p1 = (Punto)it.Current();
    if ( it.IsDone() ) {
      x_max = y_min = y_max = 9000;
    }
    else {
   	x_min = (float)p1.CordX();
   	y_max = (float)p1.CordY();
   	y_min = (float)p1.CordY();
   	
	while ( !it.IsDone() ){
      	if ( p1.CordY() > y_max )
        	    y_max = (float)p1.CordY();
      	if ( p1.CordY() < y_min ) 
        	    y_min = (float)p1.CordY();
         
        	x_max = (float)p1.CordX();
		it.Next();
        	p1 = (Punto)it.Current();
	 } 
    }
  }
  
  public int Area2(Punto p, Punto q, Punto r) {
	return ((int)p.CordX()*(int)q.CordY() - (int)p.CordY()*(int)q.CordX() + (int)p.CordY()*(int)r.CordX() - (int)p.CordX()*(int)r.CordY() + (int)q.CordX()*(int)r.CordY() - (int)r.CordX()*(int)q.CordY());
   }


  public boolean left(Punto p, Punto q, Punto r) {
	return Area2(p,q,r) > 0;
   }

  public boolean lefton(Punto p, Punto q, Punto r) {
    return Area2(p,q,r) >= 0;
   }

  public boolean intersecta(Punto p,Punto q,Punto r,Punto s) {
     return ((left(p,q,r) && left(p,q,s)) || (left(q,p,r) && left(q,p,s)) || (left(r,s,p) && left(r,s,q)) || (left(s,r,p) && left(s,r,q)));
  
    }

 public boolean es_colineal(Punto p, Punto q, Punto s) {
	float delta, d1, d11, d12;

	delta = (s.CordY()-p.CordY())*(q.CordX()-p.CordX()) - (q.CordY()-p.CordY())*(s.CordX()-p.CordX());
	delta = (float)Math.abs((double)delta);

	//System.out.println("Delta = " + delta);
	if ( delta <= ERROR ) {
	   //System.out.println("Este delta es apropiado");
	   d1  = Distancia(p,q);
	   d11 = Distancia(s,p);
	   d12 = Distancia(s,q);
	   if ( (d11 <= d1) && (d12 <= d1) )  return true;
	  }

      return false;
   }

  public PoligonoCocircular poligono_contenedor(ListaFifo lista, Punto punto) {
	PoligonoCocircular 	pol;
	Iterador		itPol;

	itPol = new Iterador(lista);
	while ( !itPol.IsDone() ) {
	  pol = (PoligonoCocircular)itPol.Current();
        if ( pol.contiene_punto(punto) ) {
	    lista.eliminar_elemento(itPol.ValueBackCurrent(), itPol.ValueCurrent());

        return pol;
        }
	    itPol.Next();
	  }
     	//System.out.println("No existe poligono contenedor");

     	return null;
    }


  public void buscar_maximos_y_minimos(ListaFifo lista){
    	Punto p;

    	p = (Punto)lista.first.eg;
    	x_min = (float)p.CordX();
    	x_max = (float)p.CordX();
   	y_max = (float)p.CordY();
   	y_min = (float)p.CordY();

    	it = new Iterador(lista);
	while ( !it.IsDone() ){
      	p = (Punto)it.Current();
      	
      	if ( p.CordX() > x_max )
        	    x_max = (float)p.CordX();
      	if ( p.CordX() < x_min ) 
        	    x_min = (float)p.CordX();
      	
      	if ( p.CordY() > y_max )
        	    y_max = (float)p.CordY();
      	if ( p.CordY() < y_min ) 
        	    y_min = (float)p.CordY();

		it.Next();
    	}
  }

  //public ListaFifo crea_puntos_ficticios(ListaOrdenada lista) {
  public ListaFifo crea_puntos_ficticios(ListaFifo lista) {
    PuntoFicticio	punto_nuevo, p;
    float   	Xcusp, Ycusp, Yn, tang1, tang2, Xb1, Xb2, difer;
    ListaFifo	listaFic;

    //this.buscar_maximos_y_minimos((ListaOrdenada)lista);
    this.buscar_maximos_y_minimos((ListaFifo)lista);
    listaFic = new ListaFifo();

    Xcusp = (float)((x_min + x_max)/2.0f);
    difer = (float)(y_max - y_min);
    Ycusp = (float)(y_max + difer)+10000; 
    
    //Xcusp = (float)((x_min + x_max)/2.0f);
    //diferx = (float)(x_max - x_min);
    //diferx = diferx*2;

    punto_nuevo = new PuntoFicticio((float)Xcusp,(float)Ycusp);
    //lista.insertar_elemento(new NodoLista((PuntoFicticio)punto_nuevo));
    p = new PuntoFicticio((float)Xcusp,(float)Ycusp);
    listaFic.insertar_elemento(new NodoLista((PuntoFicticio)p));

    Yn = (float)(y_min - 10000.0f);

    tang1 = (Ycusp - y_max)/(Xcusp - x_min);
    tang2 = (Ycusp - y_max)/(Xcusp - x_max);
    Xb1   = (Yn + tang1*x_min - y_max)/tang1;
    Xb2   = (Yn + tang2*x_max - y_max)/tang2;
    
    punto_nuevo = new PuntoFicticio((float)Xb1,(float)Yn);
    //lista.insertar_elemento(new NodoLista((PuntoFicticio)punto_nuevo));
    Xb1 = Xb1 - 10000.0f;
    Xb2 = Xb2 + 10000.0f;
    p = new PuntoFicticio((float)Xb1,(float)Yn);
    listaFic.insertar_elemento(new NodoLista((PuntoFicticio)p));

    punto_nuevo = new PuntoFicticio((float)Xb2,(float)Yn);
    //lista.insertar_elemento(new NodoLista((PuntoFicticio)punto_nuevo));
    p = new PuntoFicticio((float)Xb2,(float)Yn);
    listaFic.insertar_elemento(new NodoLista((PuntoFicticio)p));
    
    return listaFic; 
  }

 public Punto intersectaEn(Punto a, Punto b, Punto c, Punto d) {
   	float lambda1, lambda2, x, y, tang1=0.0f, tang2=0.0f;
   	Punto pto_intersec ;

   	lambda2=((b.x-a.x)*(c.y-a.y)-(b.y-a.y)*(c.x-a.x))/((b.y-a.y)*(d.x-c.x)-(b.x -a.x)*(d.y-c.y));
   	lambda1 = (c.x - a.x + lambda2*(d.x-c.x))/(b.x-a.x);
   	if(((lambda1>=0)&&(lambda1<=1))&&((lambda2>=0)&&(lambda2<=1))) {
		if (a.x != b.x) tang1 = (float)((b.y - a.y)/(b.x - a.x));
      	if (c.x != d.x) tang2 = (float)((d.y - c.y)/(d.x - c.x));
      	if (a.x == b.x) {
                x = a.x;
                y = (float)(tang2*(x - c.x) + c.y);
           	}
      	else
       	if (c.x == d.x) {
          		x = c.x;
          		y = (float)(tang1*(x - a.x) + a.y);
        	}
      	else {
 	   		x= (float)((tang1*a.x - a.y - tang2*c.x + c.y)/(tang1 - tang2));
         		y= (float)(tang1*(x - a.x) + a.y);
       	}
		pto_intersec = new Punto((float)x, (float)y);

      	return pto_intersec;
    	}
    	else return null;
 }


 public Punto calcularPuntoInterseccion(Punto p1, Punto p2, Punto q1, Punto q2) {
        float  tang1=0.0f, tang2=0.0f, x, y;

        /*Chequea si los dos segmentos son //s c/r al eje Y */
        if ((p1.CordX() == p2.CordX()) && (q1.CordX() == q2.CordX()))
                return null;

        /*Puede ser que uno de los segmentos sea // al eje Y */
        if (p1.CordX() != p2.CordX()) tang1 = (float)((p2.CordY() - p1.CordY())/(p2.CordX() - p1.CordX()));

        if (q1.CordX() != q2.CordX()) tang2=(float)((q2.CordY()- q1.CordY())/(q2.CordX() - q1.CordX()));

        if (p1.CordX() == p2.CordX()) {
                x = p1.CordX();
                y = (float)(tang2*(x - q1.CordX()) + q1.CordY());
           }
       else
         if (q1.CordX() == q2.CordX()) {
            x = q1.CordX();
            y = (float)(tang1*(x - p1.CordX()) + p1.CordY());
          }
          else
           /*Los segmentos son //s c/r al eje X */

           if (tang1 == tang2) {
              //System.out.println("Los segmentos son paralelos");

              return null;
            }
           else {
             /*Calcula el punto de interseccion de los segmentos*/
             x=(float)((tang1*p1.CordX() - p1.CordY() - tang2*q1.CordX() + q1.CordY())/(tang1 - tang2));
             y=(float)(tang1*(x - p1.CordX()) + p1.CordY());

             //System.out.println("Punto de interseccion: " + x + ":" + y);
           }

        return  (new Punto((float)x, (float)y));
    }

   public boolean vertices_iguales(Punto v1, Punto v2) {

       if ((v1.CordX() == v2.CordX()) && (v1.CordY() == v2.CordY()))
            return true;
        return false;
     }

  public int Test_Circulo(PoligonoCocircular p_t, Punto p) {
	float distancia;

	distancia = p_t.c.Calcula_Radio(p);
	if ( (float)Math.abs((double)(distancia - p_t.c.radio)) <= ERROR ) 
	   return COCIRCULAR;
	
      return OUTCIRCULO;	  
    }

 public int Test_Circulo(PoligonoCocircular p_t,PoligonoCocircular pol) {
   	float distancia, d1, d2;
   	Punto pto;

	d1 = (float)Math.abs((double)(p_t.c.centro.CordX() - pol.c.centro.CordX()));
   	d2 = (float)Math.abs((double)(p_t.c.centro.CordY() - pol.c.centro.CordY()));
   	pto = p_t.pto_eleg;  
   	distancia = pol.c.Calcula_Radio(pto);
   	if ( (d1 <= ERROR)  &&  (d2 <= ERROR) )
     	  if ( (float)Math.abs((double)(distancia - pol.c.radio)) <= ERROR ) 
		return this.COCIRCULAR;

   	if ( distancia < pol.c.radio  )
	   	return this.INCIRCULO;

   	return this.OUTCIRCULO;
  }

   public float Modulo(Vect v) {
     return (float)Math.sqrt((double)(v.CordX()*v.CordX() + v.CordY()*v.CordY()));
    }

   public float ProductoPunto(Vect v, Vect w) {
        return (w.CordX()*v.CordX() + w.CordY()*v.CordY());
    }

   public boolean producto_cruz(Vect vector1, Vect vector2) {
    	float pcruz;

    	pcruz = vector1.CordX()*vector2.CordY() - vector2.CordX()*vector1.CordY();
	if ((pcruz < 0) && ((float)Math.abs((double)pcruz) >= 1.0f)) {
        	return true;
       }
    	 else {
        	return false;
        }
    }

   public float Distancia(Punto p, Punto q) {
     return  (float)Math.sqrt( (double)((q.CordX() - p.CordX())* (q.CordX() - p.CordX()) + (q.CordY() - p.CordY())*(q.CordY() - p.CordY())));
    }

 } //fin Geometricas class


