import subprocess
import numpy as np
import pandas as pd
import seaborn as sns
from matplotlib import pyplot as plt
import scipy.constants
from sklearn import linear_model

##....................................PDF COLISIONES....................................

N = [100]
T = [60]

for i in range(0, len(T)):
    for j in range(0, len(N)):
        subprocess.call(
            ['java', '-jar', './target/MolecularDinamic-jar-with-dependencies.jar', '-Dt=' + str(T[i]),
             '-Dn=' + str(N[j])])

collisions = np.loadtxt("./pdf-colisiones.txt", delimiter='\n')

sns.distplot(collisions, hist=True, kde=False,
             bins=50,
             color='darkblue',
             hist_kws={'edgecolor': 'black'},
             ).set(xlim=(0))

print('Frecuencia: ' + str(round(len(collisions) / T[0], 3)) + ' colisiones por segundo, Promedio: ' + str(round(np.average(collisions), 3)) + '(s)')
plt.xlabel('Tiempo entre colisión (s)')
plt.ylabel('Densidad')
# plt.show()
plt.savefig('./Python/graphs/pdf-colisiones.png')
plt.close()

##....................................PDF VELOCIDADES ULTIMO TERCIO....................................

velocities_third = np.loadtxt("./pdf-velocidad-third.txt", delimiter='\n')

sns.distplot(velocities_third, hist=True, kde=False,
             bins=50,
             color='darkblue',
             hist_kws={'edgecolor': 'black'},
             ).set(xlim=(0))

print('Promedio: ' + str(round(np.average(velocities_third), 3)) + '(m/s)')
plt.xlabel('Modulo de la velocidad (m/s)')
plt.ylabel('Densidad')
# plt.show()
plt.savefig('./Python/graphs/pdf-velocidad-third.png')
plt.close()

##....................................PDF VELOCIDADES INICIAL....................................

velocities_init = np.loadtxt("./pdf-velocidad-initial.txt", delimiter='\n')

sns.distplot(velocities_init, hist=True, kde=False,
             bins=50,
             color='darkblue',
             hist_kws={'edgecolor': 'black'}).set(xlim=(0))

print('Promedio: ' + str(round(np.average(velocities_init), 3)) + '(m/s)')
plt.xlabel('Modulo de la velocidad (m/s)')
plt.ylabel('Densidad')
# plt.show()
plt.savefig('./Python/graphs/pdf-velocidad-initial.png')
plt.close()

##....................................TRAYECTORIA PARTICULA GRANDE....................................

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
        temp += ((float(dinamic[2]) ** 2 + float(dinamic[3]) ** 2) * (2 * float(static[1]) / 1000)) / \
                scipy.constants.physical_constants["Boltzmann constant"][0]  # paso a kg la masa
    x = []
    y = []
    # print(temp)
    for line in big_particle_data:
        position = line.split(',')
        x.append(float(position[0]))
        y.append(float(position[1]))
    ax.plot(x, y, label='Modulo velocidad maxima=' + str(V[i]))

ax.legend(shadow=True, fontsize='medium')
plt.xlabel('X')
plt.ylabel('Y')
# plt.show()
plt.savefig('./Python/graphs/big-particle-trajectory.png')
plt.close()

def f(x, c):
    return c * x

def error(y_array, x_array, c):
    error = 0
    for i in range(0, len(x_array)):
        y = y_array[i]
        x = x_array[i] - x_array[0]
        aux = (y - f(x, c)) ** 2
        error += aux
    return error

##....................................DCM BIG....................................

simulations = 10
z = [[] for j in range(simulations)]
t = [[] for j in range(simulations)]

for i in range(0, simulations):
    subprocess.call(
        ['java', '-jar', './target/MolecularDinamic-jar-with-dependencies.jar', '-Dt=120', '-Dn=100', '-Ddcm=big'])
    dcm_particle = open("./dcm-particle.txt", 'r').readlines()
    length = int(len(dcm_particle) / 2)
    dcm_particle = dcm_particle[length:]
    #   Z^2 = (x(t)-x(0))^2 + (y(y) -y(0))^2
    # < z2 > = 2 D t    --> D = coeficiente de difusión
    position = dcm_particle[0].split(', ')
    x0 = float(position[1])
    y0 = float(position[2])
    t_aux = t[i]
    z_aux = z[i]
    for line in dcm_particle:
        position = line.split(', ')
        t_aux.append(float(position[0]))
        x_aux = float(position[1]) - x0
        y_aux = float(position[2]) - y0
        res = pow(x_aux, 2) + pow(y_aux, 2)
        z_aux.append(res)

mean_z = np.mean(np.array(z), axis=0)
std_z = np.std(np.array(z), axis=0)
mean_t = np.mean(np.array(t), axis=0)


sum_xy = 0
sum_x2 = 0
for i in range(0, len(mean_t)):
    x = mean_t[i] - mean_t[0]
    y = mean_z[i]
    sum_xy += x * y
    sum_x2 += x ** 2

c = sum_xy / sum_x2

print('Coefficiente de difusion Big=' + str(c/2))
print('ErrorD=' + str(error(mean_z, mean_t, c/2)))
print('Error2D=' + str(error(mean_z, mean_t, c)))

regression = [f(t, c) for t in [j - min(mean_t) for j in mean_t]]
plt.scatter(mean_t, mean_z, label='<z²>')
plt.plot(mean_t, regression, color='red', label='0 = 2Dt - <z²>')
plt.ylabel('Desplazamiento cuadrático (m²)')
plt.xlabel('Tiempo (s)')
plt.legend(loc='best', shadow=True, fontsize='medium')
plt.show()

errors = []
c_left = np.linspace(c - 0.002, c, num=5000)
c_right = np.linspace(c, c + 0.002, num=5000)
c_vals = np.concatenate((c_left,c_right))
for i in range(0, len(c_vals)):
    errors.append(error(mean_z, mean_t, c_vals[i]))

plt.plot(c_vals, errors)
plt.axvline(x=c, color='r')
plt.ylabel('Error(2D)[m⁴/s²]')
plt.xlabel('2D[m²/s]')
plt.show()


plt.errorbar(mean_t, mean_z, yerr=std_z, fmt='-o', label='<z²>')

dz = pd.DataFrame(mean_z)
dt = pd.DataFrame(mean_t)
lm = linear_model.LinearRegression()
lm = lm.fit(dt, dz)
predictions = lm.predict(dt)
# print(predictions)
plt.plot(dt, predictions, color='red', label='0 = 2Dt - <z²>')
plt.title('Coeficiente de Difusión: ' + format((lm.coef_[0][0] / 2.0), '.3g') + "(m²/s)")
plt.ylabel('Desplazamiento cuadrático (m²)')
plt.xlabel('Tiempo (s)')
plt.legend(loc='best', shadow=True, fontsize='medium')
# plt.show()
plt.savefig('./Python/graphs/big-particle-dcm.png')
plt.close()

#....................................DCM SMALL....................................

simulations = 10
z = [[] for j in range(simulations)]
t = [[] for j in range(simulations)]

for i in range(0, simulations):
    subprocess.call(
        ['java', '-jar', './target/MolecularDinamic-jar-with-dependencies.jar', '-Dt=120', '-Dn=100', '-Ddcm=small'])
    dcm_particle = open("./dcm-particle.txt", 'r').readlines()
    length = int(len(dcm_particle) / 2)
    dcm_particle = dcm_particle[length:]
    #   Z^2 = (x(t)-x(0))^2 + (y(y) -y(0))^2
    # < z2 > = 2 D t    --> D = coeficiente de difusión
    position = dcm_particle[0].split(', ')
    x0 = float(position[1])
    y0 = float(position[2])
    t_aux = t[i]
    z_aux = z[i]
    for line in dcm_particle:
        position = line.split(', ')
        t_aux.append(float(position[0]))
        x_aux = float(position[1]) - x0
        y_aux = float(position[2]) - y0
        res = pow(x_aux, 2) + pow(y_aux, 2)
        z_aux.append(res)

mean_z = np.mean(np.array(z), axis=0)
std_z = np.std(np.array(z), axis=0)
mean_t = np.mean(np.array(t), axis=0)


sum_xy = 0
sum_x2 = 0
for i in range(0, len(mean_t)):
    x = mean_t[i] - mean_t[0]
    y = mean_z[i]
    sum_xy += x * y
    sum_x2 += x ** 2

c = sum_xy / sum_x2

print('Coefficiente de difusion Small=' + str(c/2))
print('ErrorD=' + str(error(mean_z, mean_t, c/2)))
print('Error2D=' + str(error(mean_z, mean_t, c)))

regression = [f(t, c) for t in [j - min(mean_t) for j in mean_t]]
plt.scatter(mean_t, mean_z, label='<z²>')
plt.plot(mean_t, regression, color='red', label='0 = 2Dt - <z²>')
plt.ylabel('Desplazamiento cuadrático (m²)')
plt.xlabel('Tiempo (s)')
plt.legend(loc='best', shadow=True, fontsize='medium')
plt.show()

errors = []
c_left = np.linspace(c - 0.002, c, num=5000)
c_right = np.linspace(c, c + 0.002, num=5000)
c_vals = np.concatenate((c_left,c_right))
for i in range(0, len(c_vals)):
    errors.append(error(mean_z, mean_t, c_vals[i]))

plt.plot(c_vals, errors)
plt.axvline(x=c, color='r')
plt.ylabel('Error(2D)[m⁴/s²]')
plt.xlabel('2D[m²/s]')
plt.show()

plt.errorbar(mean_t, mean_z, yerr=std_z, fmt='-o', label='<z²>')

dz = pd.DataFrame(mean_z)
dt = pd.DataFrame(mean_t)
lm = linear_model.LinearRegression()
lm = lm.fit(dt, dz)
predictions = lm.predict(dt)
# print(predictions)
plt.plot(dt, predictions, color='red', label='0 = 2Dt - <z²>')
plt.title('Coeficiente de Difusión: ' + format((lm.coef_[0][0] / 2.0), '.3g') + "(m²/s)")
plt.ylabel('Desplazamiento cuadrático (m²)')
plt.xlabel('Tiempo (s)')
plt.legend(loc='best', shadow=True, fontsize='medium')
# plt.show()
plt.savefig('./Python/graphs/small-particle-dcm.png')
plt.close()