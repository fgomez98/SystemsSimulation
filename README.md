# ss-tp3
Trabajo Practico #3

### Instrucciones
Situarse en la raiz y ejecutar los comandos segun corresponda

#### Compilacion
```
mvn clean install
```

#### Ejecución
Otorgar permisos de ejecución en caso de ser necesario
```
chmod 777 ./brownian-motion.sh.sh 
``` 
Para correr la simulacion correr el siguiente comando con los parametros deseados 
```
./run-off-latice.sh -Dt=[tiempo de simulacion] -Dn=[numero de particulas] -Dv=[veocidad maxima en modulo] -Dsinput=[archivo estatico] -Ddinput=[archivo dinamico]
```

La simulacion sera guardada en el archivo "brownian-motion-simulation.xyz"
Las condiciones iniciales generadas, en caso de no haber sido pasadas por parametro, son guardadas en los archivos "brownian-motion-static.txt" y "brownian-motion-dinamic.txt"


