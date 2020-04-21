import pandas as pd
import matplotlib.pyplot as plt

attributes = ['Time', 'Position']
analitical_frame = pd.read_csv("./spring-oscillator-analitical-simulation.txt", delimiter=',', usecols=attributes)
verlet_frame = pd.read_csv("./spring-oscillator-verlet-simulation.txt", delimiter=',', usecols=attributes)
beeman_frame = pd.read_csv("./spring-oscillator-beeman-simulation.txt", delimiter=',', usecols=attributes)

data_frames= [analitical_frame, verlet_frame, beeman_frame]
names_frames= ['analitical', 'verlet', 'beeman']

for i in range(0, len(data_frames)):
    plt.plot(data_frames[i]['Time'], data_frames[i]['Position'], label=str(names_frames[i]))
plt.show()

