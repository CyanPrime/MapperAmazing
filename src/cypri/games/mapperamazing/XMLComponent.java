package cypri.games.mapperamazing;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("component")
public class XMLComponent {
	String name;
	int type;
	
	
	public XMLComponent(){}
	
	public XMLComponent(String name, int type){
		this.name = name;
		this.type = type;
	}
}
