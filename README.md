# Output Transformation Server (OTS) performance test

This command line tool is designed to measure the time that **OTS** spends to process the files 
copied in and `input folder` and write the result of the process in the `output folder`.

Accepted parameters:

 - `-f` `--file` Path to an input file.
 - `-c` `--copies` Number of copies of the input file to make
 - `-i` `--input` Path to the input directory
 - `-o` `--output` Path to the output directory

## Invocation example

```
java -jar OTSPerformanceTest-24.09.29.jar -file C:\performance\example.pdf -input C:\performance\input -output C:\performance\output -copies 10000
```

## Output example

The execution of this command line tool, using these parameter values:

```
-file C:\performance\example.pdf
-input C:\performance\input
-output C:\performance\output
-copies 10000
```

Produces the following output:

```
File count 	 time (ms)
7	19
422	528
855	1030
1230	1534
1638	2040
2060	2545
2473	3051
2901	3557
3317	4068
3730	4581
4140	5089
4576	5602
4954	6113
5389	6636
5824	7151
6211	7664
6608	8177
7016	8700
7438	9224
7866	9740
8296	10256
8690	10785
9102	11300
9432	11828
9800	12346
10000	12861

Target file count reached: 10000
```

> Please note:
> - The output is .csv file using the tab as field separator
> - `example.pdf` is a 95 kb file

## Chat GPT prompt used to generate the base code

```
Write a command line application with Java 17 that receives 3 parameters:

 -f --file -> Path to an input file.
 -c --c --copies -> Number of copies of the input file to make
 -o --output -> Path to the output directory
 
Use the Apache Commons CLI library to validate the parameters: 

 - All parameters are mandatory.
 - The path of the -f parameter must be a file.
 - The path of the -o parameter must be a directory
 - The value of the -c parameter must be a positive integer greater than zero.
 
Once the parameters are validated, it launches two threads.

The first thread must copy the file of the -f parameter as many times as indicated by the -c parameter into the directory of the path indicated by the -o parameter. 
of the path indicated by the -o parameter

The second thread must count how many files are in the directory indicated by the -o parameter. It must repeat the file count
of files every 500 ms until the number of files is greater than or equal to the number indicated in the -c parameter.

Write unit tests to test the program

Use gradle to manage dependencies
```