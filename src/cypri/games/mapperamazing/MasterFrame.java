package cypri.games.mapperamazing;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MasterFrame extends JFrame{
	MapperAmazing myMA;
	Map map  = new Map();
	XStream xstream;
	public XMLPanelList xmlList;
	
	Vector<Vector<XMLObj>> xmlObjs;
	
	public DrawPanel dp = new DrawPanel(this);
	public TilePanel tilePanel = new TilePanel(this);
	public MapSizeDialog mapSizeDialog = new MapSizeDialog(this);
	
	JFileChooser fc;
	JButton loadTilesBtn = new JButton("Load Tiles"); 
	JButton loadSheetBtn = new JButton("Load Sheet"); 
	JButton changeSizeBtn = new JButton("Change Map Size");
	JButton saveBtn = new JButton("Save Map");
	JButton loadBtn = new JButton("Load Map");
	JButton undoBtn = new JButton("Undo");
	JButton redoBtn = new JButton("Redo");
	JButton brushBtn = new JButton("brush/fill");
	//JButton compBtn = new JButton("Load Frame");
	JLabel brushLabel = new JLabel("Brush Mode");
	
	JButton addLayerBtn = new JButton("Add Layer");
	JButton subLayerBtn = new JButton("Sub Layer");
	JButton upLayerBtn = new JButton("Layer++");
	JButton downLayerBtn = new JButton("Layer--");
	JLabel layerLabel = new JLabel("Number of Layers: " + 1);
	JLabel curLayerLabel = new JLabel("Current Layer: " + 1);
	
	JScrollPane jsp = new JScrollPane(dp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	public int activeBrush = -1;
	
	private Container content;
	
	public MasterFrame(MapperAmazing ma){
		super("Mapper Amazing v1.0.2.0 - by William Starkovich");
		
		String workingdir = System.getProperty("user.dir");
		fc = new JFileChooser(new File(workingdir));
		
		xmlObjs = new Vector<Vector<XMLObj>>();
		
		myMA = ma;
		
		setBounds(0, 0, 1024, 830);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new MyWindowListener(myMA));
		
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		jsp.setBounds(10, 80, 640, 480);
		
		jsp.addMouseListener(new MouseListener()
		{

			@Override
			public void mouseClicked(MouseEvent me) {
				dpMouseClicked(me.getX() + jsp.getHorizontalScrollBar().getValue(), me.getY() + jsp.getVerticalScrollBar().getValue());;
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				dpMousePressed();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				dpMouseReleased();
			}
		});
		
		jsp.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent me) {
				dpMouseMoved(me.getX() + jsp.getHorizontalScrollBar().getValue(), me.getY() + jsp.getVerticalScrollBar().getValue());
			}

			@Override
			public void mouseMoved(MouseEvent me) {
				
			}
		});
		
		
		tilePanel.setBounds(750, 80, 256, 480);
		
		loadTilesBtn.setBounds(10, 10, 100, 20);
		loadTilesBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int fcVal = fc.showOpenDialog(myMA.mf);
				
				if(fcVal == JFileChooser.APPROVE_OPTION) 
		       	{
					//"null";
					//	/*if(file.isDirectory())*/ fileName = 
					//else file.getAbsolutePath();
					tilePanel.loadTiles(fc.getSelectedFile());
		       	}
		    }
		});
		
		changeSizeBtn.setBounds(150, 10, 200, 20);
		changeSizeBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				showMapSizeChangeDialog();
		    }
		});
		

		saveBtn.setBounds(400, 10, 100, 20);
		saveBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				if(save())
		    		JOptionPane.showMessageDialog(null, "File saved!", "Save Dialog",  JOptionPane.INFORMATION_MESSAGE);
		    		
		    	else
		    		JOptionPane.showMessageDialog(null, "File not saved!", "Error!",  JOptionPane.ERROR_MESSAGE);
		    }
		});
		
		loadBtn.setBounds(550, 10, 100, 20);
		loadBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				if(load())
		    		JOptionPane.showMessageDialog(null, "File loaded!", "Load Dialog",  JOptionPane.INFORMATION_MESSAGE);
		    		
		    	else
		    		JOptionPane.showMessageDialog(null, "File not loaded!", "Error!",  JOptionPane.ERROR_MESSAGE);
		    }
		});
		
		undoBtn.setBounds(700, 10, 100, 20);
		undoBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				undo();
		    }
		});
		
		redoBtn.setBounds(850, 10, 100, 20);
		redoBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				redo();
		    }
		});
		
		brushBtn.setBounds(10, 40, 100, 20);
		brushBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				switchBrush();
		    }
		});
		
		brushLabel.setBounds(120, 40, 100, 20);
		
		addLayerBtn.setBounds(200, 40, 100, 20);
		addLayerBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				addLayerToMap();
		    }
		});
		
		subLayerBtn.setBounds(310, 40, 100, 20);
		subLayerBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				subLayerFromMap();
		    }
		});
		
		
		layerLabel.setBounds(430, 40, 150, 20);
		
		upLayerBtn.setBounds(550, 40, 100, 20);
		upLayerBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				incrementCurrentLayer();
		    }
		});
		
		downLayerBtn.setBounds(660, 40, 100, 20);
		downLayerBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				decrementCurrentLayer();
		    }
		});
		
		curLayerLabel.setBounds(770, 40, 150, 20);
		/*compBtn.setBounds(800, 40, 150, 20);
		compBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				if(loadPanel())
		    		JOptionPane.showMessageDialog(null, "Frame Loaded!", "Frame Dialog",  JOptionPane.INFORMATION_MESSAGE);
		    		
		    	else
		    		JOptionPane.showMessageDialog(null, "Frame not loaded!", "Error!",  JOptionPane.ERROR_MESSAGE);
		    }
		});*/
		
		loadSheetBtn.setBounds(890, 40, 100, 20);
		loadSheetBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int fcVal = fc.showOpenDialog(myMA.mf);
				
				if(fcVal == JFileChooser.APPROVE_OPTION) 
		       	{
					//"null";
					//	/*if(file.isDirectory())*/ fileName = 
					//else file.getAbsolutePath();
					tilePanel.loadSheet(fc.getSelectedFile());
		       	}
		    }
		});
		
		content = getContentPane();	
		setVisible(true);

		content.add(jsp);
		content.add(tilePanel);
		content.add(loadTilesBtn);
		content.add(changeSizeBtn);
		content.add(saveBtn);
		content.add(loadBtn);
		content.add(loadSheetBtn);
		
		content.add(undoBtn);
		content.add(redoBtn);
		content.add(brushBtn);
		content.add(addLayerBtn);
		content.add(subLayerBtn);
		content.add(upLayerBtn);
		content.add(downLayerBtn);
		
		//content.add(compBtn);
		content.add(brushLabel);
		content.add(layerLabel);
		content.add(curLayerLabel);
		
		loadPanel();
	}
	
	public Container getContent(){
		return content;
	}
	
	public void incrementCurrentLayer(){
		if(dp.getCurrentLayer() + 1 <= dp.getMapLayers()) dp.setCurrentLayer(dp.getCurrentLayer() + 1);
		curLayerLabel.setText("Current Layer: " + dp.getCurrentLayer());
	}
	
	public void decrementCurrentLayer(){
		if(dp.getCurrentLayer() - 1 >= 1) dp.setCurrentLayer(dp.getCurrentLayer() - 1);
		curLayerLabel.setText("Current Layer: " + dp.getCurrentLayer());
	}
	
	public void addLayerToMap(){
		dp.changeNumLayers(dp.getMapLayers() + 1);
		layerLabel.setText("Number of Layers: " + dp.getMapLayers());
	}
	
	public void subLayerFromMap(){
		if(dp.getMapLayers() - 1 >= 1) dp.changeNumLayers(dp.getMapLayers() - 1);
		layerLabel.setText("Number of Layers: " + dp.getMapLayers());
	}
	
	
	public void switchBrush(){
		dp.changeBrushMode();
		if(!dp.isFillMode()) brushLabel.setText("Brush Mode");
		else brushLabel.setText("Fill Mode");
	}
	
	public void undo(){
		dp.backUpGoBack();
		dp.restoreBackedUpTiles();
		jsp.repaint();
	}
	
	public void redo(){
		dp.backUpGoForward();
		dp.restoreBackedUpTiles();
		jsp.repaint();
	}

	public void dpMouseMoved(int x, int y){
		dp.mouseMoved(x,y);
		jsp.repaint();
	}
	
	public void dpMouseClicked(int x, int y){
		dp.mouseClicked(x,y);
		jsp.repaint();
	}
	
	public void dpMousePressed(){
		dp.mousePressed();
	}
	
	public void dpMouseReleased(){
		dp.mouseReleased();
	}
	
	public void showMapSizeChangeDialog(){
		mapSizeDialog.setVisible(true);
	}
	
	public boolean load(){
		try{
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int returnVal = fc.showOpenDialog(this);

	        if(returnVal == JFileChooser.APPROVE_OPTION) {
	        	File file = fc.getSelectedFile();
				FileInputStream x = new FileInputStream(file.getAbsolutePath());
				DataInputStream y = new DataInputStream(x);
				
				int width = y.readInt();
				int height = y.readInt();
				int layers = y.readInt();
				
				System.out.println("width: " + width + " height: " + height + " layers: " + layers);
				dp.changeNumLayers(layers);
				dp.changeMapSize(width, height);
				
				for(int k = 0; k < dp.getMapLayers(); k++){
					for(int j = 0; j < dp.getMapHeight(); j++){
						for(int i = 0; i < dp.getMapWidth(); i++){
							byte readVal = y.readByte();
							System.out.println("readVal = " + readVal);
							dp.setTileIDAsByte(k, i, j, readVal);
						}
					}
				}
				
				dp.backUpTiles();
				
				//add xml objs
				int vecNum = y.readInt();
				for(int i = 0; i < vecNum; i++){
					int objNum = y.readInt();
					System.out.println("objNum: " + objNum);
					
					
					
					for(int j = 0; j < objNum; j++){
						XMLObj tempObj = new XMLObj();
						
						int numVars = y.readInt(); 
						System.out.println("varsize: " +  numVars);
						
						for(int h = 0; h < numVars; h++){
							int value = y.readInt();
							System.out.println("value = " + value);
							tempObj.vars.add(value);
							
						}
						xmlObjs.get(i).add(tempObj);
					}
					
					System.out.println("xmlObj(i)Size: " + xmlObjs.get(i).size());
				}
				
	            return true; 
	       	}
		}
		
		catch(FileNotFoundException e)
		{
			System.out.println("File Not Found");
			return false;
		}
		
		catch(IOException e)
		{
			System.out.println("IO Exception");
			e.printStackTrace();
			return false;
		}
		
		catch(Exception e)
		{
			System.out.println("Exception");
			e.getCause();
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "" + e.getMessage() , "Error!",  JOptionPane.ERROR_MESSAGE);

			return false;
		}
				
		return false;
	}
	
	public boolean save(){
		try{
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int returnVal = fc.showOpenDialog(this);

	        if(returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
				FileOutputStream x = new FileOutputStream(file.getAbsolutePath());
				
				int allocateLength = 0;
				allocateLength += Integer.SIZE; //width
				allocateLength += Integer.SIZE; //height
				allocateLength += Integer.SIZE; //layers
				
				allocateLength += (dp.getMapWidth() * dp.getMapHeight()) * dp.getMapLayers();
				
				//length of xmlobjs
				for(Vector<XMLObj> objVec : xmlObjs){
					for(XMLObj obj : objVec){
						allocateLength += Integer.SIZE * obj.vars.size();
					}
				}
				
				System.out.println(dp.getMapWidth() + " * " + dp.getMapHeight());
				
				ByteBuffer  bb = ByteBuffer.allocate(allocateLength); 
				bb.order(ByteOrder.BIG_ENDIAN); 
				
				bb.putInt(dp.getMapWidth());
				bb.putInt(dp.getMapHeight());
				bb.putInt(dp.getMapLayers());
				for(int k = 0; k < dp.getMapLayers(); k++){
					for(int j = 0; j < dp.getMapHeight(); j++){
						for(int i = 0; i < dp.getMapWidth(); i++){
							bb.put(dp.getTileIDAsByte(k, i, j));
						}
					}
				}
				//add xml objs
				System.out.println("[start VV (size = " + xmlObjs.size() + ")]");
				bb.putInt(xmlObjs.size());
				for(Vector<XMLObj> objVec : xmlObjs){
					
					System.out.println("[start vec (size = " + objVec.size() + ")]");
					bb.putInt(objVec.size());
					
					for(XMLObj obj : objVec){
						
						System.out.print("[start obj (size = " + obj.vars.size() + ")]");
						bb.putInt(obj.vars.size());
						
						for(Integer i : obj.vars){
							System.out.print(i.intValue() + ", ");
							bb.putInt(i.intValue());
						}
						System.out.print("[end obj]");
					}
					
					System.out.println("[end vec]");
				}
				
				bb.flip();
				
				x.write(bb.array());
				
			    return true; 
	       	}
		}
		
		catch(FileNotFoundException e)
		{
			System.out.println("File Not Found");
			return false;
		}
		
		catch(IOException e)
		{
			System.out.println("IO Exception");
			return false;
		}
		
		catch(Exception e)
		{
			System.out.println("Exception");
			e.getCause();
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "" + e.getMessage() , "Error!",  JOptionPane.ERROR_MESSAGE);

			return false;
		}
				
		return false;
	}
	
	public boolean loadPanel(){
		try{
			/*fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = fc.showOpenDialog(this);

	        if(returnVal == JFileChooser.APPROVE_OPTION) {
	        	File file = fc.getSelectedFile();*/
				BufferedReader br = new BufferedReader(new FileReader(new File("panels.xml")));

		        StringBuilder sb = new StringBuilder();
		        String line;

		        while ((line = br.readLine()) != null) {
		            sb.append(line);
		        }
		        
		        String everything = sb.toString();
		        System.out.println(everything);
		        br.close();
		        
		        
		        xstream = new XStream(new DomDriver());
				
				xstream.alias("panels", XMLPanelList.class);
				xstream.alias("panel", XMLPanel.class);
				xstream.alias("component", XMLComponent.class);
				
				xstream.addImplicitArray(XMLPanelList.class, "xmlPanels", "panel");
				xstream.addImplicitArray(XMLPanel.class, "xmlComps", "component");
		        xmlList = (XMLPanelList) xstream.fromXML(everything);
		        
		        System.out.println(xstream.toXML(xmlList));
		        
		        for(int i = 0; i < xmlList.xmlPanels.size(); i++){
		        	//TODO:Seperate xmlObj creation into it's own button.
		        	Vector<XMLObj> tempObjs = new Vector<XMLObj>();
		        	xmlObjs.add(tempObjs);
		        	XMLPanel p = xmlList.xmlPanels.get(i);
		        	p.setup(this, i);
		        }
	            return true; 
	       	//}
		}
		
		catch(FileNotFoundException e)
		{
			System.out.println("File Not Found");
			return false;
		}
		
		catch(IOException e)
		{
			System.out.println("IO Exception");
			e.printStackTrace();
			return false;
		}
		
		catch(Exception e)
		{
			System.out.println("Exception");
			e.getCause();
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "" + e.getMessage() , "Error!",  JOptionPane.ERROR_MESSAGE);

			return false;
		}
				
	//	return false;
	}
	
	public void error(String msg){
		System.out.println("ERROR: " + msg);
	}
}

class MyWindowListener extends WindowAdapter {
	private MapperAmazing myMA = null;
	
	public MyWindowListener(MapperAmazing ma){
		myMA = ma;
	}
	
	public void windowClosing(WindowEvent e) {
		System.exit(0);
    }

}
