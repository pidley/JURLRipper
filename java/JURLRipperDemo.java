package org.codegaucho.pidley.JURLRipper;

import java.io.File;

public class JURLRipperDemo {

   public static void main(String[] args) {
   JURLRipper   jurlRipper   =   null ;
   
      try {
         //jurlRipper   =   new JURLRipper("http://google.com") ;
         jurlRipper   =   new JURLRipper(new  File("c:\\users\\b\\bob.txt")) ;
      } catch (Exception exception) {
         exception.printStackTrace() ;
      }
   }
}
