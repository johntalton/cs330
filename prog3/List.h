#ifndef _List_H
#define _List_H
#define SIZE 200
#include <iostream.h>


/********************************
*
*
********************************/
class List {
   public:
      List() {
      }
      ~List() {
      }
      virtual void Insert(int item){}
      virtual int Withdraw(){ return 0; }
      virtual int Find(int item){ return -1; }
      int IsMember(int item){ 
         if(Find(item) > -1)
         {
            return 1; 
         }else{
            return 0; 
         }
      }
   protected:
      //nothing here   
};

/********************************
*
*
********************************/
class SortedList: public List {
public:
   SortedList () {
   }
   virtual int FindPosition(int item){ return -1;};
   /* Place holder for lower classes */
   
   virtual operator [] (int i){ return 0; };
   /* place holder for lowwer classes */
   
   virtual void Withdraw(int pos){};
   /* place hoder for lower classes */
   
};
/********************************
*
*
********************************/
class OrderedList: public List {
public:
   OrderedList(){ 
   }
   
   virtual int FindPosition(int item){ return -1; };
   /* Place holder */
   
   virtual int operator [](int i){ return 0; };
   /* Place holder */
   
   virtual int Withdraw(int Position){ return 0; };
   /* Place holder */
   
   virtual void InsertAfter(int pos){};
   /* Place holder */
   
   virtual void InsertBefore(int pos){};
   /* Place holder */

};

/********************************
* This acraully creates the array
* and all other classes inharet it
*
********************************/
class OrderedAsArray: public OrderedList{
public:
   OrderedAsArray(){ curLength = 0; };
   
   int FindPosition(int item){ 
      /* finds a element and return its position
         else returns -1
      */
      return -1;
   };
   
   
   void Insert(int item){
      /* insert an item at the end of the list 
      */list[curLength++] = item;
      
   };
   
   int operator [](int i){ return list[i];};
   
   int Withdraw(int Position){
      /* Withdraw an Item at the position and return it
        int temp = List[Position];
        // move all items to the left one 
        return temp;
      */
      return 0;
   };
   
   void InsertAfter(int pos,int item){
      /* Shift all items from pos + 1  
         up one position then inserts item in List[pos + 1].
         Do this only if length is less then SIZE
         
      */
   };
   void InsertBefore(int pos,int item){ 
      /* Shift all items from pos - 1 
         up one position thn inserts item in List[pos - 1].
         Do this only if length is less then SIZE
         
      */
   };
 
protected:
   int curLength;
   int list[SIZE]; 
};
/********************************
* This actually Created the Linked list 
* of Nodes and manages it .. all other
* classes inharet from its list
*
********************************/
class Node{
   int data;
   int *Next;
};

class OrderedAsLinked: public OrderedList{
public:
   OrderedAsLinked(){
      /* Creat a new list with empty pointer Head 
      and a pointer Tail that points to head
      */
   };
   int FindPosition(int item){ 
      /* finds a element and return its position
         else returns -1
      */
      return -1;
   };
   
   int operator [](int i){ 
      /* findes a node at the i'th position
      in the list and returns it. If the lists length
      is less than that positon return 0
      */
      return 0;
   };
   
   void Insert(int item){
      /* Insterts a new Node in the linked
      list and updates pointers as neaded
      */
   }
   
   int Withdraw(int Position){
      /* Withdraw an Item at the position and return it
      else returns 0
      */
      return 0;
   };
   
   void InsertAfter(int pos,int item){
      /* Creats a new Node and puts it in the list.      
      changing pointers as nesaccary
      */
   };
   void InsertBefore(int pos,int item){ 
      /*  Creats a new Node and puts it in the list
      canging pointers as nececary
      */
   };
   
protected:
   int *Head;
   int *Tail;
};
/********************************
*
*
********************************/
class SortedListAsArray: public OrderedAsArray, public SortedList{
public:
   SortedListAsArray():SortedList(),OrderedAsArray(){};

   void Insert(int item){
      int posFound = 0;
      /* Inserts an item in the propter place int 
      the array and shifts the array items down one.
      Only if curSize < SIZE
      This must be done to preserve the Sorted Order
      */
      list[posFound] = item;
      curLength++;
   };
  
   void InsertAfter(int pos,int item){
      /* should return an ERROR. canot insert 
      arbitrarly into  a sorted list
      */
   };
   void InsertBefore(int pos,int item){ 
      /* should return an ERROR. canot insert 
      arbitrarly into  a sorted list
      */
   };
};
/********************************
*
*
********************************/
class SortedListAsLinked: public SortedList, public OrderedAsLinked{
public:
   SortedListAsLinked():SortedList(),OrderedAsLinked(){};
   
   void Insert(int item){
      /* Inserts an item in the propter place into
      the linked list.
      This must be done to preserve the Sorted Order
      */
   };
  
   void InsertAfter(int pos,int item){
      /* should return an ERROR. canot insert 
      arbitrarly into  a sorted list
      */
   };
   void InsertBefore(int pos,int item){ 
      /* should return an ERROR. canot insert 
      arbitrarly into  a sorted list
      */
   };
};

#endif _List_H
