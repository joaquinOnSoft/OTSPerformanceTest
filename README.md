# Output Transformation Server (OTS) performance test

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

Translated with DeepL.com (free version)
```