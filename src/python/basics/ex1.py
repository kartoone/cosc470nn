print("hello, world")
sizes=[784,15,10]
biases=[y for y in sizes[1:]]
weights=["%d %d" %(y,x) for x,y in zip(sizes[:-1], sizes[1:])]
print(biases)
print(weights)

nums = list(range(1,9))
print(nums)

for i in range(1,9):
    print(i)
