package br.ufpe.cin.rssv1;

public class ItemRSS {
	private String title;
	private String link;
	private String description;
	private String pubDate;
	
	public ItemRSS(){
		
	}
	
	public ItemRSS(String title, String link, String description, String pubDate){
		setTitle(title);
		setLink(link);
		setDescription(description);
		setPubDate(pubDate);
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getLink(){
		return link;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getPubDate(){
		return pubDate;
	}
	
	public void setTitle(String t){
		title = t;
	}
	
	public void setLink(String l){
		link = l;
	}
	
	public void setDescription(String d){
		description = d;
	}
	
	public void setPubDate(String pd){
		pubDate = pd;
	}

	@Override
	public String toString() {
		return  title;
	}
	

}
