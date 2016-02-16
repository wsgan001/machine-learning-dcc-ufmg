# Lazy Associative Classification (C++, Eclat and Diff Eclat) #
[DOWNLOAD](http://machine-learning-dcc-ufmg.googlecode.com/files/lac-itamar-01-11-2012.tar.bz2)

This is an efficient implementation of the algorithm LAC published in the paper [Lazy Associative Classification. A. Veloso, W. Meira and M. Zaki. ICDM 2006](http://www.dcc.ufmg.br/~adrianov/papers/ICDM06/Veloso-icdm06.pdf).
LAC is a machine learning algorithm based on association rules.
The algorithm was implementad in C++ and used the algorithms Elact and Diff Eclat to generate the set of rules.
To compile the file type make. In the folder input the are two files to test the implementation. To run the test type make run. To see an explanation of the program's argument type make help. If you need to discretize your data you can use
the algorithm  [MultiIntervalDiscretization](MultiIntervalDiscretization.md).

The basic command to run the algorithm is:
```
	./lazy -i traningFile -t testFile -m 3 -c 0.001 -s 1 -e 500000 

```

Where _traningFile_ is the path of file containing the instances of traning, _testFile_ is the path of file containing the instances of test, _-m 3_ is the maximum number of attributes a rule can have, _-c 0.001_ is the minimum confidence's value and _-s 1_ is the minimum support's value that a rule must achieve to not be discarded and _-e 500000_  is the size of the rule's cache.

### Example of training file ###

```
0 CLASS=1 w[0]=esporte w[1]=jovem w[2]=M w[3]=nao
1 CLASS=0 w[0]=esporte w[1]=adulto w[2]=M w[3]=nao
2 CLASS=0 w[0]=utilitário w[1]=adulto w[2]=F w[3]=nao
3 CLASS=1 w[0]=popular w[1]=adulto w[2]=M w[3]=sim
4 CLASS=1 w[0]=esporte w[1]=idoso w[2]=M w[3]=nao
5 CLASS=0 w[0]=popular w[1]=adulto w[2]=F w[3]=nao
6 CLASS=1 w[0]=esporte w[1]=idoso w[2]=F w[3]=sim
7 CLASS=1 w[0]=popular w[1]=jovem w[2]=M w[3]=sim
8 CLASS=1 w[0]=utilitário w[1]=idoso w[2]=M w[3]=sim
9 CLASS=0 w[0]=popular w[1]=jovem w[2]=M w[3]=nao

```

In the training file the first column is the identifier of the instance, the second is the class and the others are the attributes. The number of attributes per instance can be different and if the position of the attribute does not matter you can remove the brackets. Normally, in text applications the attributes is used without brackets, ex.: w=word.
### Example of test file ###

```
10 CLASS=0 w[0]=popular w[1]=jovem w[2]=F w[3]=nao
11 CLASS=0 w[0]=popular w[1]=adulto w[2]=F w[3]=nao
12 CLASS=1 w[0]=utilitário w[1]=idoso w[2]=M w[3]=sim
13 CLASS=3 w[0]=bla w[1]=bla w[2]=bla w[3]=bla
```

In the test file you have to put the class of the instance, but it is not used by the classifier. Then, you can put any value here. But, if it is the correct label, it will be easier to check the behavior of the algorithm.

### Example of output ###
```
id= 10 label= 0 prediction= 0 correct= 1 entropy= 0.9754 rules= 15 prob[0]= 0.5920 prob[1]= 0.4080
id= 11 label= 0 prediction= 0 correct= 1 entropy= 0.9062 rules= 15 prob[0]= 0.6783 prob[1]= 0.3217
id= 12 label= 1 prediction= 1 correct= 1 entropy= 0.8800 rules= 12 prob[0]= 0.2989 prob[1]= 0.7011
id= 13 label= 3 prediction= 1 correct= 0 entropy= 0.9710 rules= 0 prob[0]= 0.4000 prob[1]= 0.6000
ACCURACY 0.7500
time: classify 0.000420

```