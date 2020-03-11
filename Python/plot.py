import matplotlib.pyplot as plt
import sys

if len(sys.argv) != 2:
    print('missing parameters\n')
    print('[id particula]')
    exit(1)

# abrimos los archivos dinamic, static y nearby para importar la informacion de las particulas
# todo: deberia poder pasarlos por argumento
static_file = open("static.csv", "r")
dinamic_file = open("dinamic.csv", "r")
nearby_file = open("nearby.csv", "r")

# read retorna un str del archivo que luego es separado por lineas cada una correspondiente a la particula
static_data = static_file.read().splitlines()
dinamic_data = dinamic_file.read().splitlines()
nearby_data = nearby_file.read().replace('[', '').replace(']', '').splitlines()

# buscamos metadata en los archivos

n = int(static_data[0])
l = float(static_data[1])

marked_particle = int(sys.argv[1]) # particula consultada

particles = [] # necesitamos que las particulas esten ordenadas segun su posicion (que seria su ID)
particles_nearby = [] # particulas vecinas a la consultada

# creamos el grafico
fig, ax = plt.subplots()
ax.grid(True)
plt.axis([0, l, 0, l])

for i in range(0, n):
    data = nearby_data[i].split(' ')
    id = int(data[0])
    if id == marked_particle:
        for j in range(0, len(data)-2):
            particles_nearby.append(int(data[j+2]))  # defasado 1 lugar por el id de la particula


def is_nearby(particle_id):
    for i in range(0, len(particles_nearby)):
        if particles_nearby[i] == particle_id:
            return True
    return False


for i in range(0, n):
    radius = float(static_data[i+2])  # defasado 2 lugares por N y L
    position = dinamic_data[i+1].split(' ')  # defasado 1 lugar por el tiempo To
    x = float(position[0])
    y = float(position[1])
    if i == marked_particle:
        ax.add_artist(plt.Circle((x, y), radius, color='green'))
    elif is_nearby(i):
        ax.add_artist(plt.Circle((x, y), radius, color='red'))
    else:
        ax.add_artist(plt.Circle((x, y), radius, color='blue'))

# mostramos el grafico
plt.show()

