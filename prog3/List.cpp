#include "List.h"
#include <iostream.h>
/****************
* John Talton
* prog 3
****************/
void main () {
   SortedListAsLinked a;
   SortedListAsArray b;
   OrderedAsArray c;
   OrderedAsLinked d;
   
   a.Insert(1);
   b.Insert(2);
   c.Insert(3);
   d.Insert(4);
   
   
   cout << a[0];
   cout << b[0];   
   cout << c[0];
   cout << d[0];   
   
}


