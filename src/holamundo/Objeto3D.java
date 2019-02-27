/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holamundo;

import java.util.ArrayList;
import java.util.Random;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;

public class Objeto3D extends TriangleMesh {

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    Ventana ventana;
    TriMesh trimesh;
    boolean rotacionFrente = true;
    ObservableList<Ancla> ancla;
    boolean modoEdicion = false;
    boolean clickCara = false;
    Point3D puntoInicio;
    Sphere esferaClick;

    ObservableFloatArray arregloPuntos3D;

    ArrayList<Point3D> listaPuntos = null;
    ArrayList<Triangulo> listaTriangulos = null;
    ArrayList<Textura> listaCoordenadas = null;
    ObservableList<Ancla> anclas;

    int min = 0;
    int max = 4;
    int[] indexDeleted;
    Random r;

    public Objeto3D(TriMesh tmesh) {
        super();
        this.trimesh = tmesh;

        //this.getTexCoords().addAll(0, 0); //esta linea va por defecto                           
    }

    //se añaden las coordenadas de textura de la imagen
    public void addCoords(ArrayList<Textura> listaCoords) {

        this.listaCoordenadas = listaCoords;
        //se añaden los "vt" del archivo que vienen de 2 en 2
        this.listaCoordenadas.forEach(coord -> {

            this.getTexCoords().addAll(coord.textX, coord.textY);

        });
        /*System.out.println("sizeCoord: " + listaCoords.size() + "textCoord:" + this.getTexCoords().size());
        System.out.println("cordenanda: " + this.getTexCoords().get(0));*/
    }

    public void addPuntos(ArrayList<Point3D> listaPuntos) {
        //cuando se llamo por primera vez quedo creada esta lita (barra menu)
        this.listaPuntos = listaPuntos;
        int i = 0;
        //System.out.println("Tamaño: " + listaPuntos.size()) ;
        this.listaPuntos.forEach(punto -> {
            this.getPoints().addAll((float) punto.getX(), (float) punto.getY(), (float) punto.getZ());
            
        });

    }

    public void addTriangulos(ArrayList<Triangulo> listaTriangulos) {
        this.listaTriangulos = listaTriangulos;
        //al añadir los triangulos estos vendran con un id por defecto
        /*SE AÑADEN DESDE 1, LA LISTATRIANGULOS3D SU PRIMER VALOR (0) ES NULL*/
        for (int i = 0; i < this.listaTriangulos.size(); i++) {
            this.getFaces().addAll(listaTriangulos.get(i).vertices[0].idv - 1, listaTriangulos.get(i).coords[0] - 1,
                    listaTriangulos.get(i).vertices[1].idv - 1, listaTriangulos.get(i).coords[1] - 1,
                    listaTriangulos.get(i).vertices[2].idv - 1, listaTriangulos.get(i).coords[2] - 1);
            
            /*System.out.println("ID t:" +listaTriangulos.get(i).idt +": "+(listaTriangulos.get(i).vertices[0].idv - 1) + "/"+(listaTriangulos.get(i).coords[0] - 1) + " "+ 
                    (listaTriangulos.get(i).vertices[1].idv - 1) +"/"+(listaTriangulos.get(i).coords[1] - 1) +" " + 
                    (listaTriangulos.get(i).vertices[2].idv - 1) +"/"+ (listaTriangulos.get(i).coords[2] - 1));*/
        }
    }

    public void manejarMouseObjeto3D(Ventana ventana) {

        this.ventana = ventana;

        //cuando se presiona
        this.ventana.subScene.setOnMousePressed((me) -> {

            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();

            //Se necesia utilizar la lista trimesh FiFo 
            //Cuando se hace click en la cara y el refinamiento esta activo
            if (this.ventana.barraMenu.refinamientoActivo && me.isPrimaryButtonDown()) {

                int index = me.getPickResult().getIntersectedFace();//Se suma 1, puesto que empieza desde 1 en la lista

                if (index > 0) {//verificamos que no se haga click fuera de la malla

                    ArrayList<Triangulo> listT = this.ventana.archivoMallaRostro.getListatriangulos3D();
                    Triangulo t = listT.get(index);//Obtenemos el triangulo segun si Id
                    System.out.println("Triangulo n°: " + t.idt);
                    t.setSelected(true);//se marca el triangulo como Seleccionado!, para q sea utilizado por LeepBisection
                    NodoLista nodoT = new NodoLista(t);

                    boolean exist = existeElemento(t);

                    System.out.println("Existe el triangulo: " + exist);
                    if (exist == false) {
                        this.ventana.trimesh.selectedTriangles.insertar_elemento(nodoT);//se agrega el triangulo a la lista de seleccionados

                        if (t.vecinos[0] != null) {
                            System.out.println("Vecinos: [0]" + t.vecinos[0].idt);
                        }
                        if (t.vecinos[1] != null) {
                            System.out.println(" [1]  " + t.vecinos[1].idt);
                        }
                        if (t.vecinos[2] != null) {
                            System.out.println(" [2] " + t.vecinos[2].idt);
                        }

                        System.out.println("v1 (" + t.p1.idv + ") = X: " + t.p1.x + ": Y :" + t.p1.y + " v1: Z" + t.p1.z);
                        System.out.println("v2 (" + t.p2.idv + ") = X: " + t.p2.x + ": Y :" + t.p2.y + " v1: Z" + t.p2.z);
                        System.out.println("v3 (" + t.p3.idv + ") = X: " + t.p3.x + ": Y :" + t.p3.y + " v1: Z" + t.p3.z);
                    } else {
                        System.out.println("ya Existe");
                    }

                }
            }

            if (me.getPickResult().getIntersectedNode().getClass() == Ancla.class) {
                //si se hace click en un ancla solo movemos el punto sin rotar la cara
                //crearEsferaClick(); //crear la esfera tiene sus propias validaciones
            } else {

                if (me.isShiftDown() && me.isPrimaryButtonDown()) {
                    Ancla.listaAnclasSeleccionadas.clear();
                    System.out.println("se ha limpiado la lista de puntos para arrastrar");
                }

                clickCara = true;
                this.puntoInicio = me.getPickResult().getIntersectedPoint();
                //System.out.println("ponto de la cara -> " + me.getPickResult().getIntersectedPoint());
            }
        });

        //cuando se arrastra
        this.ventana.subScene.setOnMouseDragged((me) -> {

            double modifier = 1.0;
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);

            if (me.isControlDown()) {
                modifier = this.ventana.CONTROL_MULTIPLIER;
            }
            if (me.isShiftDown()) {
                modifier = this.ventana.SHIFT_MULTIPLIER;
            }
            if (!this.modoEdicion) { //si no esta activado el modo edicion

                if (me.isPrimaryButtonDown()) {
                    this.ventana.cameraXform.ry.setAngle(this.ventana.cameraXform.ry.getAngle() - mouseDeltaX * this.ventana.MOUSE_SPEED * modifier * this.ventana.ROTATION_SPEED);
                    this.ventana.cameraXform.rx.setAngle(this.ventana.cameraXform.rx.getAngle() + mouseDeltaY * this.ventana.MOUSE_SPEED * modifier * this.ventana.ROTATION_SPEED);

                } else if (me.isSecondaryButtonDown()) {
                    double z = this.ventana.camera.getTranslateZ();
                    double newZ = z + mouseDeltaX * this.ventana.MOUSE_SPEED * modifier;
                    this.ventana.camera.setTranslateZ(newZ);

                } else if (me.isMiddleButtonDown()) {
                    this.ventana.cameraXform2.t.setX(this.ventana.cameraXform2.t.getX() + mouseDeltaX * this.ventana.MOUSE_SPEED * modifier * this.ventana.TRACK_SPEED);
                    this.ventana.cameraXform2.t.setY(this.ventana.cameraXform2.t.getY() + mouseDeltaY * this.ventana.MOUSE_SPEED * modifier * this.ventana.TRACK_SPEED);
                }
            } else {//si es que esta activado el modo edicion

                if (clickCara) {
                    if (me.isPrimaryButtonDown()) {
                        this.ventana.cameraXform.ry.setAngle(this.ventana.cameraXform.ry.getAngle() - mouseDeltaX * this.ventana.MOUSE_SPEED * modifier * this.ventana.ROTATION_SPEED);
                        this.ventana.cameraXform.rx.setAngle(this.ventana.cameraXform.rx.getAngle() + mouseDeltaY * this.ventana.MOUSE_SPEED * modifier * this.ventana.ROTATION_SPEED);
                    }
                }
                if (me.isSecondaryButtonDown()) {
                    moverCamaraHaciaObjetivo();
                }

            }
        });

        this.ventana.subScene.setOnMouseReleased((event) -> {
            //System.out.println("se ha soltado el click");
            double distancia = this.ventana.camera.getTranslateZ() * -1;
            if (distancia > 900) {
                this.ventana.world.getChildren().forEach(e -> {
                    if (e instanceof Ancla) {
                        //se aumenta el tamaño
                        Ancla.tamañoGlobal = 6;
                        Ancla x = (Ancla) e;
                        x.setRadius(Ancla.tamañoGlobal);
                    }
                });
            } else if (distancia < 900 && distancia > 400) {
                this.ventana.world.getChildren().forEach(e -> {
                    if (e instanceof Ancla) {
                        //se aumenta el tamaño
                        Ancla.tamañoGlobal = 4;
                        Ancla x = (Ancla) e;
                        x.setRadius(Ancla.tamañoGlobal);
                    }
                });
            } else if (distancia < 400) {
                this.ventana.world.getChildren().forEach(e -> {
                    if (e instanceof Ancla) {
                        //se aumenta el tamaño
                        Ancla.tamañoGlobal = 1.5;
                        Ancla x = (Ancla) e;
                        x.setRadius(Ancla.tamañoGlobal);
                    }
                });
            }
            clickCara = false;
        });
    }

    public void crearEsferaClick() {
        /*
        1 detectar click
        2 crear esfera visible en la posicion xyz en la clase objeto 3D
        3 mostrar hacer que todos los puntos que estan contenidos dentro de la esfera cambien de color 
         */
        this.ventana.world.setOnMousePressed((event) -> {
            if ((event.getPickResult().getIntersectedNode().getClass() == Ancla.class)) {
                if (event.isPrimaryButtonDown() && modoEdicion) {
                    double x = event.getX();
                    double y = event.getY();
                    double z = event.getZ();

                    esferaClick = new Sphere(20);
                    esferaClick.setMaterial(new PhongMaterial(Color.BLACK.deriveColor(1, 1, 1, 0.08)));

                    esferaClick.setTranslateX(x);
                    esferaClick.setTranslateY(y);
                    esferaClick.setTranslateZ(z);
                    this.ventana.world.getChildren().add(esferaClick);
                    addMouseScrolling(esferaClick);
                }
            }
        });
    }

    public void addMouseScrolling(Node node) {
        node.setOnScroll((ScrollEvent event) -> {
            // Adjust the zoom factor as per your requirement
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
                zoomFactor = 2.0 - zoomFactor;
            }
            node.setScaleX(node.getScaleX() * zoomFactor);
            node.setScaleY(node.getScaleY() * zoomFactor);
        });
    }

    public void moverCamaraHaciaObjetivo() {
        double modifier = 1.0;
        modifier = this.ventana.SHIFT_MULTIPLIER;
        double z = this.ventana.camera.getTranslateZ();
        double newZ = z + mouseDeltaX * this.ventana.MOUSE_SPEED * modifier;
        this.ventana.camera.setTranslateZ(newZ);
        this.ventana.cameraXform2.t.setY(this.ventana.cameraXform2.t.getY() + mouseDeltaY * this.ventana.MOUSE_SPEED * modifier * this.ventana.TRACK_SPEED);
    }

    public void limpiarMalla() {

        this.getPoints().clear();
        this.getFaces().clear();
        this.getTexCoords().clear();
        this.ventana.world.getChildren().removeAll(anclas);
        this.trimesh.lt.clear();
        this.trimesh.lp.clear();
    }

    public void limpiarMallaOjos() {

        this.getPoints().clear();
        this.getFaces().clear();
        this.getTexCoords().clear();

    }

    public void limpiarMallaPelo() {

        this.getPoints().clear();
        this.getFaces().clear();
        this.getTexCoords().clear();

    }

    public ObservableList<Ancla> crearPuntosAncla(ObservableFloatArray puntosMalla) {

        //System.out.println("dentro del metodo create control");
        anclas = FXCollections.observableArrayList();
        this.arregloPuntos3D = puntosMalla;

        for (int i = 0; i < puntosMalla.size(); i += 3) {
            final int idx = i;
            //System.out.println("dentro de for NUMBER::" + idx);

            FloatProperty xProperty = new SimpleFloatProperty(arregloPuntos3D.get(i));
            FloatProperty yProperty = new SimpleFloatProperty(arregloPuntos3D.get(i + 1));
            FloatProperty zProperty = new SimpleFloatProperty(arregloPuntos3D.get(i + 2));

            xProperty.addListener((ObservableValue<? extends Number> ov, Number oldX, Number valueX) -> {
                arregloPuntos3D.set(idx, (float) valueX);
                System.out.println("moviendo nodo-> " + idx + " -" + " X " + valueX);
            });

            yProperty.addListener((ObservableValue<? extends Number> ov, Number oldX, Number valueY) -> {
                arregloPuntos3D.set(idx + 1, (float) valueY);
                System.out.println("moviendo nodo-> " + idx + " -" + " Y " + valueY);

            });

            zProperty.addListener((ObservableValue<? extends Number> ov, Number oldX, Number valueZ) -> {
                arregloPuntos3D.set(idx + 2, (float) valueZ);
                System.out.println("moviendo nodo-> " + idx + " -" + " Z " + valueZ);
            });
            Ancla aux = new Ancla(Color.RED, xProperty, yProperty, zProperty, this);
            aux.setVisible(false);
            anclas.add(aux);
        }
        return anclas;
    }

    public boolean existeElemento(Triangulo t) {
        Iterador i;
        i = new Iterador(this.trimesh.selectedTriangles);
        boolean existe = false;
        ArrayList<Triangulo> tSelected = new ArrayList<>();
        while (!i.IsDone()) {
            Triangulo tActual = (Triangulo) i.Current();
            tSelected.add(tActual);
            i.Next();
        }

        existe = tSelected.contains(t);

        return existe;
    }

    //En este metodo se actualiza la malla
    public void actualizarMalla(int t, int v) {

        Iterador iTriangle, iVertices;

        ArrayList<Triangulo> triangulosActualizados = new ArrayList<>();
        ArrayList<Point3D> puntosActualizados = new ArrayList<>();

        iTriangle = new Iterador(this.trimesh.lt);
        iVertices = new Iterador(this.trimesh.lp);

        int nuevoTriangleId = t + 1;
        int nuevoVerticeId = v + 1;
        //triangulosActualizados = ventana.archivoMallaRostro.getListatriangulos3D();
        //puntosActualizados = ventana.archivoMallaRostro.getListaPuntos3D();

        //se actualiza el id de los nuevos vertices
        while (!iVertices.IsDone()) {
            Punto punto = (Punto) iVertices.Current();
            if (punto.idv == 0) {
                Point3D p = new Point3D(punto.x, punto.y, punto.z);

                ((Punto) iVertices.Current()).idv = nuevoVerticeId;
                punto.idv = nuevoVerticeId;
                puntosActualizados.add(p);//Lista con los nuevos puntos tipo Point3D
                this.ventana.archivoMallaRostro.listaVerticesHM.put(punto.idv, punto);//se agrega el punto a la lista de vertices HM
                nuevoVerticeId++;
                System.out.println("Nuevo vertice: (" + punto.idv + ") :" + punto.idv + " " + punto.x + " " + punto.y + " " + punto.z);
            }
            iVertices.Next();
        }

        //se actualiza el id de los nuevos triangulos
        while (!iTriangle.IsDone()) {

            Triangulo triangle = (Triangulo) iTriangle.Current();
            if (triangle.idt == 0) { // si tiene id 0, es nuevo

                ((Triangulo) iTriangle.Current()).idt = nuevoTriangleId;
                triangle.idt = nuevoTriangleId;
                triangulosActualizados.add(triangle);//Lista con los nuevos triangulos
                nuevoTriangleId++;
                System.out.println("Nuevo Triangulo(" + triangle.idt + "): p1: " + triangle.vertices[0].idv + " :p2: " + triangle.vertices[1].idv + " :p3: " + triangle.vertices[2].idv);
            }
            iTriangle.Next();
        }

        /*Se actualizan las listas con los nuevos puntos y triangulos*/
        this.ventana.archivoMallaRostro.addPuntoListaPuntos3D(puntosActualizados);
        this.ventana.archivoMallaRostro.addTriangleListaTriangulos3D(triangulosActualizados);

        /* Se procede a limpiar la malla*/
        this.getFaces().clear();
        this.getPoints().clear();
        this.getTexCoords().clear();
        this.ventana.world.getChildren().removeAll(anclas);

        /*Se vuelve a crear la malla con los nuevos puntos y triangulos*/
        this.ventana.mallaRostro.addPuntos(this.ventana.archivoMallaRostro.getListaPuntos3D());
        this.ventana.mallaRostro.addTriangulos(this.ventana.archivoMallaRostro.getListatriangulos3D());
        this.ventana.mallaRostro.addCoords(this.ventana.archivoMallaRostro.listaTexCoords);
        this.trimesh.selectedTriangles.clear();//Se limpia la lista de triangulos seleccionados

    }

    //o actualizar
    public void eliminarTriangulos(ArrayList<Triangulo> triangulos) {
        Iterador iTriangle, iVertices;
        iTriangle = new Iterador(this.trimesh.selectedTriangles);
        int[] idTdelete = new int[this.trimesh.selectedTriangles.cantidad_elementos];
        int i = 0;

        while (!iTriangle.IsDone()) {

            Triangulo triangle = (Triangulo) iTriangle.Current();
            //System.out.println("Triangulo a Seleccionado:" + triangle.idt + ":v:" + triangle.vertices[0].idv + " " + triangle.vertices[1].idv + " " + triangle.vertices[2].idv);
            if (triangulos.contains(triangle)) {
                triangulos.remove(triangle);
            }
            iTriangle.Next();
            i++;
        }
        System.out.println("triangulos removidos: " + i);

    }

    public void triangleProperty(int id) {

        int newid;
        if (id > 0) {
            newid = (id) * 6;
        } else {
            newid = id;
        }
        getFaces().set(newid + 1, (int) 1);
        getFaces().set(newid + 3, (int) 1);
        getFaces().set(newid + 5, (int) 1);

    }

    public void rotarFrente() {
        //se trae la cabeza al frente
        //solo se puede mover x,y
        this.ventana.cameraXform.rx.setAngle(160);
        this.ventana.cameraXform.ry.setAngle(0);
        this.ventana.cameraXform.rz.setAngle(0);

        this.rotacionFrente = true;
    }

    public void rotarPerfil() {
        //se deja la cabeza de perfil
        //solo se puede rotar en y,z
        this.ventana.cameraXform.rx.setAngle(180);
        this.ventana.cameraXform.ry.setAngle(-90);
        this.ventana.cameraXform.rz.setAngle(0);
        this.rotacionFrente = false;
    }

    public double obtenerAnguloCamara() {
        double anguloCamara = this.ventana.cameraXform.ry.getAngle();
        return anguloCamara;
    }
}
