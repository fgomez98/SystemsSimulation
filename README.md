# ss-tp4
Trabajo Practico #4

### Instrucciones
Situarse en la raiz y ejecutar los comandos segun corresponda

#### Compilación
```
$> mvn clean install
```

#### Ejecución
Otorgar permisos de ejecución en caso de ser necesario
```
$> chmod 777 ./gravitatory.sh ./oscillator.sh
``` 
Para correr la simulación correr el siguiente comando con los siguientes parametros 
```
$> ./oscillator.sh -Dt=[tiempo de simulacion]
$> ./gravitatory.sh -Ds=[Paso temporal] -Dv=[Velocidad inicial de la nave] -DT=[Tiempo a simular] -Dd=[Paso temporal en días para buscar el día óptimo de despegue] -Df=[Cantidad de días después del 06/04 para iniciar la búsqueda del día óptimo de despegue] -Dt=[Cantidad de días después del 06/04 para terminar la búsqueda del día óptimo de despegue] -Dj=[0 (false) o 1 (true) para agregar a Júpiter y Venus al sistema]
```

La simulación del sistema gravitatorio será guardada en el archivo "mars.xyz"

La simulación del oscilador será guardada en los archivos "spring-oscillator-analitical-simulation.txt", "spring-oscillator-beeman-simulation.txt", "spring-oscillator-gear-simulation.txt", "spring-oscillator-verlet-simulation.txt"