package application;

//import application.Context;

/*��Ϊ����Controller�ĸ������*/
public class RootController {
	public RootController(){
	        /*��ʼ��ʱ���浱ǰControllerʵ��*/
	        Context.controllers.put(this.getClass().getSimpleName(), this);
	        //System.out.print(this.getClass());/*�������*/
	}
}
