package cypri.games.mapperamazing;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.thoughtworks.xstream.annotations.*;

@XStreamAlias("panel")
public class XMLPanel{
	@XStreamOmitField
	private MasterFrame parent;
	
	@XStreamOmitField
	JPanel myPanel;
	
	String name;
	String color;
	JLabel panelName;
	
	@XStreamImplicit(itemFieldName = "component")
	ArrayList<XMLComponent> xmlComps  = new ArrayList<XMLComponent>();

	Vector<JComponent> jComps;
	Vector<JLabel> jNames;
	
	@XStreamOmitField
	JButton activeBtn;
	
	@XStreamOmitField
	int myNum;
	
	@XStreamOmitField
	Color myColor;
	
	public XMLPanel(){}
	
	public void setup(MasterFrame parent, int myNum){
		this.parent = parent;
		this.myNum = myNum;
		
		
	    try { 
	    	Field field = Color.class.getField(color);
	    	myColor = (Color) field.get(null);
		} catch (Exception e) { e.printStackTrace(); }
		
		if(myColor == null) System.out.println("color: " + color);
		
		myPanel = new JPanel();
		
		panelName = new JLabel(name + "(type: " + myNum + ")");
		panelName.setBounds(20 +  (200 * myNum), 580, 200, 50);
		
		jComps = new Vector<JComponent>();
		jNames = new Vector<JLabel>();
		
		for(int i = 0; i < xmlComps.size(); i++){
			XMLComponent xmlc = xmlComps.get(i);
			if(xmlc.type == 0){
				JTextField tempTextField = new JTextField();
				tempTextField.setBounds(150, 30 * i, 25, 20);
				jComps.add(tempTextField);
				
				JLabel tempLabel = new JLabel(xmlc.name);
				tempLabel.setBounds(10, 30 * i, 130, 20);
				jNames.add(tempLabel);
			}
			
			if(xmlc.type == 1){
				JCheckBox tempCheckBox = new JCheckBox();
				tempCheckBox.setBounds(150, 30 * i, 25, 20);
				jComps.add(tempCheckBox);
				
				JLabel tempLabel = new JLabel(xmlc.name);
				tempLabel.setBounds(10, 30 * i, 130, 20);
				jNames.add(tempLabel);
			}
		}
		
		activeBtn = new JButton("Activate");
		activeBtn.setBounds(10, (jComps.size() * 30), 170, 20);
		activeBtn.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				 activate();
			}
		});
		//Container content = myFrame.getContentPane();	
		myPanel.setVisible(true);
		
		for(JLabel jl : jNames){
			myPanel.add(jl);
		}
		
		for(JComponent jc : jComps){
			myPanel.add(jc);
		}
		
		
		myPanel.add(activeBtn);
		
		myPanel.setBounds(10 +  (200 * myNum), 630, 200, (jComps.size() * 30) + 60);
		//myPanel.setLocationRelativeTo(null);
		myPanel.setLayout(null);
		
		parent.getContent().add(myPanel);
		parent.getContent().add(panelName);
		parent.repaint();
	}
	
	public void activate(){
		parent.activeBrush = myNum;
	}
	
	public void onMapCLick(int x, int y){
		if(parent.getDelObjMode()){
			for(int i = 0; i < parent.xmlObjs.get(myNum).size(); i++){
				XMLObj temp = parent.xmlObjs.get(myNum).get(i);
				
				if(temp.vars.get(0).intValue() == x && temp.vars.get(1).intValue() == y){
					 parent.xmlObjs.get(myNum).remove(i);
					 i--;
				}
			}
		}
		
		else{
			XMLObj tempObj = new XMLObj();
			tempObj.vars.add(Integer.valueOf(x));
			tempObj.vars.add(Integer.valueOf(y));
			
			for(int i = 0; i < xmlComps.size(); i++){
				XMLComponent c = xmlComps.get(i);
				if(c.type == 0){
					int value = Integer.parseInt(((JTextField) jComps.get(i)).getText());
					tempObj.vars.add(Integer.valueOf(value));
				}
				
				else if (c.type == 1){
					int value = ((JCheckBox) jComps.get(i)).isSelected() ? 1 : 0;
					tempObj.vars.add(Integer.valueOf(value));
				}
			}
			
			for(Integer i : tempObj.vars){
				System.out.print(i.intValue() + " ,");
			}
			
			parent.xmlObjs.get(myNum).add(tempObj);
		}
		System.out.println(" Done.");
	}
}
