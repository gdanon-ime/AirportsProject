package src;

import java.util.*;
import java.io.*;

//Criacao da classe do dijkstra
//Aqui definimos dentro da classe a matrix de adjacencia como um atributo
//Criamos a funcao de ShortestPath com o algoritmo para retornar o menor caminho
public class DijkstraClass {

    int sizeData;
    double[][] adjMatrix;

    //Segue funcoes padroes e construtores
    int getSize(){return sizeData;}
    boolean setsizeData(int sz){
        if(sz < 0){return false;}
        sizeData = sz;
        return true;
    }
    double[][] getAdj(){return adjMatrix;}
    void setAdj(double[][] inpt){adjMatrix = inpt;}
    void changeAdj(int i, int j, double val){adjMatrix[i][j] = val;}
    

    DijkstraClass(int sz){
        adjMatrix = new double[sz][sz];
        sizeData = sz;
    }

    DijkstraClass(double inpt[][]){
        adjMatrix = inpt;
        sizeData = inpt.length;
    }

    //Funcao para achar a menor distancia de uma cidade (algoritmo greedy)
    int minDistance(double[] distVal, boolean visVal[]){
        double min = Double.MAX_VALUE;
        int min_index = -1;
 
        for (int v = 0; v < this.sizeData; v++)
            if (visVal[v] == false && distVal[v] <= min) {
                min = distVal[v];
                min_index = v;
            }
 
        return min_index;
    }
    
    //Algoritmo de dijkstra
    public double ShortestPath(int source, int goal) {
        //MAX_DIST eh maior que metade do comprimento da terra
        double MAX_DIST = 3e7;
        double distance[] = new double[this.sizeData];
        double parent[] = new double[this.sizeData];
        boolean vis[] = new boolean[this.sizeData];

        //Aqui iniciamos as variaveis, a array de distance eh inicializada no maximo
        Arrays.fill(distance, MAX_DIST);
        distance[source] = 0;
        double aux = this.adjMatrix[source][goal];

        //Aqui nos retiramos da conta a linha direta entre source e goal, pois precisa haver pelo menos uma parada
        this.changeAdj(source, goal, MAX_DIST);

        for (int i = 0; i < this.sizeData - 1; i++) {
            int nodeMin = minDistance(distance, vis);
            vis[nodeMin] = true;

            for (int j = 0; j < j; j++)
                if (!vis[j] && this.adjMatrix[nodeMin][j] != 0 && distance[nodeMin] != MAX_DIST && distance[nodeMin] + this.adjMatrix[nodeMin][j] < distance[j]){
                    distance[j] = distance[nodeMin] + this.adjMatrix[nodeMin][j];
                    parent[j] = i;
                }
        }

        //Aqui retorna a linha direta entre source goal para outros casos usarem
        this.changeAdj(source, goal, aux);
        
        int node = goal;
        while(node != source){
            System.out.print(node);
            System.out.print(" <- ");
            node = parent[node];
        }
        
        return distance[goal];
    }
}
