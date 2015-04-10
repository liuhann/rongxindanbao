 package com.ever365.utils;
 
 import java.io.PrintStream;
 import java.util.Random;
 
 public class UUID
 {
   public static String[] chars = { "a", "b", "c", "d", "e", "f", 
     "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", 
     "t", "u", "v", "w", "x", "y", "z" };
 
   public static final String generate()
   {
     return java.util.UUID.randomUUID().toString();
   }
 
   public static String generateShortUuid()
   {
     StringBuffer shortBuffer = new StringBuffer();
     String uuid = generate().replace("-", "");
     for (int i = 0; i < 3; i++) {
       String str = uuid.substring(i * 4, i * 4 + 4);
       int x = Integer.parseInt(str, 16);
       shortBuffer.append(chars[(x % 26)]);
     }
     shortBuffer.append(new Double(Math.random() * 1000000.0D).intValue());
     return shortBuffer.toString();
   }
 
   public static int random(Integer max) {
     Double d = new Double(max.intValue() * Math.random());
     return d.intValue();
   }
 
   public static int[] getSequence(int no) {
     int[] sequence = new int[no];
     for (int i = 0; i < no; i++) {
       sequence[i] = i;
     }
     Random random = new Random();
     for (int i = 0; i < no; i++) {
       int p = random.nextInt(no);
       int tmp = sequence[i];
       sequence[i] = sequence[p];
       sequence[p] = tmp;
     }
     return sequence;
   }
 
   public static void main(String[] args) {
	   
	  System.out.println(random(10000000));
     long bb = System.currentTimeMillis();
 
     for (int i = 0; i < 1; i++) {
       generateShortUuid();
       System.out.println(generateShortUuid());
     }
     System.out.println(System.currentTimeMillis() - bb);
   }
 }

