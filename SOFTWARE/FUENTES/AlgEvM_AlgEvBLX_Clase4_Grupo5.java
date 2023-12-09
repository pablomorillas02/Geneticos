/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package setup;

import java.util.Random;

/**
 *
 * @author Salva y Pablo
 */
public class Ev {
    private int dimension;
    
    private Poblacion p;
    private int t;
    
    private StringBuilder log;
    
    Random aleatorio;
        
    private double minimo;
    private double maximo;
    private double optimo;
    
    private int poblacion;
    private int evaluaciones;
    private int evaluacion;
    private double probabilidad_cruce; // alfa de BLX
    private double probabilidad_mutacion;
    private double factor_mutacion;
    private int k;
    private int kReemp;
    private int nPadres;
    private double alfa;
    
    private Funcion funcion;
    
    private boolean mode; // true: EvM (BLX-Media) false: EvBLX (BLX-ALFA)
    private Individuo mejor_poblacion; // El mejor historico
    private Individuo mejor_actual; //El mejor de la población actual
    
    public Ev(int dimension, Poblacion p, StringBuilder log, Random aleatorio, 
            double minimo, double maximo, double optimo, int poblacion, 
            int evaluaciones, double probabilidad_cruce, 
            double probabilidad_mutacion, int k, int kReemp,Funcion funcion, 
            boolean mode, int nPadres, double alfa) {
        this.dimension = dimension;
        
        this.p = p;
        this.t = 0;
        
        this.log = log;
        
        this.aleatorio = aleatorio;
        
        this.minimo = minimo;
        this.maximo = maximo;
        this.optimo = optimo;
        
        this.poblacion = poblacion;
        this.evaluaciones = evaluaciones;
        this.evaluacion = 0;
        this.probabilidad_cruce = probabilidad_cruce;
        this.probabilidad_mutacion = probabilidad_mutacion;
        this.factor_mutacion = aleatorio.nextDouble(); // [0 , 1]
        this.k = k;
        this.kReemp = kReemp;
        this.nPadres = nPadres;
        this.alfa = alfa;
        
        this.funcion = funcion;
        
        this.mode = mode;
        
        this.mejor_poblacion = null;
        this.mejor_actual = null;
    }
    
    /**
     * @brief Ejecuta el algoritmo
     */
    public void ejecutar(){      
        Poblacion sigPob;
        
        evaluar();
        
        log.append("\n\nSolución inicial ----> " + funcion.evaluar(mejor_poblacion.getGen()) + "\n\n");
        
        while(evaluacion < evaluaciones){
            t++;
            sigPob = torneo();                   
            reemplazo(sigPob);
            evaluar();
        }
        
        log.append("\n\nSolución final ----> " + mejor_poblacion.getFitness() + "\n\n");       
    }

    /**
     * @brief Encuentra al mejor individuo de la población
     */
    private void evaluar(){ // Selecciona al mejor
        if(mejor_poblacion == null){
            mejor_poblacion = p.encontrar_mejor();
        }
        if(mejor_poblacion != null){
            if(mejor_poblacion.getFitness() > p.encontrar_mejor().getFitness()) mejor_poblacion = p.encontrar_mejor();
        }


        // logs
        escribir_log();
    }
    
    /**
     * @brief Genera padres, los cruza y muta. Actualiza nuevaPob (P')
     * @return devuelve los hijos (P')
     */
    private Poblacion torneo(){
        if(t == 1) log.append("\nTORNEO INICIADO");
        
        int i = 0;
        int cont = 0;
        Individuo padres[] = new Individuo[2];
        Poblacion nuevaPob = new Poblacion(poblacion, dimension, minimo, maximo, optimo, funcion, aleatorio);
                
        while(nuevaPob.gett_log() < poblacion){ // Hasta que no se complete la población no se para
            Individuo participantes[] = new Individuo[k];
            
            for(int j = 0 ; j < k ; j++){
                participantes[j] = p.getIndividuo(aleatorio.nextInt(poblacion)); // [0 , 49]
            }
            
            Individuo ganador = hallar_ganador(participantes);
            
            if(t == 1) log.append("\n\tFitness padre (" + (cont + 1) + ") = " + ganador.getFitness());
            
            padres[cont] = ganador;
            cont++;         
            
            if(cont == nPadres){
                if(this.mode){
                    if(aleatorio.nextDouble() < probabilidad_cruce){
                        if(t == 1) log.append("\n\t\tSe realiza un cruce (media)");
                        
                        nuevaPob.addIndividuo(cruceMedia(padres));
                        this.evaluacion++;
                        
                        if(aleatorio.nextDouble() < probabilidad_mutacion){
                            if(t == 1) log.append("\n\t\t\tSe muta");
                            
                            nuevaPob.cambiarInd(nuevaPob.gett_log() - 1, mutar(nuevaPob.getIndividuo(nuevaPob.gett_log() - 1)));
                        }
                    }else{
                        nuevaPob.addIndividuo(padres[aleatorio.nextInt(2)]); // [0 , 1]
                        
                        if(aleatorio.nextDouble() < probabilidad_mutacion){
                            if(t == 1) log.append("\n\t\t\tSe muta");
                            
                            nuevaPob.cambiarInd(nuevaPob.gett_log() - 1, mutar(nuevaPob.getIndividuo(nuevaPob.gett_log() - 1)));
                            this.evaluacion++;
                        }
                    }
                }else{
                    if(aleatorio.nextDouble() < probabilidad_cruce){
                        if(t == 1) log.append("\n\t\tSe realiza un cruce (blx)");
                        
                        nuevaPob.addIndividuo(cruceBLX(padres, cont));
                        evaluacion++;
                        
                        if(aleatorio.nextDouble() < probabilidad_mutacion){
                            if(t == 1) log.append("\n\t\t\tSe muta");
                            
                            nuevaPob.cambiarInd(nuevaPob.gett_log() - 1, mutar(nuevaPob.getIndividuo(nuevaPob.gett_log() - 1)));
                        }
                    }else{
                        nuevaPob.addIndividuo(padres[aleatorio.nextInt(2)]);
                        
                        if(aleatorio.nextDouble() < probabilidad_mutacion){
                            if(t == 1) log.append("\n\t\t\tSe muta");
                            
                            nuevaPob.cambiarInd(nuevaPob.gett_log() - 1, mutar(nuevaPob.getIndividuo(nuevaPob.gett_log() - 1)));
                            evaluacion++;
                        }
                    }
                }  
                
                cont = 0;
            }
        }
        
        return nuevaPob;
    }
    
    /**
     * @brief Halla el individuo con el fitness más alto de los participantes
     * @param participantes vector con individuos participantes en el torneo
     * @return Individuo con más fitness de participantes
     */
    private Individuo hallar_ganador(Individuo[] participantes){
        ordenar(participantes);
                
        return participantes[0];
    }
    
    private Poblacion getP() {
        return p;
    }

    /**
     * @brief Cruza los padres para crear un hijo con el método de la media
     * @param padres vector de padres
     * @return Hijo generado
     */
    private Individuo cruceMedia(Individuo[] padres) {  
        Individuo hijo = new Individuo(dimension, minimo, maximo, optimo, funcion, aleatorio);
        double suma = 0.0;
        
        for(int i = 0; i < dimension; i++){
            suma = 0.0;
            
            for(int j = 0; j < nPadres; j++){
                suma += padres[j].getGen()[i];
            }
            
            hijo.setGen(i, suma/(k*1.0));
        }
                
        hijo.calcular_fitness();
        if(t == 1) log.append("\n\t\tFitness hijo = " + hijo.getFitness());
        
        return hijo;
    }

    /**
     * @brief Cruza los padres para crear un hijo con el método BLX
     * @param padres vector de padres
     * @param npadres número de padres
     * @return Hijo generado
     */
    private Individuo cruceBLX(Individuo[] padres, int npadres) {     
       Individuo hijo = new Individuo(dimension, minimo, maximo, optimo, funcion, aleatorio);
       Double min;
       Double max;
       Double rang;       
     
       for(int i = 0; i < dimension; i++){
           min = Double.POSITIVE_INFINITY;
           max = Double.NEGATIVE_INFINITY;
           
           for(int j = 0; j < npadres; j++){
               if(padres[j].getGen()[i] < min){
                   min = padres[j].getGen()[i];
               }
               
               if(padres[j].getGen()[i] > max){
                   max = padres[j].getGen()[i];
               }
           }
                     
           rang = max - min;
           double interMin = min - rang * this.alfa;
           double interMax = max + rang * this.alfa;
           
           hijo.setGen(i, (interMin + (interMax - interMin) * aleatorio.nextDouble()));       
       }
        
        hijo.calcular_fitness();
        if(t == 1) log.append("\n\t\tFitness hijo = " + hijo.getFitness());

       
        return hijo;     
    }

    
    /**
     * @brief Muta aleatoriamente un alelo aleatorio también
     * @param individuo individuo a mutar
     * @return Individuo ya mutado
     */
    private Individuo mutar(Individuo individuo) {
        int alelo = aleatorio.nextInt((dimension - 1) - 0 + 1) + 0;

        individuo.getGen()[alelo] = minimo + (maximo - minimo) * aleatorio.nextDouble();
        
        individuo.calcular_fitness();
        if(t == 1) log.append("\n\t\tFitness tras mutacion = " + individuo.getFitness());
        
        return individuo;
    }

    /**
     * @brief Reemplaza la población antigua por la nueva
     * @param sigPob nueva población
     */
    private void reemplazo(Poblacion sigPob) {
        
        boolean cent = false;
        mejor_actual = p.encontrar_mejor();
        
        for(int i = 0; i < poblacion; i++){
            if(sigPob.getIndividuo(i).getFitness() == mejor_actual.getFitness()){
                cent = true;
            }
        }
                
        if(!cent){
            int participantes[] = new int[kReemp];
            for(int j = 0 ; j < kReemp ; j++){
                participantes[j] = aleatorio.nextInt(poblacion); // [0 , 49]
            }          
 
            Double max = Double.NEGATIVE_INFINITY; 
            int peor = 0;
            
            for(int j = 0 ; j < kReemp ; j++){
                if(sigPob.getIndividuo(participantes[j]).getFitness() > max){
                    peor = participantes[j];
                    max = sigPob.getIndividuo(participantes[j]).getFitness();
                }
            }   
            
            sigPob.cambiarInd(peor, mejor_actual);
        }
        
        p = sigPob;       
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
        
        log.append("\n==================================================");
        
        log.append("\n\tMejor actual: " + funcion.evaluar(mejor_poblacion.getGen()));
    }
}
