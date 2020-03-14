import subprocess
import numpy as np
import matplotlib.pyplot as plt

#.......................................................................................................................
#                    Densidad constante(=4) variacion de ruido
#.......................................................................................................................
#
#
# N = [40, 100, 400, 4000]
# L = [3.1, 5.0, 10.0, 31.6]
# n = [0.1, 1.2, 2.5, 3.6, 4.7, 5.0]
#
# for i in range(0, len(N)):
#     for j in range(0, len(n)):
#         subprocess.call(['java', '-jar', 'CellularAutomata-jar-with-dependencies.jar', '-Dl=' + str(L[i]), '-Dn=' + str(N[i]), '-Dr=' + str(n[j]), '-Dt=6000'])
#         velocity_averages = np.loadtxt("./off-latice-va-" + str(N[i]) + "-" + str(n[j]) + ".txt", delimiter='\n')
#         time = [i for i in range(1, len(velocity_averages) + 1)]
#         fig, ax = plt.subplots()
#         ax.set_title("N:" + str(N[i]) + " L:" + str(L[i]) + " Ruido:" + str(n[j]))
#         ax.plot(time, velocity_averages)
#         ax.set_xlabel('time (s)')
#         ax.set_ylabel('va')
#         plt.grid(True)
#         plt.show()
#
#
#.......................................................................................................................
#                   Grafico con los datos recopilados de Densidad constante(=4) variacion de ruido
#.......................................................................................................................
#
#
# fig, ax = plt.subplots()
# N = [40, 100, 400, 4000]
# X = [0.1, 1.2, 2.5, 3.6, 4.7, 5.0]
# Y = [[1, 0.94, 0.75, 0.53, 0.25, 0.2], [1, 0.93, 0.72, 0.47, 0.19, 0.13], [1, 0.92, 0.7, 0.37, 0.055, 0.05], [1, 0.8, 0.45, 0.19, 0.02, 0.015]]
# for i in range(0, len(Y)):
#     y = Y[i]
#     ax.plot(X, y, 'o--', label='N='+ str(N[i]))
#
# legend = ax.legend(loc='upper right', shadow=True, fontsize='x-large')
# ax.set_title("Va en funcion del rudio")
# ax.set_ylabel('Va')
# ax.set_xlabel('ruido')
# plt.show()
#
#
#.......................................................................................................................
#                    Ruido constante variacion de densidad
#.......................................................................................................................
#
#
# p = [0.1, 0.5, 1.0, 2.0]
# N = [40, 200, 400, 800]
# L = 20
# n = [2.0]
#
# for i in range(0, len(n)):
#     for j in range(0, len(N)):
#         subprocess.call(['java', '-jar', 'CellularAutomata-jar-with-dependencies.jar', '-Dl=' + str(L), '-Dn=' + str(N[j]), '-Dr=' + str(n[i]), '-Dt=5000'])
#         velocity_averages = np.loadtxt("./off-latice-va-" + str(N[j]) + "-" + str(n[i]) + ".txt", delimiter='\n')
#         time = [i for i in range(1, len(velocity_averages) + 1)]
#         fig, ax = plt.subplots()
#         ax.set_title("Densidad:" + str(p[j]) + " Ruido:" + str(n[i]))
#         ax.plot(time, velocity_averages)
#         ax.set_xlabel('time (s)')
#         ax.set_ylabel('va')
#         plt.grid(True)
#         plt.show()
#
#
#.......................................................................................................................
#                   Grafico con los datos recopilados de Ruido constante variacion de densidad
#.......................................................................................................................
#
#
# fig, ax = plt.subplots()
#
#
# X = [0.1, 0.5, 1.0, 2.0, 5.0, 8.0]
# Y = [0.19, 0.26, 0.44, 0.64,  0.76,   0.81]
# ax.plot(X, Y, 'o--')
# ax.set_title("Va en funcion de densidad")
# ax.set_ylabel('Va')
# ax.set_xlabel('densidad')
# plt.show()
