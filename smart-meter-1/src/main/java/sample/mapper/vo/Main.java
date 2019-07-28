package sample.mapper.vo;
public class Main {

  public static boolean isNumeric(String string) {
      return string.matches("^[-+]?\\d+(\\.\\d+)?$");
  }
  
  
  public static void main(String[] args) {
	   boolean numeric = isNumeric("13123");
	   System.out.println(numeric);
  }
}