package cypri.games.mapperamazing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class TilePanel extends JPanel implements MouseListener{
	private MasterFrame parent;
	ImageIcon[] imgs;
	ImageIcon[] cursor;
	private int[] tileSizeToCursor = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 0,
									     -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, 1};
	private int tileSize = 32;
	private int tileLineWidth = 8;
	private int tileChoice = -1;
	private int mouseTileX = 0;
	private int mouseTileY = 0;
	
	public TilePanel(MasterFrame parent){
		super();
		this.parent = parent;
		imgs = new ImageIcon[0];
		addMouseListener(this);
		cursor = new ImageIcon[] {new ImageIcon("imgs/cursor/cursor16.png"), new ImageIcon("imgs/cursor/cursor32.png")};
	}
	
	@Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, tileSize * tileLineWidth, this.getHeight());
        
    //    System.out.println(imgs.size());
    //    
        for(int i = 0; i < imgs.length; i++){
        	ImageIcon img = imgs[i];
        	
        	int tempHeight = 0;
        	int tempWidth = i;
        	
        	while(tempWidth >= tileLineWidth){
        		tempWidth -= tileLineWidth;
        		tempHeight++;
        	}
        	
        	if(img != null){     
        		img.paintIcon(this, g, tempWidth * tileSize, tempHeight * tileSize);
        	}
        }
        
        if(mouseTileX <= 9) cursor[tileSizeToCursor[tileSize]].paintIcon(this, g, mouseTileX * tileSize,  mouseTileY * tileSize);
      //  g.setColor(Color.BLACK);
      //  g.drawString("choice: " + tileChoice, 10, 200);
    }
	
	public int getChoice(){
		if(tileChoice >= 0) return tileChoice;
		else return -1;
	}
	
	public int getTileSize(){
		return tileSize;
	}
	
	public void loadTiles(File folder){
		parent.dp.clearTiles();
		//Vector<String> tempImgs = new Vector<ImageIcon>();
		File[] listOfFiles = folder.listFiles();

		/*for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	if(file.getName().substring(file.getName().length() - 3, file.getName().length()).equals("png")){
		    		tempImgs.add(new ImageIcon(file.getAbsolutePath()));
		    		System.out.println(file.getName());
		    	}
		    }
		}*/
		
		
		String xml = "";
		
		try {
			xml = readFile(folder.getAbsolutePath() + "/tilemap.xml", Charset.defaultCharset());
		} catch (IOException e) { e.printStackTrace(); }
		
		int numTiles = getXMLTileNum(xml);
		
		imgs = new ImageIcon[numTiles];
		
		for(int i = 0; i < numTiles; i++){
			Object[] result = getTileDataFromXML(xml, i);
			Integer id = (Integer) result[0];
			String name = (String) result[1];
			
			System.out.println("objID = " + id.intValue());
			System.out.println("objName = " + name);
			System.out.println("absPath = " + folder.getAbsolutePath());
			
			imgs[id.intValue()] = new ImageIcon(folder.getAbsolutePath() + "/" + name);//tempImgs.get();
			parent.dp.addTile(folder.getAbsolutePath() + "/" + name);
		}
		
		repaint();
		revalidate();
		
		parent.dp.revalidate();
		parent.dp.repaint();
	}
	
	static String readFile(String path, Charset encoding) throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	public int getXMLTileNum(String xml){
		String[] lines = xml.split("\n");
		
		int numLines = 0;
		for(int i = 0; i < lines.length; i++){
			System.out.println("lines[i].length(): " + lines[i].replace(" ", "").replace("\t", "").length());
			if(lines[i].replace(" ", "").replace("\t", "").length() > 10) numLines++;
		}
		
		//int result = lines.length - 2;
		//if(result < 0) result = -1;
		
		return numLines;
	}
	
	/*public ImageIcon matchIconByName(Vector<ImageIcon> icons, String name){
		for(int i = 0; i < icons.size(); i++){
			if(icons.get(i).)
		}
	}*/
	
	public Object[] getTileDataFromXML(String xml, int IDtoGet){
		
		Object[] returnObj = new Object[2];
		
		String[] lines = xml.split("\n");
		for(String line : lines){
			String[] spaces = line.split(" ");
			
			if(spaces.length > 1){
				int currentID = Integer.parseInt(spaces[1].substring(4, spaces[1].length() - 1));
				
				
				if(currentID == IDtoGet){
					System.out.println(spaces[1]);
					System.out.println("currentID = " + currentID);
					if(spaces.length > 1){
						String name = spaces[2].substring(6, spaces[2].length() - 1);
						
						System.out.println(spaces[1]);
						System.out.println("name = " + name);
						
						returnObj[0] = Integer.valueOf(currentID);
						returnObj[1] = name;
					}
				}
			}
		}
		
		return returnObj;
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		
		System.out.println("testing...");
		
		boolean zeroCoords = false;
		
		int mouseX = me.getX();
		int mouseY = me.getY();
		
		if(mouseX == 0 && mouseY == 0) zeroCoords = true;
		
		if(mouseX > 0) mouseX = mouseX/tileSize;
		else mouseX = 0;
		
		if(mouseY > 0) mouseY = mouseY/tileSize;
		else mouseY = 0;
		
		mouseTileX = mouseX;
		mouseTileY = mouseY;
		
		tileChoice = (mouseY * tileLineWidth) + mouseX;
		
		System.out.println("x: " + mouseX + " y: " + mouseY + " tileChoice: " + tileChoice);
		
		if((tileChoice > imgs.length) || zeroCoords) tileChoice = -1;
		else parent.activeBrush = -1;
		
		repaint();
		revalidate();
	}
}
