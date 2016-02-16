# Multi-Interval Discretization of Continuous-Valued Attributes for Classification Learning (C++) #
[DOWNLOAD](http://machine-learning-dcc-ufmg.googlecode.com/files/discretize-itamar-19-11-2013.tar.bz2)

This is an implementation of the algorithm presented in the paper:

[Fayyad, Usama M.; Irani, Keki B. (1993) "Multi-Interval Discretization of Continuous-Valued Attributes for Classification Learning". hdl:2014/35171. , Proceedings of the International Joint Conference on Uncertainty in AI (Q334 .I571 1993), pp. 1022-1027](http://trs-new.jpl.nasa.gov/dspace/handle/2014/35171)

Discretization refers to the process of converting or partitioning continuous attributes, features or variables to discretized or nominal attributes/features/variables/intervals.
We used this algorithm, because our associative algorithm, [LACLazyAssociativeAlgorithmCpp](LACLazyAssociativeAlgorithmCpp.md), only works with discretized attributes.

To compile the file type make. In the folder input the are two files to test the implementation. To run the test type make run. To see an explanation of the program's argument type make help.

The basic command to run the algorithm is:

```
./discretize -i itraining.csv -t test.csv -r 1-4 -p -l
```

Where -i is the traning file, -t the test file (optional)m -r the columns to be discretized (separeted by comma, you can use intervals too, ex.: 1-4), -p to print the cut points and -l to write the output file in LAC's format.

### Example of input file ###
```
151,5.1,3.5,1.4,0.2,Iris-setosa
152,4.9,3.0,1.4,0.2,Iris-setosa
153,4.7,3.2,1.3,0.2,Iris-setosa
154,4.6,3.1,1.5,0.2,Iris-setosa
155,5.0,3.6,1.4,0.2,Iris-setosa
156,5.4,3.9,1.7,0.4,Iris-setosa
157,4.6,3.4,1.4,0.3,Iris-setosa
158,5.0,3.4,1.5,0.2,Iris-setosa
159,4.4,2.9,1.4,0.2,Iris-setosa
160,4.9,3.1,1.5,0.1,Iris-setosa
```

The last column must be the class of the instance. If you pass the test file, this column will be ignored. If you want the output to be in LAC's format, instead of csv, the first column must be the identifier of the instance.

### Example of output file ###
```
151 w[1]=5.55 w[2]=INF w[3]=2.45 w[4]=0.8 CLASS=Iris-setosa
152 w[1]=5.55 w[2]=3.35 w[3]=2.45 w[4]=0.8 CLASS=Iris-setosa
153 w[1]=5.55 w[2]=3.35 w[3]=2.45 w[4]=0.8 CLASS=Iris-setosa
154 w[1]=5.55 w[2]=3.35 w[3]=2.45 w[4]=0.8 CLASS=Iris-setosa
155 w[1]=5.55 w[2]=INF w[3]=2.45 w[4]=0.8 CLASS=Iris-setosa
156 w[1]=5.55 w[2]=INF w[3]=2.45 w[4]=0.8 CLASS=Iris-setosa
157 w[1]=5.55 w[2]=INF w[3]=2.45 w[4]=0.8 CLASS=Iris-setosa
158 w[1]=5.55 w[2]=INF w[3]=2.45 w[4]=0.8 CLASS=Iris-setosa
159 w[1]=5.55 w[2]=2.95 w[3]=2.45 w[4]=0.8 CLASS=Iris-setosa
160 w[1]=5.55 w[2]=3.35 w[3]=2.45 w[4]=0.8 CLASS=Iris-setosa
```
This output is in LAC's format, you can see the explanation here [LACLazyAssociativeAlgorithmCpp](LACLazyAssociativeAlgorithmCpp.md).