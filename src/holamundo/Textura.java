/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holamundo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author PC
 */
public class Textura {

    int idText;
    float textX;
    float textY;
    ArrayList<Textura> texturas;
    Ventana v;
    public Textura(float textX, float textY) {
        
        Ventana v = new Ventana();
        this.textX = textX;
        this.textY = textY;
        
    }

    public Textura() {

    }
  

    public void setId(int id) {
        this.idText = id;
    }
     
    
    //metodo qyue realiza el proimedio de las texturas que se asignarán al nuevo vertice!!
    public Textura promedio(Textura x, Textura y){
        
        float texturaX = x.textX;
        float texturaY = x.textY;
        
        float texturaX2 = y.textX;
        float texturaY2 = y.textY;
        
        float promX,promY;
        
        promX = (texturaX + texturaX2)/2;
        promY = (texturaY + texturaY2)/2;
        
        this.textX = promX;
        this.textY = promY;
        
        //buscarTexturaAprox();
        return this;
    }
    

}
