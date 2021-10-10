package com.example.musicplayer;

class CyclicDoubleInt{
   class Node{
      int data;
      Node previous; //pointer to previous node
      Node next;     //pointer to next node
      
      public Node(int data){
         this.data = data;
         next = previous = null;
      }
      
      public Node(int data, Node next, Node previous){
         this.data = data;
         this.next = next;
         this.previous = previous;
      }
   }
   
   //initially head is set to null
   Node head = null;
   
   public void insertNode(int data){
      
      if(head == null){
         head = new Node(data);
      }
      
      else if(head.next == null){
         Node node = new Node(data, head, head);
         head.next = head.previous = node;
         
      }
      
      else{
         Node node = new Node(data, head, head.previous);
         Node temp = head.previous;
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
      Node current = head;

      do{
         str += current.data + ", ";
         current = current.next;
      }
      while (current != head);
        
      return str + "]";
   }
      
}
