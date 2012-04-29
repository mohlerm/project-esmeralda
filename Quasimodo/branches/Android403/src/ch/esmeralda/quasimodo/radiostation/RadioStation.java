package ch.esmeralda.quasimodo.radiostation;

public class RadioStation {
	public RadioStation(String name, String url) {
		this.name = name;
		this.url = url;
		this.newtag = false;
	}
	public String name;
	public String url;
	public boolean newtag;
	
	@Override
	public String toString(){
		return name+": "+url;
	}
}
