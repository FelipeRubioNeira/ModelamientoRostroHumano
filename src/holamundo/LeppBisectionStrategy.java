// ************ LeppBisectionStrategy.java **************
package holamundo;

import java.lang.*;

class LeppBisectionStrategy {

    Triangulo t;
    TriMesh trimesh;
    int num;

    LeppBisectionStrategy(TriMesh _trimesh) {
        this.trimesh = _trimesh;
        System.out.println("Trimesh tiene: " + this.trimesh.selectedTriangles.cantidad_elementos + " triangulos para refinar!!");
    }

    public void refine() {
        //agregamos a una lista los triangulos seleccionados
        Iterador i = new Iterador(this.trimesh.striangles());

        while (!i.IsDone()) {
            Triangulo triangle = (Triangulo) i.Current();
            //System.out.println("Triangulo a Refinar:" + triangle.idt + ":v:" + triangle.p1.idv + " " + triangle.p2.idv + " " + triangle.p3.idv);
            if (triangle.isSelected()) {//Sí esta seleccionado, se refina
                Triangulo refined = null; // triangulo null
                do {
                    refined = refineTriangle(triangle);//devuelve un triangulo refinado???
                } while (refined != triangle);
            }
            i.Next();//Pasamos al siguiente nodo(triangulo)
        }
    }

    //Retorna un Triangulo "Refinado"
    Triangulo refineTriangle(Triangulo triangle) {//recibe como parametro el triangulo seleccionado!

        Triangulo neighbor = triangle.longestEdgeNeighbor();//Devuelve el t vecinos con la arista mas larga
        Triangulo refined = null;

        //System.out.println("triangle: " + triangle.idt + "V: " + triangle.p1.idv + " " + triangle.p2.idv + " " + triangle.p3.idv);
        //System.out.println("neighbor arista mas larga: " + neighbor.idt + "V: " + neighbor.p1.idv + " " + neighbor.p2.idv + " " + neighbor.p3.idv);
        if (triangle.isTerminal()) {
            refineTerminal(triangle);
            refined = triangle;
        } else {
            assert neighbor != null;
            refined = refineTriangle(neighbor);
        }

        return refined;
    }

    void refineTerminal(Triangulo triangle) {
        Segmento edge = triangle.longestEdge(); // Devuelve la arista más larga del triángulo
        Punto newVertex = edge.biseccion();  // devuelve el punto medio de la arista
        trimesh.addVertex(newVertex);

        Triangulo neighbor = triangle.longestEdgeNeighbor();

        Triangulo newTriangle1 = Library.bisect(triangle, newVertex);
        trimesh.addTriangle(newTriangle1);
        triangle.setSelected(false);

        //System.out.println("Triangulo a Seleccionado:" + triangle.idt + ":v:" + triangle.vertices[0].idv + " " + triangle.vertices[1].idv + " " + triangle.vertices[2].idv);
        //System.out.println("nuevo Triangulo:" + newTriangle1.idt + ":v:" + newTriangle1.p1.idv + " " + newTriangle1.p2.idv + " " + newTriangle1.p3.idv);
        
        if (neighbor != null) {
            Triangulo newTriangle2 = Library.bisect(neighbor, newVertex);
            trimesh.addTriangle(newTriangle2);
            neighbor.setSelected(false);

            triangle.addNeighbor(newTriangle2);
            newTriangle2.addNeighbor(triangle);

            neighbor.addNeighbor(newTriangle1);
            newTriangle1.addNeighbor(neighbor);
        }
    }

} //fin LeppBisectionStrategy class

