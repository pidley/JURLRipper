package org.codegaucho.pidley.JURLRipper;

import java.io.BufferedReader ;
import java.io.File ;
import java.io.FileInputStream;
import java.io.InputStreamReader ;
import java.net.URL ;

public class JURLRipper {
private String  URL         =   null ;
private String  urlText     =   null ;
private boolean isPacked    =   true ;

   private JURLRipper() { }
   
   public JURLRipper(File file) throws InstantiationException {
   String newURL   =   null ;
   boolean isPacked   =   false ;
   
      System.out.println("file length is " + file.length()) ;
      setURLText(returnURLText(file)) ;
      while ( !isPacked ) {
         newURL   =   packURLText(this.getURLText()) ;
         setURLText(newURL) ;
         isPacked = getIsPacked() ;
      }
      newURL   =   normalizeURLText(this.getURLText()) ;
      System.out.println("packed string: " + this.getURLText()) ;
      
   }
   
   public JURLRipper(String URL) throws InstantiationException {
   String newURL   =   null ;
   
      if ( isURLReachable(URL) ) {
         setURL(URL) ;
         setURLText(returnURLText()) ;
         newURL   =   packURLText(URL) ;
         System.out.println("Is packed: " + getIsPacked()) ;
      } else 
         throw new InstantiationException("URL not reachable") ;
   }
   
   private char[] createPackedText(String oldText, boolean[] packArray) {
   char[]  newText      =   null ;
   int     falseCount   =   0 ;
   int     newPosition  =   0 ; 
      
      for ( int i = 0 ; i < packArray.length ; i++ )
         falseCount  +=   ((packArray[i] == false) ? 1 : 0) ;
      
      newText         =   new char[oldText.length() - falseCount] ;
      for ( int i = 0 ; i < oldText.length() ; i++ ) {
         if ( packArray[i] )
            newText[newPosition++]  =  oldText.charAt(i) ;
      }
      
      return newText ;
   }
   
   private boolean[] createPackArray(int size) {
   boolean[] packArray   =   null ;
   
      packArray         =   new boolean[size] ;
      for ( int i = 0 ; i < packArray.length ; i++ )
         packArray[i]   =   true ;
      
      return packArray ;
   }
   public boolean getIsPacked() {
   
      return isPacked ;
   }
   
   public String getURL() {
       
      return URL ;
   }
   
   public String getURLText() {
      
      return urlText ;
   }
     
   private boolean isURLReachable(String url) {
   Object    content      =   null ;
   URL       connection   =   null ;
   boolean   reachable    =   false ;
   
      if ( (url != null) && (!url.isEmpty()) ) {
         try {
            connection      =   new URL(url) ;
            content         =   connection.getContent() ;
         } catch (Exception e) {
            e.printStackTrace() ;
            reachable       =   false ;
         } 
      } else 
         reachable          =   false ;
      
      return reachable ;
   }
   
   private String normalizeURLText(String urlText) {
   String    newURLText      =   null ;
   String    tag             =   null ;
   boolean   normalArray[]   =   null ;
   boolean   tagClosed       =   true ;
   boolean   tagOpened       =   false ;
   char      theCharacter    =   ' ' ;
   int       blankSpread     =   -1 ;
   int       nextIndex       =   -1 ;
   int       position        =   0 ;
   
      newURLText                                       =   new String() ; 
      for ( position = 0 ; position < urlText.length() ; position++ ) {
         theCharacter                                  =   urlText.charAt(position) ;
         switch ( theCharacter ) {  
             case '<' :   tagOpened   =   true ;
                          tagClosed   =   false ;
                          if ( urlText.charAt(position + 1) != '/' ) 
                             tag      =   returnTag(urlText, (position + 1)) ;
                          break ;
             case '/' :   if ( urlText.charAt(position + 1) == '>' ) {
                             newURLText  +=  "></" + tag ;
                             continue ;
                          }
                          break ;
             case '>' :   tagOpened   =   false ;
                          tagClosed   =   true ;
                          break ;
         }    
         
         newURLText  += theCharacter ;
      }
      
      return null ;
   }
   
   private String packURLText(String urlText) {
   boolean   tagOpened      =   false ;
   boolean   packArray[]    =   null ;
   char      theCharacter   =   0x0000 ;
   char      newURLText[]   =   null ;
   int       position       =   0 ;
   
      newURLText                                       =   new char[urlText.length()] ;
      packArray                                        =   createPackArray(urlText.length()) ;
      setIsPacked(true) ;
      
      for ( position = 0 ; position < newURLText.length ; position++ ) {
         theCharacter                                  =   urlText.charAt(position) ;
         switch ( theCharacter ) {
             case '<' :   tagOpened                    =   true ;
                          packArray[position]          =   true ;
                          packArray[position + 1]      =   ((urlText.charAt(position + 1) == ' ') ? false : true) ;
                          break ;
             case '>' :   tagOpened                    =   false ;
                          packArray[position]          =   true ;
                          packArray[position - 1]      =   ((urlText.charAt(position - 1) == ' ') ? false : true) ;
                          break ;
             case '=' :   if ( tagOpened ) {
                             packArray[position - 1]   =   ((urlText.charAt(position - 1) == ' ') ? false : true) ;
                             packArray[position + 1]   =   ((urlText.charAt(position + 1) == ' ') ? false : true) ;
                          }
                          break ;
             default  :   if ( packArray[position] != false )   
                             packArray[position]       =   true ;
         }

         if ( packArray[position] == false )
            setIsPacked(false) ;
      }

      return new String(createPackedText(urlText, packArray)) ;
   }
   
   private String returnTag(String urlText, int position) {
   String tag             =   null ;
   char   theCharacter    =   0x0000 ;
   int    blankIndex      =   -1 ;
      
      tag          =   new String() ;
      blankIndex   =   position ;
      while ((theCharacter = urlText.charAt(blankIndex++)) != ' ')
         tag      += theCharacter ;
      
      return tag ;
   }
       
   private String returnURLText(File file) {
   BufferedReader    bufferedReader      =   null ;
   InputStreamReader inputStreamReader   =   null ;
   FileInputStream   fileInputStream     =   null ;
   char              text[]              =   null ;
   int i ;
   
      try {
         fileInputStream     =   new FileInputStream(file) ;
         inputStreamReader   =   new InputStreamReader(fileInputStream, "UTF8") ;
         bufferedReader      =   new BufferedReader(inputStreamReader) ;
         text                =   new char[(int)file.length()] ;
         
         i = bufferedReader.read(text) ;
         bufferedReader.close() ;
      } catch (Exception e) {
         e.printStackTrace(); 
      }      
      
      return new String(text) ;
   }
   
   private String returnURLText() {
   BufferedReader      bufferedReader      =   null ;
   InputStreamReader   inputStreamReader   =   null ;
   String              buffer              =   null ;
   String              urlText             =   null ;
   URL                 url                 =   null ;
   
      if ( isURLReachable(getURL()) ) {
         try {
            url                 =   new URL(getURL()) ;
            inputStreamReader   =   new InputStreamReader(url.openStream()) ;
            bufferedReader      =   new BufferedReader(inputStreamReader) ;
            urlText             =   new String() ;
         
            while ( (buffer   =   bufferedReader.readLine()) != null ) {
               urlText  +=   buffer + "\\r\\n" ;    
            }
         
         } catch (Exception exception) {
            exception.printStackTrace() ;
         }
      }  
      
      return urlText ;
   }
   
   private final void setIsPacked(boolean isPacked) {
       
      this.isPacked   =   isPacked ;
   }
   
   private final void setURL(String URL) {
  
      if ( (URL != null) && (!URL.isEmpty()) )
         try {
            this.URL   =   new String(URL) ;
         } catch (Exception exception) {
            exception.printStackTrace() ;
         }
   }
   
   private final void setURLText(String urlText) {
       
      if ( (urlText != null) && (!urlText.isEmpty()) )
         try {
            this.urlText   =   urlText ;
         } catch (Exception exception) {
            exception.printStackTrace() ;
         }
   }
}