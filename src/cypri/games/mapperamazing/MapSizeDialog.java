package cypri.games.mapperamazing;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class MapSizeDialog extends JFrame{
	private MasterFrame parent;
	
	
	private JLabel widthLabel;
	private JLabel heightLabel;
	private JTextField widthField;
	private JTextField heightField;
	
	private JButton okay;
	private JButton cancel;
	
	public MapSizeDialog(MasterFrame parent){
		super("Set Map Size");
		this.parent = parent;
		
		setBounds(0, 0, 300, 160);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		widthLabel = new JLabel("Width:");
		widthLabel.setBounds(30, 20, 70, 30);
		heightLabel = new JLabel("Height:");
		heightLabel.setBounds(30, 50, 70, 30);
		
		widthField = new JTextField("50", 4);
		widthField.setBounds(100, 20, 150, 30);
		heightField = new JTextField("25", 4);
		heightField.setBounds(100, 50, 150, 30);
		
		okay = new JButton("Okay");
		okay.setBounds(200, 90, 80, 25);
		okay.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				changeMapSize();
		    }
		});
		
		cancel = new JButton("Cancel");
		cancel.setBounds(110, 90, 80, 25);
		cancel.addActionListener(new ActionListener(){
			@Override
		    public void actionPerformed (ActionEvent event) {
				hideDialog();
		    }
		});
		
		setLocationRelativeTo(parent);
		getContentPane().setLayout(null);
		
		Container content = getContentPane();	
		//setVisible(true);
		
		content.add(widthLabel);
		content.add(heightLabel);
		content.add(widthField);
		content.add(heightField);
		content.add(okay);
		content.add(cancel);
	}
	
	public void hideDialog(){
		setVisible(false);
	}
	
	public void changeMapSize(){
		int sizeX = 20;
		int sizeY = 20;
		
		try{
			sizeX = Integer.parseInt(widthField.getText());
			sizeY = Integer.parseInt(heightField.getText());
		}
		
		catch(Exception e){
			parent.error("Map size was not able to be parsed to number.");
		}
		
		finally{
			parent.dp.changeMapSize(sizeX, sizeY);
			setVisible(false);
		}
	}
}
