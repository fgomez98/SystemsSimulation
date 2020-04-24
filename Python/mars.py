import pandas as pd
import matplotlib.pyplot as plt

attributes = ['Days','Distance']

data_frame = pd.read_csv("./distances_mars.txt", delimiter=',', usecols=attributes)
data_frame.plot.scatter(x='Days', y='Distance', c='DarkBlue', legend=None)

plt.ylabel('Distancia minima a marte (km)')
plt.xlabel('Tiempo (dias)')
plt.show()

attributes = ['Initial Velocity','Time']

data_frame = pd.read_csv("./distances_velocity_mars.txt", delimiter=',', usecols=attributes)
data_frame.plot.scatter(x='Initial Velocity', y='Time', c='DarkBlue', legend=None)

plt.ylabel('Tiempo a marte (s)')
plt.xlabel('Velocidad inicial (km/s)')
plt.show()
