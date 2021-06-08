/* * * * * * * * * * * *
 * Author: Leonard Wang
 * Date: 2021/6/8
 * * * * * * * * * * * */
package application;

import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.GraphicsEnvironment;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

/*把RootController设为父类,以便从全局来管理当前类*/
public class NotePadController extends RootController implements Initializable {
	
	/*parent组件,存放其他所有child组件*/
	@FXML private VBox vbox;
    /*ScrollPane作为文本框的存放窗体,其中包括了TextArea*/
    @FXML private ScrollPane scrollPane;
    @FXML private TextArea textArea;
    @FXML private Label statusLabel;
    /*MenuBar用于存放菜单栏*/
    @FXML private MenuBar menuBar;
    /*File菜单下*/
    @FXML private Menu fileMenu;
    @FXML private MenuItem newItem;
    @FXML private MenuItem openItem;
    @FXML private MenuItem saveItem;
    @FXML private MenuItem saveasItem;
    @FXML private MenuItem exitItem;
    /*Edit菜单下*/
    @FXML private Menu editMenu;
    @FXML private MenuItem cutItem;
    @FXML private MenuItem copyItem;
    @FXML private MenuItem pasteItem;
    @FXML private MenuItem undoItem;
    @FXML private MenuItem redoItem;
    @FXML private MenuItem findItem;
    @FXML private MenuItem replaceItem;
    @FXML private MenuItem replaceAllItem;
    @FXML private MenuItem dateItem;
    @FXML private MenuItem pageFormatItem;
    /*Style菜单下*/
    @FXML private Menu styleMenu;
    @FXML private RadioMenuItem wordwrapItem;
    @FXML private MenuItem fontItem;
    /*Watch菜单下*/
    @FXML private Menu watchMenu;
    @FXML private RadioMenuItem statusBarItem;
    @FXML private MenuItem zoomInItem;
    @FXML private MenuItem zoomOutItem;
    /*Help菜单下*/
    @FXML private Menu helpMenu;
    @FXML private MenuItem aboutItem;
    /*HBox以及中的Label*/
    @FXML private HBox hBoxOverall;
    @FXML private Label sysLabel;
    @FXML private Label charsetLabel;
    
    /*新建文件*/
    @FXML void newItemPressed(ActionEvent event) {
    	/*如果内容已经保存了直接新建一个文件*/
    	if (save) {  
			/*清除文本框的内容*/
			textArea.setText("");
			/*更改窗口标题*/
			stage.setTitle("NewFile-MyNotepad");
			/*将开打的文件的路径设置为null*/
			path = null;
		}
    	/*文本域内有内容且文本没有保存时跳出提示*/
    	else if (!save && !textArea.getText().isEmpty()) { 
    		/*首先进行布局*/
			Stage newStage = new Stage();
			VBox newVBox = new VBox();
			newVBox.setPadding(new Insets(5,10,5,20));
			newVBox.setSpacing(5);
			Label label1 = new Label("You have not saved the file yet!!!");
			Label label2 = new Label("Press \"Confirm\" to create a new File.");
			Label label3= new Label("Press \"Canel\" to cancel.");
			newVBox.getChildren().addAll(label1,label2,label3);
			Button confirmButton = new Button("OK");
			Button cancelButton = new Button("Cancel");
			HBox hBox = new HBox();
			hBox.setPadding(new Insets(20,10,5,80));
			hBox.setSpacing(30);
			hBox.getChildren().addAll(confirmButton,cancelButton);
			BorderPane rootNode = new BorderPane();
			rootNode.setTop(newVBox);
			rootNode.setCenter(hBox);
			
			Scene scene = new Scene(rootNode,300,150);
			newStage.setTitle("Tips");
			newStage.setScene(scene);
			newStage.show();
			/*以下才是真正起作用的代码,上面是在布局*/
			/*记得要给lambda表达式的事件event重新命名一下防止与主操作冲突*/
			confirmButton.setOnAction(pressedEvent->{
				/*清除文本框的内容*/
				textArea.setText("");
				/*更改窗口标题*/
				stage.setTitle("NewFile-MyNotePad");
				/*将开打的文件的路径设置为null*/
				path = null;
				newStage.close();
			});
			/*两个事件作为局部变量允许重名*/
			cancelButton.setOnAction(pressedEvent->{
				newStage.close(); 
			});
			
		}
    }
        
    private String path = null;/*存储打开的文件的路径*/
    private boolean save = false; /*保存状态默认为false,为保存*/
    private int startIndex = 0; /*字符串开始的位置*/
    private Stage stage;/*注意这个stage跟Main中的不是一个,因此要获取main中的stage,如下*/
    public void getMainStage(Stage stage) {
        this.stage = stage;
    }
    /*打开文件并把内容显示到stage中,要注意编码问题,中文可能会显示为乱码*/
    @FXML void openItemPressed(ActionEvent event) {
    	//Font.font("宋体", 14);
    	FileChooser fileChooser = new FileChooser(); /*fileChooser打开一个目录*/
	fileChooser.setTitle("FileChooser-Open"); /*设置文件选择框的标题*/
	/*过滤文件,仅选择文本文件(.txt)打开*/
	fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT","*.txt"));/*这里"TXT"是有必要的,起码不能是空的,否则会报错*/
	File file = fileChooser.showOpenDialog(stage); /*打开文件*/

	/*如果文件存在*/
	if (file != null && file.exists()) { 
		/*将文件路径 保留到成员变量path中*/
		path = file.getPath();
		stage.setTitle(file.getName());
	    BufferedReader br = null;
	    try {
		/*建立字符流*/
		InputStreamReader in = new InputStreamReader(new FileInputStream(file), "UTF-8");/*设置编码方式*/
		br = new BufferedReader(in);
		/*读取文本内容*/
		StringBuffer srtingBuffer = new StringBuffer();
		String content = null;
		while ((content = br.readLine()) != null) srtingBuffer.append(content).append(System.getProperty("line.separator"));/*换行符*/
	       /*读到textArea中*/
		textArea.setText(srtingBuffer.toString());
	    } catch (IOException e) {
		e.printStackTrace();
	    } finally {
		try {
		    if (br != null) br.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
		

    }
    
    /*保存*/
    @FXML void saveItemPressed(ActionEvent event) {
    	/*需要判断是新建之后的保存 还是打开之后的保存*/
    			/*新建之后的保存*/
    			if (path == null) {
    				FileChooser fc = new FileChooser();
    				fc.setTitle("FileChooser-Save");
    				/*获取被用户选择的文件*/
    				File file = fc.showSaveDialog(stage);
    				/*如果用户选了 而且文件是存在的*/
    				if (file != null && !file.exists()) {
    					/*将多行文本框中的内容写到file指向的文件中去*/
    					try {
    						/*创建输出流*/
    						FileOutputStream out = new FileOutputStream(file);
    						out.write(textArea.getText().getBytes());
    						out.flush();
    						out.close();
    						save = true; /*保存*/
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
    				}
    			} else {/*打开之后的保存*/
    				try {
    					/*创建输出流*/
    					FileOutputStream out = new FileOutputStream(path);
    					out.write(textArea.getText().getBytes());
    					out.flush();
    					out.close();
    					save = true;
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
    }
    
    /*另存为: 过程类似Open和Save, 通过文件选择进行*/
    @FXML void saveasItemPressed(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Save As");		
		File file = fileChooser.showSaveDialog(stage);
		/*注意此处保存文件的格式是不确定的,应该手动输入*/
		if (file != null) {
			try {
				FileOutputStream out = new FileOutputStream(file);
				String str = textArea.getText();
				byte[] bs = str.getBytes();/*字符串转换成字节数组,方便流操作*/
				out.write(bs);
				out.flush();
				out.close();
				save = true;/*记得修改状态*/
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
 
    /*页面格式,直接调用*/
    @FXML private void pageFormatItemPressed() {
        PageFormat pf = new PageFormat();
        PrinterJob.getPrinterJob().pageDialog(pf);
    }
    
    /*退出: 没保存文件时一定要提示*/
    @FXML void exitItemPressed(ActionEvent event) {
		if (save) {
			Platform.exit();
		} else if (!save && !textArea.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.titleProperty().set("Tips");
			alert.headerTextProperty().set("Your File has not been saved yet!!!");
			alert.show();
		}
    }
    
    /* "Edit"菜单栏:
     * 主要通过Cilpboard类实现 Java提供了剪切板Clipboard
     * 无论复制/粘贴/剪切都可以通过提供的类完成
     * 复制主要通过Clipboard类将文本读入剪切板进行缓存
     * 粘贴可以视为复制的逆过程
     * 剪切几乎是与复制一样的操作,只是把复制的内容用null代替
     * */
    /*剪切: 实际上只是比复制多了一步把选中的文本替换掉*/
    @FXML void cutItemPressed(ActionEvent event) {
    	/*获取系统剪贴板*/
    	Clipboard clipboard = Clipboard.getSystemClipboard();
    	ClipboardContent content = new ClipboardContent();
    	/*选取文本*/
     	String temp = textArea.getSelectedText();
    	/*将获取到的文本放到系统剪贴板中*/
		content.putString(temp);
    	/*把内容放到文本剪贴板中*/
    	clipboard.setContent(content);
    	/*用空字符串来代替选中的字符串*/
    	textArea.replaceSelection("");
    }

    /*复制: 通过Clipboard把选定的内容放入其中缓存*/
    @FXML void copyItemPressed(ActionEvent event) {
    	/*获取系统剪贴板*/
    	Clipboard clipboard = Clipboard.getSystemClipboard();
    	ClipboardContent content = new ClipboardContent();
    	/*选取文本*/
    	String temp = textArea.getSelectedText();
    	/*将获取到的文本放到系统剪贴板中*/
    	content.putString(temp);
    	/*把内容放到文本剪贴板中*/
    	clipboard.setContent(content);
    }
    
    /*粘贴: 把Clipboard中的内容读出来,可视为复制的逆过程*/
    @FXML void pasteItemPressed(ActionEvent event) {
    	/*获取系统剪贴板*/
    	Clipboard clipboard = Clipboard.getSystemClipboard();
    	if (clipboard.hasContent(DataFormat.PLAIN_TEXT)) {
    		String content = clipboard.getContent(DataFormat.PLAIN_TEXT).toString();
    		/*如果要粘贴时，鼠标已经选取了一段字符就替换掉*/
    		if (textArea.getSelectedText() != null) { 
    			textArea.replaceSelection(content);
    		} 
    		else { 
    			/*如果要粘贴时，鼠标没有选取字符，直接从光标后开始粘贴*/
    			int mouse = textArea.getCaretPosition(); /*获取文本域鼠标所在位置*/
    			textArea.insertText(mouse, content);/*插入文本*/
    		}
    	}
    }
    
    /*搜索应该有向前和向后两种选项*/
    @FXML void findItemPressed(ActionEvent event) {
    	/*是否大小敏感*/
    	Label label_caseInsensitive = new Label("CaseInsensitive");
    	CheckBox caseInsensitive = new CheckBox();
    	
    	/*首先创建HBox和VBox用于存放控件*/
    	HBox hbox = new HBox();
    	hbox.setPadding(new Insets(20, 5, 20, 5));/*此处正确包含Insets的包应该是 javafx.geometry.Insets*/
		hbox.setSpacing(5);
		Label tipLable = new Label("Find Content:");/*提示用户输入*/
		TextField tofindDomain = new TextField();/*用于接收用户输入的信息*/
		Button findNextButton = new Button("Find Next");
		hbox.getChildren().addAll(tipLable, tofindDomain,findNextButton);/*添加控件*/

    	HBox checkHbox = new HBox();
    	checkHbox.setPadding(new Insets(5, 5, 5, 5));
    	checkHbox.setSpacing(5);
    	checkHbox.getChildren().addAll(label_caseInsensitive, caseInsensitive);/*添加控件*/
		
	/*搜索起点*/
	VBox findRootNode = new VBox();
	findRootNode.getChildren().addAll(hbox,checkHbox);

	/*创建findStage搜索框*/
	Stage findStage = new Stage();
	Scene scene1 = new Scene(findRootNode, 450, 90);/*显示搜索框*/
	findStage.setTitle("Find");
	findStage.setScene(scene1);
	findStage.setResizable(false); /*固定窗口大小*/
	findStage.show();
		/*处理findNextButton按钮被按动的事件*/
		findNextButton.setOnAction((ActionEvent eventF) -> {
			//System.out.println(textArea.getText());/*测试语句*/
			String textString = textArea.getText(); /*获取记事本的文本域的字符串*/
			//System.out.println(textString);/*测试语句*/
			String tofindString = tofindDomain.getText(); /*获取要查找的字符串*/
		if(caseInsensitive.isSelected()) {/*如果大小写不敏感*/
				//System.out.println(textArea.getText());/*测试语句*/
				textString = textArea.getText().toLowerCase(); /*获取记事本的文本域的字符串*/
				//System.out.println(textString);/*测试语句*/
				tofindString = tofindDomain.getText().toLowerCase(); /*获取要查找的字符串*/
		}/*如果敏感就不toLowerCase()*/
			if (!tofindDomain.getText().isEmpty()) {/*要查询的字段非空时*/
				//System.out.println(textString.contains(tofindString));/*测试语句*/
				if (textString.contains(tofindString)) {/*当找到要查询的字段时*/
					/*如果搜索达到了末尾*/
					if (startIndex == -1) {
						/*弹出警告对话框用于提示没有此信息*/
						Alert alert1 = new Alert(AlertType.WARNING);
						alert1.titleProperty().set("Tips");
						alert1.headerTextProperty().set("The end of the file has been reached, NO More Content!");
						alert1.show();
					}
					startIndex = textString.indexOf(tofindString,startIndex);/*如果要实现大小写不敏感这句一定要改成从下一处大小不敏感的地方开始*/
					if (startIndex >= 0 && startIndex < textArea.getText().length()) {
						textArea.selectRange(startIndex, startIndex + tofindString.length());
						startIndex += tofindString.length(); 
					}
				}
				/*如果文本域中不包含要查找的字符串*/
				else{
					/*弹出警告对话框用于提示没有此信息*/
					Alert alert1 = new Alert(AlertType.WARNING);
					alert1.titleProperty().set("Tips");
					alert1.headerTextProperty().set("No Such Content!");
					alert1.show();
				}
			} 
			/*用户没有输入内容时提示错误信息*/
			else if (tofindDomain.getText().isEmpty()) {
				Alert alert1 = new Alert(AlertType.WARNING);
				alert1.titleProperty().set("Wrong!");
				alert1.headerTextProperty().set("Yout input in null.");
				alert1.show();
			}
		});
	    }
 
    /*向前搜索*/
    @FXML void findPreItemPressed(ActionEvent event) {
    	
    }
    
    /*替换: Replace的过程与搜索是很相近的,只是要多处理替换的部分*/
    /*替换应该包括当前替换和替换全部*/
    @FXML void replaceItemPressed(ActionEvent event) {
    	/*依然是创建VBox和HBox,用于存放所需的组件*/
    	HBox findHbox = new HBox();
    	findHbox.setPadding(new Insets(20, 5, 10, 8));
    	findHbox.setSpacing(5);
		Label label1 = new Label("Find Next:");
		TextField tofindDomain = new TextField();
		findHbox.getChildren().addAll(label1, tofindDomain);

		HBox replaceHbox = new HBox();
		replaceHbox.setPadding(new Insets(5, 5, 20, 8));
		replaceHbox.setSpacing(5);
		Label label2 = new Label("Replace Content:");
		TextField toreplaceDomain = new TextField();
		replaceHbox.getChildren().addAll(label2, toreplaceDomain);

		VBox v1 = new VBox();
		v1.getChildren().addAll(findHbox, replaceHbox);

		VBox v2 = new VBox();
		v2.setPadding(new Insets(21, 5, 20, 10));
		v2.setSpacing(13);
		Button findButton = new Button("Find");
		Button replaceButton = new Button("Replace");
		v2.getChildren().addAll(findButton, replaceButton);

		HBox replaceRootNode = new HBox();
		replaceRootNode.getChildren().addAll(v1, v2);

		Stage replaceStage = new Stage();
		Scene scene = new Scene(replaceRootNode, 430, 120);
		replaceStage.setTitle("Replace");
		replaceStage.setScene(scene);
		replaceStage.setResizable(false); /*固定窗口大小*/
		replaceStage.show();

		findButton.setOnAction((ActionEvent e) -> {
			String textString = textArea.getText(); /*获取记事本文本域的字符串*/
			String tofindString = tofindDomain.getText(); /*获取查找内容的字符串*/
			if (!tofindDomain.getText().isEmpty()) {
				if (textString.contains(tofindString)) {
					/*没找到*/
					if (startIndex == -1) {
						Alert alert1 = new Alert(AlertType.WARNING);
						alert1.titleProperty().set("Tips");
						alert1.headerTextProperty().set("The end of the file has been reached, NO More Content!");
						alert1.show();
					}
					startIndex = textArea.getText().indexOf(tofindDomain.getText(),startIndex);/*得到find结果的开始位置*/
					if (startIndex >= 0 && startIndex < textArea.getText().length()) {/*如果该位置合法*/
						textArea.selectRange(startIndex, startIndex + tofindDomain.getText().length());/*确定选中范围为要求的文本长度*/
						startIndex += tofindDomain.getText().length(); /*并将位置跳到已经走过的文本之后*/
					}
					replaceButton.setOnAction((ActionEvent e2) -> {
						 /*替换内容为空提示对话框*/
						if(toreplaceDomain.getText().isEmpty()) { 
							Alert alert1 = new Alert(AlertType.WARNING);
							alert1.titleProperty().set("Wrong");
							alert1.headerTextProperty().set("Replace Content is NULL!");
							alert1.show();
						}
						/*替换内容不为空则替换*/
						else {
							/* 直接替换的化会造成第一次替换后如果不继续find而是继续点击replace会直接插入的bug
							 * 解决方法就是设置条件判断: 只有选中的区域大小等于查找域的大小才替换
							 * */
							if(textArea.getSelection().getLength() == tofindDomain.getText().length())
								textArea.replaceSelection(toreplaceDomain.getText());
						}
					});
				}
				if (!textString.contains(tofindString)) {
					Alert alert1 = new Alert(AlertType.WARNING);
					alert1.titleProperty().set("Tips");
					alert1.headerTextProperty().set("Cannot find the content");
					alert1.show();
				}
				
			} 
			else if (tofindDomain.getText().isEmpty()) {
				Alert alert1 = new Alert(AlertType.WARNING);
				alert1.titleProperty().set("WRONG");
				alert1.headerTextProperty().set("Your input in the find TextField is NULL!");
				alert1.show();
			}
		});
    } 
    
    /*替换全部*/
    @FXML void replaceAllItemPressed() {
    	Label label_caseInsensitive = new Label("CaseInsensitive");
    	CheckBox caseInsensitive = new CheckBox();
    	HBox checkHbox = new HBox();
    	checkHbox.setPadding(new Insets(5, 5, 5, 5));
    	checkHbox.setSpacing(5);
    	checkHbox.getChildren().addAll(label_caseInsensitive, caseInsensitive);/*添加控件*/
    	/*依然是创建VBox和HBox,用于存放所需的组件*/
    	HBox findHbox = new HBox();
    	findHbox.setPadding(new Insets(20, 5, 10, 8));
    	findHbox.setSpacing(5);
		Label label1 = new Label("Find Next:");
		TextField tofindDomain = new TextField();
		findHbox.getChildren().addAll(label1, tofindDomain);

		HBox replaceHbox = new HBox();
		replaceHbox.setPadding(new Insets(5, 5, 20, 8));
		replaceHbox.setSpacing(5);
		Label label2 = new Label("Replace Content:");
		TextField toreplaceDomain = new TextField();
		replaceHbox.getChildren().addAll(label2, toreplaceDomain);
		
		VBox v1 = new VBox();
		v1.getChildren().addAll(findHbox, replaceHbox,checkHbox);

		VBox v2 = new VBox();
		v2.setPadding(new Insets(21, 5, 20, 10));
		v2.setSpacing(13);
		Button findButton = new Button("Find");
		Button replaceButton = new Button("Replace");
		Button replaceAllButton = new Button("Replace ALL");
		v2.getChildren().addAll(findButton, replaceButton,replaceAllButton);
	
		HBox replaceRootNode = new HBox();
		replaceRootNode.getChildren().addAll(v1, v2);

		Stage replaceStage = new Stage();
		Scene scene = new Scene(replaceRootNode, 450, 150);
		replaceStage.setTitle("Replace");
		replaceStage.setScene(scene);
		replaceStage.setResizable(false); /*固定窗口大小*/
		replaceStage.show();
		
		findButton.setOnAction((ActionEvent e) -> {
			String textString = textArea.getText(); /*获取记事本文本域的字符串*/
			String tofindString = tofindDomain.getText(); /*获取查找内容的字符串*/
			String toreplaceString = toreplaceDomain.getText();/*获取要替换的串*/
			if (!tofindDomain.getText().isEmpty()) {	
				if(caseInsensitive.isSelected()) {
				if (textString.toLowerCase().contains(tofindString.toLowerCase())) {/*没有更多的时候*/
					if (startIndex == -1) {
						Alert alert1 = new Alert(AlertType.WARNING);
						alert1.titleProperty().set("Tips");
						alert1.headerTextProperty().set("The end of the file has been reached, NO More Content!");
						alert1.show();
					}
					startIndex = textArea.getText().indexOf(tofindString,startIndex);/*得到find结果的开始位置*/
					if (startIndex >= 0 && startIndex < textArea.getText().length()) {/*如果该位置合法*/
						textArea.selectRange(startIndex, startIndex + tofindString.length());/*确定选中范围为要求的文本长度*/
						startIndex += tofindString.length(); /*并将位置跳到已经走过的文本之后*/
					}
					replaceButton.setOnAction((ActionEvent e2) -> {
						 /*替换内容为空提示对话框*/
						if(toreplaceDomain.getText().isEmpty()) { 
							Alert alert1 = new Alert(AlertType.WARNING);
							alert1.titleProperty().set("Wrong");
							alert1.headerTextProperty().set("Replace Content is NULL!");
							alert1.show();
						}
						/*替换内容不为空则替换*/
						else {
							/* 直接替换的化会造成第一次替换后如果不继续find而是继续点击replace会直接插入的bug
							 * 解决方法就是设置条件判断: 只有选中的区域大小等于查找域的大小才替换
							 * */
							if(textArea.getSelection().getLength() == tofindString.length())
								textArea.replaceSelection(toreplaceString);
						}
					});
					/*替换全部*/
					replaceAllButton.setOnAction((ActionEvent e3)->{
						if(caseInsensitive.isSelected())
							textArea.setText(textArea.getText().toLowerCase().replaceAll(tofindString,toreplaceString));
						else
							textArea.setText(textArea.getText().replaceAll(tofindString,toreplaceString));
					});	
				}}/*选中大小写不敏感时候*/
			else {
				if (textString.contains(tofindString)) {/*没有更多的时候*/
				if (startIndex == -1) {
					Alert alert1 = new Alert(AlertType.WARNING);
					alert1.titleProperty().set("Tips");
					alert1.headerTextProperty().set("The end of the file has been reached, NO More Content!");
					alert1.show();
				}
				startIndex = textArea.getText().indexOf(tofindString,startIndex);/*得到find结果的开始位置*/
				if (startIndex >= 0 && startIndex < textArea.getText().length()) {/*如果该位置合法*/
					textArea.selectRange(startIndex, startIndex + tofindString.length());/*确定选中范围为要求的文本长度*/
					startIndex += tofindString.length(); /*并将位置跳到已经走过的文本之后*/
				}
				replaceButton.setOnAction((ActionEvent e2) -> {
					 /*替换内容为空提示对话框*/
					if(toreplaceDomain.getText().isEmpty()) { 
						Alert alert1 = new Alert(AlertType.WARNING);
						alert1.titleProperty().set("Wrong");
						alert1.headerTextProperty().set("Replace Content is NULL!");
						alert1.show();
					}
					/*替换内容不为空则替换*/
					else {
						/* 直接替换的化会造成第一次替换后如果不继续find而是继续点击replace会直接插入的bug
						 * 解决方法就是设置条件判断: 只有选中的区域大小等于查找域的大小才替换
						 * */
						if(textArea.getSelection().getLength() == tofindString.length())
							textArea.replaceSelection(toreplaceString);
					}
				});
				/*替换全部*/
				replaceAllButton.setOnAction((ActionEvent e3)->{
					if(caseInsensitive.isSelected())
						textArea.setText(textArea.getText().toLowerCase().replaceAll(tofindString,toreplaceString));
					else
						textArea.setText(textArea.getText().replaceAll(tofindString,toreplaceString));
				});	
			}	/*这是查找到的时候*/
				if(caseInsensitive.isSelected()) {
					if (!textString.toLowerCase().contains(tofindString)) {
						Alert alert1 = new Alert(AlertType.WARNING);
						alert1.titleProperty().set("Tips");
						alert1.headerTextProperty().set("Cannot find the content");
						alert1.show();
					}else if (tofindDomain.getText().isEmpty()) {
						Alert alert1 = new Alert(AlertType.WARNING);
						alert1.titleProperty().set("WRONG");
						alert1.headerTextProperty().set("Your input in the find TextField is NULL!");
						alert1.show();
					}
				}else {
					if (!textString.contains(tofindString)) {
						Alert alert1 = new Alert(AlertType.WARNING);
						alert1.titleProperty().set("Tips");
						alert1.headerTextProperty().set("Cannot find the content");
						alert1.show();
					}else if (tofindDomain.getText().isEmpty()) {
						Alert alert1 = new Alert(AlertType.WARNING);
						alert1.titleProperty().set("WRONG");
						alert1.headerTextProperty().set("Your input in the find TextField is NULL!");
						alert1.show();
					}
				}	
			} 
		  }
		});  	
    }
    
    /*撤销/恢复操作:  stackoverflow.com/questions/40638845/implementing-undo-redo-in-javafx*/
    /*撤销*/
    @FXML private void undoItemPressed() {textArea.undo();}
    /*恢复*/
    @FXML private void redoItemPressed() {textArea.redo();}

    /*用于显示状态栏*/
    private void statusBar() {
        try {
            int line = 0, row = 0, FindStartPos;/*行,列和光标位置*/
            String totalString, subString;/*totalString用于保存所有的字符*/
            String[] stringArray;
            totalString = textArea.getText();/*得到textArea中的所有字符*/
            FindStartPos = textArea.getCaretPosition();/*获取caretPosition的值*/
            //System.out.println("FindStartPos"+FindStartPos);/*测试语句*/
            if(FindStartPos > 0)
            	subString = totalString.substring(0, FindStartPos);/*保存从0到FindStartPos的字符*/
            else subString = totalString.substring(FindStartPos, 0); 
            stringArray = subString.split("\n");
            line = stringArray.length;/*找到当前第几行*/
            /*连续按下回车时*/
            if (line == 0) {
                totalString = totalString + "*";
                subString = totalString.substring(0, FindStartPos + 1);
                stringArray = subString.split("\n");/*通过换行符分隔*/
                line = stringArray.length;
                row = stringArray[line - 1].length() - 1;
            } else {
                row = stringArray[line - 1].length();
            }
            statusLabel.setText("Line: " + line + " Row: " + (row + 1) + " Totoal: " + totalString.length() + " words");
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    /*日期/时间: 获取系统时间并输出到当前光标位置*/
    @FXML void dateItemPressed(ActionEvent event) {
    	/*首先得到系统时间*/
    	SimpleDateFormat timeNow = new SimpleDateFormat();/*格式化时间*/
    	timeNow.applyPattern("yyyy-MM-dd HH:mm:ss a");/*a为am/pm的标记*/
        Date date = new Date();/*获取当前时间*/
        String content = timeNow.format(date);
        /*其余部分与"Copy"操作几乎一致只是不经过Clipboard*/
        /*获取系统剪贴板*/
    	Clipboard clipboard = Clipboard.getSystemClipboard();
    	if (clipboard.hasContent(DataFormat.PLAIN_TEXT)) {
    		/*如果要粘贴时，鼠标已经选取了一段字符就替换掉*/
    		if (textArea.getSelectedText() != null) { 
    			textArea.replaceSelection(content);
    		} 
    		else { 
    			/*如果要粘贴时，鼠标没有选取字符，直接从光标后开始粘贴*/
    			int mouse = textArea.getCaretPosition(); /*获取文本域鼠标所在位置*/
    			textArea.insertText(mouse, content);/*插入文本*/
    		}
    	}
    }
  
    /* Style 菜单栏
     * wordWrap通过提供的函数即可容易实现
     * 重点在于选择字体的实现
     * 最开始时bug为Font一次只能设置一次,之后再选择就报错--这是由于多次使用同一控件->在xxx.getChildren().add()时很容易造成这个错误
     * 解决方法是先删除再创建, 这里直接创建1000个对象(实际上数量随机)
     * 但是即使重新生成了vb[flag]作为scene的root仍然要clear掉每一个child组件再add回去,因为实际上这些组件的id还是一样的
     * */
    HBox h1 = new HBox();
    HBox h2 = new HBox();
    VBox[] vb = new VBox[1000];/*生成1000个VBox对象供使用*/
    ComboBox<String> cb1 = new ComboBox<String>();/*设置字体样式*/
    ComboBox<String> cb2 = new ComboBox<String>();/*设置字体大小*/
    ComboBox<String> cb3 = new ComboBox<String>();/*设置字体颜色*/
    CheckBox ckb1 = new CheckBox();/*粗体*/
    CheckBox ckb2 = new CheckBox();/*斜体*/
    String selectFontType;int selectFontSize=12; String selectFontColor = "-fx-text-fill:black";/*设置字体默认值*/
    Font font;
    private int flag = 0;/*状态变量用于查看这是第几次打开Font设置*/
    
    /*修改字体*/
    @FXML void fontItemPressed(ActionEvent event) {
    	++flag;//System.out.println(flag);/*测试输出语句*/
    	vb[flag] = new VBox();/*初始化当前VBox*/
	    
    	//创建Label以便输出到界面
    	Label label1 = new Label("Font Name");  Label label2 = new Label("Font Size"); Label label3 = new Label("Font Color");
    	Label label_ckb1 = new Label("Bold"); Label label_ckb2 = new Label("Italic");
    	/*ge:代表此电脑上的所有字体的对象*/
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	if(flag > 1) h1.getChildren().clear();/*直接clear掉效率更高*/
        h1.getChildren().addAll(label1, cb1,label2, cb2, label3, cb3);/*ComboBox包含在HBox1中*/
        h1.setSpacing(10);/*设置间隙: hbox，vbox用这个 对比 GirdPane用setHgap，setVgap;*/
	    
	    if(flag > 1) h2.getChildren().clear();/*第二次后先移除*/
	    h2.getChildren().addAll(ckb1,label_ckb1,ckb2,label_ckb2);/*把两个CheckBox包含到HBox2中去*/
	    h2.setAlignment(Pos.CENTER);/*把两个CheckBox设置到中间*/
	    
	    if(flag > 1) {
	    	vb[flag].getChildren().clear();
//	    	vb[flag].getChildren().remove(h1); vb[flag].getChildren().remove(h2);
	    }/*先移除原有的*/
	    vb[flag].getChildren().addAll(h1,h2);/*把两个HBox再放到VBox中去*/
	    
	    String[] getFontName = ge.getAvailableFontFamilyNames();/*获取本机上的所有字体样式*/
	    if(flag > 1) cb1.getItems().clear();
	    cb1.setPromptText("Font");
	    cb1.getItems().addAll(getFontName);/*把所有的字体样式放到ComboBox1中去*/
	   
	    String[] getFontSize=new String[100];/*设置变量用于存放不同的字体大小*/
	    for(int i=0;i<100;i++)
	        getFontSize[i]=i+1+"";/*得到从1到100的Size*/
	    if(flag > 1) cb2.getItems().clear();
	    cb2.setPromptText("Size");
	    cb2.getItems().addAll(getFontSize);/*把这些Size放到ComboBox2中*/
	    /*向ComboBox中添加五个备选项*/
	    if(flag > 1) cb3.getItems().clear();
	    cb3.setPromptText("Color");
	    cb3.getItems().add(0,"BLACK");
	    cb3.getItems().add(1,"RED");
	    cb3.getItems().add(2,"BLUE");
	    cb3.getItems().add(3,"YELLOW");
	    cb3.getItems().add(4,"PINK");    
	    
	    cb1.setOnAction(e->{
	        selectFontType = cb1.getValue();
	        setFont();
	    });
	    cb2.setOnAction(e->{
	        selectFontSize = Integer.parseInt(cb2.getValue());/*直接从ComboBox2直接得到Size值即可*/
	        setFont();
	    });
	    cb3.setOnAction(e->{/*设置颜色时可能出现的问题帮助: docs.oracle.com/javase/8/javafx/api/javafx/scene/doc-files/cssref.html#intropublicapi*/
	    	selectFontColor = "-fx-text-fill:"+cb3.getValue();/*尝试直接赋值,避免另设setFontColor*/
	    	//System.out.println(cb3.getValue());/*测试语句*/
	        setFont();
	    });
	    ckb1.setOnAction(e->{
	        setFont();
	    });
	    ckb2.setOnAction(e->{
	        setFont();
	    });
	
	    vb[flag].setSpacing(10);

	    Scene scene = new Scene(vb[flag],600,100);

	    Stage stage = new Stage();
	    stage.setTitle("choose word type");
	    stage.setScene(scene);
	    stage.show();
    }

    /*用于设置Font*/
    public void setFont(){
  	  	font = Font.font(selectFontType,selectFontSize);
        textArea.setFont(Font.font(selectFontType,selectFontSize));
        textArea.setStyle(selectFontColor);
        /*下面是对粗体/斜体的选择*/
        if(ckb1.isSelected()&&ckb2.isSelected()){
      	  textArea.setFont(Font.font(selectFontType, FontWeight.BOLD, FontPosture.ITALIC,selectFontSize));
        }
        else if(ckb1.isSelected()&&!ckb2.isSelected())
        	textArea.setFont(Font.font(selectFontType, FontWeight.BOLD,FontPosture.REGULAR,selectFontSize));
        else if(ckb2.isSelected()&&!ckb1.isSelected())
        	textArea.setFont(Font.font(selectFontType,FontWeight.NORMAL,FontPosture.ITALIC,selectFontSize));
        else if(!ckb1.isSelected()&&!ckb2.isSelected())
        	textArea.setFont(Font.font(selectFontType,FontWeight.NORMAL,FontPosture.REGULAR,selectFontSize));
        //System.out.println(""+ckb1.isSelected()+ckb2.isSelected());
	}
   
    /*自动换行*/
    @FXML void wordwrapItemPressed(ActionEvent event) {
    	/*启用自动换行功能*/
    	textArea.setWrapText(true);    
    }

    /*Watch菜单下*/
    /*打开状态栏*/
    @FXML void statusBarItemPressed(ActionEvent event) {
        statusLabel.setVisible(true);/*statusBarItem.isSelected()*/
    	/*激活状态栏*/
    	statusLabel.setText("Line: " + 1 + " Row: " + (0 + 1) + " Total: " + 0 + " words ");
    	/*监听鼠标点击*/
        textArea.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	statusBar();
                /*监听文本变化*/
                        if (!textArea.isUndoable()){
                            undoItem.setDisable(true);
                        }else if (textArea.isUndoable()){
                        	undoItem.setDisable(false);                    
                        }
                        if (!textArea.isRedoable()){
                            redoItem.setDisable(true);
                        }else if (textArea.isRedoable()){
                        	redoItem.setDisable(false);
                        }
                		if(textArea.getSelectedText().equals("")){
                			copyItem.setDisable(true);
                			cutItem.setDisable(true);
                			//System.out.println("cutItem.isDisable(): " + getCutItemStatus() + " copyItem.isDisable(): " + getCopyItemStatus());/*调试语句*/
                		}else {
                			copyItem.setDisable(false);
                			cutItem.setDisable(false);
                			//System.out.println("cutItem.isDisable(): " + getCutItemStatus() + " copyItem.isDisable(): " + getCopyItemStatus());/*调试语句*/
                		}
            }
        });
        /*监听键盘抬起*/
        textArea.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ALT) {
                	menuBar.requestFocus();
                } else {
                    statusBar();
                    if (!textArea.isUndoable()){
                        undoItem.setDisable(true);
                    }else if (textArea.isUndoable()){
                    	undoItem.setDisable(false);                    
                    }
                    if (!textArea.isRedoable()){
                        redoItem.setDisable(true);
                    }else if (textArea.isRedoable()){
                    	redoItem.setDisable(false);
                    }
            		if(textArea.getSelectedText().equals("")){
            			copyItem.setDisable(true);
            			cutItem.setDisable(true);
            		}else {
            			copyItem.setDisable(false);
            			cutItem.setDisable(false);
            		}
                }
            }
        });  	
    }
    
    /*用于缩放*/
    /*放大*/
    @FXML void zoomInItemPressed(ActionEvent event) {
    	selectFontSize+=1;
    	if(selectFontSize >= 100) selectFontSize = 100;
  	  	font = Font.font(selectFontType,selectFontSize);
        textArea.setFont(Font.font(selectFontType,selectFontSize));
    }
    /*缩小*/
    @FXML void zoomOutItemPressed(ActionEvent event) {
    	selectFontSize -= 1;
        if (selectFontSize <= 1) {
        	selectFontSize = 1;
        }
  	  	font = Font.font(selectFontType,selectFontSize);
        textArea.setFont(Font.font(selectFontType,selectFontSize)); 	
    }
    
    /*关于: 跳转到给定的网页(使用超链接)/对话框/帮助文档*/
    @FXML void aboutItemPressed(ActionEvent event) {
    	/*启用一个对话框*/
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("About MyNotepad");
    	alert.setHeaderText("About");   	
		/*定义Label,Text和HyperLink用于输出*/
    	Label label = new Label("Information about MyNotePad application. \n");
    	Text foretips =  new Text("\n If you want to know more about my app,\n"
    						+ "click the link below\n");
    	Hyperlink link = new Hyperlink("https://github.com/Yukinoshita-Yukinoml/JavaApp_MyNotePad.git");/*设置一个超链接*/
    	link.setText("MyRepo");
		link.setOnAction((ActionEvent e) -> {
		    System.out.println("This link is clicked");
		});/*点击超链接事件*/ 
		Text retrotips = new Text( "\n © All Rights Reserved\n");
		
		/*通过TextFlow把文本和超链接混合起来*/
    	TextFlow text = new TextFlow(foretips,link,retrotips);
    	text.setMaxWidth(Double.MAX_VALUE);
    	text.setMaxHeight(Double.MAX_VALUE);
    	GridPane.setVgrow(text, Priority.ALWAYS);
    	GridPane.setHgrow(text, Priority.ALWAYS);

    	/*将控件添加到GridPane中用于显示*/
    	GridPane detailsContent = new GridPane();
    	detailsContent.setMaxWidth(Double.MAX_VALUE);    	
    	detailsContent.add(label, 0, 0);
    	detailsContent.add(text, 0, 1);

    	/*显示文本*/
    	alert.getDialogPane().setContent(detailsContent);
    	
    	/*显示对话框*/
    	alert.showAndWait();
    }

    /*initialize方法用于绑定children组件到其相应的parent上以实现窗口大小自适应*/
    @Override public void initialize(URL location, ResourceBundle resources) {    	
    	/*MenuBar组件的宽度绑定为VBox的宽度*/
    	menuBar.prefWidthProperty().bind(vbox.widthProperty());
        /*ScrollPane组件的宽度和高度绑定为VBox的相应值*/
        scrollPane.prefWidthProperty().bind(vbox.widthProperty());
        scrollPane.prefHeightProperty().bind(vbox.heightProperty());
        /*TextArea组件的宽度和高度绑定为ScrollPane的相应值, 其实也可以直接设成VBox的相应值*/
        textArea.prefWidthProperty().bind(scrollPane.widthProperty());
        textArea.prefHeightProperty().bind(scrollPane.heightProperty());
        /*HBox组件宽度绑定为VBOX相应值*/
        hBoxOverall.prefWidthProperty().bind(vbox.widthProperty());
        
        /* 设置hot key
         * SHORTCUT_DOWN 对于所有平台适用
		 * CONTROL_DOWN 和 META_DOWN 值分别依赖于Windows和MacOS平台
		 * */
		newItem.setMnemonicParsing(true);
		newItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));	
		openItem.setMnemonicParsing(true);
		openItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		saveItem.setMnemonicParsing(true);
		saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));		
		saveasItem.setMnemonicParsing(true);
		saveasItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN,KeyCombination.CONTROL_DOWN));
		undoItem.setMnemonicParsing(true);
		undoItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
		redoItem.setMnemonicParsing(true);
		redoItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
		findItem.setMnemonicParsing(true);
		findItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
		replaceItem.setMnemonicParsing(true);
		replaceItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
		
        /*监听鼠标滑轮*/
        textArea.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                //判断是否CTRL键按下
                if (event.isControlDown() && event.getDeltaY() < 0) {
                	selectFontSize -= 1;
                    if (selectFontSize <= 1) {
                    	selectFontSize = 1;
                    }
              	  	font = Font.font(selectFontType,selectFontSize);
                    textArea.setFont(Font.font(selectFontType,selectFontSize)); 
                } else if (event.isControlDown() && event.getDeltaY() > 0) {
                	selectFontSize+=1;
                	if(selectFontSize >= 100) selectFontSize = 100;
              	  	font = Font.font(selectFontType,selectFontSize);
                    textArea.setFont(Font.font(selectFontType,selectFontSize));
                }
            }
        });

        /*监听鼠标是否落下*/
        textArea.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /*监听文本变化*/
                if (!textArea.isUndoable()){
                    undoItem.setDisable(true);
                }else if (textArea.isUndoable()){
                	undoItem.setDisable(false);                    
                }
                if (!textArea.isRedoable()){
                    redoItem.setDisable(true);
                }else if (textArea.isRedoable()){
                	redoItem.setDisable(false);
                }
        		if(textArea.getSelectedText().equals("")){
        			copyItem.setDisable(true);
        			cutItem.setDisable(true);
//                			System.out.println("cutItem.isDisable(): " + getCutItemStatus() + " copyItem.isDisable(): " + getCopyItemStatus());/*调试语句*/
        		}else {
        			copyItem.setDisable(false);
        			cutItem.setDisable(false);
//                			System.out.println("cutItem.isDisable(): " + getCutItemStatus() + " copyItem.isDisable(): " + getCopyItemStatus());/*调试语句*/
        		}
            }
        });
    
        /*监听键盘是否抬起*/
        textArea.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
            	statusBar();
                /*监听文本变化*/
                        if (!textArea.isUndoable()){
                            undoItem.setDisable(true);
                        }else if (textArea.isUndoable()){
                        	undoItem.setDisable(false);                    
                        }
                        if (!textArea.isRedoable()){
                            redoItem.setDisable(true);
                        }else if (textArea.isRedoable()){
                        	redoItem.setDisable(false);
                        }
                		if(textArea.getSelectedText().equals("")){
                			copyItem.setDisable(true);
                			cutItem.setDisable(true);
                			//System.out.println("cutItem.isDisable(): " + getCutItemStatus() + " copyItem.isDisable(): " + getCopyItemStatus());/*调试语句*/
                		}else {
                			copyItem.setDisable(false);
                			cutItem.setDisable(false);
                			//System.out.println("cutItem.isDisable(): " + getCutItemStatus() + " copyItem.isDisable(): " + getCopyItemStatus());/*调试语句*/
                		}
            }
        });
    }
 
    /*状态变量save的get和set方法,用于获知和改变当前文件保存的状态(保存-true/未保存-false)*/
	public boolean isSave() {return save;}
	public void setSave(boolean save) {this.save = save;}

	/*状态变量startIndex的get/set方法,用于获知当前光标位置(设置用不上)*/
	public int getStartIndex() {return startIndex;}
	public void setStartIndex(int startIndex) {this.startIndex = startIndex;}    
   
	/*用于判断当前textArea是否为空,若为空则返回true*/
	public boolean isTextAreaEmpty() {return textArea.getText().isEmpty();}
	/*获取当前是否选中文本状态,默认为选中,即true*/
	public boolean isTextAreaSelected() {
		if(textArea.getSelectedText().equals("")) return false;
		return true;
	}
	
	/*用于设置灰度*/
	public void setCutItemDisabled(boolean status) {cutItem.setDisable(status);}
	public void setCopyItemDisabled(boolean status) {copyItem.setDisable(status);}
	/*用于测试得到是否为不可选中*/
	public boolean getCutItemStatus() {return cutItem.isDisable();}
	public boolean getCopyItemStatus() {return copyItem.isDisable();}
}    
