package br.ufpe.cin.rssv1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(MainActivity.this, SetPreferenceActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class PrefsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
		}		
	}	
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		private ListView mRssFeed; 

		public PlaceholderFragment() {
		}

		@Override
		public void onStart() {
			super.onStart();
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String url = settings.getString("feedlink", getResources().getString(R.string.feedlink));
			new CarregarFeed().execute(url);

		}

		private class CarregarFeed extends AsyncTask<String, Void, List<ItemRSS>> {

			@Override
			protected List<ItemRSS> doInBackground(String... params) {
				String result = "";
				List<ItemRSS> itemsFeed = new ArrayList<ItemRSS>();
				InputStream in = null;
				try {
					URL url = new URL(params[0]);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					in = conn.getInputStream();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					for (int count; (count = in.read(buffer)) != -1;) {
						out.write(buffer, 0, count);
					}
					byte[] response = out.toByteArray();
					result = new String(response, "UTF-8");
					
					itemsFeed = parse(result);
				    										
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return itemsFeed;
			}

			private List<ItemRSS> parse(String result) throws XmlPullParserException, IOException{
				List<ItemRSS> ListResult = new ArrayList<ItemRSS>();
//				String title = "";
//				String link = "";
//				String description = "";
//				String pubDate = "";
				
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				
				XmlPullParser parser = factory.newPullParser();
				parser.setInput(new StringReader(result));
				
				int eventType = parser.getEventType();
				String text = "";
				ItemRSS item = new ItemRSS();
				String name = "";
				
				while(eventType != XmlPullParser.END_DOCUMENT){
					name = parser.getName();
					switch (eventType) {
					case XmlPullParser.START_TAG:
						if(name.equalsIgnoreCase("item"))
							item = new ItemRSS();
						break;
					case XmlPullParser.TEXT:
						text = parser.getText();
						break;

					case XmlPullParser.END_TAG:
						if(name.equalsIgnoreCase("item"))
							ListResult.add(item);
						else if(name.equalsIgnoreCase("title"))
							item.setTitle(text);
						else if(name.equalsIgnoreCase("link"))
							item.setLink(text);
						else if(name.equalsIgnoreCase("description"))
							item.setDescription(text);
						else if(name.equalsIgnoreCase("pubDate"))
							item.setPubDate(text);						
						break;

					default:
						break;
					}
					eventType = parser.next();
				}
				
				/*while(parser.next() != XmlPullParser.END_DOCUMENT){
					if(parser.getEventType() == XmlPullParser.START_TAG){
						String tag = parser.getName();
						if(tag.equals("item")){
							while(parser.next()!=XmlPullParser.END_TAG){
								//Log.i("XMLparsing", parser.getName());
								if(parser.getEventType() == XmlPullParser.START_TAG){
									String tagItemAberta = parser.getName();
									Log.i("XML ",tagItemAberta.toString());
									
									switch (tagItemAberta.toString()) {
										case "link":
											link = parser.nextText();
											break;
										case "title":
											title = parser.nextText();
											break;
										case "description":
											description = parser.nextText();
											break;
										case "pubDate":
											pubDate = parser.nextText();
											break;
										default:
											parser.next();
											break;
									}									
									parser.nextTag();
								}
							}
							ListResult.add(new ItemRSS(title, link, description, pubDate));
						}
					}
				}*/
				return ListResult;
				
			}
			
			//private ProgressDialog dialog;
			@Override
			protected void onPreExecute() {
				//dialog = ProgressDialog.show(getActivity(), "", "Carregando. Por favor aguarde...", true);
				//mRssFeed.getAdapter().
			}

			@Override
			protected void onPostExecute(List<ItemRSS> result) {
				//dialog.dismiss();
				
				ArrayAdapter<ItemRSS> adapter = new ArrayAdapter<ItemRSS>(getActivity(),
				        android.R.layout.simple_list_item_1, result);
				
				mRssFeed.setAdapter(adapter);
				
				mRssFeed.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Object item = parent.getAdapter().getItem(position);
						//ArrayAdapter<ItemRSS> adapter = (ArrayAdapter<ItemRSS>) parent.getAdapter();
						//ItemRSS item = adapter.getItem(position);
						if(item instanceof ItemRSS)
						{
//							Log.i("RSS title",((ItemRSS) item).getTitle());
//							Log.i("RSS link",((ItemRSS) item).getLink());
//							Log.i("RSS desc",((ItemRSS) item).getDescription());
//							Log.i("RSS date",((ItemRSS) item).getPubDate());
//							Toast.makeText(getActivity(), ((ItemRSS) item).getLink(), Toast.LENGTH_SHORT).show();
							
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((ItemRSS) item).getLink())));
						}
						
					}
				});
			}

		}
	
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			mRssFeed =  (ListView) rootView.findViewById(R.id.listview);
			
			return rootView;
		}
	}
}
