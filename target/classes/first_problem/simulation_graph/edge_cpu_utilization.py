import matplotlib.pyplot as plt

x_axis = [0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150]
y_axis_hierarchical = [0, 34.0593/6, 52.2596/6, 48.5305/6, 57.5847/6, 58.5319/6, 61.8633/6 , 63.941/6, 61.8172/6, 67.0532/6, 69.5614/6]
y_axis_multi_user_sche = [0, 421.36/6, 455.994/6, 473.9977/6, 489.1049/6, 493.2699/6, 498.1754/6, 503.324/6, 498.6485/6, 509.2382/6, 507.7263/6]
y_axis_selective_rand_rr = [0, 26.5017/6, 40.3319/6, 24.7242/6, 25.9203/6, 31.34/6, 38.8306/6, 42.0076/6, 49.4952/6, 45.8596/6, 57.9212/6]
y_axis_intelligent = [0, 113.979/6, 130.6192/6, 145.5785/6, 158.4901/6, 161.6715/6, 169.5709/6, 181.7302/6, 177.6656/6, 183.5252/6, 179.1498/6]

plt.plot(x_axis, y_axis_hierarchical, marker='x', label='DELTA', linewidth=1, color='blue')
plt.plot(x_axis, y_axis_multi_user_sche, marker='o', label='Multi-user-scheduling', linewidth=1, color='red')
plt.plot(x_axis, y_axis_selective_rand_rr, marker='^', label='Selective-random-RR', linewidth=1, color='green')
plt.plot(x_axis, y_axis_intelligent, marker='v', label='Intelligent-TO', linewidth=1, color='orange')  
plt.title('CPU Utilisation of Edge')

plt.ylabel('CPU Utilisation of Edge (%)')
plt.xlabel('No. of Applications')
plt.legend(loc=(0.5, 0.6))
#plt.legend(loc='best')
plt.savefig('edge_cpu_util.png', bbox_inches='tight')
#plt.show()
