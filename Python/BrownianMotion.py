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
            ['java', '-jar', 'MolecularDinamic-jar-with-dependencies.jar', '-Dt=' + str(T[i]), '-Dn=' + str(N[j])])

collisions = np.loadtxt("./pdf-colisiones.txt", delimiter='\n')
collisions_frequency = collisions[0]
collisions = collisions[1:]

# collisions = open('./pdf-colisiones.txt', 'r').read().split('\n')
# collisions_frequency = float(collisions[0])
# print(collisions_frequency)
# collisions = collisions[1:]
# collisions = [float(line.strip()) for line in collisions if line]
# print(collisions)

print(collisions)

sns.distplot(collisions, hist=True, kde=True,
             bins=50,
             color='darkblue',
             hist_kws={'edgecolor': 'black'},
             kde_kws={'linewidth': 2}).set(xlim=(0))

plt.title(str(round(collisions_frequency, 3)) + ' colisiones por segundo')
plt.xlabel('Tiempo entre colisión (s)')
plt.ylabel('Densidad')
plt.show()

sns.kdeplot(collisions)

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

big_particle_data = open('./big-particle-trajectory.txt', 'r').readlines()

size = len(big_particle_data)
x = []
y = []
for line in big_particle_data:
    position = line.split(',')
    x.append(float(position[0]))
    y.append(float(position[1]))

plt.plot(x, y)
plt.title('Trayectoria de la particula grande')
plt.xlabel('X')
plt.ylabel('Y')
plt.show()

dcm_particle = open("./dcm-particle.txt", 'r').readlines()
length = int(len(dcm_particle) / 2)
dcm_particle = dcm_particle[0:length]

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