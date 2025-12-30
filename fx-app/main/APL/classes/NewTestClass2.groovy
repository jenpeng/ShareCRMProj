//demo
class NewTestClass2 {
  
  //对外提供的方法
  public static String action(){
    return "success"
  }
  
  //debug 时候的入口方法
  public static void main(String[] args){
    String ret = action();
    log.info(ret)
  }
  
}
