/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holamundo;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.DrawMode;

public class BarraMenu extends Group {

    Group barra;
    MenuBar menuBar;
    ManejoArchivo manejoArchivo;
    Ventana ventana;
    boolean refinamientoActivo;

    public BarraMenu(Ventana ventana) {
        this.ventana = ventana;
        refinamientoActivo = false;
        System.out.println("se ha creado la barra");
    }

    public void construirBarraMenu() { //se crea el menu y se le añaden los eventos
        //Barra de menu  Top    
        barra = new Group();
        menuBar = new MenuBar();
        Menu file = new Menu("Archivo");
        Menu menuRefinamiento = new Menu("Refinamiento");
        Menu menuPelo = new Menu("Pelo");

        MenuItem itemCargar = new MenuItem("Cargar Malla");
        MenuItem itemGuardar = new MenuItem("Guardar");
        MenuItem itemExport = new MenuItem("Exportar Malla");
        MenuItem salir = new MenuItem("Salir");

        MenuItem itemSeleccionarTriangulos = new MenuItem("Seleccionar Triangulos");
        MenuItem itemRefinamiento = new MenuItem("Lepp Bisection");
        MenuItem itemVecinos = new MenuItem("imprimir Vecinos");
        MenuItem itemPeloCorto = new MenuItem("Pelo corto");
        MenuItem itemPeloLargo = new MenuItem("Pelo largo");
        MenuItem itemEliminarPelo = new MenuItem("Eliminar Pelo");

        file.getItems().addAll(itemCargar, itemGuardar, itemExport, salir);
        //menuRefinamiento.getItems().addAll(itemSeleccionarTriangulos, itemRefinamiento, itemVecinos);
        menuPelo.getItems().addAll(itemPeloCorto, itemPeloLargo, itemEliminarPelo);

        menuBar.getMenus().addAll(file, menuPelo);
        barra.getChildren().add(menuBar);

        /*Eventos para los botones de la barra menu
        *
        *
        *
        *
         */
        itemCargar.setOnAction((ActionEvent ae) -> {
            try {
                abrirArchivo();

                /*UNA VEZ CARGADA UNA MALLA, SE PROCEDE PARALELAMENTE A CARGAR LOS OJOS*/
            } catch (Exception e) {
                System.out.println("No se ha cargado ningun archivo");
            }
        });

        itemPeloCorto.setOnAction(e -> { //se carga el pelo corto
            abrirArchivoPelo("peloCorto");
        });

        itemPeloLargo.setOnAction(e -> { //se carga el pelo largo
            abrirArchivoPelo("peloLargo");
        });

        itemEliminarPelo.setOnAction(e -> {
            this.ventana.mallaPelo.limpiarMallaPelo();
        });

        itemGuardar.setOnAction((e) -> {

            if (this.ventana.caraCargada) {
                System.out.println("sobre escribiendo archivo ...");
                this.ventana.archivoMallaRostro.guardarArchivoDisco();
            } else {
                System.out.println("No existen archivos cargados para guardar o el formato no es valido");
            }

        });

        itemSeleccionarTriangulos.setOnAction((e) -> {
            System.out.println(":::Seleccione Triangulos Para refinar:::");
            if (!refinamientoActivo) {
                this.refinamientoActivo = true;
                //this.ventana.trimesh.lt.imprime_lista_elementos();
                System.out.println("Refinamiento activado*");
                this.ventana.vistaMallaRostro.setDrawMode(DrawMode.LINE);
            } else {
                this.refinamientoActivo = false;
                System.out.println("refinamiento desactivado");
            }
        });

        itemRefinamiento.setOnAction((e) -> {

            if (ventana.vistaMallaRostro.isVisible()) { //si la malla es visible
                LeppBisectionStrategy lepp = new LeppBisectionStrategy(ventana.getTriMesh()); //obtenemos todos los elementos
                if (ventana.trimesh.striangles().cantidad_elementos > 0) { //si existen elementos 

                    System.out.println("triangulos trimesh: " + ventana.trimesh.lt.cantidad_elementos
                            + "vertices trimesh" + ventana.trimesh.lp.cantidad_elementos);

                    int t = ventana.trimesh.lt.cantidad_elementos;//se almacena el id para luego asignarlo a los nuevos triangulos
                    int v = ventana.trimesh.lp.cantidad_elementos;
                    //actualizarLP
                    ventana.archivoMallaRostro.actualizarVerticesTrimesh();
                    lepp.refine();//nos genera otra lista con nuevos puntos y triangulos 

                    refinamientoActivo = false;
                    System.out.println("refinamiento desactivado");

                    ventana.mallaRostro.actualizarMalla(t, v);
                    //se agregan las nuevas anclas en el espacio 3D
                    this.ventana.world.getChildren().addAll(this.ventana.mallaRostro.crearPuntosAncla(this.ventana.mallaRostro.getPoints()));

                }

            }
            System.out.println("se han refinado los triangulos");
        });

        itemVecinos.setOnAction((e) -> {
            ventana.archivoMallaRostro.imprimirVecinos();
        });

        itemExport.setOnAction((e) -> {
            if (ventana.caraCargada) {
                this.ventana.archivoMallaRostro.exportarArchivoM2d();
            }

        });

        salir.setOnAction((ActionEvent ae) -> {
            this.ventana.stage.close();
        });

    }

    public void abrirArchivo() {

        //this trimesh es un parametro que se le pasa por defecto, 
        //tambien le pasamos ventana para que tenga acceso a todos los elemtnos de esta
        this.ventana.archivoMallaRostro = new ManejoArchivo(this.ventana.getTriMesh(), this.ventana);

        this.ventana.archivoMallaRostro.cargarArchivoMalla(); //esto nos deja una lista en manejo archivo

        this.ventana.archivoMallaRostro.SetearCoordenadasVertices();
        //se instancia el mallaRostro de la clase ventana (se trabaja con las listas 3D)
        this.ventana.mallaRostro.addPuntos(this.ventana.archivoMallaRostro.getListaPuntos3D());
        this.ventana.mallaRostro.addTriangulos(this.ventana.archivoMallaRostro.getListatriangulos3D());

        //Elementos De la Malla
        //System.out.println("Triangulos: " + this.ventana.archivoMallaRostro.getListatriangulos3D().size() + " | Vertices: " + this.ventana.archivoMallaRostro.getListaPuntos3D().size());
        //Cargamos la malla a la Escena
        this.ventana.vistaMallaRostro.setMesh(this.ventana.mallaRostro);
        this.ventana.mallaRostro.addCoords(this.ventana.archivoMallaRostro.listaTexCoords);

        //añadimos puntos "ancla" para manipular la malla
        this.ventana.world.getChildren().addAll(this.ventana.mallaRostro.crearPuntosAncla(this.ventana.mallaRostro.getPoints()));

        if (this.ventana.archivoMallaRostro.objNeighbors == true) { //si cargamos un archivo obj
            this.ventana.archivoMallaRostro.setAllNeighbors();
            System.out.println("Se han seteado todos los vecinos una vez");
            this.ventana.archivoMallaRostro.objNeighbors = false;
        }
        this.ventana.caraCargada = true;
        //ventana.archivoM2D = true;

        abrirArchivoOjos();//Una vez cargado el rostro se procede a cargar la malla de los ojos

        //this.manejoArchivo.setearCoordenadasTextura();
    }

    public void abrirArchivoOjos() {

        this.ventana.archivoMallaOjos = new ManejoArchivo(this.ventana.getTriMeshOjos(), this.ventana);
        //File file = new File("C:/Users/PC/Desktop/retratoHablado-Version6/Rostros/Ojos/eyesHd.obj");
        File file = new File("./Rostros/Ojos/EyesHd.obj");
        System.out.println("Archivo ojos encontrado : " + file.exists());
        this.ventana.archivoMallaOjos.leerObj(file);
        //se instancia el mallaRostro de la clase ventana
        this.ventana.mallaOjos.addPuntos(this.ventana.archivoMallaOjos.getListaPuntos3D());
        this.ventana.mallaOjos.addTriangulos(this.ventana.archivoMallaOjos.getListatriangulos3D());

        //Elementos De la Malla
        System.out.println("Triangulos: " + this.ventana.mallaOjos.getFaces().size() + " | Vertices: " + this.ventana.mallaOjos.getPoints().size());
        //Cargamos la malla a la Escena
        this.ventana.vistaMallaOjos.setMesh(this.ventana.mallaOjos);
        this.ventana.mallaOjos.addCoords(this.ventana.archivoMallaOjos.listaTexCoords);

        //this.ventana.world.getChildren().addAll(this.ventana.mallaOjos.crearPuntosAncla(this.ventana.mallaOjos.getPoints()));
    }

    public void abrirArchivoPelo(String tipoPelo) {

        //por mientras cargaremos el pelo corto
        this.ventana.archivoMallaPelo = new ManejoArchivo(this.ventana.getTriMeshPelo(), this.ventana);
        File file;
        if ("peloCorto".equals(tipoPelo)) {
            //file = new File("C:/Users/PC/Desktop/retratoHablado-Version6/Rostros/Pelo/peloCorto.obj");
            file = new File("./Rostros/Pelo/peloCorto.obj");
        } else {
            file = new File("./Rostros/Pelo/peloLargo.obj");
            //file = new File("C:/Users/PC/Desktop/retratoHablado-Version6/Rostros/Pelo/peloLargo.obj");

        }
        this.ventana.archivoMallaPelo.leerObj(file);
        //se instancia el mallaRostro de la clase ventana

        if (this.ventana.peloCargado) {
            // se llama al nuevo metodo cargar pelo
            this.ventana.cambiarPelo();
        }

        this.ventana.mallaPelo.addPuntos(this.ventana.archivoMallaPelo.getListaPuntos3D());
        this.ventana.mallaPelo.addTriangulos(this.ventana.archivoMallaPelo.getListatriangulos3D());

        this.ventana.vistaMallaPelo.setMesh(this.ventana.mallaPelo);
        this.ventana.mallaPelo.addCoords(this.ventana.archivoMallaPelo.listaTexCoords);
        this.ventana.seleccionarTexturaPelo(tipoPelo);

        this.ventana.peloCargado = true;

    }

    public MenuBar getmenuBar() {
        return menuBar;
    }

}
