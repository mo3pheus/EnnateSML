# mlMonk  
A Java framework for practicing Machine Learning algorithms. The idea is to implement ML algorithms using a high level programming language. I have written the code to extract, load and transform the data into internal classes that you can easily play with. 

Class Data - is the building block for mlMonk - it will hold a single row of numeric data and a String representing a classId. The numeric features are held in a Double[] called fields, its length is stored in a separate variable called numFields and classId is the variable representing the classification or original label of your sample. 

For e.g. when you load the file Iris.data.txt into the mlMonk framework - will load it into List<Data> trainingData and List<Data> testingData respectively. Each element in these lists is of the type Data - so a single row of data such as the one shown below:

5.0,3.6,1.4,0.2,Iris-setosa

is held in Data as Double[] fields = {5.0d,3.6d,1.4d,0.2d}; numFields = 4; and classId = "Iris-setosa"

All operations that follow operate on this basic building block. 

Please email any questions you have to skorgaonkar@egen.solutions.

Copyright (c) 2017 Egen Solutions.
