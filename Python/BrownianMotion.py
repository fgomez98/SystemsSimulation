import subprocess
import numpy as np
import scipy
from scipy import stats
import seaborn as sns
from matplotlib import pyplot as plt

N = [100]
T = [60]

results = [[0.0 for x in range(len(N))] for y in range(len(T))]

for i in range(0, len(T)):
    for j in range(0, len(N)):
        subprocess.call(
            ['java', '-jar', './target/MolecularDinamic-jar-with-dependencies.jar', '-Dt=' + str(T[i]), '-Dn=' + str(N[j])])

collisions = np.loadtxt("./pdf-colisiones.txt", delimiter='\n')
collisions_frequency = collisions[0]
collisions = collisions[1:]

sns.distplot(collisions, hist=True, kde=True,
             bins=50,
             color='darkblue',
             hist_kws={'edgecolor': 'black'},
             kde_kws={'linewidth': 2}).set(xlim=(0))

plt.title(str(round(collisions_frequency, 3)) + ' colisiones por segundo')
plt.xlabel('Tiempo entre colisión (s)')
plt.ylabel('Densidad')
plt.show()

plt.title(str(round(collisions_frequency, 3)) + ' colisiones por segundo')
plt.xlabel('Tiempo entre colisión (s)')
plt.ylabel('Densidad')
plt.show()

velocities_third = np.loadtxt("./pdf-velocidad-third.txt", delimiter='\n')
velocities_third_avg = velocities_third[0]
velocities_third = velocities_third[1:]

sns.distplot(velocities_third, hist=True, kde=True,
             bins=50,
             color='darkblue',
             hist_kws={'edgecolor': 'black'},
             kde_kws={'linewidth': 2}).set(xlim=(0))

plt.title('Promedio: ' + str(round(velocities_third_avg, 3)) + '(m/s)')
plt.xlabel('Modulo de la velocidad (m/s)')
plt.ylabel('Densidad')
plt.show()

velocities_init = np.loadtxt("./pdf-velocidad-initial.txt", delimiter='\n')
velocities_init_avg = velocities_init[0]
velocities_init = velocities_init[1:]

sns.distplot(velocities_init, hist=True, kde=True,
             bins=50,
             color='darkblue',
             hist_kws={'edgecolor': 'black'},
             kde_kws={'linewidth': 2}).set(xlim=(0))

plt.title('Promedio: ' + str(round(velocities_init_avg, 3)) + '(m/s)')
plt.xlabel('Modulo de la velocidad (m/s)')
plt.ylabel('Densidad')
plt.show()

# creamos el grafico
fig, ax = plt.subplots()
ax.grid(True)
plt.axis([0, 0.5, 0, 0.5])

V = [0.1, 0.5, 0.15, 0.25]

for i in range(0, len(V)):
    subprocess.call(['java', '-jar', './target/MolecularDinamic-jar-with-dependencies.jar', '-Dt=120', '-Dn=100'])
    big_particle_data = open('./big-particle-trajectory.txt', 'r').readlines()
    x = []
    y = []
    for line in big_particle_data:
        position = line.split(',')
        x.append(float(position[0]))
        y.append(float(position[1]))
    ax.plot(x, y, label='Modulo velocidad maxima='+ str(V[i]))

ax.legend(shadow=True, fontsize='medium')
plt.title('Trayectoria de la particula grande')
plt.xlabel('X')
plt.ylabel('Y')
plt.show()

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