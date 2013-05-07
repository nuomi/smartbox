package com.demonzym.smartbox.protocal;

/**
 * һЩ�̶�����ַ
 * @author Administrator
 *
 */
public class ConstValues {
	
//	public static final String HOST_URL[] = new String[]{
//			"http://tv1.hdpfans.com/~rss.get.sitelist/rss/1/xml/1/ajax/1/administrator/1/key/2e82c4eba58760463338f2951f832265",
//			"http://tv2.hdpfans.com/~rss.get.sitelist/rss/1/xml/1/ajax/1/administrator/1/key/2e82c4eba58760463338f2951f832265",
//			"http://tv3.hdpfans.com/~rss.get.sitelist/rss/1/xml/1/ajax/1/administrator/1/key/2e82c4eba58760463338f2951f832265",
//			"http://tv4.hdpfans.com/~rss.get.sitelist/rss/1/xml/1/ajax/1/administrator/1/key/2e82c4eba58760463338f2951f832265",
//	};
	
	public static final String HOST_URL = "http://v.guozitv.com";
//	public static final String HOST_URL = "http://www.baidu.com";
	
	public static final String SEARCH_URL = "http://v.guozitv.com/search/";
	
	public static final String TV_URL = new String(
			"http://v.guozitv.com/live/uname/admin"
	);
	
	//AndroidTVActivity的handler队列值
	public static final int TV_start1 = 1;
	public static final int TV_start2 = 2;
	public static final int TV_start3 = 3;
	public static final int TV_next=4;
	public static final int TV_last=5;
	public static final int TV_play_next =6;
	public static final int TV_exit = 7;
	
	public static final int HTTP_REQUEST_EXCEPTION = 5; // �������쳣
	public static final int PLAY_NEXT_TV = 24;
	public static final int REQUEST_TIMELISTS_SUCCESS = 26;
	public static final int REQUEST_TIMELISTS_FAIL = 27;

	public static final String tv_server="http://www.7po.com/";
	public static final String url_tv_recommend="http://www.7po.com/xml.php?active";
	public static final String url_tv_new = "http://www.7po.com/interface.php?mod=kaiyuan";		//新的TV_Uri
	public static final String URL_REG = "http://v.guozitv.com/regapp.php";
	public static final String URL_LOGIN = "http://v.guozitv.com/loginapp.php";
	public static final String URL_FAV = "http://v.guozitv.com/member/addmemdb.php";
	
	public static final String url_update = "http://v.guozitv.com/api/smartbox.php"; // apk更新uri
	
	public static final String URL_LOGO = "http://v.guozitv.com/images/logo.png";

	
	public static final String TESTDATA = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><data><class  classname=\"全部类型\">    <foobar><name><![CDATA[喜剧]]></name>     <url>http://v.guozitv.com/sort/0/1/2/1</url>    </foobar>    <foobar><name><![CDATA[爱情]]></name>     <url>http://v.guozitv.com/sort/0/1/3/1</url>    </foobar>    <foobar><name><![CDATA[动作]]></name>     <url>http://v.guozitv.com/sort/0/1/4/1</url>    </foobar>    <foobar><name><![CDATA[恐怖]]></name>     <url>http://v.guozitv.com/sort/0/1/5/1</url>    </foobar>    <foobar><name><![CDATA[科幻]]></name>     <url>http://v.guozitv.com/sort/0/1/6/1</url>    </foobar>    <foobar><name><![CDATA[剧情]]></name>     <url>http://v.guozitv.com/sort/0/1/7/1</url>    </foobar>    <foobar><name><![CDATA[犯罪]]></name>    <url>http://v.guozitv.com/sort/0/1/8/1</url>    </foobar>    <foobar><name><![CDATA[奇幻]]></name>     <url>http://v.guozitv.com/sort/0/1/9/1</url>    </foobar>    <foobar><name><![CDATA[战争]]></name>     <url>http://v.guozitv.com/sort/0/1/10/1</url>    </foobar>    <foobar><name><![CDATA[悬疑]]></name>     <url>http://v.guozitv.com/sort/0/1/11/1</url>    </foobar>    <foobar><name><![CDATA[动画]]></name>     <url>http://v.guozitv.com/sort/0/1/12/1</url>    </foobar>    <foobar><name><![CDATA[文艺]]></name>     <url>http://v.guozitv.com/sort/0/1/13/1</url>    </foobar>    <foobar><name><![CDATA[伦理]]></name>     <url>http://v.guozitv.com/sort/0/1/14/1</url>    </foobar>    <foobar><name><![CDATA[纪录]]></name>     <url>http://v.guozitv.com/sort/0/1/15/1</url>    </foobar>    <foobar><name><![CDATA[传记]]></name>     <url>http://v.guozitv.com/sort/0/1/16/1</url>    </foobar>    <foobar><name><![CDATA[歌舞]]></name>     <url>http://v.guozitv.com/sort/0/1/17/1</url>    </foobar>    <foobar><name><![CDATA[古装]]></name>     <url>http://v.guozitv.com/sort/0/1/18/1</url>    </foobar>    <foobar><name><![CDATA[历史]]></name>     <url>http://v.guozitv.com/sort/0/1/19/1</url>    </foobar>    <foobar><name><![CDATA[惊悚]]></name>     <url>http://v.guozitv.com/sort/0/1/20/1</url>    </foobar>    <foobar><name><![CDATA[其他]]></name>     <url>http://v.guozitv.com/sort/0/1/21/1</url>    </foobar>    </class></data>";	
}
