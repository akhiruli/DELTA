import matplotlib.pyplot as plt

x_axis = [0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150]
y_axis_hierarchical = [0, 0.4605, 0.5637, 0.8932, 1.0162, 1.1948, 1.3775, 1.568, 1.8816, 1.9769, 2.1595]
y_axis_multi_user_sche = [0, 1.5584, 2.7424, 3.8879, 5.0649, 5.9905, 7.291, 8.4599, 9.9478, 11.018, 12.3021]
y_axis_selective_rand_rr = [0, 1.7156, 2.0454, 5.1055, 6.7343, 6.4607, 6.4647, 7.2444, 7.2048, 8.404, 7.5734]
y_axis_intelligent = [0, 0.9711, 1.6465, 2.0405, 2.5027, 3.0455, 3.3914, 3.6678, 4.5382, 4.8044, 5.5849]

plt.plot(x_axis, y_axis_hierarchical, marker='x', label='DELTA', linewidth=1, color='blue')
plt.plot(x_axis, y_axis_multi_user_sche, marker='o', label='Multi-user-scheduling', linewidth=1, color='red')
plt.plot(x_axis, y_axis_selective_rand_rr, marker='^', label='Selective-random-RR', linewidth=1, color='green')
plt.plot(x_axis, y_axis_intelligent, marker='v', label='Intelligent-TO', linewidth=1, color='orange')

plt.title('UE Energy Consumption')

plt.ylabel('Average Energy Consumption at UE (In Joules)')
plt.xlabel('No. of Applications')
plt.legend(loc='upper center')
plt.savefig('energy_consumption.png', bbox_inches='tight')
#plt.show()
