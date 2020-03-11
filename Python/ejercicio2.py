import subprocess
from subprocess import Popen, PIPE
import sys

import matplotlib
import matplotlib.pyplot as plt

# Generar distintos inputs de manera random y estudiar la eficiencia del algoritmo (medida en tiempo de cálculo)
# en función de N y del número de celdas (MxM).Comparar con el método de fuerza bruta que mide las distancias
# entre todos los pares posibles de partículas. Considerar L=20, rc=1 y r=0.25.

L = 20
R_C = 1
R_MAX = 0.25

N = [100, 500, 2500, 5000]
M = [5, 10, 20, 40]

results = [[0.0 for x in range(len(N))] for y in range(len(M))]

if len(sys.argv) != 3:
    print('missing parameters\n')
    print('[metodo] (brute | cim) [contorno] (true | false)')
    exit(1)

method = str(sys.argv[1]).casefold() # metodo a usar
contorno = str(sys.argv[2]).casefold() # contorno o sin contorno

if (method != 'brute') & (method != 'cim'):
    print('invalid first parameter')
    exit(1)

if (contorno != 'true') & (contorno != 'false'):
    print('invalid second parameter')
    exit(1)

if contorno == 'true':
    contorno = True
elif contorno == 'false':
    contorno = False

for i in range(0, len(M)):
    for j in range(0, len(N)):
        # Generamos inputs de manera random
        subprocess.call(['java', '-jar', 'Generate-jar-with-dependencies.jar', '-Dl=' + str(L), '-Dn=' + str(N[j]), '-Drmax=' + str(R_MAX)])
        # Computamos
        # subprocess.call(['java', '-jar', 'Compute-jar-with-dependencies.jar', '-Dm=' + str(M[i]), '-Drc=' + str(R_C), '-Dmethod=' + method])
        if contorno:
            proc = Popen(['java', '-jar', 'Compute-jar-with-dependencies.jar', '-Dc', '-Dm=' + str(M[i]), '-Drc=' + str(R_C), '-Dmethod=' + method], stdout=PIPE)
        else:
            proc = Popen(['java', '-jar', 'Compute-jar-with-dependencies.jar', '-Dm=' + str(M[i]), '-Drc=' + str(R_C),
                          '-Dmethod=' + method], stdout=PIPE)
        elaspsed_time = proc.communicate()[0].decode('utf-8').strip()
        results[i][j] = float(elaspsed_time)


fig, ax = plt.subplots()
for i in range(0, len(M)):
    y = results[i]
    ax.plot(N, y, 'o--', label='M='+ str(M[i]))

legend = ax.legend(loc='upper center', shadow=True, fontsize='x-large')
legend.get_frame().set_facecolor('C0')
if contorno:
    ax.set_title(method + ' con condiciones periódicas de contorno')
else:
    ax.set_title(method + ' sin condiciones periódicas de contorno')
ax.set_ylabel('time (s)')
ax.set_xlabel('N')

matplotlib.pyplot.show()

