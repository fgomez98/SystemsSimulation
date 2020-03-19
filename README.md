# ss-tp2
Trabajo Practico #2

### Instrucciones
Situarse en la raiz y ejecutar los comandos segun corresponda

#### Compilacion
```
mvn clean install
```

#### Ejecución
Otorgar permisos de ejecución en caso de ser necesario
```
chmod 777 ./run-off-latice.sh 
``` 
Para correr las simulacion correr el siguiente comando con los parametros deseados 
Los parametros Dsinput , Ddinput son opcionales.
```
./run-off-latice.sh -Dt=[iteraciones] -Dr=[amplitud de ruido]  -Dn=[numero de particulas] -Dl=[longitud] -Dsinput=[Archivo estatico] -Ddinput=[Archivo dinamico]
```

La simulacion sera guardada en el archivo "off-latice-simulation.xyz"
Las condiciones iniciales generadas, en caso de no haber sido pasadas por parametro, son guardadas en los archivos "off-latice-static.txt" y "off-latice-dinamic.txt"


