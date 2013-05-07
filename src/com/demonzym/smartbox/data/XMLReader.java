package com.demonzym.smartbox.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.graphics.Movie;
import android.util.Log;

/**
 * ����xml�����е���ݽṹȫ�������data������
 * @author Administrator
 *
 */
public class XMLReader {
	
	private static boolean isRootNull(Element e){
		if(e == null)
			return true;
		if(e.getChildNodes().getLength() < 1)
			return true;
		return false;
	}
	
	public static List<Foobar> getFoobarsFromXmlNew(String xml) {
		List<Foobar> foobars = new ArrayList<Foobar>();
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		factory = DocumentBuilderFactory.newInstance();

		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
			// �ҵ���Element
			Element root = document.getDocumentElement();
			if(isRootNull(root))
				return foobars;
//			NodeList nl = root.getChildNodes();
//			Node node = nl.item(0);
			NodeList nodes;
//			String localname;
//			if(node.getNodeType() == Node.ELEMENT_NODE){
//		    	Element element = (Element)node;
//				localname = element.getNodeName();
//				if(localname.equals("class")){
//					if(element.getAttribute("classname").equals("leixing")){
//						nodes = element.getChildNodes();
//					}
//				}
//			}
			nodes = root.getElementsByTagName(Foobar.foobar);
			// �����ڵ������ӽڵ�,foobars ������foobar
			Foobar foobar = null;
			for (int i = 0; i < nodes.getLength(); i++) {
				foobar = new Foobar();
				// ��ȡfoobarԪ�ؽڵ�
				Element foobarElement=(Element)(nodes.item(i));
				// ��ȡid
				Element id = (Element) foobarElement.getElementsByTagName(Foobar.id).item(0);
				if(notNull(id))
					foobar.setId(id.getFirstChild().getNodeValue());
				// ��ȡtitle
				Element title = (Element) foobarElement.getElementsByTagName(Foobar.title).item(0);
				if(notNull(title))
					foobar.setTitle(title.getFirstChild().getNodeValue());
				// ��ȡmimg
				Element mimg = (Element) foobarElement.getElementsByTagName(Foobar.mimg).item(0);
				if(notNull(mimg))
					foobar.setmImg(mimg.getFirstChild().getNodeValue());
				// ��ȡimage
				Element image = (Element) foobarElement.getElementsByTagName(Foobar.image).item(0);
				if(notNull(image))
					foobar.setImage(image.getFirstChild().getNodeValue());
				// ��ȡurl
				Element url = (Element) foobarElement.getElementsByTagName(Foobar.url).item(0);
				if(notNull(url))
					foobar.setUrl(url.getFirstChild().getNodeValue());
				// ��ȡname
				Element name = (Element) foobarElement.getElementsByTagName(Foobar.name).item(0);
				if(notNull(name))
					foobar.setName(name.getFirstChild().getNodeValue());
				// ��ȡlink
				Element link = (Element) foobarElement.getElementsByTagName(Foobar.link).item(0);
				if(notNull(link)){
					foobar.setLink(link.getFirstChild().getNodeValue());
				}					
				// ��ȡtype
				Element type = (Element) foobarElement.getElementsByTagName(Foobar.type).item(0);
				if(notNull(type))
					foobar.setType(type.getFirstChild().getNodeValue());
				if(!notNull(image)&&notNull(url)&&!notNull(id)){
					foobars.add(foobar);
					Log.v("xulongheng.......foobars", foobar+"");
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return foobars;
	}
	
	public static List<Foobar> getFoobarsFromXml_search(String xml) {
		List<Foobar> foobars = new ArrayList<Foobar>();
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		factory = DocumentBuilderFactory.newInstance();

		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
			// �ҵ���Element
			Element root = document.getDocumentElement();
			if(isRootNull(root))
				return foobars;
			NodeList nodes;

			nodes = root.getElementsByTagName(Foobar.foobar);
			// �����ڵ������ӽڵ�,foobars ������foobar
			Foobar foobar = null;
			for (int i = 0; i < nodes.getLength(); i++) {
				foobar = new Foobar();
				// ��ȡfoobarԪ�ؽڵ�
				Element foobarElement=(Element)(nodes.item(i));
				// ��ȡid
				Element id = (Element) foobarElement.getElementsByTagName(Foobar.id).item(0);
				if(notNull(id))
					foobar.setId(id.getFirstChild().getNodeValue());
				// ��ȡtitle
				Element title = (Element) foobarElement.getElementsByTagName(Foobar.title).item(0);
				if(notNull(title))
					foobar.setTitle(title.getFirstChild().getNodeValue());
				// ��ȡmimg
				Element mimg = (Element) foobarElement.getElementsByTagName(Foobar.mimg).item(0);
				if(notNull(mimg))
					foobar.setmImg(mimg.getFirstChild().getNodeValue());
				// ��ȡimage
				Element image = (Element) foobarElement.getElementsByTagName(Foobar.image).item(0);
				if(notNull(image))
					foobar.setImage(image.getFirstChild().getNodeValue());
				// ��ȡurl
				Element url = (Element) foobarElement.getElementsByTagName(Foobar.url).item(0);
				if(notNull(url))
					foobar.setUrl(url.getFirstChild().getNodeValue());
				// ��ȡname
				Element name = (Element) foobarElement.getElementsByTagName(Foobar.name).item(0);
				if(notNull(name))
					foobar.setName(name.getFirstChild().getNodeValue());					
				// ��ȡtype
				Element type = (Element) foobarElement.getElementsByTagName(Foobar.type).item(0);
				if(notNull(type))
					foobar.setType(type.getFirstChild().getNodeValue());
				// ��ȡlink
				Element link = (Element) foobarElement.getElementsByTagName(Foobar.link).item(0);
		
				if(notNull(link)){
					foobar.setLink(link.getFirstChild().getNodeValue());
					//foobars.add(foobar);					
				}
				foobars.add(foobar);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return foobars;
	}
	
	public static List<Foobar> getFoobarsFromXml(String xml) {
		List<Foobar> foobars = new ArrayList<Foobar>();
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		factory = DocumentBuilderFactory.newInstance();

		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
			// �ҵ���Element
			Element root = document.getDocumentElement();
			if(isRootNull(root))
				return foobars;
			NodeList nodes = root.getElementsByTagName(Foobar.foobar);
			// �����ڵ������ӽڵ�,foobars ������foobar
			Foobar foobar = null;
			for (int i = 0; i < nodes.getLength(); i++) {
				foobar = new Foobar();
				// ��ȡfoobarԪ�ؽڵ�
				Element foobarElement=(Element)(nodes.item(i));
				// ��ȡid
				Element id = (Element) foobarElement.getElementsByTagName(Foobar.id).item(0);
				if(notNull(id))
					foobar.setId(id.getFirstChild().getNodeValue());
				// ��ȡlink
				Element link = (Element) foobarElement.getElementsByTagName(Foobar.link).item(0);
				if(notNull(link))
					foobar.setLink(link.getFirstChild().getNodeValue());
				// ��ȡtitle
				Element title = (Element) foobarElement.getElementsByTagName(Foobar.title).item(0);
				if(notNull(title))
					foobar.setTitle(title.getFirstChild().getNodeValue());
				// ��ȡimage
				Element image = (Element) foobarElement.getElementsByTagName(Foobar.image).item(0);
				if(notNull(image))
					foobar.setImage(image.getFirstChild().getNodeValue());
				// ��ȡurl
				Element url = (Element) foobarElement.getElementsByTagName(Foobar.url).item(0);
				if(notNull(url))
					foobar.setUrl(url.getFirstChild().getNodeValue());
				// ��ȡname
				Element name = (Element) foobarElement.getElementsByTagName(Foobar.name).item(0);
				if(notNull(name))
					foobar.setName(name.getFirstChild().getNodeValue());
				foobars.add(foobar);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return foobars;
	}
	
	public static MovieListData getMovieListFromXml(String xml) {
		MovieListData mld = new MovieListData();
		List<Foobar> foobars = mld.mFoobarList;
		
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		factory = DocumentBuilderFactory.newInstance();

		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
			// �ҵ���Element
			Element root = document.getDocumentElement();
			if(isRootNull(root))
				return null;
			Element infos = (Element) root.getElementsByTagName(MovieListData.INFO).item(0);
			Element count = (Element) infos.getElementsByTagName(MovieListData.COUNT).item(0);
			if(notNull(count))
				mld.setCount(count.getFirstChild().getNodeValue());
			Element pagecount = (Element) infos.getElementsByTagName(MovieListData.PAGECOUNT).item(0);
			if(notNull(pagecount))
				mld.setPageCount(pagecount.getFirstChild().getNodeValue());
			Element page = (Element) infos.getElementsByTagName(MovieListData.PAGE).item(0);
			if(notNull(page))
				mld.setPage(page.getFirstChild().getNodeValue());
			Element pagesize = (Element) infos.getElementsByTagName(MovieListData.PAGESIZE).item(0);
			if(notNull(pagesize))
				mld.setPagesize(pagesize.getFirstChild().getNodeValue());
			
			Element rows = (Element) root.getElementsByTagName(MovieListData.ROWS).item(0);
			NodeList nodes = rows.getElementsByTagName(Foobar.foobar);
			// �����ڵ������ӽڵ�,foobars ������foobar
			Foobar foobar = null;
			for (int i = 0; i < nodes.getLength(); i++) {
				foobar = new Foobar();
				// ��ȡfoobarԪ�ؽڵ�
				Element foobarElement=(Element)(nodes.item(i));
				// ��ȡtitle
				Element id = (Element) foobarElement.getElementsByTagName(Foobar.id).item(0);
				if(notNull(id))
					foobar.setId(id.getFirstChild().getNodeValue());
				// ��ȡimage
				Element image = (Element) foobarElement.getElementsByTagName(Foobar.image).item(0);
				if(notNull(image))
					foobar.setImage(image.getFirstChild().getNodeValue());
				// ��ȡlink
				Element link = (Element) foobarElement.getElementsByTagName(Foobar.link).item(0);
				if(notNull(link)){
					foobar.setUrl(link.getFirstChild().getNodeValue());
				}else{
					continue;
				}
				// ��ȡname
				Element name = (Element) foobarElement.getElementsByTagName(Foobar.name).item(0);
				if(notNull(name))
					foobar.setName(name.getFirstChild().getNodeValue());
				foobars.add(foobar);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return mld;
	}

	public static MovieData getMovieDataFromXml(String xml) {
		MovieData md = new MovieData();
		ArrayList<Foobar> foobars = md.mFoobarList;
		
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		factory = DocumentBuilderFactory.newInstance();

		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
			//�ҵ���Element
			Element root = document.getDocumentElement();
			if(isRootNull(root))
				return null;
			Element id = (Element) root.getElementsByTagName(MovieData.id).item(0);
			if(notNull(id))
				md.setId(id.getFirstChild().getNodeValue());
			Element name = (Element) root.getElementsByTagName(MovieData.name).item(0);
			if(notNull(name))
				md.setName(name.getFirstChild().getNodeValue());
//			Element site = (Element) root.getElementsByTagName(MovieData.site).item(0);
//			if(notNull(site))
//				md.setSite(site.getFirstChild().getNodeValue());
			Element img = (Element) root.getElementsByTagName(MovieData.img).item(0);
			if(notNull(img))
				md.setImg(img.getFirstChild().getNodeValue());
			Element director = (Element) root.getElementsByTagName(MovieData.director).item(0);
			if(notNull(director))
				md.setDirector(director.getFirstChild().getNodeValue());
			Element actor = (Element) root.getElementsByTagName(MovieData.actor).item(0);
			if(notNull(actor))
				md.setActor(actor.getFirstChild().getNodeValue());
			Element desc = (Element) root.getElementsByTagName(MovieData.desc).item(0);
			if(notNull(desc))
				md.setDesc(desc.getFirstChild().getNodeValue());			
			Element date = (Element) root.getElementsByTagName(MovieData.date).item(0);
			if(notNull(date))
				md.setDate(date.getFirstChild().getNodeValue());
			
			Element year = (Element) root.getElementsByTagName(MovieData.year).item(0);
			if(notNull(year))
				md.setYear(year.getFirstChild().getNodeValue());
			Element comm = (Element) root.getElementsByTagName(MovieData.comm).item(0);
			if(notNull(comm))
				md.setComm(comm.getFirstChild().getNodeValue());
			Element score = (Element) root.getElementsByTagName(MovieData.score).item(0);
			if(notNull(score))
				md.setScore(score.getFirstChild().getNodeValue());
			Element anothername = (Element) root.getElementsByTagName(MovieData.anothername).item(0);
			if(notNull(anothername))
				md.setAnotherName(anothername.getFirstChild().getNodeValue());
			Element lanage = (Element) root.getElementsByTagName(MovieData.lanage).item(0);
			if(notNull(lanage))
				md.setLanage(lanage.getFirstChild().getNodeValue());
			Element mlong = (Element) root.getElementsByTagName(MovieData.mlong).item(0);
			if(notNull(mlong))
				md.setLong(mlong.getFirstChild().getNodeValue());
			Element type = (Element) root.getElementsByTagName(MovieData.type).item(0);
			if(notNull(type))
				md.setType(type.getFirstChild().getNodeValue());
			Element region = (Element) root.getElementsByTagName(MovieData.region).item(0);
			if(notNull(region))
				md.setRegion(region.getFirstChild().getNodeValue());
			
			Element url = (Element) root.getElementsByTagName(MovieData.url).item(0);
			Element site = (Element) url.getElementsByTagName(MovieData.site).item(0);
			if(notNull(site)){
				foobars = new ArrayList<Foobar>();
				//����site�ֶΣ��������������
				NodeList sitenodes = url.getElementsByTagName(MovieData.site);
				for(int j = 0; j < sitenodes.getLength(); j++){
					foobars.clear();
					Element siteelement = (Element) sitenodes.item(j);
					String sitename = siteelement.getAttribute("sitename");
					NodeList nodes = siteelement.getElementsByTagName(Foobar.foobar);
					// �����ڵ������ӽڵ�,foobars ������foobar
					Foobar foobar = null;
					for (int i = 0; i < nodes.getLength(); i++) {
						foobar = new Foobar();
						// ��ȡfoobarԪ�ؽڵ�
						Element foobarElement=(Element)(nodes.item(i));
						// ��ȡtitle
						Element id1 = (Element) foobarElement.getElementsByTagName(Foobar.id).item(0);
						if(notNull(id1))
							foobar.setId(id1.getFirstChild().getNodeValue());
						// ��ȡimage
						Element image = (Element) foobarElement.getElementsByTagName(Foobar.image).item(0);
						if(notNull(image))
							foobar.setImage(image.getFirstChild().getNodeValue());
						// ��ȡlink
						Element url1 = (Element) foobarElement.getElementsByTagName(Foobar.url).item(0);
						if(notNull(url1)){
							if(url1.getChildNodes().getLength() == 1){
								foobar.setUrl(url1.getFirstChild().getNodeValue());
							}else{
								foobar.setUrl(url1.getChildNodes().item(1).getNodeValue());
							}
						}
						// ��ȡname
						Element name1 = (Element) foobarElement.getElementsByTagName(Foobar.name).item(0);
						if(notNull(name))
							foobar.setName(name1.getFirstChild().getNodeValue());
						foobars.add(foobar);
					}
					
					md.mMap.put(sitename, foobars);
				}
			}else{
			NodeList nodes = url.getElementsByTagName(Foobar.foobar);
			// �����ڵ������ӽڵ�,foobars ������foobar
			Foobar foobar = null;
			for (int i = 0; i < nodes.getLength(); i++) {
				foobar = new Foobar();
				// ��ȡfoobarԪ�ؽڵ�
				Element foobarElement=(Element)(nodes.item(i));
				// ��ȡtitle
				Element id1 = (Element) foobarElement.getElementsByTagName(Foobar.id).item(0);
				if(notNull(id1))
					foobar.setId(id1.getFirstChild().getNodeValue());
				// ��ȡimage
				Element image = (Element) foobarElement.getElementsByTagName(Foobar.image).item(0);
				if(notNull(image))
					foobar.setImage(image.getFirstChild().getNodeValue());
				// ��ȡlink
				Element url1 = (Element) foobarElement.getElementsByTagName(Foobar.url).item(0);
				if(notNull(url1)){
					if(url1.getChildNodes().getLength() == 1){
						foobar.setUrl(url1.getFirstChild().getNodeValue());
					}else{
						foobar.setUrl(url1.getChildNodes().item(1).getNodeValue());
					}
				}
				// ��ȡname
				Element name1 = (Element) foobarElement.getElementsByTagName(Foobar.name).item(0);
				if(notNull(name))
					foobar.setName(name1.getFirstChild().getNodeValue());
				foobars.add(foobar);
			}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return md;
	}


	
	/**
<url>
<foobar>http://data1.hdpfans.com:80/letv.php?id=1664697</foobar>
<dur>2802</dur>
</url>
<total_dur>2802</total_dur>
	 */
	public static TvUrlData getTvUrlFromXml(String xml) {
		TvUrlData mud = new TvUrlData();
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		factory = DocumentBuilderFactory.newInstance();

		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
			// �ҵ���Element
			Element root = document.getDocumentElement();
			if(isRootNull(root))
				return null;
			
			Element total_dur = (Element) root.getElementsByTagName(TvUrlData.total_dur).item(0);
			if(notNull(total_dur))
				mud.setTotalDur(total_dur.getFirstChild().getNodeValue());
			Element url = (Element) root.getElementsByTagName(TvUrlData.url).item(0);
			if(notNull(url)){
				NodeList foobarlist = root.getElementsByTagName(TvUrlData.foobar);
				for(int i = 0; i < foobarlist.getLength(); i++){
					mud.addFoobar(((Element)foobarlist.item(i)).getFirstChild().getNodeValue());
				}
				NodeList durlist = root.getElementsByTagName(TvUrlData.dur);
				for(int i = 0; i < durlist.getLength(); i++){
					mud.addDur(((Element)durlist.item(i)).getFirstChild().getNodeValue());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (DOMException e){
			e.printStackTrace();
		}
		return mud;
	}
	
	public static HashMap<TvClass,ArrayList<TvChannel>> getTvLiveMapFromXml(String xml){
		HashMap<TvClass, ArrayList<TvChannel>> map =
				new HashMap<TvClass, ArrayList<TvChannel>>();
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		factory = DocumentBuilderFactory.newInstance();
		NodeList nodes;
		Element root;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
			// �ҵ���Element
			root = document.getDocumentElement();
			if(isRootNull(root))
				return map;
			
			nodes = root.getChildNodes();
			TvClass currentclass = null;
			for(int i = 0; i < nodes.getLength(); i++){
				Node node = (Node) nodes.item(i);    //�ж��Ƿ�ΪԪ������
				String localname;
			    if(node.getNodeType() == Node.ELEMENT_NODE){
			    	Element element = (Element)node;
					localname = element.getNodeName();
					if(localname.equals("class")){
						String id = element.getAttribute("id");
						String qid = element.getAttribute("Qid");
						String classname =
								element.getAttribute("classname");
						currentclass = new TvClass(id, qid, classname);
						ArrayList<TvChannel> tvchannel_list = new ArrayList<TvChannel>();
						
						map.put(currentclass, tvchannel_list);
					}
					else if(localname.equals("channel")){
						String cid = element.getAttribute("Cid");
						String id = element.getAttribute("id");
						String qid = element.getAttribute("Qid");
						String name = element.getAttribute("name");
						String epg = element.getAttribute("epg");
						String img = element.getAttribute("img");

						ArrayList<TvLink> tvlink_list = new ArrayList<TvLink>();
						
						NodeList nl = element.getElementsByTagName("tvlink");
						for (int j = 0; j < nl.getLength(); j++) {
							Element tvlElement=(Element)(nl.item(j));
							
							String link = tvlElement.getAttribute("link");
							String source = tvlElement.getAttribute("source");
							tvlink_list.add(new TvLink(link, source));
						}
						TvChannel tc = new TvChannel(cid, id, qid, name, epg, img, tvlink_list);
						map.get(currentclass).add(tc);
					}
			    }			    
			}
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (DOMException e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static boolean notNull(Element e){
		return e != null && e.getChildNodes().getLength() != 0;
	}
}
