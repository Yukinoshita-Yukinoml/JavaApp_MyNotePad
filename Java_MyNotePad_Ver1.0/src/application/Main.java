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
	
	/*�������л�ȡNotePadControllerʵ��*/
	private NotePadController controller;
	@Override
	public void start(Stage stage) throws Exception {
			VBox root = 
				         FXMLLoader.load(getClass().getResource("Notepad.fxml"));
	        //root.setStyle("-fx-background-color: pink");/*������ɫ����Ϊ��ɫ*/
			//Font.font("����", 14);/*Ĭ������Ϊ����*/
			Scene scene = new Scene(root);			
			stage.setTitle("Untitled.txt"); // displayed in window's title bar
			stage.setScene(scene);
			stage.show();
			controller  = (NotePadController) Context.controllers.get(NotePadController.class.getSimpleName());
			controller.getMainStage(stage);/*��ȡmain��stage*/
		    /*���������Ƿ�ر�*/
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	            /* ����ر��¼�
	             * ����ҪMain�����NotePadController������Ժͷ���-->����
	             * */
				@Override public void handle(WindowEvent event) {
					/*���Ѿ�������*/
					if (controller.isSave()) {/*����NotePadController�ĺ���*/	
						stage.close();/*�رմ���*/
					} else if (!controller.isSave() && !controller.isTextAreaEmpty()) {/*ע�⾲̬�ϳɵ�����*/
						Alert alert = new Alert(AlertType.WARNING);
						alert.titleProperty().set("Tips");
						alert.headerTextProperty().set(
								"Your File has not been saved yet!!!\n"
								+ "If you want to exit without saving anyway, please clear the textArea first.");
						alert.show();
						controller.saveasItemPressed(null);/*�Զ����������ļ�ѡ��*/
					}	            	
					//System.out.println("Window closed!");/*�������*/
				}				
	        }			
			);
}
	
	public static void main(String[] args) {
		//System.out.println("Default Charset=" + Charset.defaultCharset()); //�鿴��ǰ����
		launch(args);

	}
}
