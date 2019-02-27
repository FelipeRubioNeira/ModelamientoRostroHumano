/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holamundo;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;

public class Ventana extends Application {

    boolean caraCargada = false, peloCargado = false;
    boolean archivoM2D = false;

    final Group root = new Group();
    boolean modoEdicion = false;
    final Xform axisGroup = new Xform();
    final Xform moleculeGroup = new Xform();
    final Xform world = new Xform();

    TriMesh trimesh = new TriMesh();
    TriMesh trimeshOjos = new TriMesh();
    TriMesh trimeshPelo = new TriMesh();

    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    static double CAMERA_INITIAL_DISTANCE = -600;// Distancia del observador
    static double CAMERA_INITIAL_X_ANGLE = 160.0;//Angulo en el que parte la figura X
    static double CAMERA_INITIAL_Y_ANGLE = 0.0;//Angulo en el que parte la figura Y
    static final double CAMERA_NEAR_CLIP = 0.1;
    static final double CAMERA_FAR_CLIP = 10000.0;
    static final double AXIS_LENGTH = 300.0;
    static final double CONTROL_MULTIPLIER = 0.1;
    static final double SHIFT_MULTIPLIER = 10.0;
    static final double MOUSE_SPEED = 0.1;
    static final double ROTATION_SPEED = 2.0;
    static final double TRACK_SPEED = 0.3;

    int altoPantalla = 400;
    int anchoPantalla = 900;

    private static final double MAX_SCALE = 10.0d;
    private static final double MIN_SCALE = .1d;

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;

    static ManejoArchivo archivoMallaRostro;
    ManejoArchivo archivoMallaOjos, archivoMallaPelo;
    Objeto3D mallaRostro, mallaOjos, mallaPelo;
    MeshView vistaMallaRostro, vistaMallaOjos, vistaMallaPelo;
    PhongMaterial materialRostro, materialOjos, materialPelo;

    private Scene scene;
    SubScene subScene;
    BarraMenu barraMenu;
    HBox toolsBox, buttonBox;
    VBox sliderBox;
    VBox labelBox;
    Slider ejeX, ejeY, ejeZ;
    Label labelX, labelY, labelZ;
    Button btnModoEdicion;

    String OUTSIDE_TEXT = "Outside Label";

    int id;
    int numColors = 10;

    ArrayList<Sphere> puntos = new ArrayList<>();
    Stage stage;
    boolean pintarBoolean = false;
    boolean cambiarGenero = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Editor de Retratos Hablados - version 6.0");// Titulo de la Ventana  
        Parent rootBorder = content();

        scene = new Scene(rootBorder, this.anchoPantalla, this.altoPantalla, true);

        this.stage = stage;
        actualizarTamañoSubScene(stage.widthProperty(), stage.heightProperty(), this.subScene, this.toolsBox);

        stage.setScene(scene);
        stage.show();

    }

    private Parent content() {

        mallaRostro = new Objeto3D(this.trimesh);
        mallaOjos = new Objeto3D(this.getTriMeshOjos());
        mallaPelo = new Objeto3D(this.getTriMeshPelo());

        vistaMallaRostro = new MeshView();
        vistaMallaOjos = new MeshView();
        vistaMallaPelo = new MeshView();

        BorderPane borderPane = new BorderPane();
        crearSubScena(borderPane);
        construirPaneles(borderPane);
        buildAxes();
        return borderPane;
    }

    public void construirPaneles(BorderPane border) {
        /*      Posicion de los Elementos    */
        barraMenu = new BarraMenu(this);
        barraMenu.construirBarraMenu();

        String image = Ventana.class.getResource("triangulate.jpg").toExternalForm();

        toolsBox = new HBox();
        toolsBox.setPadding(new Insets(2, 5, 2, 5));
        toolsBox.setSpacing(1);
        toolsBox.setStyle("-fx-background-image: url('" + image + "');"
                // + "-fx-background-size: cover;"
                + "-fx-background-height:auto;"
                + "-fx-background-width:auto;");
        toolsBox.setAlignment(Pos.TOP_LEFT);

        //Botones Bottom  
        buttonBox = new HBox();
        Button btnCambiarVistaMalla = new Button("Line");
        Button btnCambiarGenero = new Button("Hombre");
        Button btnLimpiarEscene = new Button("Limpiar");
        btnLimpiarEscene.setStyle("-fx-color : #DF0101");

        btnModoEdicion = new Button("Modo Edicion");
        buttonBox.setPadding(new Insets(10, 4, 4, 0));
        buttonBox.getChildren().addAll(btnCambiarVistaMalla, btnModoEdicion, btnLimpiarEscene);
        //se agregan los botones para cambiar de color
        ToggleGroup grupoBotonesColorRostro = new ToggleGroup();

        RadioButton btnFace1 = new RadioButton();
        btnFace1.setUserData("1");
        btnFace1.setStyle("-fx-color : #ead2d2");
        btnFace1.setToggleGroup(grupoBotonesColorRostro);
        btnFace1.setSelected(true);

        RadioButton btnFace2 = new RadioButton();
        btnFace2.setUserData("2");
        btnFace2.setStyle("-fx-color : #cd9d7d");
        btnFace2.setToggleGroup(grupoBotonesColorRostro);

        RadioButton btnFace3 = new RadioButton();
        btnFace3.setUserData("3");
        btnFace3.setToggleGroup(grupoBotonesColorRostro);
        btnFace3.setStyle("-fx-color : #996b4d");

        RadioButton btnFace4 = new RadioButton();
        btnFace4.setUserData("4");
        btnFace4.setToggleGroup(grupoBotonesColorRostro);
        btnFace4.setStyle("-fx-color : #996b4d");

        RadioButton btnFace5 = new RadioButton();
        btnFace5.setUserData("5");
        btnFace5.setStyle("-fx-color : #3e2819");
        btnFace5.setToggleGroup(grupoBotonesColorRostro);
        VBox cajaRostro = new VBox();
        HBox cajaColores = new HBox();
        cajaColores.setPadding(new Insets(10, 4, 4, 0));
        cajaColores.getChildren().addAll(btnFace1, btnFace2, btnFace3, btnFace4, btnFace5);
        cajaRostro.getChildren().addAll(btnCambiarGenero, cajaColores);
        ToggleGroup grupoBotonesColorOjos = new ToggleGroup();

        RadioButton btnOjos1 = new RadioButton();
        btnOjos1.setUserData("1");
        btnOjos1.setStyle("-fx-color : #000000");
        btnOjos1.setToggleGroup(grupoBotonesColorOjos);
        btnOjos1.setSelected(true);

        RadioButton btnOjos2 = new RadioButton();
        btnOjos2.setUserData("2");
        btnOjos2.setStyle("-fx-color : #5DADE2");
        btnOjos2.setToggleGroup(grupoBotonesColorOjos);

        RadioButton btnOjos3 = new RadioButton();
        btnOjos3.setUserData("3");
        btnOjos3.setToggleGroup(grupoBotonesColorOjos);
        btnOjos3.setStyle("-fx-color : #4E342E");

        RadioButton btnOjos4 = new RadioButton();
        btnOjos4.setUserData("4");
        btnOjos4.setToggleGroup(grupoBotonesColorOjos);
        btnOjos4.setStyle("-fx-color : #689F38");

        VBox cajaColoresOjos = new VBox();
        Label ojosLabel = new Label();
        ojosLabel.setText("Color Ojos:");
        ojosLabel.setStyle("-fx-background-color : #FDFEFE");
        HBox toggleColoresOjos = new HBox();
        toggleColoresOjos.setPadding(new Insets(10, 4, 4, 0));
        toggleColoresOjos.getChildren().addAll(btnOjos1, btnOjos2, btnOjos3, btnOjos4);
        cajaColoresOjos.getChildren().addAll(ojosLabel, toggleColoresOjos);

        labelBox = new VBox();
        labelBox.setPadding(new Insets(0, 2, 1, 5));
        sliderBox = new VBox();
        sliderBox.setSpacing(3);
        ejeX = new Slider(-180.0, 180.0, 0);
        labelX = new Label("X ");
        labelX.setTextFill(Color.ALICEBLUE);
        ejeY = new Slider(-180.0, 180.0, 0);
        labelY = new Label("Y ");
        labelY.setTextFill(Color.ALICEBLUE);
        ejeZ = new Slider(-180.0, 180.0, 0);
        labelZ = new Label("Z ");
        labelZ.setTextFill(Color.ALICEBLUE);

        sliderBox.getChildren().addAll(ejeX, ejeY, ejeZ);
        labelBox.getChildren().addAll(labelX, labelY, labelZ);

        Label reporter = new Label();
        createMonitoredLabel(reporter);
        reporter.setTextFill(Color.WHITE);
        VBox layout = new VBox();
        layout.getChildren().setAll(reporter);
        layout.setFillWidth(true);

        eventoBotones(btnCambiarVistaMalla, btnLimpiarEscene, btnModoEdicion, btnCambiarGenero, ejeX, ejeY, ejeZ, grupoBotonesColorRostro, grupoBotonesColorOjos);
        toolsBox.getChildren().addAll(buttonBox, labelBox, sliderBox, cajaRostro, cajaColoresOjos, layout);

        border.setTop(barraMenu.getmenuBar()); //<---- Barra de Menu
        border.setBottom(toolsBox);

        System.out.println("se ha añadido la barra y el canvas al border pane");
    }

    public void seleccionarTexturaPelo(String tipoPelo) {
        if ("peloCorto".equals(tipoPelo)) {
            materialPelo.setDiffuseMap(new Image(getClass().getResource("texturasPelo/peloCorto.png").toString(), true));
        } else {
            materialPelo.setDiffuseMap(new Image(getClass().getResource("texturasPelo/peloLargo.jpg").toString(), true));
        }

        vistaMallaPelo.setMaterial(materialPelo);
    }

    //la sub escena es donde se va a mostrar el contenido 
    private void crearSubScena(BorderPane border) {
        subScene = new SubScene(root, this.anchoPantalla, this.altoPantalla, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.GREY.brighter());
        construirCamera();
        agregarEventosTeclado(border);

        materialRostro = new PhongMaterial();
        materialOjos = new PhongMaterial();
        materialPelo = new PhongMaterial();

        materialRostro.setDiffuseMap(new Image(getClass().getResource("FemaleFace/asianWomen.jpg").toString(), true));
        //materialRostro.setBumpMap(new Image(getClass().getResourceAsStream("FemaleFace/asianWomen.jpg")));
        //materialRostro.setSpecularMap(new Image(getClass().getResourceAsStream("FemaleFace/asianWomen.jpg")));
        //materialRostro.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("FemaleFace/asianWomen.jpg")));
        vistaMallaRostro.setMaterial(materialRostro);

        materialOjos.setDiffuseMap(new Image(getClass().getResource("colorOjos/blue/ojosAzules.jpg").toString(), true));
        vistaMallaOjos.setMaterial(materialOjos);

        vistaMallaRostro.setDrawMode(DrawMode.FILL);
        //vistaMalla3D.setCullFace(CullFace.FRONT);

        world.getChildren().addAll(vistaMallaRostro, vistaMallaOjos, vistaMallaPelo);

        root.getChildren().add(world);
        mallaRostro.manejarMouseObjeto3D(this);

        subScene.setCamera(camera);
        border.setCenter(subScene);
    }

    public void construirCamera() {
        System.out.println("buildCamera()");
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(0.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);

    }

    private void buildAxes() {
        System.out.println("buildAxes()");

        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKTURQUOISE);
        blueMaterial.setSpecularColor(Color.AQUA);

        final Box xAxis = new Box(AXIS_LENGTH, 1.5, 1.5);
        final Box yAxis = new Box(1.5, AXIS_LENGTH, 1.5);
        final Box zAxis = new Box(1.5, 1.5, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        Text txtX = new Text("X+");
        Text txtY = new Text("Y+");
        Text txtZ = new Text("Z+");
        Text txtX2 = new Text("X-");
        Text txtY2 = new Text("Y-");
        Text txtZ2 = new Text("Z-");

        txtX.setTranslateX(200);
        txtY.setTranslateY(200);
        txtZ.setTranslateZ(200);
        txtX2.setTranslateX(-200);
        txtY2.setTranslateY(-200);
        txtZ2.setTranslateZ(-200);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis, txtX, txtY, txtZ, txtX2, txtY2, txtZ2);
        axisGroup.setVisible(false);
        world.getChildren().addAll(axisGroup);
    }

    /**
     * ******** SECCIÓN DE EVENTOS *********
     */
    private void eventoBotones(Button btnMalla, Button btnLimpiar, Button btnModoEdicion, Button cambiarGenero,
            Slider X, Slider Y, Slider Z,
            ToggleGroup grupoColoresRostro, ToggleGroup grupoColoresOjos) {
        /*Slider Event*/
        X.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                cameraXform.rx.setAngle((double) newValue);
            }
        });

        Y.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                cameraXform.ry.setAngle((double) newValue);
            }
        });

        Z.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                cameraXform.rz.setAngle((double) newValue);
            }
        });

        /*RadioButton Events*/
        grupoColoresRostro.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if (grupoColoresRostro.getSelectedToggle() != null) {
                // se cabiara de color dependiendo del boton que este apretado
                switch (newValue.getUserData().toString()) {
                    case "1":
                        System.out.println("Color 1");
                        if (this.cambiarGenero) {
                            materialRostro.setDiffuseMap(new Image(getClass().getResource("FemaleFace/asianWomen.jpg").toString(), true));
                        } else {
                            materialRostro.setDiffuseMap(new Image(getClass().getResource("MaleFace/maleEuropean.jpg").toString(), true));
                        }
                        vistaMallaRostro.setMaterial(materialRostro);
                        break;
                    case "2":
                        if (this.cambiarGenero) {
                            materialRostro.setDiffuseMap(new Image(getClass().getResource("FemaleFace/europeanWomen.jpg").toString(), true));
                        } else {
                            materialRostro.setDiffuseMap(new Image(getClass().getResource("MaleFace/AsianMen.jpg").toString(), true));
                        }
                        vistaMallaRostro.setMaterial(materialRostro);
                        break;
                    case "3":
                        if (this.cambiarGenero) {
                            materialRostro.setDiffuseMap(new Image(getClass().getResource("FemaleFace/indianWomen.jpg").toString(), true));
                        } else {
                            materialRostro.setDiffuseMap(new Image(getClass().getResource("MaleFace/indianMen.jpg").toString(), true));
                        }
                        vistaMallaRostro.setMaterial(materialRostro);
                        break;
                    case "4":
                        if (this.cambiarGenero) {
                            materialRostro.setDiffuseMap(new Image(getClass().getResource("FemaleFace/morena.jpg").toString(), true));
                        } else {
                            materialRostro.setDiffuseMap(new Image(getClass().getResource("MaleFace/male2.jpg").toString(), true));
                        }
                        vistaMallaRostro.setMaterial(materialRostro);
                        break;
                    case "5":
                        if (this.cambiarGenero) {
                            materialRostro.setDiffuseMap(new Image(getClass().getResource("FemaleFace/africanWomen.jpg").toString(), true));
                        } else {
                            materialRostro.setDiffuseMap(new Image(getClass().getResource("MaleFace/maleBlack.jpg").toString(), true));
                        }
                        vistaMallaRostro.setMaterial(materialRostro);
                        break;
                }
            }
        });

        grupoColoresOjos.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if (grupoColoresRostro.getSelectedToggle() != null) {
                // se cabiara de color dependiendo del boton que este apretado
                switch (newValue.getUserData().toString()) {
                    case "1":
                        System.out.println("Ojos Negros ");
                        materialOjos.setDiffuseMap(new Image(getClass().getResource("colorOjos/black/ojosNegros.jpg").toString(), true));
                        vistaMallaOjos.setMaterial(materialOjos);
                        break;
                    case "2":
                        System.out.println("Ojos Azules ");
                        materialOjos.setDiffuseMap(new Image(getClass().getResource("colorOjos/blue/ojosAzules.jpg").toString(), true));
                        vistaMallaOjos.setMaterial(materialOjos);
                        break;
                    case "3":
                        System.out.println("Ojos Cafes ");
                        materialOjos.setDiffuseMap(new Image(getClass().getResource("colorOjos/brown/ojosCafe.jpg").toString(), true));
                        vistaMallaOjos.setMaterial(materialOjos);
                        break;
                    case "4":
                        System.out.println("Ojos Verdes ");
                        materialOjos.setDiffuseMap(new Image(getClass().getResource("colorOjos/green/ojosVerdes.jpg").toString(), true));
                        vistaMallaOjos.setMaterial(materialOjos);
                        break;
                }
            }
        });

        btnMalla.setOnAction(actionEvent -> {
            if ("Fill".equals(btnMalla.getText())) {
                btnMalla.setText("Line");
                btnMalla.setStyle("-fx-color : #E6E6E6");
                this.vistaMallaRostro.setDrawMode(DrawMode.FILL);
            } else {
                btnMalla.setText("Fill");
                btnMalla.setStyle("-fx-background-color: YELLOW");
                this.vistaMallaRostro.setDrawMode(DrawMode.LINE);
            }

        });

        cambiarGenero.setOnAction(actionEvent -> {
            this.cambiarGenero = !this.cambiarGenero;
            if (this.cambiarGenero) {
                cambiarGenero.setText("Mujer");
            } else {
                cambiarGenero.setText("Hombre");
            }
        });

        btnModoEdicion.setOnAction(e -> {
            cambiarEstadoEdicion();
        });

        btnLimpiar.setOnAction(Event -> {
            System.out.println("Eliminando todo de la escena");

            resetEscene();

        });

    }

    public void cambiarEstadoEdicion() {
        if ("Modo Edicion".equals(btnModoEdicion.getText())) {
            //el modo edicion desactivado y se activa
            btnModoEdicion.setText("Modo Visualizacion");
            this.mallaRostro.modoEdicion = true;
            //System.out.println("modo edicion " + this.mallaRostro.modoEdicion);
            btnModoEdicion.setStyle("-fx-color : #298A08");

            this.world.getChildren().forEach(node -> {
                if (node.getClass() == Ancla.class) {
                    node.setVisible(true);
                }
            });

        } else {
            //modo edicion desactivado
            btnModoEdicion.setText("Modo Edicion");
            this.mallaRostro.modoEdicion = false;
            //System.out.println("modo edicion " + modoEdicion);
            btnModoEdicion.setStyle("-fx-color : #E6E6E6");

            this.world.getChildren().forEach(node -> {
                if (node.getClass() == Ancla.class) {
                    node.setVisible(false);
                }
            });

        }
    }

    //Informacion de la subScena para ajustarla a la ventana
    public void actualizarTamañoSubScene(ReadOnlyProperty ancho, ReadOnlyProperty alto, SubScene subscene, HBox buttonBox) {

        ancho.addListener((v, oldValue, newValue) -> {
            //System.out.println("ancho pantalla " + newValue);
            subscene.widthProperty().set((double) newValue);
        });

        alto.addListener((v, oldValue, newValue) -> {
            //System.out.println("alto pantalla " + newValue);
            subscene.heightProperty().set((double) newValue - 150);

        });
    }

    //Información de la posición del puntero en el Rostro
    private void createMonitoredLabel(Label reporter) {

        world.setOnMouseMoved((MouseEvent event) -> {
            String msg = " X: " + event.getX()
                    + "\n Y: " + event.getY()
                    + "\n Z: " + event.getZ();
            reporter.setText(msg);
        });

        world.setOnMouseExited((MouseEvent event) -> {
            String msg = " X: " + event.getX() + "\n Y: " + event.getY() + "\n Z: " + event.getZ();
            reporter.setText(msg);
        });
    }

    public void agregarEventosTeclado(BorderPane border) {

        border.setOnKeyPressed((KeyEvent ke) -> {
            //Activar o desactivar modo Editar Rostro

            if (ke.isControlDown() && ke.getCode() == KeyCode.S) {
                if (this.archivoMallaRostro != null) {
                    this.archivoMallaRostro.guardarArchivoDisco();
                } else {
                    System.out.println("ningun archivo en memoria");
                }

            }

            if (ke.getCode() == KeyCode.E) {
                cambiarEstadoEdicion();
            }
            //Rotar Rostro Frente/Perfil
            if (ke.getCode() == KeyCode.R) {
                if (!this.mallaRostro.rotacionFrente) {
                    this.mallaRostro.rotarFrente();

                } else {
                    this.mallaRostro.rotarPerfil();

                }
            }
            //CARGAR MENU Ctrl + O
            if (ke.isControlDown() && ke.getCode() == KeyCode.O) {
                try {
                    barraMenu.abrirArchivo();
                } catch (Exception e) {
                    System.out.println("No se ha cargado ningun archivo");
                }
            }
            //Mostrar/Ocultar Ejes X-Y-Z
            if (ke.getCode() == KeyCode.X) {
                axisGroup.setVisible(!axisGroup.isVisible());
            }
        });
    }

    public void resetEscene() {
        //se limpian los triangulos, vertices y puntos ficticios de edicion
        System.out.println("Se ha limpiado la escena");

        if (caraCargada) {
            this.mallaRostro.limpiarMalla();
            this.mallaOjos.limpiarMallaOjos();
            this.caraCargada = false;
        }
        if (peloCargado) {
            this.mallaPelo.limpiarMallaPelo();
            this.peloCargado = false;
        }
    }

    //se agrega esta linea de codigo para cambiar solamente el pelo
    public void cambiarPelo() {
        if (peloCargado) {
            this.mallaPelo.limpiarMallaPelo();
            this.peloCargado = false;
        }
    }

    public TriMesh getTriMesh() {
        return this.trimesh;
    }

    public TriMesh getTriMeshOjos() {
        return this.trimeshOjos;
    }

    public TriMesh getTriMeshPelo() {
        return this.trimeshPelo;
    }
}
