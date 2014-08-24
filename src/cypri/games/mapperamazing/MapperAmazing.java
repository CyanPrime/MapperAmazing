package cypri.games.mapperamazing;

public class MapperAmazing {
	protected static MasterFrame mf;
	
	public MapperAmazing(){
		mf = new MasterFrame(this);
	}
	
	public static void main(String[] args){
		MapperAmazing ma = new MapperAmazing();
	}
}
