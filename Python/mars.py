import pandas as pd
import matplotlib.pyplot as plt

attributes = ['Days','Distance']

# lectura archivos
analitical_frame = pd.read_csv("./distances_mars.txt", delimiter=',', usecols=attributes)

plt.plot(analitical_frame['Days'], analitical_frame['Distance'], label='analitical')
plt.legend()
plt.ylabel('Dist (m)')
plt.xlabel('Tiempo (s)')
plt.show()
