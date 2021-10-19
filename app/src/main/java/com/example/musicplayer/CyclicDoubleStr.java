package com.example.musicplayer;

class CyclicDoubleStr {
   class StrNode {
      String data;
      StrNode previous; //pointer to previous node
      StrNode next;     //pointer to next node
      
      public StrNode(String data){
         this.data = data;
         next = previous = null;
      }
      
      public StrNode(String data, StrNode next, StrNode previous){
         this.data = data;
         this.next = next;
         this.previous = previous;
      }
   }
   
   //initially head is set to null
   StrNode head = null;
   
   public void insertNode(String data){
      
      if(head == null){
         head = new StrNode(data);
      }
      
      else if(head.next == null){
         StrNode node = new StrNode(data, head, head);
         head.next = head.previous = node;
         
      }
      
      else{
         StrNode node = new StrNode(data, head, head.previous);
         StrNode temp = head.previous;
         temp.next = node;
         head.previous = node;
                  
      }
   }

   public void deleteAllNodes() {
      if(head != null) {
         StrNode temp, current;
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
      StrNode current = head;

      do{
         str += current.data + ", ";
         current = current.next;
      }
      while (current != head);
        
      return str + "]";
   }
      
}
