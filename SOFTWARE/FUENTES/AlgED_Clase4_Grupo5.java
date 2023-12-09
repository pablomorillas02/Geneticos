/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package setup;

import java.util.Random;

/**
 *
 * @author Pablo y Salva
 */
public class Ed {
    private int dimension;
    
    private Poblacion p;
    
    private StringBuilder log;
    
    Random aleatorio;
        
    private double minimo;
    private double maximo;
    private double optimo;
    
    private int poblacion;
    private int evaluaciones;
    private double probabilidad_cruce;
    private double factor_mutacion;
    
    Funcion funcion;
    
    private Individuo mejor_poblacion;
    private int t;
    private int evaluacion;
    private int k;
    
    public Ed(int dimension, Poblacion p, StringBuilder log, Random aleatorio, double minimo, double maximo, double optimo, int poblacion, int evaluaciones, double probabilidad_cruce, Funcion funcion, int k) {
        this.dimension = dimension;
        
        this.p = p;
        
        this.log = log;
        
        this.aleatorio = aleatorio;
        
        this.minimo = minimo;
        this.maximo = maximo;
        this.optimo = optimo;
        
        this.poblacion = poblacion;
        this.evaluaciones = evaluaciones;
        this.probabilidad_cruce = probabilidad_cruce;
        this.factor_mutacion = aleatorio.nextDouble(); // [0 , 1]
        
        this.funcion = funcion;
        
        this.mejor_poblacion = null;
        this.t = 0;
        this.evaluacion = 0;
        this.k = k;
    }
    
    /**
     * @brief Ejecuta el algoritmo
     */
    void ejecutar(){      
        t = 0;
        
        p.inicializar_fitness();
        evaluar();
        
        log.append("\n\nSolución inicial ----> " + funcion.evaluar(mejor_poblacion.getGen()) + "\n\n");
        
        while(evaluacion < evaluaciones){            
            Poblacion nueva_poblacion = new Poblacion(poblacion , dimension , minimo , maximo , optimo , funcion , aleatorio);
            
            for(int i = 0 ; i < poblacion ; i++){
                Individuo objetivo = torneo();
                Individuo aleatorio_1 = p.getIndividuo(aleatorio.nextInt(poblacion));
                Individuo aleatorio_2 = p.getIndividuo(aleatorio.nextInt(poblacion));  
                
                Individuo nuevo = new Individuo(dimension , minimo , maximo , optimo , funcion , aleatorio);
                
                factor_mutacion = aleatorio.nextDouble();
                
                for(int j = 0 ; j < dimension ; j++){
                    if(aleatorio.nextDouble() < probabilidad_cruce) nuevo.getGen()[j] = vector_1_diferencia(p.getIndividuo(i) , aleatorio_1 , aleatorio_2 , j);
                    else nuevo.getGen()[j] = objetivo.getGen()[j];
                }
                
                nuevo.calcular_fitness();
                if(nuevo.getFitness() > p.getIndividuo(i).getFitness()){
                    if(t == 1) log.append("\n\t\t\tEl nuevo individuo empeora al padre");
                    
                    nuevo = p.getIndividuo(i);
                }
                
                nueva_poblacion.addIndividuo(nuevo);
                evaluacion++;
            }
            
            p = nueva_poblacion;
            evaluar();
            
            t++;
        }
    }

    /**
     * @brief Encuentra al mejor individuo de la población
     */
    private void evaluar(){ // Selecciona al mejor
        if(mejor_poblacion == null){
            mejor_poblacion = p.encontrar_mejor();
        }
        if(mejor_poblacion != null){
            if(mejor_poblacion.getFitness() >= p.encontrar_mejor().getFitness()) mejor_poblacion = p.encontrar_mejor();
        }
        
        // logs
        escribir_log();
    }
    
    /**
     * @return devuelve los el individuo objetivo
     */
    private Individuo torneo(){
        if(t == 1) log.append("\nTORNEO INICIADO");
        
        Individuo candidatos[] = new Individuo[k];
        
        for(int i = 0 ; i < k ; i++){
            candidatos[i] = p.getIndividuo(aleatorio.nextInt(poblacion));
        }
        
        return hallar_ganador(candidatos);
    }
    
    /**
     * @brief Halla el individuo con el fitness más alto de los participantes
     * @param participantes vector con individuos participantes en el torneo
     * @return Individuo con más fitness de participantes
     */
    private Individuo hallar_ganador(Individuo[] participantes){
        ordenar(participantes);
                
        if(t == 1) log.append("\n\tFitness Padre = " + participantes[0].getFitness());
        
        return participantes[0];
    }
    
    public Poblacion getP() {
        return p;
    }
    
    /**
     * @brief Ordena el vector dado
     * @param individuos vector a ordenar
     */
    private void ordenar(Individuo[] individuos){
        quicksort(individuos, 0, k - 1);
    }
    
    /**
     * @brief Algoritmo quicksort para ordenación de individuos por fitness de
     * mayor a menor
     * @param individuos vector de individuos a ordenar
     * @param inf infimo del vector
     * @param sup supremo del vector
     */
    private void quicksort(Individuo[] individuos, int inf, int sup){
        Individuo pivote = individuos[inf];
        int i = inf;
        int j = sup;
        Individuo auxiliar;
        
        while(i < j){
            while(individuos[i].getFitness() <= pivote.getFitness() && i < j) i++;
            while(individuos[j].getFitness() > pivote.getFitness()) j--;
            if(i < j){
                auxiliar = individuos[i];
                individuos[i] = individuos[j];
                individuos[j] = auxiliar;
            }
        }
        
        individuos[inf] = individuos[j];
        individuos[j] = pivote;
        
        if(inf < j - 1){
            quicksort(individuos, inf, j - 1);
        }
        if(j + 1 < sup){
            quicksort(individuos, j + 1, sup);
        }
    }
    
    /*
     * @brief actualiza los logs con los fitness de la población t
     */
    private void escribir_log(){
        log.append("\n================================================== -> POBLACION: " + t + "\n");
        
        for(int i = 0 ; i < poblacion ; i++){
            log.append("\t->" + p.getIndividuo(i).getFitness() + "\n");
        }
        
        log.append("\n==================================================\n");
        
        log.append("\nMejor actual: " + funcion.evaluar(mejor_poblacion.getGen()));
    }
    
    /**
     * @param padre padres del hijo
     * @param a1 individuo sleccionado aleatorioamente
     * @param a2 individuo seleccionado aleatoriamente
     * @param j alelo seleccionado para cruzar
     * @return alelo calculado con el operador
     */
    private double vector_1_diferencia(Individuo padre, Individuo a1, Individuo a2, int j){     
        if(t == 1) log.append("\n\t\t Se realiza un cruce");
        
        return padre.getGen()[j] + factor_mutacion*(a1.getGen()[j] - a2.getGen()[j]);
    }
}
