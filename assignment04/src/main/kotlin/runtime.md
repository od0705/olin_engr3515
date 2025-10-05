I ran runtime tests for all four of my algorithms at arrays of size 10, 100, 1000, and 10000.
The lists were randomly generated using kotlin.random and each experiment was performed three times and averaged. 
It appears that, for larger sets of data, merge was the fastest method, followed by insertion sort.
However, for smaller data sets, selection sort was the fastest.
I was surprised that heap sort often came out to be the slowest, but this may be due to the runtime of assembling
the heap one unit at a time.