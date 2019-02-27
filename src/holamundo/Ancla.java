/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holamundo;

import java.util.ArrayList;
import javafx.beans.property.FloatProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

/*
    la clase ancla permite mover varias aristas al mismo tiempo a traves de un vertice
 */
public class Ancla extends Sphere {

    double posicionInicioX;
    double posicionInicioY;
    double posicionInicioZ;

    double dragDeltaX;
    double dragDeltaY;
    double dragDeltaZ;

    //estos nos serviran cuando queramos arrastrar mas de un ancla
    static ArrayList<Ancla> listaAnclasSeleccionadas = new ArrayList<>();
    static Ancla anclaAux;

    final int velocidadPunto = 8;
    private final FloatProperty x, y, z;
    Objeto3D objeto3D;
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY, orgTranslateZ;
    Color color;
    static double tamañoGlobal = 2;

    Ancla(Color color, FloatProperty x, FloatProperty y, FloatProperty z, Objeto3D objeto3D) {

        this.objeto3D = objeto3D;
        this.color = color;
        this.setRadius(tamañoGlobal);

        //this.setMaterial(new PhongMaterial(this.color));
        this.setMaterial(new PhongMaterial(Color.RED.deriveColor(1, 1, 1, 0.3)));

        this.setTranslateX(x.get());
        this.setTranslateY(y.get());
        this.setTranslateZ(z.get());

        this.x = x;
        this.y = y;
        this.z = z;

        x.bind(this.translateXProperty());
        y.bind(this.translateYProperty());
        z.bind(this.translateZProperty());
        enableDrag();
    }

    public void aumentarRadio() {
        //el tamaño aunmente un 25%
        double radioFinal = tamañoGlobal + (tamañoGlobal * 0.50);
        this.setRadius(radioFinal);
        //this.setMaterial(new PhongMaterial(Color.YELLOW));
        this.setMaterial(new PhongMaterial(Color.YELLOW.deriveColor(1, 1, 1, 0.05)));

    }

    public void disminuirRadio() {
        this.setRadius(tamañoGlobal);
        this.setMaterial(new PhongMaterial(Color.RED.deriveColor(1, 1, 1, 0.2)));
    }

    private void enableDrag() {

        final Delta dragDelta = new Delta();

        this.setOnMouseEntered((MouseEvent me) -> {
            // System.out.println("radio actual " + tamañoGlobal);
            //si esta activado el modo edicion
            if (this.objeto3D.modoEdicion) {

                getScene().setCursor(Cursor.HAND);
                //cuando entra cambiaremos dicho nodo , alterando su color y tamañano
                aumentarRadio();

                if (me.isShiftDown()) {
                    getScene().setCursor(Cursor.HAND);

                    if (!Ancla.listaAnclasSeleccionadas.contains(this)) {

                        this.orgSceneX = me.getSceneX();
                        this.orgSceneY = me.getSceneY();
                        this.orgTranslateX = this.getTranslateX();
                        this.orgTranslateY = this.getTranslateY();

                        Ancla.listaAnclasSeleccionadas.add(this);
                        System.out.println("se ha añadido un nuevo nodo ");

                    }
                }
            }
        });

        this.setOnMousePressed((MouseEvent me) -> {
            // record a delta distance for the drag and drop operation.

            getScene().setCursor(Cursor.MOVE);

            if (me.isShiftDown() && this.objeto3D.modoEdicion && me.isPrimaryButtonDown()) {
                Ancla.anclaAux = this;
                System.out.println("ancla aux seleccionada " + anclaAux.x.get() + " " + anclaAux.y.get());

            } else {
                double angulo = this.objeto3D.obtenerAnguloCamara();
                if (angulo > 25 && angulo < 90) { //mirando hacia la izquieda
                    orgSceneX = me.getSceneX();
                    orgSceneY = me.getSceneY();
                    orgTranslateY = this.getTranslateY();
                    orgTranslateX = this.getTranslateZ();

                } else if (angulo < -25 && angulo > -90) { //mirando hacia la derecha
                    System.out.println("MOUSE PRESSED CARA HACIA LA DERECHA");
                    orgSceneX = me.getSceneX();
                    orgSceneY = me.getSceneY();
                    orgTranslateY = this.getTranslateY();
                    orgTranslateX = this.getTranslateZ();

                } else {
                    orgSceneX = me.getSceneX();
                    orgSceneY = me.getSceneY();
                    orgTranslateX = this.getTranslateX();
                    orgTranslateY = this.getTranslateY();
                }

            }

            dragDelta.x = me.getX() - this.getTranslateX();
            dragDelta.y = me.getY() - this.getTranslateY();

        });

        this.setOnMouseDragged((me) -> {

            //veremos la posicion de la camara
            //si se presiona mientras esta la tecla shift
            if (me.isShiftDown() && me.isPrimaryButtonDown()) {

                Ancla.listaAnclasSeleccionadas.forEach((nodo) -> {

                    //posicion "real" del nodo en el contexto
                    double offsetX = (me.getSceneX() - orgSceneX) / velocidadPunto;
                    double offsetY = (me.getSceneY() - orgSceneY) / velocidadPunto;

                    //distancia desde el punto seleccionado a todos los puntos
                    double diferenciaX = anclaAux.posicionInicioX - nodo.posicionInicioX;
                    double diferenciaY = anclaAux.posicionInicioY - nodo.posicionInicioY;

                    double nuevoX = (nodo.orgTranslateX + offsetX) - diferenciaX;
                    double nuevoY = (nodo.orgTranslateY - offsetY) - diferenciaY;

                    nodo.setTranslateX(nuevoX);
                    nodo.setTranslateY(nuevoY);

                });
            } else { //si no estamos moviendo varios puntos a la vez
                double newTranslateX;
                double newTranslateY;
                double angulo = this.objeto3D.obtenerAnguloCamara();

                if (angulo > 25 && angulo < 90) { // la cara esta rotada hacia la izquierda 

                    this.dragDeltaX = (me.getSceneX() - orgSceneX) / velocidadPunto;
                    this.dragDeltaY = (me.getSceneY() - orgSceneY) / velocidadPunto;
                    newTranslateX = orgTranslateX - this.dragDeltaX;
                    newTranslateY = orgTranslateY - this.dragDeltaY;
                    //System.out.println("x:" + newTranslateX + "- y:" + newTranslateY);
                    setTranslateY(newTranslateY);
                    setTranslateZ(newTranslateX);

                } else if (angulo < -25 && angulo > -90) { // la cara esta rotada hacia la derecha 
                    System.out.println("MOUSE DRAG CARA HACIA LA DERECHA");
                    this.dragDeltaX = (me.getSceneX() - orgSceneX) / velocidadPunto;
                    this.dragDeltaY = (me.getSceneY() - orgSceneY) / velocidadPunto;
                    newTranslateX = orgTranslateX + this.dragDeltaX;
                    newTranslateY = orgTranslateY - this.dragDeltaY;
                    //System.out.println("x:" + newTranslateX + "- y:" + newTranslateY);
                    setTranslateY(newTranslateY);
                    setTranslateZ(newTranslateX);

                } else {
                    //entre mas dividamos mas serca estará del mouse , pero se movera mas lento
                    this.dragDeltaX = (me.getSceneX() - orgSceneX) / velocidadPunto;
                    this.dragDeltaY = (me.getSceneY() - orgSceneY) / velocidadPunto;
                    newTranslateX = orgTranslateX + this.dragDeltaX;
                    newTranslateY = orgTranslateY - this.dragDeltaY;
                    //System.out.println("x:" + newTranslateX + "- y:" + newTranslateY);
                    setTranslateX(newTranslateX);
                    setTranslateY(newTranslateY);
                }

            }
        });

        this.setOnMouseExited((MouseEvent me) -> {
            if (this.objeto3D.modoEdicion) {
                getScene().setCursor(Cursor.DEFAULT);

                if (!me.isStillSincePress()) {
                    disminuirRadio();
                }

            }
        });

        this.setOnMouseReleased((me) -> {
            if (me.isStillSincePress()) {
                disminuirRadio();
                getScene().setCursor(Cursor.HAND);

            }
            System.out.println("se ha soltado el mouse");

        });

    }

    private class Delta {

        double x, y, z;
    }
}
