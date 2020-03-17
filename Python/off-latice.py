import subprocess
import numpy as np
import matplotlib.pyplot as plt

#.......................................................................................................................
#                    Densidad constante(=4) variacion de ruido
#.......................................................................................................................
#
#

# N = [40, 100, 400, 4000]
# L = [3.1, 5, 10, 31.6]
# n = [0.1, 1.2, 2.5, 3.6, 4.7, 5.0]
#
# for i in range(0, len(N)):
#     fig, axs = plt.subplots(len(n), figsize=(10,30))
#     for j in range(0, len(n)):
#         subprocess.call(['java', '-jar', 'CellularAutomata-jar-with-dependencies.jar', '-Dl=' + str(L[i]), '-Dn=' + str(N[i]), '-Dr=' + str(n[j]), '-Dt=5000'])
#         velocity_averages = np.loadtxt("./off-latice-va-" + str(N[i]) + "-" + str(n[j]) + ".txt", delimiter='\n')
#         time = [i for i in range(1, len(velocity_averages) + 1)]
#         axs[j].plot(time, velocity_averages)
#         axs[j].set_title("N:" + str(N[i]) + " Ruido:" + str(n[j]))
#         axs[j].set_xlabel('time (s)')
#         axs[j].set_ylabel('va')
#         axs[j].set_yticks(np.linspace(min(velocity_averages), max(velocity_averages), num=20))
#         axs[j].grid()
#
#     # plt.grid(True)
#     plt.show()
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

# fig = plt.figure()
# N = [40, 100, 400, 4000]
# X = [0.1, 1.2, 2.5, 3.6, 4.7, 5.0]
#
# Y40 = np.array([[1, 0.94, 0.75, 0.53, 0.25, 0.2], [0.99, 0.94, 0.74, 0.54, 0.25, 0.21], [1, 0.93, 0.74, 0.53, 0.24, 0.19]])
# mean40 = np.mean(Y40, axis=0)
# std40 = np.std(Y40, axis=0)
#
# Y100 = np.array([[1, 0.93, 0.72, 0.47, 0.19, 0.13], [1, 0.93, 0.71, 0.46, 0.15, 0.12], [1, 0.94, 0.72, 0.47, 0.15, 0.12]])
# mean100 = np.mean(Y100, axis=0)
# std100 = np.std(Y100, axis=0)
#
# Y400 = np.array([[1, 0.92, 0.7, 0.37, 0.055, 0.05], [1, 0.93, 0.69, 0.35, 0.063, 0.058], [1, 0.92, 0.69, 0.38, 0.062, 0.057]])
# mean400 = np.mean(Y400, axis=0)
# std400 = np.std(Y400, axis=0)
#
# Y4000 = np.array([[1, 0.8, 0.45, 0.19, 0.02, 0.015], [0.99, 0.9, 0.6, 0.21, 0.025, 0.018], [1, 0.89, 0.57, 0.18, 0.023, 0.019]])
# mean4000 = np.mean(Y4000, axis=0)
# std4000 = np.std(Y4000, axis=0)
#
# plt.errorbar(X, mean40, yerr=std40, label='N=40', fmt='-o')
# plt.errorbar(X, mean100, yerr=std100, label='N=100', fmt='-o')
# plt.errorbar(X, mean400, yerr=std400, label='N=400', fmt='-o')
# plt.errorbar(X, mean4000, yerr=std4000, label='N=4000', fmt='-o')
# plt.legend(loc='best', shadow=True, fontsize='x-large')
# plt.title("Va en funcion del rudio")
# plt.ylabel('Va')
# plt.xlabel('ruido')
# plt.show()

#
#
#.......................................................................................................................
#                    Ruido constante variacion de densidad
#.......................................................................................................................
#
#
# p = [0.1, 0.5, 1.0, 2.0, 5.0, 8.0]
# N = [40, 200, 400, 800, 2000, 3200]
# L = 20
# n = [2.0]
#
# for i in range(0, len(n)):
#     fig, axs = plt.subplots(len(N), figsize=(10, 30))
#     for j in range(0, len(N)):
#         subprocess.call(['java', '-jar', 'CellularAutomata-jar-with-dependencies.jar', '-Dl=' + str(L), '-Dn=' + str(N[j]), '-Dr=' + str(n[i]), '-Dt=5000'])
#         velocity_averages = np.loadtxt("./off-latice-va-" + str(N[j]) + "-" + str(n[i]) + ".txt", delimiter='\n')
#         time = [i for i in range(1, len(velocity_averages) + 1)]
#
#         axs[j].plot(time, velocity_averages)
#         axs[j].set_title("Densidad:" + str(p[j]) + " Ruido:" + str(n[i]))
#         axs[j].set_xlabel('time (s)')
#         axs[j].set_ylabel('va')
#         axs[j].set_yticks(np.linspace(min(velocity_averages), max(velocity_averages), num=20))
#         axs[j].grid()
#
#         # ax.set_title("Densidad:" + str(p[j]) + " Ruido:" + str(n[i]))
#         # ax.plot(time, velocity_averages)
#         # ax.set_xlabel('time (s)')
#         # ax.set_ylabel('va')
#
#     plt.show()
#.......................................................................................................................
#                   Grafico con los datos recopilados de Ruido constante variacion de densidad
#.......................................................................................................................
#
#
# fig = plt.figure()
#
# X = [0.1, 0.5, 1.0, 2.0, 5.0, 8.0]
# Y = np.array([[0.19, 0.26, 0.44, 0.64, 0.76, 0.81], [0.18, 0.32, 0.53, 0.63, 0.77, 0.82], [0.14, 0.28, 0.41, 0.67, 0.77, 0.81]])
# mean = np.mean(Y, axis=0)
# std = np.std(Y, axis=0)
#
# plt.errorbar(X, mean, yerr=std, fmt='-o')
# plt.title("Va en funcion de densidad")
# plt.ylabel('Va')
# plt.xlabel('densidad')
# plt.show()