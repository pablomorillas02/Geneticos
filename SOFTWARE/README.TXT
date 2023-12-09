==========================
PRÁCTICA 2 METAHEURISTICAS
==========================

En la carpeta BIN se incluye:

	- setup.jar: Ejecutable del programa. Para ejecutarlo abra "EJECUTAR.bat"
	
	- EJECUTAR.bat: Menú ejecutable para el programa. Puede seleccionar entre:
		 
		1) Ejecuta el algoritmo declarado con la función también declarada en el config.txt (Nota:
		   No ejecutar con las funciones MAPE y RMSE)
		2) Ejecuta el algoritmo declarado con la función MAPE o RMSE (previamente declarada en el
		   config.txt) junto con un fichero de datos. (Nota: No usar esta opción con otra función.
		   La dimensión se ajusta automáticamente a 5).
		3) Salir

	- Archivos de texto de las funciones: Archivos de donde el programa recoge los datos de cada función
	a evaluar (mínimo, máximo y óptimo global), cada uno con el nombre correspondiente.

	- daido-tra.dat: Archivo de datos para el apartado de MAPE y RMSE

	- config.txt: Archivo de configuración donde se podrá definir la función que evaluará el programa,
	el algoritmo a ejecutar y todos los parámetros necesarios.

		Nota 1: Para las funciones, poner el nombre del fichero (Ej: Ackley.txt)
		Nota 2: Los algoritmos que admite este programa son EvM, EvBLX y ED 

	- Carpeta log: Carpeta donde saldrán los resultados de la ejecución del programa, en varios ficheros 
	de texto con el nombre de la función evaluada, el nombre del algoritmo usado y cada semilla usada
