/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.shape.Sphere;
import javafx.stage.FileChooser;

public class ManejoArchivo {

    boolean esM2d = false;

    String globalFileName;
    //TriMesh trimesh;
    //ListaFifo lista_puntos;
    private double xmin, xmax, ymin, ymax;
    double zmin = 0, zmax = 0;//nuevos valores

    File direccionFile; //la ubicacion relativa del archivo con el que se esta trabajando
    int id;
    int idV = 1;
    int idT = 1;
    int idC = 1;

    //Listas Hashmap
    HashMap<Integer, Punto> listaVerticesHM = new HashMap<>();
    HashMap<Integer, Triangulo> listaTriangulosHM = new HashMap<>();

    //listas mas importantes para la generacion del rostro
    ArrayList<Point3D> listaPuntos3D = new ArrayList<>();
    ArrayList<Triangulo> listaTriangulos3D = new ArrayList<>();
    ArrayList<Textura> listaTexCoords = new ArrayList<>();

    //Listas para mover puntos
    ObservableList<Point3D> listaPuntos3dObservable = FXCollections.observableArrayList();
    ObservableList<Triangulo> listaTriangulosObservable = FXCollections.observableArrayList();

    Ventana ventana;
    Triangulo triangle;
    TriMesh trimesh;

    public boolean objNeighbors;

    public ManejoArchivo(TriMesh trimesh, Ventana ventana) {
        this.trimesh = trimesh;
        this.ventana = ventana;
        objNeighbors = false;
    }

    public ArrayList<Punto> convertirPuntosLista() {

        ArrayList<Punto> listaNuevosPuntos = new ArrayList<>();

        float[] arregloPuntos = volcarPuntos();

        System.out.println("Size Areglo Vetices: " + arregloPuntos.length);
        int id = 1;
        for (int i = 0; i < arregloPuntos.length; i += 3) {
            Punto punto = new Punto(arregloPuntos[i], arregloPuntos[i + 1], arregloPuntos[i + 2]);
            punto.idv = id;
            listaNuevosPuntos.add(punto);
            id++;
        }
        return listaNuevosPuntos;
    }

    public void actualizarVerticesTrimesh() {

        ArrayList<Punto> newVertices = convertirPuntosLista();

        System.out.println("VerticesLista: " + newVertices.size());
        this.trimesh.lp.clear();

        newVertices.forEach(e -> {
            trimesh.addVertex3D(e);
        });
    }

    public void cargarArchivoMalla() {
        System.out.println("Load malla 3D");
        // se crean las listas de triangulos y vertices idenpentiende del formato que se este leyendo

        //Lectura de la Malla    
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(new File("."));
        this.direccionFile = fileChooser.showOpenDialog(this.ventana.stage);

        System.out.println("file: " + direccionFile);

        if (direccionFile != null) {
            System.out.println("Abriendo Archivo");

            String nombreArchivo = direccionFile.getName();
            StringTokenizer stk = new StringTokenizer(nombreArchivo, ". ");
            String nombre = stk.nextToken();
            String extension = stk.nextToken();
            leerFormato(direccionFile, extension);
            //System.out.println("Extension " + extension);
        } else {
            System.out.println("NULL");
        }

    }

    public void leerFormato(File file, String extension) {

        //System.out.println("archivo con extension: " + extension);
        switch (extension) {
            case "obj":
                this.esM2d = false;
                System.out.println("ES UN ARCHIVO OBJ");
                System.out.println("leyendo " + file + " -> extension " + extension);
                objNeighbors = true; //se pueden setear los vecinos
                leerObj(file);
                break;
            case "m2d":
                this.esM2d = true;
                System.out.println("ES UN ARCHIVO M2D");
                System.out.println("leyendo " + file + " -> extension " + extension);
                leerM2d(file);
                break;
        }

    }

    //se leer perfectamente el obj
    public void leerObj(File name) {
        //listaTriangulos3D.add(null); //supuestamente para que parta desde el 1 
        //System.out.println("leyendo archovo obj y construyendo malla ...");
        try {

            BufferedReader buffer = new BufferedReader(new FileReader(name));
            System.out.println("Directorio: " + name);
            String line;
            while ((line = buffer.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                StringTokenizer stk = new StringTokenizer(line);

                if (stk.countTokens() < 3) {
                    //System.out.println(line + " esta linea es boulshit");
                    continue;
                }
                String tipo = stk.nextToken();
                //System.out.println(tipo);

                if (" ".equals(tipo)) {
                    continue;
                }
                if ("#".equals(tipo)) {
                    continue;
                }

                int id = this.idV;

                switch (tipo) {
                    //Lectura Vertices
                    case "v":
                    case "V":
                        float x,
                         y,
                         z;

                        x = Float.parseFloat(stk.nextToken());
                        y = Float.parseFloat(stk.nextToken());
                        z = Float.parseFloat(stk.nextToken());
                        //System.out.println(x + " " + y + " " + z);
                        if (id == 1) {
                            xmin = x;
                            xmax = x;
                            ymin = y;
                            ymax = y;
                            zmin = z;
                            zmax = z;
                        }
                        if (x < xmin) {
                            xmin = x;
                        }
                        if (x > xmax) {
                            xmax = x;
                        }
                        if (y < ymin) {
                            ymin = y;
                        }
                        if (y > ymax) {
                            ymax = y;
                        }
                        if (z < zmin) {
                            zmin = z;
                        }
                        if (z > zmax) {
                            zmax = z;
                        }
                        Point3D punto3D = new Point3D(x, y, z);
                        listaPuntos3D.add(punto3D);

                        this.listaPuntos3dObservable.add(punto3D);
                        //Creamos un objeto tipo punto para saber vertices,triangulos y vecinos
                        Punto vertice = new Punto(x, y, z);
                        vertice.setIdv(id);
                        trimesh.addVertex3D(vertice);
                        listaVerticesHM.put(id, vertice);
                        this.idV++;
                        break;
                    // Lectura Triangulos.
                    case "f":
                        //Se lee la F en el archivo ---  ejemplo --- f 2/1 38/2 1/3

                        //almacena ambos valores
                        String vertCoord1 = stk.nextToken();
                        String vertCoord2 = stk.nextToken();
                        String vertCoord3 = stk.nextToken();

                        String[] verticeCoordenada = vertCoord1.split("/");
                        int v1 = Integer.parseInt(verticeCoordenada[0]);
                        int coord1 = Integer.parseInt(verticeCoordenada[1]);

                        //System.out.println("textCoords [0]: " + verticeCoordenada[0] + " - " + verticeCoordenada[1]);
                        verticeCoordenada = vertCoord2.split("/");
                        int v2 = Integer.parseInt(verticeCoordenada[0]);
                        int coord2 = Integer.parseInt(verticeCoordenada[1]);
                        //System.out.println("textCoords [1]: " + verticeCoordenada[0] + " - " + verticeCoordenada[1]);

                        verticeCoordenada = vertCoord3.split("/");
                        int v3 = Integer.parseInt(verticeCoordenada[0]);
                        int coord3 = Integer.parseInt(verticeCoordenada[1]);
                        // System.out.println("textCoords [2]: " + verticeCoordenada[0] + " - " + verticeCoordenada[1]);

                        //NO SE AÑADIAN TEXTURAS EN OBJ!!
                        listaVerticesHM.get(v1).setTexturaIDaux(coord1);
                        listaVerticesHM.get(v2).setTexturaIDaux(coord2);
                        listaVerticesHM.get(v3).setTexturaIDaux(coord3);

                        Triangulo t = new Triangulo(listaVerticesHM.get(v1), coord1, listaVerticesHM.get(v2), coord2, listaVerticesHM.get(v3), coord3);
                        listaTriangulos3D.add(t);

                        listaTriangulosObservable.add(t);
                        t.setIdt(idT);
                        trimesh.addTriangle(t);
                        listaTriangulosHM.put(idT, t);
                        idT++;
                        break;

                    case "vt":

                        float c1,
                         c2;

                        c1 = Float.parseFloat(stk.nextToken());
                        c2 = Float.parseFloat(stk.nextToken());
                        Textura textura = new Textura(c1, c2);
                        textura.setId(idC);
                        listaTexCoords.add(textura);

                        //System.out.println("coordenada de textura numero " + idC);
                        idC++;
                        break;

                    default:
                        System.out.println("Warning, line not recognized:" + line + "'");
                        break;
                }

            }
        } catch (IOException e) {
            System.out.println("# Excepcion en load(): con nombre de archivo ");
        };

    }

    public void leerM2d(File name) {

        System.out.println("leyendo archivo m2d y construyendo malla ...");
        //listaTriangulos3D.add(null); //para que parta desde el 1 
        try {

            BufferedReader buffer = new BufferedReader(new FileReader(name));
            //System.out.println("Directorio: " + name);
            String line;

            while ((line = buffer.readLine()) != null) {

                if (line.length() == 0) {
                    continue;
                }
                //System.out.println(line);//Muestra toda la linea que se leerá desde el archivo

                StringTokenizer stk = new StringTokenizer(line);
                int cantidad = stk.countTokens();
                char type = stk.nextToken().charAt(0);
                //System.out.println("");
                //System.out.print("cantidad elementos -> " + cantidad + ":" + type + ": ");

                if (type == ' ') {
                    continue;
                }
                if (type == '#') {
                    continue;
                }

                int id = Integer.parseInt(stk.nextToken());
                //System.out.print(" " + id + ": ");

                switch (type) {
                    //Lectura Vertices
                    case 'v':
                        float x,
                         y,
                         z;

                        x = Float.parseFloat(stk.nextToken());
                        y = Float.parseFloat(stk.nextToken());
                        z = Float.parseFloat(stk.nextToken());
                        //System.out.print(" " + x + " " + y + " " + z);

                        if (stk.hasMoreTokens()) {
                            String sobrante = stk.nextToken();
                            // System.out.print(" + " + sobrante);
                        }

                        if (id == 1) {
                            xmin = x;
                            xmax = x;
                            ymin = y;
                            ymax = y;
                            zmin = z;
                            zmax = z;
                        }
                        if (x < xmin) {
                            xmin = x;
                        }
                        if (x > xmax) {
                            xmax = x;
                        }
                        if (y < ymin) {
                            ymin = y;
                        }
                        if (y > ymax) {
                            ymax = y;
                        }
                        if (z < zmin) {
                            zmin = z;
                        }
                        if (z > zmax) {
                            zmax = z;
                        }
                        Point3D punto3D = new Point3D(x, y, z);
                        listaPuntos3D.add(punto3D);
                        this.listaPuntos3dObservable.add(punto3D);
                        //Creamos un objeto tipo punto para saber vertices,triangulos y vecinos

                        Punto vertice = new Punto(x, y, z);
                        vertice.setIdv(id);
                        trimesh.addVertex3D(vertice);
                        listaVerticesHM.put(id, vertice);
                        this.id++;
                        //System.out.println("Leyendo Vertices: " + id);
                        break;
                    // Lectura Triangulos.                    // Lectura Triangulos.

                    case 't':

                        String vertCoord1 = stk.nextToken();
                        String vertCoord2 = stk.nextToken();
                        String vertCoord3 = stk.nextToken();

                        String[] verticeCoordenada = vertCoord1.split("/");

                        int v1 = Integer.parseInt(verticeCoordenada[0]);
                        int idC1 = Integer.parseInt(verticeCoordenada[1]);

                        verticeCoordenada = vertCoord2.split("/");
                        int v2 = Integer.parseInt(verticeCoordenada[0]);
                        int idC2 = Integer.parseInt(verticeCoordenada[1]);

                        verticeCoordenada = vertCoord3.split("/");
                        int v3 = Integer.parseInt(verticeCoordenada[0]);
                        int idC3 = Integer.parseInt(verticeCoordenada[1]);

                        listaVerticesHM.get(v1).setTexturaIDaux(idC1);
                        listaVerticesHM.get(v2).setTexturaIDaux(idC2);
                        listaVerticesHM.get(v3).setTexturaIDaux(idC3);

                        System.out.println("v1 " + listaVerticesHM.get(v1).idv + ": v2 : " + listaVerticesHM.get(v2).idv + ": v3 : " + listaVerticesHM.get(v3).idv + ": texturas: " + idC1 + " " + idC2 + " " + idC3);
                        //System.out.println("");
                        //System.out.println(" " + v1 + " " + idC1 + " " + v2 + " " + idC2 + " " + v3 + " " + idC3);
                        Triangulo t = new Triangulo(listaVerticesHM.get(v1), idC1, listaVerticesHM.get(v2), idC2, listaVerticesHM.get(v3), idC3);
                        t.setIdt(id);
                        //lista mas importante
                        listaTriangulos3D.add(t);
                        listaTriangulosObservable.add(t);
                        trimesh.addTriangle(t);
                        listaTriangulosHM.put(id, t);
                        //id++;//Para que sirve este ID???
                        break;
                    // Lectura Vecinos.
                    case 'n':

                        String n1,
                         n2,
                         n3;
                        int idN1,
                         idN2,
                         idN3;

                        id = id - 1;

                        //Siempre se lee el tipo de dato, luego el id y finalmente los 3 nodos
                        switch (cantidad) {
                            case 5:
                                //System.out.println("Esta linea tiene 5 elementos");
                                n1 = stk.nextToken();
                                n2 = stk.nextToken();
                                n3 = stk.nextToken();

                                //id de los triangulos vecinos
                                //Es necesario restar 1, pues las listas comienzan desde 0, y al leer los ID estos empiezan desde 1
                                idN1 = Integer.parseInt(n1) - 1;
                                idN2 = Integer.parseInt(n2) - 1;
                                idN3 = Integer.parseInt(n3) - 1;

                                listaTriangulos3D.get(id).vecinos[0] = listaTriangulos3D.get(idN1);
                                listaTriangulos3D.get(id).vecinos[1] = listaTriangulos3D.get(idN2);
                                listaTriangulos3D.get(id).vecinos[2] = listaTriangulos3D.get(idN3);

                                /* System.out.println("T:" + (id + 1) + ": " + listaTriangulos3D.get(idN1).idt + ""
                                        + listaTriangulos3D.get(idN2).idt + ""
                                        + listaTriangulos3D.get(idN3).idt);*/
                                break;
                            case 4:
                                n1 = stk.nextToken();
                                n2 = stk.nextToken();
                                //System.out.println(n1 + " " + n2);
                                idN1 = Integer.parseInt(n1) - 1;
                                idN2 = Integer.parseInt(n2) - 1;
                                listaTriangulos3D.get(id).vecinos[0] = listaTriangulos3D.get(idN1);
                                listaTriangulos3D.get(id).vecinos[1] = listaTriangulos3D.get(idN2);
                                /*System.out.println("T:" + (id + 1) + ": " + listaTriangulos3D.get(idN1).idt + ""
                                        + listaTriangulos3D.get(idN2).idt);*/
                                break;
                            case 3:
                                n1 = stk.nextToken();
                                idN1 = Integer.parseInt(n1) - 1;
                                //System.out.println(n1);
                                listaTriangulos3D.get(id).vecinos[0] = listaTriangulos3D.get(idN1);
                                // System.out.println("T:" + (id + 1) + ": " + listaTriangulos3D.get(idN1).idt);
                                break;
                        }

                        //System.out.println("Vecinos" + listaTriangulos3D.get(id).idt + " ");
                        //listaTriangulos3D.get(id).imprimirVecinos();
                        break;
                    //lectura coordenadas de textura

                    case 'c':

                        float c1,
                         c2;

                        c1 = Float.parseFloat(stk.nextToken());
                        c2 = Float.parseFloat(stk.nextToken());

                        Textura textura = new Textura(c1, c2);
                        textura.idText = id;
                        listaTexCoords.add(textura);

                        //coordenadasTextura.add(c2);
                        //System.out.println("Textura[" + textura.idText + "] : " + textura.textX + " " + textura.textY);
                        break;

                    default:
                        System.out.println("Warning, line not recognized:" + line + "'");
                        break;
                }

            }
        } catch (IOException e) {
            System.out.println("# Excepcion en load(): con nombre de archivo ");
        };

        System.out.println("cantidad de T: " + listaTriangulos3D.size() + " Leídos");
    }
    //este metodo se encarga de leer el archivo y crear cada lista ya sea de puntos, triangulos etc

    public float[] volcarPuntos() {
        int tamañoArreglo = this.ventana.mallaRostro.arregloPuntos3D.size();
        float[] arreglo = new float[tamañoArreglo];
        this.ventana.mallaRostro.arregloPuntos3D.toArray(arreglo);
        return arreglo;
    }

    //permite guardar un archivo de texto con extension m2d
    public void exportarArchivoM2d() {

        String texto = guardarArchivoMemoria();

        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("M2D files (*.m2d)", "*.m2d");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(this.ventana.stage);

        if (file != null) {
            /*si existe una ubicacion valida para el archivo entonces lo guardamos
            le pasamos el texto y la ubicacion*/
            SaveFile(texto, file);
        }
    }

    private void SaveFile(String texto, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(texto);
            fileWriter.close();
        } catch (IOException ex) {
            System.err.println("error al guardar el archivo");
        }
        System.out.println("archivo guardado.");
        ventana.archivoM2D = true;
    }

    public void addPuntoListaPuntos3D(ArrayList<Point3D> puntos) {

        //listaPuntos3D.clear();
        System.out.println("Cantidad de nuevos Vertices: " + puntos.size() + ": v3d :" + listaPuntos3D.size());

        for (int i = 0; i < puntos.size(); i++) {
            listaPuntos3D.add(puntos.get(i));
        }

        System.out.println("Lista total de Vertices: " + puntos.size() + ": v3d :" + listaPuntos3D.size());

    }

    public void addTriangleListaTriangulos3D(ArrayList<Triangulo> triangle) {
        System.out.println("sizelista nuevos T:" + listaTriangulos3D.size());
        //listaTriangulos3D.clear();
        for (int i = 0; i < triangle.size(); i++) {
            listaTriangulos3D.add(triangle.get(i));
        }

        System.out.println("sizelista nuevos T:" + listaTriangulos3D.size());
    }

    public String guardarArchivoMemoria() {
        System.out.println("recopilando datos en memoria");
        float[] arreglo = volcarPuntos();

        //texto es el String que contendrá toda la info del documento
        String texto = "";

        String lineaVertice = "";
        String lineaTriangulo = "";
        String lineaVecino = "";
        String lineaCoord = "";

        String intro = "####\n"
                + "# M2D Archivo generado por Retratos Hablados \n"
                + "# Vertices :" + this.listaPuntos3D.size() + "\n"
                + "# Triangulos :" + this.listaTriangulos3D.size() + "\n"
                + "####\n";

        texto = texto.concat(intro);

        int idv = 1; //variable para id de cada vertice

        /*
         Se guardan los vertices
         */
        //vamos a leer cada 3 coordenadas y avanzar
        for (int i = 0; i < arreglo.length; i += 3) {
            lineaVertice = "v " + (idv) + " "
                    + arreglo[i] + " "
                    + arreglo[i + 1] + " "
                    + arreglo[i + 2] + "\n";
            texto = texto.concat(lineaVertice);
            idv++;
        }

        // se guardan los triangulos
        for (int i = 0; i < this.listaTriangulos3D.size(); i++) {
            lineaTriangulo = "t " + (i + 1) + " "
                    + this.listaTriangulos3D.get(i).p1.getIdv() + "/"
                    + this.listaTriangulos3D.get(i).coords[0] + " "
                    + this.listaTriangulos3D.get(i).p2.getIdv() + "/"
                    + this.listaTriangulos3D.get(i).coords[1] + " "
                    + this.listaTriangulos3D.get(i).p3.getIdv() + "/"
                    + this.listaTriangulos3D.get(i).coords[2] + "\n";
            texto = texto.concat(lineaTriangulo);
        }

        //se guardan los vecinos
        for (int i = 0; i < this.listaTriangulos3D.size(); i++) {
            lineaVecino = "n " + (i + 1) + " ";
            if (this.listaTriangulos3D.get(i).vecinos[0] != null) {
                lineaVecino = lineaVecino.concat(this.listaTriangulos3D.get(i).vecinos[0].idt + " ");
            }
            if (this.listaTriangulos3D.get(i).vecinos[1] != null) {
                lineaVecino = lineaVecino.concat(this.listaTriangulos3D.get(i).vecinos[1].idt + " ");
            }
            if (this.listaTriangulos3D.get(i).vecinos[2] != null) {
                lineaVecino = lineaVecino.concat(this.listaTriangulos3D.get(i).vecinos[2].idt + "\n");
            } else {
                lineaVecino = lineaVecino.concat("\n");
            }

            texto = texto.concat(lineaVecino);
        }

        int indiceCoord = 1;
        //se guardan las coordenadas
        for (int i = 0; i < listaTexCoords.size(); i++) {
            lineaCoord = "c " + (indiceCoord) + " ";
            lineaCoord = lineaCoord.concat(listaTexCoords.get(i).textX
                    + " " + listaTexCoords.get(i).textY + "\n");
            texto = texto.concat(lineaCoord);
            indiceCoord++;

        }
        System.out.println("Coordenas guardadas " + lineaCoord);

        return texto;
    }

    //este metodo permite sobre escribir el fichero actual
    public void guardarArchivoDisco() {

        //podemos guardar el archivo si la lista no es nula o si hay elementos dentro
        if (this.ventana.mallaRostro.listaPuntos != null && !this.ventana.mallaRostro.listaPuntos.isEmpty()) {

            try {
                String texto = guardarArchivoMemoria();

                BufferedWriter bw = new BufferedWriter(new FileWriter(direccionFile));
                bw.write("");
                SaveFile(texto, direccionFile);

            } catch (IOException ex) {
                Logger.getLogger(ManejoArchivo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.out.println("no se puede guardar ya que no hay nada");
        }

    }

    //metodo que permite setear todos los vecinos, para archivos obj 
    public void setAllNeighbors() {
        //System.out.println("seteando todos los vecinos");
        int count;
        Iterator itrT, itrV;
        ArrayList<Triangulo> T = new ArrayList(listaTriangulos3D);

        itrT = T.iterator();
        while (itrT.hasNext()) {
            //System.out.println("seteando vecinos!!");
            count = 0;

            Triangulo triangulo = (Triangulo) itrT.next();

            ArrayList<Triangulo> V = new ArrayList(listaTriangulos3D);
            itrV = V.iterator();
            while (itrV.hasNext()) {
                //System.out.println("dentro del while");

                Triangulo vecino = (Triangulo) itrV.next();

                //System.out.println("triangle: "+ triangulo.toString() + "| | vecino:" + vecino.toString() );
                if ((triangulo != vecino) && triangulo.setNeighbor(vecino)) {

                    //System.out.println("Triangulo[" + triangulo.idt + "]");
                    count++;
                }
            }

        }
    }

    /*Metodos secundarios*/
    public void imprimirVecinos() {
        listaTriangulos3D.forEach(i -> {
            try {//Algunos triangulos no poseen todos los vecinos
                System.out.println("Triangulo[" + i.idt + "]: " + "v[0]" + i.vecinos[0].idt + " - v[1]" + i.vecinos[1].idt + " - v[2]" + i.vecinos[2].idt);
            } catch (Exception e) {

            }
        });

    }

    //Metodos que devuelve la lista con los vertices que seran usados para generar la malla
    public ArrayList getListaPuntos3D() {
        return listaPuntos3D;
    }

    public ArrayList getListatriangulos3D() {
        return listaTriangulos3D;
    }
    //FIN METODOS MALLA

    public ListaFifo getListaVertices() {
        return trimesh.vertices();
    }

    public ListaFifo getListaTriangulos() {
        return trimesh.triangles();
    }

    //Metodo para setear las coordenadas
    public void SetearCoordenadasVertices() {

        System.out.println("SE VAN SE SETEAR LAS COORDENADAS DE TEXTURA");

        int a, b, c;

        for (int i = 0; i < listaTriangulos3D.size(); i++) {

            //Obtenemos los Id de las coordenas...
            a = listaTriangulos3D.get(i).coords[0] - 1;
            b = listaTriangulos3D.get(i).coords[1] - 1;
            c = listaTriangulos3D.get(i).coords[2] - 1;

            System.out.println("T(" + listaTriangulos3D.get(i).idt + ") :" + listaTriangulos3D.get(i).coords[0] + "/"
                    + listaTriangulos3D.get(i).coords[1] + "/"
                    + listaTriangulos3D.get(i).coords[2]);
            //... para luego buscarlas en la lista de coordenadas de textura
            listaTriangulos3D.get(i).setTexturas(listaTexCoords.get(a), 0);
            listaTriangulos3D.get(i).setTexturas(listaTexCoords.get(b), 1);
            listaTriangulos3D.get(i).setTexturas(listaTexCoords.get(c), 2);

            System.out.println("Textura: " + listaTexCoords.get(a).idText + "/" + listaTexCoords.get(b).idText + "/" + listaTexCoords.get(c).idText);
        }

    }

}//fin de la clase
