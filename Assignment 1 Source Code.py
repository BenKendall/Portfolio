Algo = int(input("Please select either Algorithm 1 or Algorithm 2 by entering 1 or 2: "))

if Algo == 1:
    b = int(input("Enter the Value of b: "))
    n = int(input("Enter the Value of n: "))
    p = 1
    
    for _ in range(n):
        p = p * b
        
    print(str(b) + " to the power of " + str(n) + " = " + str(p))

elif Algo == 2:
    b = int(input("Enter the Value of b: "))
    n = int(input("Enter the Value of n: "))

    if n == 0:
        p = 1
    elif n > 0:
        p = b ** (n//2)
        if n % 2 == 0:
            p = p * p
        else:
            p = b * p * p

    print(str(b) + " to the power of " + str(n) + " = " + str(p))
    
