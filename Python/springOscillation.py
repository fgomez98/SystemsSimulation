import pandas as pd
import matplotlib.pyplot as plt
import subprocess
import math


def calculateMSE(real_df: pd.DataFrame, aproximation_df: pd.DataFrame):
    acc = 0.0
    for i in range(0, len(real_df)):
        acc += (real_df['Position'][i] - aproximation_df['Position'][i]) ** 2
    return acc / len(real_df)


attributes = ['Time', 'Position']
names_frames = ['verlet', 'beeman', 'gear']
dts = [0.01, 0.001, 0.0001]
errors = [[0.0 for i in dts] for j in dts]

for j in range(0, len(dts)):
    # ejecucion
    subprocess.call(
        ['java', '-jar', './target/MolecularDinamicOscillator-jar-with-dependencies.jar', '-Dt=' + str(dts[j])])

    # lectura archivos
    analitical_frame = pd.read_csv("./spring-oscillator-analitical-simulation.txt", delimiter=',', usecols=attributes)
    verlet_frame = pd.read_csv("./spring-oscillator-verlet-simulation.txt", delimiter=',', usecols=attributes)
    beeman_frame = pd.read_csv("./spring-oscillator-beeman-simulation.txt", delimiter=',', usecols=attributes)
    gear_frame = pd.read_csv("./spring-oscillator-gear-simulation.txt", delimiter=',', usecols=attributes)
    data_frames = [verlet_frame, beeman_frame, gear_frame]

    # plots
    plt.plot(analitical_frame['Time'], analitical_frame['Position'], label='analitical')
    for i in range(0, len(data_frames)):
        # calculo y guardado de error
        mse = calculateMSE(analitical_frame, data_frames[i])
        errors[i][j] = mse

        plt.plot(data_frames[i]['Time'], data_frames[i]['Position'],
                 label=str(names_frames[i]) + ' ECM: ' + format(mse, '.3g') + ' (m²)')
    plt.legend()
    plt.axis([0, 5, -1, 1])
    plt.ylabel('Posición (m)')
    plt.xlabel('Tiempo (s)')
    plt.show()

# pasamos a log10
dts = [math.log10(dt) for dt in dts]
errors = [[math.log10(error) for error in row] for row in errors]

for i in range(0, len(errors)):
    plt.plot(dts, errors[i], label=str(names_frames[i]))
plt.legend()
plt.ylabel('log10(ECM)')
plt.xlabel('log10(dt) [s]')
plt.show()