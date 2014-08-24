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
	
	private int[][] tileID = new int[35][25];
	private Vector<int[][]> backUpMaps = new Vector<int[][]>();
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
        
		g.fillRect(0, 0, tileID.length * tileSize, tileID[0].length * tileSize);
		
		if(tiles.size() > 0){
			for(int x = 0; x < tileID.length; x++){
				for(int y = 0; y < tileID[0].length; y++){
					try{
						tiles.get(tileID[x][y]).paintIcon(this, g, x * tileSize, y * tileSize);
					}
					
					catch(ArrayIndexOutOfBoundsException e){
						tiles.get(tileID[0][0]).paintIcon(this, g, x * tileSize, y * tileSize);
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
	}
	
	public void addTile(String name){
		tiles.add(new ImageIcon(name));
	}
	
	public void clearTiles(){
		tiles.clear();
	}
	
	public void changeMapSize(int newWidth, int newHeight){
	//	if(newWidth > 0 && newHeight > 0){
			int tileSize = parent.tilePanel.getTileSize();
			
			int[][] temp = new int[tileID.length][tileID[0].length];
			for(int i = 0; i < tileID.length; i++){
				temp[i] = Arrays.copyOf(tileID[i], tileID[i].length);
			}
			
			
			tileID = new int[newWidth][newHeight];
			parent.error("height: " + tileID[0].length);
			
			int aWidth = newWidth;
			
			if(temp.length < newWidth) aWidth =  temp.length;
			
			for(int i = 0; i < aWidth; i++){
				tileID[i] = Arrays.copyOf(temp[i], newHeight);
			}
			
			this.repaint();
			setPreferredSize(new Dimension(newWidth * tileSize, newHeight * tileSize));
			this.revalidate();
			parent.jsp.revalidate();
			
			backUpTiles();
			System.out.println(tileID[0].length);
	//}
		//else parent.error("Map width and height must be positive.");
	}
	
	
	public int getMapWidth(){
		return tileID.length;
	}
	
	
	public int getMapHeight(){
		return tileID[0].length;
	}
	
	public byte getTileIDAsByte(int x, int y){
		if(tileID[x][y] < 255){
			return (byte) (tileID[x][y]);
		}
		
		return -1;
	}
	
	public void setTileIDAsByte(int x, int y, byte id){
		Byte bid = new Byte(id);
		tileID[x][y] = bid.intValue();
	}
	
	public void backUpTiles(){
		System.out.println("backing up tiles: " + backUpNum);
		backUpGoDefault();
		System.out.println("backing up tiles2: " + backUpNum);
		int[][] temp = new int[tileID.length][tileID[0].length];
		for(int i = 0; i < tileID.length; i++){
			temp[i] = Arrays.copyOf(tileID[i], tileID[i].length);
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
			int[][] vecArray = backUpMaps.get(backUpNum);
			int[][] temp = new int[vecArray.length][vecArray[0].length];
			for(int i = 0; i < vecArray.length; i++){
				temp[i] = Arrays.copyOf(vecArray[i], vecArray[i].length);
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
	
	public void drawTiles(int x, int y){
		int tileSize = parent.tilePanel.getTileSize();
		
		int mouseX = x;
		int mouseY = y;
		
		if(mouseX > 0) mouseX = mouseX/tileSize;
		else mouseX = 0;
		
		if(mouseY > 0) mouseY = mouseY/tileSize;
		else mouseY = 0;
		
		int choice = parent.tilePanel.getChoice();
		
		if(choice >= 0 && choice < tiles.size()){
			if(tileID[0].length > mouseY && tileID.length > mouseX){
				tileID[mouseX][mouseY] = choice;
			}
		}
		
		else{
			parent.error("Tile choice larger than tile vector.");
		}
		
		revalidate();
	}
	
	private void setTile(int x, int y, int id){
		tileID[x][y] = id;
	}
	
	private boolean tileCheck(int x, int y, int id){
		if(x < 0) return false;
		if(y < 0) return false;
		if(x >= tileID.length) return false;
		if(y >= tileID[0].length) return false;
		if(tileID[x][y] == id) return true;
		else return false;
	}
	
	private void fillCheck(int x, int y, int id, int idToFill){
		if(tileID[x][y] != id){
			if(idToFill == -1) idToFill = tileID[x][y];
			
			setTile(x,y,id);
			
			if(tileCheck(x - 1, y, idToFill)) fillCheck(x - 1,y,id, idToFill);
			if(tileCheck(x + 1, y, idToFill)) fillCheck(x + 1,y,id, idToFill);
			if(tileCheck(x, y - 1, idToFill)) fillCheck(x,y - 1,id, idToFill);
			if(tileCheck(x, y + 1, idToFill)) fillCheck(x,y + 1,id, idToFill);
		}
	}
	
	public void fillTiles(int x, int y){
		int tileSize = parent.tilePanel.getTileSize();
		
		int mouseX = x;
		int mouseY = y;
		
		if(mouseX > 0) mouseX = mouseX/tileSize;
		else mouseX = 0;
		
		if(mouseY > 0) mouseY = mouseY/tileSize;
		else mouseY = 0;
		
		int choice = parent.tilePanel.getChoice();
		
		if(choice >= 0 && choice < tiles.size()){
			if(tileID[0].length > mouseY && tileID.length > mouseX){
				fillCheck(mouseX,mouseY,choice, -1);
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
			if(!fill) drawTiles(x, y);
			else fillTiles(x, y);
		
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
		if(mouseButtonDown) drawTiles(x, y);
	}
}