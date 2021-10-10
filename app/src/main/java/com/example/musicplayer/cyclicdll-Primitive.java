package com.example.musicplayer;

class CyclicDoublePrim<T>{
   class Node<T>{
      T data;
      Node previous; //pointer to previous node
      Node next;     //pointer to next node
      
      public Node(T data){
         this.data = data;
         next = previous = null;
      }
      
      public Node(T data, Node next, Node previous){
         this.data = data;
         this.next = next;
         this.previous = previous;
      }
   }
   
   //initially head is set to null
   Node head = null;
   
   public void insertNode(T data){
      
      if(head == null){
         head = new Node<T>(data);
      }
      
      else if(head.next == null){
         Node<T> node = new Node<T>(data, head, head);
         head.next = head.previous = node;
         
      }
      
      else{
         Node<T> node = new Node<T>(data, head, head.previous);
         Node<T> temp = head.previous;
         temp.next = node;
         head.previous = node;
                  
      }
   }
   
   /*public void printList() {
      Node<T> current = head;
   
      if (head == null) 
         System.out.println("[]");
      else if (head.next == null)
         System.out.println(current.data);
      
      else{
         do{
            System.out.print(current.data + " ");
            current = current.next;
         }      
         while(current != head);
         System.out.println();

         }
   }*/
   
   public String toString() {
      if (head == null)
         return "[]";
      else if (head.next == null)
         return "[" + head.data + "]";

      String str = "[";
      Node<T> current = head;

      do{
         str += current.data + ", ";
         current = current.next;
      }
      while (current != head);
        
      return str + "]";
   }
      
}
