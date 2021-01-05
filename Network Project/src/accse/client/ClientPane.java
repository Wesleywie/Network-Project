package accse.client;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

public class ClientPane extends GridPane
{
	Socket socket = null;
	//inputStreams
	InputStream is = null;
	BufferedReader br = null;
	//outputStreams
	OutputStream os = null;
	BufferedOutputStream bos = null;
	DataOutputStream dos = null;
	
	private String grayURL = "/api/GrayScale"; //http request that gets passed into post request
	private String rotateURL = "/api/Rotate";
	private String ErosionURL = "/api/Erosion";
	private String DilationURL = "/api/Dilation";
	private String CropURL = "/api/Crop";
	private String CannyURL = "/api/Canny";
	private String FastURL = "/api/Fast";
	private String ORBURL = "/api/ORB";
	
	private File selectedFile = null;
	private String ImagePath = null;
	private double deg = 0;
	
	public ClientPane()
	{
		Image imgV = new Image("file:data/ImageUpload.jpg"); //default image
		ImageView imgEdit = new ImageView(imgV); //image view
		imgEdit.setFitHeight(300); //image sizing
		imgEdit.setFitWidth(500);
		
		Button btnGrayScale = new Button("Grayscale"); //buttons
		btnGrayScale.setDisable(true); //disbales button if user has not uploaded image

		TextField textfield1 = new TextField(); //textArea
		textfield1.setPromptText("Degree of rotation");
		textfield1.setDisable(true);
		Button btnRotate = new Button("Rotate"); 
		btnRotate.setDisable(true);
		
		Button btnErosion = new Button("Erode"); 
		btnErosion.setDisable(true);
		
		Button btnDilation = new Button("Dilate");
		btnDilation.setDisable(true);
		
		Button btnCrop = new Button("Crop"); 
		btnCrop.setDisable(true);
		
		Button btnCanny = new Button("Canny"); 
		btnCanny.setDisable(true);

		Button btnFast = new Button("Fast"); 
		btnFast.setDisable(true);
		
		Button btnORB = new Button("ORB"); 
		btnORB.setDisable(true);
		
		Button btnResetImage = new Button("Reset Image"); 
		btnResetImage.setDisable(true);
		
		TextArea textArea = new TextArea(); //textArea
		
		Label label2 = new Label("Crop");
		TextField textfield2 = new TextField(); //textArea
		textfield2.setPromptText("x");
		
		TextField textfield3 = new TextField(); //textArea
		textfield3.setPromptText("y");
		
		TextField textfield4 = new TextField(); //textArea
		textfield4.setPromptText("width");
		
		TextField textfield5 = new TextField(); //textArea
		textfield5.setPromptText("height");
		
		
		imgEdit.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()  //image upload event handler
		{

		     @Override
		     public void handle(MouseEvent event) 
		     {
		    	 FileChooser fileChooser = new FileChooser(); //file choosing
		    	 fileChooser.setInitialDirectory(new File("data/images"));
		    	 selectedFile = fileChooser.showOpenDialog(null);
		    	 Image imgS = new Image(selectedFile.toURI().toString());
		    	 imgEdit.setImage(imgS);
		    	 
			     Connect();  //connects to server once user has uploaded image
			     
				 textArea.appendText("Client Connected to the server\r\n");
					
				btnGrayScale.setDisable(false); //enables all buttons once image uploadeed
				textfield1.setDisable(false);
				btnRotate.setDisable(false);
				btnErosion.setDisable(false);
				btnDilation.setDisable(false);
				btnCrop.setDisable(false);
				btnCanny.setDisable(false);
				btnFast.setDisable(false);
				btnORB.setDisable(false);
				btnResetImage.setDisable(false);
		     }
		     
		});				
		
		
		btnGrayScale.setOnAction(e -> //btn grayscale
		{
			String encodedFile = null;
			Connect(); //connects to server
			
			try
			{
				//DOS(BOS(OS))
				//Create a File handle
				File imageFile = new File(selectedFile.getAbsoluteFile().toString());

				//read the File into a FileInputStream
				FileInputStream fileInputStreamReader = new FileInputStream(imageFile);

				//Put the file contents into a byte[]
				byte[] bytes = new byte[(int)imageFile.length()];
				fileInputStreamReader.read(bytes);

				//Encode the bytes into a base64 format string
				encodedFile = new String(Base64.getEncoder().encodeToString(bytes));

				//get the bytes of this encoded string
				byte[] bytesToSend = encodedFile.getBytes();

				//Construct a POST HTTP REQUEST
				dos.write(("POST " + grayURL +" HTTP/1.1\r\n").getBytes());
				dos.write(("Content-Type: " +"application/text\r\n").getBytes());
				dos.write(("Content-Length: " + encodedFile.length() + "\r\n").getBytes());

				dos.write(("\r\n").getBytes());
				dos.write(bytesToSend);
				dos.write(("\r\n").getBytes());
				dos.flush();
				
				textArea.appendText("POST Request Sent\r\n");
				//read text response
				String response = "";
				String line = "";
				
				while(!(line = br.readLine()).equals(""))
				{
					response += line +"\n";
				}
				System.out.println(response);

				String imgData = "";
				while((line = br.readLine())!=null)
				{
					imgData += line;
				}
				
				String base64Str = imgData.substring(imgData.indexOf('\'')+1,imgData.lastIndexOf('}')-1);

				byte[] decodedString = Base64.getDecoder().decode(base64Str);
				//Display the image
				Image grayImg = new Image(new ByteArrayInputStream(decodedString));
				imgEdit.setImage(grayImg);
				imgEdit.setFitHeight(300); //image sizning
				imgEdit.setFitWidth(500);
			}
			catch(IOException ex) 
			{				
				ex.printStackTrace(); 
			}
			
			});
		
		btnRotate.setOnAction(e ->
		{
			String encodedFile = null;
			Connect();
			
			try
			{
				//DOS(BOS(OS))
				//Create a File handle
				File imageFile = new File(selectedFile.getAbsoluteFile().toString());

				//read the File into a FileInputStream
				FileInputStream fileInputStreamReader = new FileInputStream(imageFile);

				//Put the file contents into a byte[]
				byte[] bytes = new byte[(int)imageFile.length()];
				fileInputStreamReader.read(bytes);

				//Encode the bytes into a base64 format string
				encodedFile = new String(Base64.getEncoder().encodeToString(bytes));

				//get the bytes of this encoded string
				byte[] bytesToSend = encodedFile.getBytes();

				//Construct a POST HTTP REQUEST
				dos.write(("POST " + rotateURL + " HTTP/1.1\r\n").getBytes());
				dos.write(("Content-Type: " +"application/text\r\n").getBytes());
				dos.write(("Content-Length: " + encodedFile.length() + "\r\n").getBytes());

				dos.write(("\r\n").getBytes());
				dos.write(bytesToSend);
				dos.write(("\r\n").getBytes());
				dos.flush();
				
				textArea.appendText("POST Request Sent\r\n");
				//read text response
				String response = "";
				String line = "";
				
				while(!(line = br.readLine()).equals(""))
				{
					response += line +"\n";
				}
				System.out.println(response);

				String imgData = "";
				while((line = br.readLine())!=null)
				{
					imgData += line;
				}
				
				String base64Str = imgData.substring(imgData.indexOf('\'')+1,imgData.lastIndexOf('}')-1);

				byte[] decodedString = Base64.getDecoder().decode(base64Str);
				//Display the image
				Image rotateImg = new Image(new ByteArrayInputStream(decodedString));
				
				deg = Double.parseDouble(textfield1.getText());
				imgEdit.setRotate(imgEdit.getRotate() + deg); 
				
				imgEdit.setImage(rotateImg);
				imgEdit.setFitHeight(300);
				imgEdit.setFitWidth(500);
				
			}
			catch(IOException ex) 
			{				
				ex.printStackTrace(); 
			}

		});
		
		//er
		btnErosion.setOnAction(e ->
		{
			String encodedFile = null;
			Connect();
			
			try
			{
				//DOS(BOS(OS))
				//Create a File handle
				File imageFile = new File(selectedFile.getAbsoluteFile().toString());

				//read the File into a FileInputStream
				FileInputStream fileInputStreamReader = new FileInputStream(imageFile);

				//Put the file contents into a byte[]
				byte[] bytes = new byte[(int)imageFile.length()];
				fileInputStreamReader.read(bytes);

				//Encode the bytes into a base64 format string
				encodedFile = new String(Base64.getEncoder().encodeToString(bytes));

				//get the bytes of this encoded string
				byte[] bytesToSend = encodedFile.getBytes();

				//Construct a POST HTTP REQUEST
				dos.write(("POST " + ErosionURL + " HTTP/1.1\r\n").getBytes());
				dos.write(("Content-Type: " +"application/text\r\n").getBytes());
				dos.write(("Content-Length: " + encodedFile.length() + "\r\n").getBytes());

				dos.write(("\r\n").getBytes());
				dos.write(bytesToSend);
				dos.write(("\r\n").getBytes());
				dos.flush();
				
				textArea.appendText("POST Request Sent\r\n");
				//read text response
				String response = "";
				String line = "";
				
				while(!(line = br.readLine()).equals(""))
				{
					response += line +"\n";
				}
				System.out.println(response);

				String imgData = "";
				while((line = br.readLine())!=null)
				{
					imgData += line;
				}
				
				String base64Str = imgData.substring(imgData.indexOf('\'')+1,imgData.lastIndexOf('}')-1);

				byte[] decodedString = Base64.getDecoder().decode(base64Str);
				//Display the image
				Image erodedImg = new Image(new ByteArrayInputStream(decodedString));
				imgEdit.setImage(erodedImg);
				imgEdit.setFitHeight(300);
				imgEdit.setFitWidth(500);
			}
			catch(IOException ex) 
			{				
				ex.printStackTrace(); 
			}

		});
		
		//dil
		btnDilation.setOnAction(e ->
		{
			String encodedFile = null;
			Connect();
			
			try
			{
				//DOS(BOS(OS))
				//Create a File handle
				File imageFile = new File(selectedFile.getAbsoluteFile().toString());

				//read the File into a FileInputStream
				FileInputStream fileInputStreamReader = new FileInputStream(imageFile);

				//Put the file contents into a byte[]
				byte[] bytes = new byte[(int)imageFile.length()];
				fileInputStreamReader.read(bytes);

				//Encode the bytes into a base64 format string
				encodedFile = new String(Base64.getEncoder().encodeToString(bytes));

				//get the bytes of this encoded string
				byte[] bytesToSend = encodedFile.getBytes();

				//Construct a POST HTTP REQUEST
				dos.write(("POST " + DilationURL + " HTTP/1.1\r\n").getBytes());
				dos.write(("Content-Type: " +"application/text\r\n").getBytes());
				dos.write(("Content-Length: " + encodedFile.length() + "\r\n").getBytes());

				dos.write(("\r\n").getBytes());
				dos.write(bytesToSend);
				dos.write(("\r\n").getBytes());
				dos.flush();
				
				textArea.appendText("POST Request Sent\r\n");
				//read text response
				String response = "";
				String line = "";
				
				while(!(line = br.readLine()).equals(""))
				{
					response += line +"\n";
				}
				System.out.println(response);

				String imgData = "";
				while((line = br.readLine())!=null)
				{
					imgData += line;
				}
				
				String base64Str = imgData.substring(imgData.indexOf('\'')+1,imgData.lastIndexOf('}')-1);

				byte[] decodedString = Base64.getDecoder().decode(base64Str);
				//Display the image
				Image dilatedImg = new Image(new ByteArrayInputStream(decodedString));
				imgEdit.setImage(dilatedImg);
				imgEdit.setFitHeight(300);
				imgEdit.setFitWidth(500);
			}
			catch(IOException ex) 
			{				
				ex.printStackTrace(); 
			}

		});
		//crop
		btnCrop.setOnAction(e ->
		{
			String encodedFile = null;
			Connect();
			
			try
			{
				//DOS(BOS(OS))
				//Create a File handle
				File imageFile = new File(selectedFile.getAbsoluteFile().toString());

				//read the File into a FileInputStream
				FileInputStream fileInputStreamReader = new FileInputStream(imageFile);

				//Put the file contents into a byte[]
				byte[] bytes = new byte[(int)imageFile.length()];
				fileInputStreamReader.read(bytes);

				//Encode the bytes into a base64 format string
				encodedFile = new String(Base64.getEncoder().encodeToString(bytes));

				//get the bytes of this encoded string
				byte[] bytesToSend = encodedFile.getBytes();

				//Construct a POST HTTP REQUEST
				dos.write(("POST " + CropURL + " HTTP/1.1\r\n").getBytes());
				dos.write(("Content-Type: " +"application/text\r\n").getBytes());
				dos.write(("Content-Length: " + encodedFile.length() + "\r\n").getBytes());

				dos.write(("\r\n").getBytes());
				dos.write(bytesToSend);
				dos.write(("\r\n").getBytes());
				dos.flush();
				
				textArea.appendText("POST Request Sent\r\n");
				//read text response
				String response = "";
				String line = "";
				
				while(!(line = br.readLine()).equals(""))
				{
					response += line +"\n";
				}
				System.out.println(response);

				String imgData = "";
				while((line = br.readLine())!=null)
				{
					imgData += line;
				}
				
				String base64Str = imgData.substring(imgData.indexOf('\'')+1,imgData.lastIndexOf('}')-1);

				byte[] decodedString = Base64.getDecoder().decode(base64Str);
				//Display the image
				Image CropImg = new Image(new ByteArrayInputStream(decodedString));				
				imgEdit.setImage(CropImg);
				imgEdit.setFitHeight(300);
				imgEdit.setFitWidth(500);
			}
			catch(IOException ex) 
			{				
				ex.printStackTrace(); 
			}

		});
		
		//canny
		btnCanny.setOnAction(e ->
		{
			String encodedFile = null;
			Connect();
			
			try
			{
				//DOS(BOS(OS))
				//Create a File handle
				File imageFile = new File(selectedFile.getAbsoluteFile().toString());

				//read the File into a FileInputStream
				FileInputStream fileInputStreamReader = new FileInputStream(imageFile);

				//Put the file contents into a byte[]
				byte[] bytes = new byte[(int)imageFile.length()];
				fileInputStreamReader.read(bytes);

				//Encode the bytes into a base64 format string
				encodedFile = new String(Base64.getEncoder().encodeToString(bytes));

				//get the bytes of this encoded string
				byte[] bytesToSend = encodedFile.getBytes();

				//Construct a POST HTTP REQUEST
				dos.write(("POST " + CannyURL + " HTTP/1.1\r\n").getBytes());
				dos.write(("Content-Type: " +"application/text\r\n").getBytes());
				dos.write(("Content-Length: " + encodedFile.length() + "\r\n").getBytes());

				dos.write(("\r\n").getBytes());
				dos.write(bytesToSend);
				dos.write(("\r\n").getBytes());
				dos.flush();
				
				textArea.appendText("POST Request Sent\r\n");
				//read text response
				String response = "";
				String line = "";
				
				while(!(line = br.readLine()).equals(""))
				{
					response += line +"\n";
				}
				System.out.println(response);

				String imgData = "";
				while((line = br.readLine())!=null)
				{
					imgData += line;
				}
				
				String base64Str = imgData.substring(imgData.indexOf('\'')+1,imgData.lastIndexOf('}')-1);

				byte[] decodedString = Base64.getDecoder().decode(base64Str);
				//Display the image
				Image cannyImg = new Image(new ByteArrayInputStream(decodedString));
				imgEdit.setImage(cannyImg);
				imgEdit.setFitHeight(300);
				imgEdit.setFitWidth(500);
			}
			catch(IOException ex) 
			{				
				ex.printStackTrace(); 
			}

		});	
		
		
		//Fast
		btnFast.setOnAction(e ->
		{
			String encodedFile = null;
			Connect();
			
			try
			{
				//DOS(BOS(OS))
				//Create a File handle
				File imageFile = new File(selectedFile.getAbsoluteFile().toString());

				//read the File into a FileInputStream
				FileInputStream fileInputStreamReader = new FileInputStream(imageFile);

				//Put the file contents into a byte[]
				byte[] bytes = new byte[(int)imageFile.length()];
				fileInputStreamReader.read(bytes);

				//Encode the bytes into a base64 format string
				encodedFile = new String(Base64.getEncoder().encodeToString(bytes));

				//get the bytes of this encoded string
				byte[] bytesToSend = encodedFile.getBytes();

				//Construct a POST HTTP REQUEST
				dos.write(("POST " + FastURL + " HTTP/1.1\r\n").getBytes());
				dos.write(("Content-Type: " +"application/text\r\n").getBytes());
				dos.write(("Content-Length: " + encodedFile.length() + "\r\n").getBytes());

				dos.write(("\r\n").getBytes());
				dos.write(bytesToSend);
				dos.write(("\r\n").getBytes());
				dos.flush();
				
				textArea.appendText("POST Request Sent\r\n");
				//read text response
				String response = "";
				String line = "";
				
				while(!(line = br.readLine()).equals(""))
				{
					response += line +"\n";
				}
				System.out.println(response);

				String imgData = "";
				while((line = br.readLine())!=null)
				{
					imgData += line;
				}
				
				String base64Str = imgData.substring(imgData.indexOf('\'')+1,imgData.lastIndexOf('}')-1);

				byte[] decodedString = Base64.getDecoder().decode(base64Str);
				//Display the image
				Image FastImg = new Image(new ByteArrayInputStream(decodedString));
				imgEdit.setImage(FastImg);
				imgEdit.setFitHeight(300);
				imgEdit.setFitWidth(500);
			}
			catch(IOException ex) 
			{				
				ex.printStackTrace(); 
			}

		});
				
		//ORB
		btnORB.setOnAction(e ->
		{
			String encodedFile = null;
			Connect();
			
			try
			{
				//DOS(BOS(OS))
				//Create a File handle
				File imageFile = new File(selectedFile.getAbsoluteFile().toString());

				//read the File into a FileInputStream
				FileInputStream fileInputStreamReader = new FileInputStream(imageFile);

				//Put the file contents into a byte[]
				byte[] bytes = new byte[(int)imageFile.length()];
				fileInputStreamReader.read(bytes);

				//Encode the bytes into a base64 format string
				encodedFile = new String(Base64.getEncoder().encodeToString(bytes));

				//get the bytes of this encoded string
				byte[] bytesToSend = encodedFile.getBytes();

				//Construct a POST HTTP REQUEST
				dos.write(("POST " + ORBURL + " HTTP/1.1\r\n").getBytes());
				dos.write(("Content-Type: " +"application/text\r\n").getBytes());
				dos.write(("Content-Length: " + encodedFile.length() + "\r\n").getBytes());

				dos.write(("\r\n").getBytes());
				dos.write(bytesToSend);
				dos.write(("\r\n").getBytes());
				dos.flush();
				
				textArea.appendText("POST Request Sent\r\n");
				//read text response
				String response = "";
				String line = "";
				
				while(!(line = br.readLine()).equals(""))
				{
					response += line +"\n";
				}
				System.out.println(response);

				String imgData = "";
				while((line = br.readLine())!=null)
				{
					imgData += line;
				}
				
				String base64Str = imgData.substring(imgData.indexOf('\'')+1,imgData.lastIndexOf('}')-1);

				byte[] decodedString = Base64.getDecoder().decode(base64Str);
				//Display the image
				Image ORBImg = new Image(new ByteArrayInputStream(decodedString));
				imgEdit.setImage(ORBImg);
				imgEdit.setFitHeight(300);
				imgEdit.setFitWidth(500);
			}
			catch(IOException ex) 
			{				
				ex.printStackTrace(); 
			}

		});
		
		btnResetImage.setOnAction(e -> //resets image to orginal upload
		{ 
			Connect();
			textArea.appendText("Image Reset\r\n");
			Image imgS = new Image(selectedFile.toURI().toString());
			imgEdit.setImage(imgS);
			imgEdit.setRotate(0); 
		});
		
		BorderPane root = new BorderPane(imgEdit); //Layout	
		root.setPadding(new Insets(100));
		
		VBox layout = new VBox();  //Layout	
		HBox Layout2 = new HBox();
		
		VBox vCrop = new VBox();
		vCrop.getChildren().addAll(label2,textfield2,textfield3,textfield4,textfield5);
		
		layout.setPadding(new Insets(10, 300, 200, 250));
		
		Layout2.getChildren().addAll(btnGrayScale,textfield1,btnRotate,btnErosion,btnDilation,btnCrop,btnCanny,btnFast,btnORB,btnResetImage);

		layout.getChildren().addAll(root,textArea,Layout2,vCrop);
		
		getChildren().addAll(layout); 
		
	}
	
	public void Connect() //connect function
	{
		try
		{
			socket = new Socket("localhost",5000);
			//bind streams
			is = socket.getInputStream();			
			br = new BufferedReader(new InputStreamReader(is));											
			os = socket.getOutputStream();			
			bos = new BufferedOutputStream(os);			
			dos = new DataOutputStream(bos);	
		} 
		catch(IOException ex) 
		{				
			ex.printStackTrace(); 
		}
	}


}
