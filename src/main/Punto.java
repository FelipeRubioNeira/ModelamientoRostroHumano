package main;

/**
 * ********** Punto.java *************
 */
class Punto extends ElementoGeometrico {

    protected float x;
    protected float y;
    protected float z;
    protected static int lastid;
    protected int idv;
    protected Punto next;
    protected boolean ficticio;
    int idCoordenada;
    int auxIdT;
    Textura texuraPunto; //correspone a la coordenada de textura de cada punto 
    
    
    Punto(float _x, float _y, float _z) {
        x = _x;
        y = _y;
        z = _z;
        idv = 0;
        ficticio = false;
        //this.texuraPunto = new Textura();
    }

    public void setTexturaPunto(float texturaX, float texturaY) {
        this.texuraPunto.textX = texturaX;
        this.texuraPunto.textY = texturaY;
    }
    
    public void setTextura(Textura t){        
        this.texuraPunto = t;    
    }
    
    public void setTexturaIDaux(int t) {
        this.auxIdT = t;
    }

    public int get_iDTextura() {
        return auxIdT;
    }

    Punto() {
        x = (float) 0.0;
        y = (float) 0.0;
        z = (float) 0.0;
        idv = 0;
        ficticio = false;
    }

    Punto(float _x, float _y) {
        x = _x;
        y = _y;
        idv = 0;
        ficticio = false;
    }

    Punto(int _x, int _y) {
        x = (float) _x;
        y = (float) _y;
        idv = 0;
        ficticio = false;
    }

    Punto(int _x, int _y, int _z) {
        x = (float) _x;
        y = (float) _y;
        z = (float) _z;
        idv = 0;
        ficticio = false;
    }

  

    public static void setLastIdv(int lid) {
        lastid = lid;
    }

    public static int getLastIdv() {
        return lastid;
    }

    public float CordX() {
        return x;
    }

    public float CordY() {
        return y;
    }

    public float CordZ() {
        return z;
    }

    public void setIdv(int id) {
        idv = id;
    }

    public int getIdv() {
        return idv;
    }

    public void setVirtual(boolean fict) {
        ficticio = fict;
    }

    public boolean isVirtual() {
        return ficticio;
    }

    public float Distancia(Punto p) {
        return (float) Math.sqrt((double) ((this.CordX() - p.CordX()) * (this.CordX() - p.CordX()) + (this.CordY() - p.CordY()) * (this.CordY() - p.CordY())));

    }

    public void Imprime_Elemento() {
        System.out.println(CordX() + ":" + CordY() + ":" + CordZ());
    }

    public String imprimir() {
        return "(" + this.x + "," + this.y + "," + this.z + ")";
    }

    public boolean ElementosIguales(ElementoGeometrico eg) {
        Punto p;

        p = (Punto) eg;
        return ((p.CordX() == this.CordX()) && (p.CordY() == this.CordY()));
    }

    public ElementoGeometrico Clone() {
        return new Punto((float) this.CordX(), (float) this.CordY());
    }
} //fin Punto class
