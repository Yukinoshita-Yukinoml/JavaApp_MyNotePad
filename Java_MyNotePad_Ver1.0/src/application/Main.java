package application;
	
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
//import java.nio.charset.Charset;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
//import javafx.scene.text.Font;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	
	/*从容器中获取NotePadController实例*/
	private NotePadController controller;
	@Override
	public void start(Stage stage) throws Exception {
			VBox root = 
				         FXMLLoader.load(getClass().getResource("Notepad.fxml"));
	        //root.setStyle("-fx-background-color: pink");/*背景颜色设置为粉色*/
			//Font.font("宋体", 14);/*默认字体为宋体*/
			Scene scene = new Scene(root);			
			stage.setTitle("Untitled.txt"); // displayed in window's title bar
			stage.setScene(scene);
			stage.show();
			controller  = (NotePadController) Context.controllers.get(NotePadController.class.getSimpleName());
			controller.getMainStage(stage);/*获取main中stage*/
		    /*监听窗口是否关闭*/
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	            /* 处理关闭事件
	             * 这里要Main类调用NotePadController类的属性和方法-->报错
	             * */
				@Override public void handle(WindowEvent event) {
					/*若已经被保存*/
					if (controller.isSave()) {/*调用NotePadController的函数*/	
						stage.close();/*关闭窗口*/
					} else if (!controller.isSave() && !controller.isTextAreaEmpty()) {/*注意静态合成的问题*/
						Alert alert = new Alert(AlertType.WARNING);
						alert.titleProperty().set("Tips");
						alert.headerTextProperty().set(
								"Your File has not been saved yet!!!\n"
								+ "If you want to exit without saving anyway, please clear the textArea first.");
						alert.show();
						controller.saveasItemPressed(null);/*自动弹出保存文件选项*/
					}	            	
					//System.out.println("Window closed!");/*测试语句*/
				}				
	        }			
			);
}
	
	public static void main(String[] args) {
		//System.out.println("Default Charset=" + Charset.defaultCharset()); //查看当前编码
		launch(args);

	}
}
