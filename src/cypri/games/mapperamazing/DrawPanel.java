package cypri.games.mapperamazing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DrawPanel extends JPanel /*implements MouseListener, MouseMotionListener*/{
	private MasterFrame parent;
	private boolean mouseButtonDown = false;
	
	private int[][][] tileID = new int[1][35][25];
	private Vector<int[][][]> backUpMaps = new Vector<int[][][]>();
	private int currentLayer = 1;
	private int backUpNum = 0;
	private boolean fill = false;
	private Vector<ImageIcon> tiles = new Vector<ImageIcon>();
	
	
	public DrawPanel(MasterFrame parent){
		this.parent = parent;
		
		setVisible(true);
		
		//scrollPane = new JScrollPane(cgc);
		//add(scrollPane);
	}
	
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.magenta);
        
        int tileSize = parent.tilePanel.getTileSize();
        
        //fill rect by tilemap width (tileID[0]) and tilemap height (tileID[0][0])
		g.fillRect(0, 0, tileID[0].length * tileSize, tileID[0][0].length * tileSize);
		
		if(tiles.size() > 0){
			for(int k = 0; k < tileID.length; k++){
				for(int x = 0; x < tileID[0].length; x++){
					for(int y = 0; y < tileID[0][0].length; y++){
						try{
							tiles.get(tileID[k][x][y]).paintIcon(this, g, x * tileSize, y * tileSize);
						}
						
						catch(ArrayIndexOutOfBoundsException e){
							tiles.get(tileID[k][0][0]).paintIcon(this, g, x * tileSize, y * tileSize);
						}
					}
				}
			}
		}
		
		g.setColor(Color.red);
        
		if(parent.xmlList != null){
			System.out.println("not null");
			int panelNum = 0;
	        for(int i = 0; i < parent.xmlList.xmlPanels.size(); i++){
	        	System.out.println("panels side > 0");
	        	XMLPanel p = parent.xmlList.xmlPanels.get(i);
	        	for(XMLObj o :  parent.xmlObjs.get(p.myNum)){
	        		System.out.println("drawing");
	        		g.fillRect(o.vars.get(0) * tileSize, o.vars.get(1) * tileSize,  tileSize, tileSize);
	        		g.drawString("type: " + p.myNum, o.vars.get(0) * tileSize, o.vars.get(1) * tileSize);
	        	}
	        	panelNum++;
	        }
		}
		
		parent.error(tileID.length + " | " + tileID[0].length + " | " + tileID[0][0].length);
	}
	
	public void addTile(String name){
		tiles.add(new ImageIcon(name));
	}
	
	public void addTile(ImageIcon img){
		tiles.add(img);
	}
	
	public void clearTiles(){
		tiles.clear();
	}
	
	public void changeMapSize(int newWidth, int newHeight){
	//	if(newWidth > 0 && newHeight > 0){
			int tileSize = parent.tilePanel.getTileSize();
			int[][][] temp = new int[tileID.length][tileID[0].length][tileID[0][0].length];
			//do this for every layer
			for(int k = 0; k < tileID.length; k++){	
				
				//copy old array
				for(int i = 0; i < tileID[0].length; i++){
					temp[k][i] = Arrays.copyOf(tileID[k][i], tileID[k][i].length);
				}
				
			}	
			
			//create new array
			tileID = new int[temp.length][newWidth][newHeight];
			parent.error("height: " + tileID[0][0].length);
			
			//create var aWidth, and give it the size of the old array if it's longer then the old array
			int aWidth = newWidth;
			if(temp[0].length < newWidth) aWidth =  temp[0].length;
			
			for(int k = 0; k < tileID.length; k++){			
				//copy old array into new array
				for(int i = 0; i < aWidth; i++){
					tileID[k][i] = Arrays.copyOf(temp[k][i], newHeight);
				}
			}
			
			//repaint the screen
			this.repaint();
			setPreferredSize(new Dimension(newWidth * tileSize, newHeight * tileSize));
			this.revalidate();
			parent.jsp.revalidate();
			
			backUpTiles();
			System.out.println(tileID[0].length);
	//}
		//else parent.error("Map width and height must be positive.");
	}
	
	public void changeNumLayers(int newLayerNum){
	//	if(newWidth > 0 && newHeight > 0){
			int tileSize = parent.tilePanel.getTileSize();
			int[][][] temp = new int[tileID.length][tileID[0].length][tileID[0][0].length];
			//do this for every layer
			for(int k = 0; k < tileID.length; k++){	
				
				//copy old array
				temp[k] = Arrays.copyOf(tileID[k], tileID[k].length);
		
				
			}
			
			//parent.error("CNL->temp[0].length (1): " + temp[0].length);
			
			//create new array
			tileID = new int[newLayerNum][temp[0].length][temp[0][0].length];
			//parent.error("height: " + tileID[0][0].length);
			
			//parent.error("CNL->tileID[0].length (1): " + tileID[0].length);
			
			//create var aWidth, and give it the size of the old array if it's longer then the old array
			int aLayerNum = newLayerNum;
			if(temp.length < newLayerNum) aLayerNum =  temp.length;
			
			//copy old array into new array
		
			for(int k = 0; k < aLayerNum; k++){
				for(int i = 0; i < tileID[0].length; i++){
					tileID[k][i] = Arrays.copyOf(temp[k][i], tileID[0][0].length);
				}
			}
	
			
			if(getCurrentLayer() > getMapLayers()) setCurrentLayer(getMapLayers());
			//parent.error("CNL->tileID[0].length (2): " + tileID[0].length);
			//repaint the screen
			this.repaint();
			//setPreferredSize(new Dimension(tileID[0].length * tileSize, tileID[0][0].length * tileSize));
			this.revalidate();
			parent.jsp.revalidate();
			
			//backUpTiles();
			System.out.println(tileID.length);
		
	//}
		//else parent.error("Map width and height must be positive.");
	}
	
	public int getMapLayers(){
		return tileID.length;
	}
	
	public int getMapWidth(){
		return tileID[0].length;
	}
	
	
	public int getMapHeight(){
		return tileID[0][0].length;
	}
	
	public int getCurrentLayer(){
		return currentLayer;
	}
	
	public void setCurrentLayer(int i){
		currentLayer = i;
	}
	
	public byte getTileIDAsByte(int layer, int x, int y){
		if(tileID[layer][x][y] < 255){
			return (byte) (tileID[layer][x][y]);
		}
		
		return -1;
	}
	
	public void setTileIDAsByte(int layer, int x, int y, byte id){
		Byte bid = new Byte(id);
		tileID[layer][x][y] = bid.intValue();
	}
	
	public void backUpTiles(){
		System.out.println("backing up tiles: " + backUpNum);
		backUpGoDefault();
		System.out.println("backing up tiles2: " + backUpNum);
		int[][][] temp = new int[tileID.length][tileID[0].length][tileID[0][0].length];
		for(int k = 0; k < tileID.length; k++){
			for(int i = 0; i < tileID[0].length; i++){
				temp[k][i] = Arrays.copyOf(tileID[k][i], tileID[k][i].length);
			}
		}
		
		backUpMaps.add(temp);
		while(backUpMaps.size() >= 20){
			backUpMaps.remove(0);
		}

		backUpGoForward();
		System.out.println("backing up tiles3: " + backUpNum);
		
	}
	
	public void restoreBackedUpTiles(){
		System.out.println("restoring backed up tiles: " + backUpNum);
		//backUpGoBack();
		if(backUpMaps.size() > 0){
			int[][][] vecArray = backUpMaps.get(backUpNum);
			int[][][] temp = new int[vecArray.length][vecArray[0].length][vecArray[0][0].length];
			for(int k = 0; k < vecArray.length; k++){
				for(int i = 0; i < vecArray[0].length; i++){
					temp[k][i] = Arrays.copyOf(vecArray[k][i], vecArray[k][i].length);
				}
			}
			tileID = temp;
			revalidate();
		}
	}
	
	public void backUpGoForward(){
		backUpNum++;
		
		if(backUpNum >= backUpMaps.size()) backUpNum = backUpMaps.size() - 1;
	}
	
	public void backUpGoBack(){
		backUpNum--;
		
		if(backUpNum < 0) backUpNum = 0;
	}
	
	public void backUpGoDefault(){
		backUpNum = backUpMaps.size();
		backUpGoBack();
	}
	
	public void drawTiles(int layer, int x, int y){
		int tileSize = parent.tilePanel.getTileSize();
		
		int mouseX = x;
		int mouseY = y;
		
		if(mouseX > 0) mouseX = mouseX/tileSize;
		else mouseX = 0;
		
		if(mouseY > 0) mouseY = mouseY/tileSize;
		else mouseY = 0;
		
		int choice = parent.tilePanel.getChoice();
		
		if(choice >= 0 && choice < tiles.size()){
			if(tileID[layer][0].length > mouseY && tileID[layer].length > mouseX){
				tileID[layer][mouseX][mouseY] = choice;
			}
		}
		
		else{
			parent.error("Tile choice larger than tile vector.");
		}
		
		revalidate();
	}
	
	private void setTile(int layer, int x, int y, int id){
		tileID[layer][x][y] = id;
	}
	
	private boolean tileCheck(int layer, int x, int y, int id){
		if(x < 0) return false;
		if(y < 0) return false;
		if(layer >= tileID.length) return false;
		if(x >= tileID[0].length) return false;
		if(y >= tileID[0][0].length) return false;
		if(tileID[layer][x][y] == id) return true;
		else return false;
	}
	
	private void fillCheck(int layer, int x, int y, int id, int idToFill){
		if(tileID[layer][x][y] != id){
			if(idToFill == -1) idToFill = tileID[layer][x][y];
			
			setTile(layer,x,y,id);
			
			if(tileCheck(layer, x - 1, y, idToFill)) fillCheck(layer, x - 1,y,id, idToFill);
			if(tileCheck(layer, x + 1, y, idToFill)) fillCheck(layer, x + 1,y,id, idToFill);
			if(tileCheck(layer, x, y - 1, idToFill)) fillCheck(layer, x,y - 1,id, idToFill);
			if(tileCheck(layer, x, y + 1, idToFill)) fillCheck(layer, x,y + 1,id, idToFill);
		}
	}
	
	public void fillTiles(int layer, int x, int y){
		int tileSize = parent.tilePanel.getTileSize();
		
		int mouseX = x;
		int mouseY = y;
		
		if(mouseX > 0) mouseX = mouseX/tileSize;
		else mouseX = 0;
		
		if(mouseY > 0) mouseY = mouseY/tileSize;
		else mouseY = 0;
		
		int choice = parent.tilePanel.getChoice();
		
		if(choice >= 0 && choice < tiles.size()){
			if(tileID[layer][0].length > mouseY && tileID[layer].length > mouseX){
				fillCheck(layer, mouseX,mouseY,choice, -1);
			}
		}
		
		else{
			parent.error("Tile choice larger than tile vector.");
		}
		
		revalidate();
	}

	public void changeBrushMode(){
		fill = !fill;
	}
	
	public boolean isFillMode(){
		return fill;
	}

	public void mousePressed() {
		if(parent.activeBrush < 0) mouseButtonDown = true;
	}


	public void mouseReleased() {
		System.out.println("mouseReleased: " + backUpNum);
		mouseButtonDown = false;
		backUpTiles();
	}

	public void mouseClicked(int x, int y) {
		if(parent.activeBrush < 0){
			if(!fill) drawTiles(currentLayer - 1, x, y);
			else fillTiles(currentLayer- 1, x, y);
		
			backUpTiles();
		}

		else{
			int tileSize = parent.tilePanel.getTileSize();
			int tx = 0;
			int ty = 0;
			if(x > 0) tx = x/tileSize;
			if(y > 0) ty = y/tileSize;
			parent.xmlList.xmlPanels.get(parent.activeBrush).onMapCLick(tx, ty);
			revalidate();
		}
	}

	public void mouseMoved(int x, int y) {
		if(mouseButtonDown) drawTiles(currentLayer - 1, x, y);
	}

	public int getTileID(int layer, int x, int y) {
		return (tileID[layer][x][y]);
	}

	public void setTileID(int layer, int x, int y, int id) {
		tileID[layer][x][y] = id;
	}
}