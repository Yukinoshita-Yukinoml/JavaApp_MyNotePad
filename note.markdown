**<font size = 6>Category</font>**

--------

[TOC]

--------



# ReadingGuide

This is an info about myNotePad project. </br>

You will get the main functions of the notepad, and some of the problems I have met.</br>

If you have any other questions, please check the links I put below. Thanks for reading.

--------



## Function

* File  
* Edit 
* Style 
* Help 

### File
* New
* Open
* Save
* Save As
* Exit

### Edit
* Cut
* Copy
* Paste
* Find
* Replace

### Watch
* StatusBar
* ZoomIn/Out

### Style
* Word Wrap
* Font

### Help
* About

--------

## Q&A
**1.** ***Q: How to solve the problem of Inappropriate coding in the text?***</br>
&emsp; ***A: When we Open a new file from our system, we choose the :InputStreamReader" so that we can choose the encoding way we want. Like this:*** </br>

```java
InputStreamReader in = new InputStreamReader(new FileInputStream(file), "UTF-8");/*Set the encoding way*/
```

**2.** ***Q: How to fix the ERROR:  "java.lang.IllegalArgumentException: Children: duplicate children added:.. "*** </br>
&emsp; ***A: This problem is caused by adding the  same Control multiple times.</br>The way I used to fix this bug is to set a  local variable named "flag" and delete all the Controls once the value of flag is above 1. After that, We should rebuild those Controls*** </br>

```java
 /*The code block is just a instance. If you want to know more details, please open the  File:Src.*/
 private int flag = 0;/*Used to check the times we open.*/
 VBox[] vb = new VBox[1000];/*Generate 1000 VBox Objects(In fact, you can set any value you want.)*/
 if(flag > 1) {vb[flag].getChildren().clear();}/*If flag > 1, clear all the children*/
 vb[flag].getChildren().addAll(h1,h2);/*ReCreate the vb with h1,h2, which are two children of vb. Their type is HBox.*/
```

***3. Q: How to implement the Status Bar?</br>***

***A: Add the Keyboard&Mouse listener of the Text Area to make sure that we can always update the status. like the following codes:</br>***

```java
if(FindStartPos > 0)
	subString = totalString.substring(0, FindStartPos);/*Save the characters from address 0 to the address named FindStartPos*/
else subString = totalString.substring(FindStartPos, 0); 
stringArray = subString.split("\n");
line = stringArray.length;/*Find the current line.*/
/*When we press the "Enter" key */
if (line == 0) {
	totalString = totalString + "*";
    subString = totalString.substring(0, FindStartPos + 1);
    stringArray = subString.split("\n");/*Split them with the Line-breaks.*/
    line = stringArray.length;
    row = stringArray[line - 1].length() - 1;
} else {
	row = stringArray[line - 1].length();
}
statusLabel.setText("Line: " + line + " Row: " + (row + 1) + " Totoal: " + totalString.length() + " words");
```

***4. Q: How to disable the MenuItem when they should not be used?</br>***

***A: We should add the listener like this：</br>***

```java
textArea.disableProperty().addListener(object,old_valuemnew_value->{...});
```

***Or we can judge them directly like this:***

```java
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
/*The code segments below are the way to slove the problem: */
if(textArea.getSelectedText().equals("")){
    copyItem.setDisable(true);
    cutItem.setDisable(true);
}else {
    copyItem.setDisable(false);
    cutItem.setDisable(false);
}
```

--------

## Links

[MainTheme](https://blog.csdn.net/Cimiy_/article/details/95447247)</br>

[Get the location of the ChoieBox in FXML file](https://blog.csdn.net/Bangtidy/article/details/105606787)</br>

***Some documents about the error: "java.lang.IllegalArgumentException: Children: duplicate children added"***
[HelpDocument1](https://stackoverflow.com/questions/23044935/java-lang-illegalargumentexception-children-duplicate-children-added-parent)</br>
[HelpDocument2](https://blog.csdn.net/qq_46539113/article/details/111993751)</br>
[HelpDocument3](https://stackoom.com/question/3GzMN/Javafx%E5%B0%86%E7%AA%97%E6%A0%BC%E5%8A%A8%E6%80%81%E6%B7%BB%E5%8A%A0%E5%88%B0vbox-Duplicate-Children%E9%94%99%E8%AF%AF)</br>
[Help4Document](https://stackoverflow.com/questions/35157701/adding-duplicate-objects-to-gridpane-as-new-row)</br>

[Communications among the Controllers in JavaFX](https://blog.csdn.net/D578332749/article/details/80701441)</br>