package com.example.musicplayer;

class CyclicDoubleInt{
   class IntNode {
      int data;
      IntNode previous; //pointer to previous node
      IntNode next;     //pointer to next node
      
      public IntNode(int data){
         this.data = data;
         next = previous = null;
      }
      
      public IntNode(int data, IntNode next, IntNode previous){
         this.data = data;
         this.next = next;
         this.previous = previous;
      }
   }
   
   //initially head is set to null
   IntNode head = null;
   
   public void insertNode(int data){
      
      if(head == null){
         head = new IntNode(data);
      }
      
      else if(head.next == null){
         IntNode node = new IntNode(data, head, head);
         head.next = head.previous = node;
         
      }
      
      else{
         IntNode node = new IntNode(data, head, head.previous);
         IntNode temp = head.previous;
         temp.next = node;
         head.previous = node;
                  
      }
   }

   public void deleteAllNodes() {
      if(head != null) {
         IntNode temp, current;
         current = head.next;

         while (current != head) {
            temp = current.next;
            current = null;
            current = temp;
         }
         head = null;

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
      IntNode current = head;

      do{
         str += current.data + ", ";
         current = current.next;
      }
      while (current != head);
        
      return str + "]";
   }
      
}
