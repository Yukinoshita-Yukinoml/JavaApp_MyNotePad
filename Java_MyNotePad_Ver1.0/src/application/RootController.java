package application;

//import application.Context;

/*作为所有Controller的父类存在*/
public class RootController {
	public RootController(){
	        /*初始化时保存当前Controller实例*/
	        Context.controllers.put(this.getClass().getSimpleName(), this);
	        //System.out.print(this.getClass());/*调试语句*/
	}
}
