package com.example.musicplayer;

import android.util.Log;

/*
Alessa's Implementation of a CDLL (Slighly Rearranged by
ya boy to make it work better for the song objects)
 */
class CyclicDouble {
   //initially head is set to null
   Node head = null;
   private final String TAG = "Traverse";
   
   public void insertNode(Song song){
      
      if(head == null){
         head = new Node(song);
      }
      
      else if(head.next == null){
         Node node = new Node(song, head, head);
         head.next = head.previous = node;
         
      }
      
      else{
         Node node = new Node(song, head, head.previous);
         Node temp = head.previous;
         temp.next = node;
         head.previous = node;
                  
      }
   }
   
   public String toString() {
      if (head == null)
         return "[]";
      else if (head.next == null)
         return "[" + head.song.getTitle() + "]";

      String str = "[";
      Node current = head;

      do{
         str += current.song.getTitle() + ", ";
         current = current.next;
      }
      while (current != head);
        
      return str + "]";
   }

   public Song getSong(Song song) {
      return song;
   }
      
}

   class Node {
   Song song;
   Node previous; //pointer to previous node
   Node next;     //pointer to next node

   public Node(Song song){
      this.song = song;
      next = previous = null;
   }

   public Node(Song song, Node next, Node previous){
      this.song = song;
      this.next = next;
      this.previous = previous;
   }
}
