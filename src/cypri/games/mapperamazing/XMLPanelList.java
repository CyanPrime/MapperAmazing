package cypri.games.mapperamazing;

import java.util.Vector;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.*;
import com.thoughtworks.xstream.io.xml.DomDriver;

@XStreamAlias("panels")
public class XMLPanelList {
	
	@XStreamImplicit(itemFieldName = "panel")
	Vector<XMLPanel> xmlPanels;
	
	public XMLPanelList(){ };
}
