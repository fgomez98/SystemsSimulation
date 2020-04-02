import subprocess
import numpy as np
import scipy
from scipy import stats
import seaborn as sns
from matplotlib import pyplot as plt
import scipy.constants

N = [100]
T = [60]

for i in range(0, len(T)):
    for j in range(0, len(N)):
        subprocess.call(
            ['java', '-jar', './target/MolecularDinamic-jar-with-dependencies.jar', '-Dt=' + str(T[i]), '-Dn=' + str(N[j])])

collisions = np.loadtxt("./pdf-colisiones.txt", delimiter='\n')

sns.distplot(collisions, hist=True, kde=True,
             bins=50,
             color='darkblue',
             hist_kws={'edgecolor': 'black'},
             kde_kws={'linewidth': 2}).set(xlim=(0))

plt.title('Frecuencia: '+ str(round(len(collisions) / T[0], 3)) + ' colisiones por segundo, Promedio: ' + str(round(np.average(collisions), 3)) + '(s)')
plt.xlabel('Tiempo entre colisión (s)')
plt.ylabel('Densidad')
# plt.show()
plt.savefig('./Python/graphs/pdf-colisiones.png')
plt.close()

velocities_third = np.loadtxt("./pdf-velocidad-third.txt", delimiter='\n')

sns.distplot(velocities_third, hist=True, kde=True,
             bins=50,
             color='darkblue',
             hist_kws={'edgecolor': 'black'},
             kde_kws={'linewidth': 2}).set(xlim=(0))

plt.title('Promedio: ' + str(round(np.average(velocities_third), 3)) + '(m/s)')
plt.xlabel('Modulo de la velocidad (m/s)')
plt.ylabel('Densidad')
# plt.show()
plt.savefig('./Python/graphs/pdf-velocidad-third.png')
plt.close()

velocities_init = np.loadtxt("./pdf-velocidad-initial.txt", delimiter='\n')

sns.distplot(velocities_init, hist=True, kde=True,
             bins=50,
             color='darkblue',
             hist_kws={'edgecolor': 'black'},
             kde_kws={'linewidth': 2}).set(xlim=(0))

plt.title('Promedio: ' + str(round(np.average(velocities_init), 3)) + '(m/s)')
plt.xlabel('Modulo de la velocidad (m/s)')
plt.ylabel('Densidad')
# plt.show()
plt.savefig('./Python/graphs/pdf-velocidad-initial.png')
plt.close()

# creamos el grafico
fig, ax = plt.subplots()
ax.grid(True)
plt.axis([0, 0.5, 0, 0.5])

V = [0.1, 0.15, 0.25, 0.5]

for i in range(0, len(V)):
    subprocess.call(['java', '-jar', './target/MolecularDinamic-jar-with-dependencies.jar', '-Dt=120', '-Dn=100'])
    big_particle_data = open('./big-particle-trajectory.txt', 'r').readlines()
    static_data = open('./brownian-motion-static.txt', 'r').readlines()
    dinamic_data = open('./brownian-motion-dinamic.txt', 'r').readlines()
    dinamic_data = dinamic_data[1:]
    static_data = static_data[2:]
    temp = 0.0
    for j in range(0, len(static_data)):
        dinamic = dinamic_data[j].split(' ')
        static = static_data[j].split(' ')
        temp += ((float(dinamic[2])**2 + float(dinamic[3])**2) * (float(static[1])/1000)) / scipy.constants.physical_constants["Boltzmann constant"][0]  # paso a kg la masa
    x = []
    y = []
    print(temp)
    for line in big_particle_data:
        position = line.split(',')
        x.append(float(position[0]))
        y.append(float(position[1]))
    ax.plot(x, y, label='Modulo velocidad maxima='+ str(V[i]))

ax.legend(shadow=True, fontsize='medium')
plt.title('Trayectoria de la particula grande')
plt.xlabel('X')
plt.ylabel('Y')
# plt.show()
plt.savefig('./Python/graphs/big-particle-trajectory.png')
plt.close()

dcm_particle = open("./dcm-particle.txt", 'r').readlines()
length = int(len(dcm_particle) / 2)
dcm_particle = dcm_particle[length:]

#   Z^2 = (x(t)-x(0))^2 + (y(y) -y(0))^2
# < z2 > = 2 D t    --> D = coeficiente de difusión
position = dcm_particle[0].split(', ')
x0 = float(position[1])
y0 = float(position[2])
t = []
z = []
for line in dcm_particle:
    position = line.split(', ')
    t.append(float(position[0]))
    x_aux = float(position[1]) - x0
    y_aux = float(position[2]) - y0
    res = pow(x_aux, 2) + pow(y_aux, 2)
    z.append(res)

plt.plot(t, z)
plt.title('DCM: ' + str(sum(z) / len(t)))
plt.xlabel('Tiempo (s)')
plt.ylabel('Desplazamiento cuadrático (m^2)')
plt.show()